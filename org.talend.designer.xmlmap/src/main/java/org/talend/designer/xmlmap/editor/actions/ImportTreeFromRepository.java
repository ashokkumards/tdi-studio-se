// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.xmlmap.editor.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.commons.xml.XmlUtil;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Concept;
import org.talend.core.model.metadata.builder.connection.ConceptTarget;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.connection.MdmConceptType;
import org.talend.core.model.metadata.builder.connection.SchemaTarget;
import org.talend.core.model.metadata.builder.connection.XMLFileNode;
import org.talend.core.model.metadata.builder.connection.XmlFileConnection;
import org.talend.core.model.metadata.builder.connection.impl.XmlXPathLoopDescriptorImpl;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.properties.XmlFileConnectionItem;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.core.ui.IMDMProviderService;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.xmlmap.model.emf.xmlmap.AbstractInOutTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.InputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.NodeType;
import org.talend.designer.xmlmap.model.emf.xmlmap.OutputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.TreeNode;
import org.talend.designer.xmlmap.model.emf.xmlmap.XmlmapFactory;
import org.talend.designer.xmlmap.parts.TreeNodeEditPart;
import org.talend.designer.xmlmap.ui.dialog.RepositoryXmlSelectionDialog;
import org.talend.designer.xmlmap.ui.tabs.MapperManager;
import org.talend.designer.xmlmap.util.XmlMapConnectionBuilder;
import org.talend.designer.xmlmap.util.XmlMapUtil;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.utils.ConnectionContextHelper;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.treeNode.Attribute;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.treeNode.Element;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.treeNode.FOXTreeNode;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.treeNode.NameSpaceNode;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.util.StringUtil;
import org.talend.repository.ui.wizards.metadata.connection.files.xml.util.TreeUtil;

/**
 * DOC talend class global comment. Detailled comment
 */
public class ImportTreeFromRepository extends SelectionAction {

    private MapperManager mapperManager;

    private TreeNode schemaNode;

    private Shell shell;

    private Map<String, Integer> xpathAndOrder = new HashMap<String, Integer>();

    private boolean input;

    private List schemaTargets;

    private String absoluteXPathQuery = "";

    public static final String ID = "org.talend.designer.xmlmap.editor.actions.ImportTreeFromRepository";

    private static final String RELATIVE_PATH = "../";

    private static final String RELATIVE_PATH_PATTERN = "\\.\\./";

    List<XMLFileNode> root;

    List<XMLFileNode> group;

    List<XMLFileNode> loop;

    List<TreeNode> groupElements;

    TreeNode loopNode = null;

    private List<String> targetAbsolutePath;

    /**
     * DOC talend ImportTreeFromRepository constructor comment.
     * 
     * @param part
     */
    public ImportTreeFromRepository(IWorkbenchPart part, Shell shell) {
        super(part);
        this.shell = shell;
        setId(ID);
        setText("Import From Repository");
    }

    @Override
    public void run() {
        targetAbsolutePath = null;
        loopNode = null;
        xpathAndOrder.clear();
        RepositoryXmlSelectionDialog reviewDialog = new RepositoryXmlSelectionDialog(shell, new String[] { "XML", "MDM" });
        if (reviewDialog.open() == Window.OK) {
            TreeNode treeNodeRoot = XmlMapUtil.getTreeNodeRoot(schemaNode);

            XmlMapUtil.detachNodeConnections(treeNodeRoot, mapperManager.getCopyOfMapData(), true);
            schemaNode.getChildren().clear();
            RepositoryNode repositoryNode = reviewDialog.getResult();

            Item item = repositoryNode.getObject().getProperty().getItem();
            try {
                if (item instanceof XmlFileConnectionItem) {
                    XmlFileConnectionItem xmlitem = (XmlFileConnectionItem) item;
                    XmlFileConnection connection = (XmlFileConnection) xmlitem.getConnection();
                    prepareEmfTreeFromXml(connection);
                } else if (item instanceof MDMConnectionItem) {
                    String selectedSchema = (String) repositoryNode.getProperties(EProperties.LABEL);
                    MDMConnection connection = (MDMConnection) ((MDMConnectionItem) item).getConnection();
                    prepareEmfTreeFromMdm(connection, selectedSchema);
                }
            } catch (Exception e) {
                ExceptionHandler.process(e);
            } finally {
                if (schemaNode.getChildren().isEmpty()) {
                    TreeNode rootNode = createModel();
                    rootNode.setName("root");
                    rootNode.setNodeType(NodeType.ELEMENT);
                    rootNode.setType(XmlMapUtil.DEFAULT_DATA_TYPE);
                    rootNode.setXpath(XmlMapUtil.getXPath(schemaNode.getXpath(), "root", NodeType.ELEMENT));
                    schemaNode.getChildren().add(rootNode);
                    showError();
                }
            }
            if (loopNode == null && !schemaNode.getChildren().isEmpty()) {
                schemaNode.getChildren().get(0).setLoop(true);
                schemaNode.getChildren().get(0).setMain(true);
            }

            AbstractInOutTree tree = null;
            if (schemaNode.eContainer() instanceof InputXmlTree) {
                XmlMapConnectionBuilder.rebuildLinks(schemaNode, mapperManager.getCopyOfMapData());
                mapperManager.refreshInputTreeSchemaEditor((InputXmlTree) schemaNode.eContainer());
                tree = (InputXmlTree) schemaNode.eContainer();
            } else if (schemaNode.eContainer() instanceof OutputXmlTree) {
                mapperManager.refreshOutputTreeSchemaEditor((OutputXmlTree) schemaNode.eContainer());
                tree = (OutputXmlTree) schemaNode.eContainer();
            }
            if (tree != null) {
                mapperManager.getProblemsAnalyser().checkProblems(tree);
                mapperManager.getMapperUI().updateStatusBar();
            }
        }
    }

    private void prepareEmfTreeFromXml(XmlFileConnection connection) {
        if (!connection.isInputModel()) {
            String file = connection.getXmlFilePath();
            List<FOXTreeNode> list = new ArrayList<FOXTreeNode>();
            // fix for TDI-20671 , root element is loop in output
            String rootXpath = null;
            if (!connection.getRoot().isEmpty()) {
                rootXpath = connection.getRoot().get(0).getXMLPath();
            } else if (!connection.getLoop().isEmpty()) {
                rootXpath = connection.getLoop().get(0).getXMLPath();
            }
            if (rootXpath == null) {
                return;
            }
            File xmlFile = new File(file);
            if (xmlFile.exists()) {
                list = TreeUtil.getFoxTreeNodesForXmlMap(xmlFile.getAbsolutePath(), rootXpath);
            } else if (connection.getFileContent() != null && connection.getFileContent().length > 0) {
                String xsdFile = initFileContent(connection);
                if (xsdFile != null && new File(xsdFile).exists()) {
                    list = TreeUtil.getFoxTreeNodesForXmlMap(xsdFile, rootXpath);
                }
            }

            root = connection.getRoot();
            loop = connection.getLoop();
            group = connection.getGroup();
            groupElements = new ArrayList<TreeNode>();
            prepareModelFromOutput(list, schemaNode);
            if (loopNode != null) {
                fillGroup(loopNode, groupElements);
            } else {
                if (!schemaNode.getChildren().isEmpty()) {
                    schemaNode.getChildren().get(0).setLoop(true);
                }
            }
        } else {
            List<SchemaTarget> schemaTargets = null;
            if (!connection.getSchema().isEmpty() && connection.getSchema().get(0) instanceof XmlXPathLoopDescriptorImpl) {
                absoluteXPathQuery = ((XmlXPathLoopDescriptorImpl) connection.getSchema().get(0)).getAbsoluteXPathQuery();
                schemaTargets = ((XmlXPathLoopDescriptorImpl) connection.getSchema().get(0)).getSchemaTargets();
            }

            // fix for TDI-8707 : only import mapped elements from connection to xml map
            if (schemaTargets == null || schemaTargets.isEmpty()) {
                return;
            }

            this.schemaTargets = schemaTargets;
            String file = connection.getXmlFilePath();
            List<FOXTreeNode> list = new ArrayList<FOXTreeNode>();
            File xmlFile = new File(file);
            if (xmlFile.exists()) {
                list = TreeUtil.getFoxTreeNodesForXmlMap(xmlFile.getAbsolutePath(), absoluteXPathQuery);
            } else if (connection.getFileContent() != null && connection.getFileContent().length > 0) {
                String xsdFile = initFileContent(connection);
                if (xsdFile != null && new File(xsdFile).exists()) {
                    list = TreeUtil.getFoxTreeNodesForXmlMap(xsdFile, absoluteXPathQuery);
                }
            }
            prepareEmfTree(list, schemaNode);
        }

    }

    private void prepareEmfTree(List<FOXTreeNode> list, TreeNode parent) {
        prepareEmfTree(list, parent, false);
    }

    private void prepareEmfTree(List<FOXTreeNode> list, TreeNode parent, boolean ignoreMapped) {
        if (list == null || list.isEmpty()) {
            return;
        }
        String xPath = parent.getXpath();
        TreeNode realParentNode = parent;
        if (parent.isSubstitution() || parent.isChoice()) {
            realParentNode = XmlMapUtil.getRealParentNode(parent);
            if (realParentNode != null) {
                xPath = realParentNode.getXpath();
            }
        }
        for (FOXTreeNode foxNode : list) {
            TreeNode createTreeNode = createModel();
            createTreeNode.setOptional(foxNode.isOptional());
            String label = foxNode.getLabel();
            createTreeNode.setName(label);
            if (foxNode instanceof Element) {
                createTreeNode.setNodeType(NodeType.ELEMENT);
                if (foxNode.isChoice()) {
                    createTreeNode.setChoice(foxNode.isChoice());
                } else if (foxNode.isSubstitution()) {
                    createTreeNode.setSubstitution(foxNode.isSubstitution());
                }
            } else if (foxNode instanceof Attribute) {
                createTreeNode.setNodeType(NodeType.ATTRIBUT);
            } else if (foxNode instanceof NameSpaceNode) {
                createTreeNode.setNodeType(NodeType.NAME_SPACE);
                createTreeNode.setDefaultValue(foxNode.getDefaultValue());
                if (createTreeNode.getName() == null || createTreeNode.getName().equals("")) {
                    createTreeNode.setName(XmlMapUtil.DEFAULT_NAME_SPACE_PREFIX);
                }
            }
            createTreeNode.setXpath(XmlMapUtil.getXPath(xPath, label, createTreeNode.getNodeType()));
            if (foxNode.getDataType() != null && !"".equals(foxNode.getDataType())) {
                if (foxNode.getDataType().equals("id_Date")) {
                    createTreeNode.setPattern("\"dd-MM-yyyy\"");//$NON-NLS-1$
                }
                createTreeNode.setType(foxNode.getDataType());
            } else {
                createTreeNode.setType(XmlMapUtil.DEFAULT_DATA_TYPE);
            }

            // tempXpath is current xpath remove schema node xpath like: row1:newColumn1
            String tempXpath = createTreeNode.getXpath().substring(schemaNode.getXpath().length() + 1);
            if (createTreeNode.isChoice() || createTreeNode.isSubstitution()) {
                if (!isMappedChoiceSubs(foxNode, xPath)) {
                    continue;
                }

            } else if (!ignoreMapped && !isMappedChild(tempXpath)) {
                continue;
            }

            if (tempXpath.equals(absoluteXPathQuery)) {
                createTreeNode.setLoop(true);
                createTreeNode.setOptional(foxNode.isOptional());
                loopNode = createTreeNode;
            }

            parent.getChildren().add(createTreeNode);
            if (createTreeNode.isLoop()) {
                XmlMapUtil.upsetMainNode(createTreeNode);
            }
            if (foxNode.getChildren() != null && !foxNode.getChildren().isEmpty()) {
                prepareEmfTree(foxNode.getChildren(), createTreeNode, ignoreMapped);
            }
        }

    }

    private boolean isMappedChoiceSubs(FOXTreeNode choiceSubs, String parentXpath) {
        // if any child of the choice or subs is mapped to target schema , it should be created in xmlmap
        for (FOXTreeNode child : choiceSubs.getChildren()) {
            NodeType nodeType = null;
            if (child instanceof Element) {
                nodeType = NodeType.ELEMENT;
            } else if (child instanceof Attribute) {
                nodeType = NodeType.ATTRIBUT;
            } else if (child instanceof NameSpaceNode) {
                nodeType = NodeType.NAME_SPACE;
            }
            String tempPath = XmlMapUtil.getXPath(parentXpath, child.getLabel(), nodeType);
            tempPath = tempPath.substring(schemaNode.getXpath().length() + 1);
            if (isMappedChild(tempPath)) {
                return true;
            }
        }
        return false;
    }

    private void prepareEmfTreeFromMdm(MDMConnection connection, String selectedConcept) {
        if (connection == null || selectedConcept == null) {
            return;
        }
        EList<Concept> schemas = connection.getSchemas();
        Concept selected = null;
        for (int i = 0; i < schemas.size(); i++) {
            Concept concept = schemas.get(i);
            if (selectedConcept.equals(concept.getLabel())) {
                selected = concept;
                break;
            }
        }

        if (selected != null) {
            IMDMProviderService service = (IMDMProviderService) GlobalServiceRegister.getDefault().getService(
                    IMDMProviderService.class);
            if (service != null) {
                boolean initConcepts = service.initConcepts(connection);
                if (initConcepts) {
                    if (!MdmConceptType.OUTPUT.equals(selected.getConceptType())) {
                        String prefix = service.getXPathPrefixValue(selected);
                        prefix = TalendTextUtils.removeQuotes(prefix);
                        absoluteXPathQuery = selected.getLoopExpression();
                        EList<ConceptTarget> conceptTargets = selected.getConceptTargets();
                        if (conceptTargets == null || absoluteXPathQuery == null) {
                            return;
                        }
                        this.schemaTargets = conceptTargets;
                        List<FOXTreeNode> list = TreeUtil.getFoxTreeNodesForXmlMap(getTempTemplateXSDFile().getAbsolutePath(),
                                absoluteXPathQuery);
                        TreeNode pNode = schemaNode;
                        if (MdmConceptType.RECEIVE.equals(selected.getConceptType())) {
                            List<FOXTreeNode> updateNodesList = TreeUtil.parseMDMUpdateReport(shell, true);
                            if (updateNodesList == null) {
                                if (prefix != null && !"".equals(prefix)) {
                                    String[] preValues = prefix.split(XmlMapUtil.XPATH_SEPARATOR);
                                    for (String value : preValues) {
                                        if (!"".equals(value)) {
                                            TreeNode createTreeNode = createModel();
                                            createTreeNode.setName(value);
                                            createTreeNode.setNodeType(NodeType.ELEMENT);
                                            createTreeNode.setType(XmlMapUtil.DEFAULT_DATA_TYPE);
                                            createTreeNode.setXpath(XmlMapUtil.getXPath(pNode.getXpath(),
                                                    createTreeNode.getName(), createTreeNode.getNodeType()));
                                            pNode.getChildren().add(createTreeNode);
                                            pNode = createTreeNode;
                                        }
                                    }
                                    absoluteXPathQuery = prefix + absoluteXPathQuery;
                                }
                            } else {
                                TreeNode rootNode = createDefaultTreeNode(pNode, "exchange"); //$NON-NLS-1$
                                TreeNode reportNode = createDefaultTreeNode(rootNode, "report"); //$NON-NLS-1$
                                TreeNode itemNode = createDefaultTreeNode(rootNode, "item"); //$NON-NLS-1$
                                // parse the update content..
                                prepareEmfTree(updateNodesList, reportNode, true);
                                pNode = itemNode;
                                absoluteXPathQuery = "/exchange/item" + absoluteXPathQuery; //$NON-NLS-1$
                            }
                        }
                        prepareEmfTree(list, pNode);
                    } else {
                        List<FOXTreeNode> list = TreeUtil.getFoxTreeNodesForXmlMap(getTempTemplateXSDFile().getAbsolutePath(),
                                selected.getRoot().get(0).getXMLPath());

                        root = selected.getRoot();
                        loop = selected.getLoop();
                        group = selected.getGroup();
                        groupElements = new ArrayList<TreeNode>();

                        prepareModelFromOutput(list, schemaNode);
                        if (loopNode != null) {
                            fillGroup(loopNode, groupElements);
                        } else {
                            if (!schemaNode.getChildren().isEmpty()) {
                                schemaNode.getChildren().get(0).setLoop(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private TreeNode createDefaultTreeNode(TreeNode parentNode, String name) {
        TreeNode createTreeNode = createModel();
        createTreeNode.setName(name);
        createTreeNode.setNodeType(NodeType.ELEMENT);
        createTreeNode.setType(XmlMapUtil.DEFAULT_DATA_TYPE);
        createTreeNode
                .setXpath(XmlMapUtil.getXPath(parentNode.getXpath(), createTreeNode.getName(), createTreeNode.getNodeType()));
        parentNode.getChildren().add(createTreeNode);

        return createTreeNode;
    }

    private boolean isMappedChild(String tempXpath) {
        if (targetAbsolutePath == null) {
            targetAbsolutePath = new ArrayList<String>();
            targetAbsolutePath.add(absoluteXPathQuery);
            Pattern regex = Pattern.compile(RELATIVE_PATH_PATTERN, Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE //$NON-NLS-1$
                    | Pattern.MULTILINE);
            for (Object obj : schemaTargets) {
                String relativeXPathQuery = "";
                if (obj instanceof SchemaTarget) {
                    relativeXPathQuery = ((SchemaTarget) obj).getRelativeXPathQuery();
                } else if (obj instanceof ConceptTarget) {
                    relativeXPathQuery = ((ConceptTarget) obj).getRelativeLoopExpression();
                }

                StringBuffer tempAbsolute = new StringBuffer();
                tempAbsolute.append(absoluteXPathQuery);
                tempAbsolute.append(XmlMapUtil.XPATH_SEPARATOR);
                tempAbsolute.append(relativeXPathQuery);

                if (relativeXPathQuery.startsWith(RELATIVE_PATH)) {
                    Matcher regexMatcher = regex.matcher(relativeXPathQuery);
                    int relativeLength = 0;
                    while (regexMatcher.find()) {
                        relativeLength++;
                    }
                    if (relativeLength > 0) {
                        String subRelativeQuery = relativeXPathQuery.substring(relativeLength * RELATIVE_PATH.length(),
                                relativeXPathQuery.length());
                        String[] absoluteSplit = absoluteXPathQuery.split(XmlMapUtil.XPATH_SEPARATOR);
                        if (absoluteSplit.length > relativeLength) {
                            tempAbsolute = new StringBuffer();
                            for (int i = 0; i < absoluteSplit.length - relativeLength; i++) {
                                tempAbsolute.append(absoluteSplit[i]);
                                tempAbsolute.append(XmlMapUtil.XPATH_SEPARATOR);
                            }
                            tempAbsolute.append(subRelativeQuery);
                        }

                    }
                }
                targetAbsolutePath.add(tempAbsolute.toString());
            }
        }

        for (String absTarget : targetAbsolutePath) {
            if (absTarget.startsWith(tempXpath)) {
                return true;
            }
        }

        return false;
    }

    /*
     * same as XmlFileOutputStep2Form.initXmlTreeData()
     */
    private String initFileContent(XmlFileConnection connection) {
        byte[] bytes = connection.getFileContent();
        Project project = ProjectManager.getInstance().getCurrentProject();
        IProject fsProject = null;
        try {
            fsProject = ResourceUtils.getProject(project.getTechnicalLabel());
        } catch (PersistenceException e2) {
            ExceptionHandler.process(e2);
        }
        if (fsProject == null) {
            return null;
        }
        String temPath = fsProject.getLocationURI().getPath() + File.separator + "temp";
        String fileName = "";

        String pathStr = connection.getXmlFilePath();
        if (connection.isContextMode()) {
            ContextType contextType = ConnectionContextHelper.getContextTypeForContextMode(connection, true);
            pathStr = TalendQuoteUtils.removeQuotes(ConnectionContextHelper.getOriginalValue(contextType, pathStr));
        }
        if (pathStr != null && XmlUtil.isXMLFile(pathStr)) {
            fileName = StringUtil.TMP_XML_FILE;
        } else if (pathStr != null && XmlUtil.isXSDFile(pathStr)) {
            fileName = StringUtil.TMP_XSD_FILE;
        }
        File temfile = new File(temPath + File.separator + fileName);
        if (!temfile.exists()) {
            try {
                temfile.createNewFile();
            } catch (IOException e) {
                ExceptionHandler.process(e);
            }
        }

        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(temfile);
            outStream.write(bytes);
            outStream.close();
        } catch (FileNotFoundException e1) {
            ExceptionHandler.process(e1);
        } catch (IOException e) {
            ExceptionHandler.process(e);
        }

        return temfile.getPath();

    }

    private File getTempTemplateXSDFile() {
        IPath tempPath = new Path(System.getProperty("user.dir")).append("temp"); //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-2$
        File tempFile = tempPath.toFile();
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        File file = new File(tempFile, "template.xsd"); //$NON-NLS-1$
        return file;
    }

    private void prepareModelFromOutput(List<FOXTreeNode> foxTreeNodes, TreeNode parent) {
        TreeNode realParent = XmlMapUtil.getRealParentNode(parent);
        for (FOXTreeNode foxNode : foxTreeNodes) {
            TreeNode createTreeNode = createModel();
            createTreeNode.setOptional(foxNode.isOptional());
            String label = foxNode.getLabel();
            createTreeNode.setName(label);
            if (foxNode instanceof Element) {
                createTreeNode.setNodeType(NodeType.ELEMENT);
                if (foxNode.isChoice()) {
                    createTreeNode.setChoice(foxNode.isChoice());
                } else if (foxNode.isSubstitution()) {
                    createTreeNode.setSubstitution(foxNode.isSubstitution());
                }
            } else if (foxNode instanceof Attribute) {
                createTreeNode.setNodeType(NodeType.ATTRIBUT);
            } else if (foxNode instanceof NameSpaceNode) {
                createTreeNode.setNodeType(NodeType.NAME_SPACE);
                createTreeNode.setDefaultValue(foxNode.getDefaultValue());
                if (createTreeNode.getName() == null || createTreeNode.getName().equals("")) {
                    createTreeNode.setName(XmlMapUtil.DEFAULT_NAME_SPACE_PREFIX);
                }
            }
            if (foxNode.getDataType() != null && !"".equals(foxNode.getDataType())) {
                if (foxNode.getDataType().equals("id_Date")) {
                    createTreeNode.setPattern("\"dd-MM-yyyy\"");//$NON-NLS-1$
                }
                createTreeNode.setType(foxNode.getDataType());
            } else {
                createTreeNode.setType(XmlMapUtil.DEFAULT_DATA_TYPE);
            }
            String xPath = XmlMapUtil.getXPath(realParent.getXpath(), label, createTreeNode.getNodeType());
            createTreeNode.setXpath(xPath);
            if (createTreeNode.isSubstitution() || createTreeNode.isChoice()) {
                if (isMappedChoiceSubsOutput(foxNode, realParent.getXpath())) {
                    parent.getChildren().add(createTreeNode);
                }
            } else {
                String subXpath = xPath.substring(schemaNode.getXpath().length() + 1, xPath.length());
                XMLFileNode found = findXmlFileNode(subXpath, root);
                if (found == null) {
                    found = findXmlFileNode(subXpath, group);
                    if (found != null) {
                        groupElements.add(createTreeNode);
                    }
                }
                if (found == null) {
                    found = findXmlFileNode(subXpath, loop);
                    if (found != null) {
                        if (loopNode == null) {
                            loopNode = createTreeNode;
                            loopNode.setLoop(true);
                            loopNode.setOptional(foxNode.isOptional());
                        }
                    }
                }
                if (found == null) {
                    continue;
                } else {
                    xpathAndOrder.put(createTreeNode.getXpath(), found.getOrder());
                    Integer index = null;
                    List<TreeNode> children = parent.getChildren();
                    for (int i = 0; i < children.size(); i++) {
                        Integer order = xpathAndOrder.get(children.get(i).getXpath());
                        if (found.getOrder() < order) {
                            index = i;
                        }
                    }
                    if (index == null) {
                        parent.getChildren().add(createTreeNode);
                    } else {
                        parent.getChildren().add(index, createTreeNode);
                    }
                }
            }
            prepareModelFromOutput(foxNode.getChildren(), createTreeNode);
        }
    }

    private boolean isMappedChoiceSubsOutput(FOXTreeNode node, String pXpath) {
        List<FOXTreeNode> children = node.getChildren();
        List<FOXTreeNode> subsChoice = new ArrayList<FOXTreeNode>();
        XMLFileNode found = null;
        for (FOXTreeNode child : children) {
            if (child.isSubstitution() || child.isChoice()) {
                subsChoice.add(child);
            } else {
                String childXpath = "";
                if (child instanceof Element) {
                    childXpath = XmlMapUtil.getXPath(pXpath, child.getLabel(), NodeType.ELEMENT);
                } else if (child instanceof Attribute) {
                    childXpath = XmlMapUtil.getXPath(pXpath, child.getLabel(), NodeType.ATTRIBUT);
                } else if (child instanceof NameSpaceNode) {
                    childXpath = XmlMapUtil.getXPath(pXpath, child.getLabel(), NodeType.NAME_SPACE);
                }

                found = findXmlFileNode(childXpath, root);
                if (found == null) {
                    found = findXmlFileNode(childXpath, group);
                }
                if (found == null) {
                    found = findXmlFileNode(childXpath, loop);
                }
                if (found != null) {
                    break;
                }
            }
        }

        if (found != null) {
            return true;
        }

        if (!subsChoice.isEmpty()) {
            for (FOXTreeNode foxNode : subsChoice) {
                boolean isChildMapped = isMappedChoiceSubsOutput(foxNode, pXpath);
                if (isChildMapped) {
                    return true;
                }
            }
        }

        return found != null;
    }

    private XMLFileNode findXmlFileNode(String subXpath, List<XMLFileNode> list) {
        String pXpath = "";
        for (XMLFileNode fileNode : list) {
            if (fileNode.getAttribute().equals("attri")) {
                String tempXpath = pXpath + XmlMapUtil.XPATH_SEPARATOR + XmlMapUtil.XPATH_ATTRIBUTE + fileNode.getXMLPath();
                if (subXpath != null && subXpath.endsWith(tempXpath)) {
                    return fileNode;
                }
            } else if (fileNode.getAttribute().equals("ns")) {
                String tempXpath = pXpath + XmlMapUtil.XPATH_SEPARATOR + XmlMapUtil.XPATH_NAMESPACE + fileNode.getXMLPath();
                if (subXpath != null && subXpath.endsWith(tempXpath)) {
                    return fileNode;
                }
            } else {
                pXpath = fileNode.getXMLPath();
                if (subXpath != null && subXpath.endsWith(pXpath)) {
                    return fileNode;
                }
            }
            if (fileNode.getXMLPath() != null && fileNode.getXMLPath().equals(subXpath)) {
                return fileNode;
            }
        }
        return null;
    }

    private void fillGroup(TreeNode loopElement, List<TreeNode> groupElements) {
        final EObject parent = loopElement.eContainer();
        if (parent instanceof TreeNode) {
            if (groupElements.contains(parent)) {
                ((TreeNode) parent).setGroup(true);
            }
            if (parent.eContainer() != null && !(parent.eContainer().eContainer() instanceof AbstractInOutTree)) {
                fillGroup((TreeNode) parent, groupElements);
            }
        }

    }

    @Override
    protected boolean calculateEnabled() {
        if (getSelectedObjects().isEmpty()) {
            return false;
        } else {
            // get the last selection to run the action
            Object s = getSelectedObjects().get(0);
            if (s instanceof List && !((List) s).isEmpty()) {
                List selectedarts = (List) s;
                Object object = selectedarts.get(selectedarts.size() - 1);
                if (object instanceof TreeNodeEditPart) {
                    TreeNodeEditPart parentPart = (TreeNodeEditPart) object;
                    schemaNode = (TreeNode) parentPart.getModel();
                    if (schemaNode.eContainer() instanceof AbstractInOutTree && XmlMapUtil.DOCUMENT.equals(schemaNode.getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setMapperManager(MapperManager mapperManager) {
        this.mapperManager = mapperManager;
    }

    public void update(Object selection) {
        setSelection(new StructuredSelection(selection));
    }

    private void showError() {
        MessageDialog.openError(null, "Error", "Import from repository fail, please check your repository connection!");
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    private TreeNode createModel() {
        if (input) {
            return XmlmapFactory.eINSTANCE.createTreeNode();
        } else {
            return XmlmapFactory.eINSTANCE.createOutputTreeNode();
        }
    }

}
