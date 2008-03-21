// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.editor.properties.controllers;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.image.ImageProvider;
import org.talend.core.CorePlugin;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.QueriesConnection;
import org.talend.core.model.metadata.builder.connection.Query;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryObject;
import org.talend.designer.core.DesignerPlugin;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.EmfComponent;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.properties.controllers.creator.SelectAllTextControlCreator;
import org.talend.designer.core.ui.editor.properties.controllers.generator.IDynamicProperty;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC nrousseau class global comment. Detailled comment
 */
public abstract class AbstractRepositoryController extends AbstractElementPropertySectionController {

    protected static final String REPOSITORY_CHOICE = "REPOSITORY_CHOICE";

    private static final int STANDARD_REPOSITORY_WIDTH = 250;

    /**
     * DOC nrousseau AbstractRepositoryController constructor comment.
     * 
     * @param dp
     */
    public AbstractRepositoryController(IDynamicProperty dp) {
        super(dp);
    }

    protected abstract String getRepositoryTypeParamName();

    protected abstract String getRepositoryChoiceParamName();

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.editor.properties.controllers.AbstractElementPropertySectionController#createControl(org.eclipse.swt.widgets.Composite,
     * org.talend.core.model.process.IElementParameter, int, int, int, org.eclipse.swt.widgets.Control)
     */
    @Override
    public Control createControl(Composite subComposite, IElementParameter param, int numInRow, int nbInRow, int top,
            Control lastControl) {
        CCombo combo;

        Control lastControlUsed = lastControl;

        combo = new CCombo(subComposite, SWT.BORDER);
        IElementParameter propertyTypeParameter = param.getChildParameters().get(getRepositoryTypeParamName());
        FormData data;
        String[] originalList = propertyTypeParameter.getListItemsDisplayName();
        List<String> stringToDisplay = new ArrayList<String>();
        for (int i = 0; i < originalList.length; i++) {
            stringToDisplay.add(originalList[i]);
        }
        combo.setItems(stringToDisplay.toArray(new String[0]));
        combo.setEditable(false);
        combo.setEnabled(!propertyTypeParameter.isReadOnly());
        if (elem instanceof Node) {
            combo.setToolTipText(VARIABLE_TOOLTIP + propertyTypeParameter.getVariableName());
        }

        CLabel labelLabel = getWidgetFactory().createCLabel(subComposite, propertyTypeParameter.getDisplayName());
        data = new FormData();
        if (lastControl != null) {
            data.left = new FormAttachment(lastControl, 0);
        } else {
            data.left = new FormAttachment((((numInRow - 1) * MAX_PERCENT) / nbInRow), 0);
        }
        data.top = new FormAttachment(0, top);
        labelLabel.setLayoutData(data);
        if (numInRow != 1) {
            labelLabel.setAlignment(SWT.RIGHT);
        }
        // *********************
        data = new FormData();
        int currentLabelWidth = STANDARD_LABEL_WIDTH;
        GC gc = new GC(labelLabel);
        Point labelSize = gc.stringExtent(propertyTypeParameter.getDisplayName());
        gc.dispose();

        if ((labelSize.x + ITabbedPropertyConstants.HSPACE) > currentLabelWidth) {
            currentLabelWidth = labelSize.x + ITabbedPropertyConstants.HSPACE;
        }

        if (param.isRepositoryValueUsed()) {
            param.setReadOnly(true);
        }

        if (numInRow == 1) {
            if (lastControl != null) {
                data.left = new FormAttachment(lastControl, currentLabelWidth);
            } else {
                data.left = new FormAttachment(0, currentLabelWidth);
            }

        } else {
            data.left = new FormAttachment(labelLabel, 0, SWT.RIGHT);
        }
        data.top = new FormAttachment(0, top);
        combo.setLayoutData(data);
        combo.addSelectionListener(listenerSelection);
        combo.setData(PARAMETER_NAME, param.getName() + ":" + propertyTypeParameter.getName());
        lastControlUsed = combo;

        String propertyType = (String) propertyTypeParameter.getValue();
        if (propertyType != null && propertyType.equals(EmfComponent.REPOSITORY)) {
            lastControlUsed = addRepositoryChoice(subComposite, lastControlUsed, numInRow, nbInRow, top, param);
        }

        // **********************
        hashCurControls.put(param.getName() + ":" + propertyTypeParameter.getName(), combo);

        Point initialSize = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        dynamicProperty.setCurRowSize(initialSize.y + ITabbedPropertyConstants.VSPACE);
        return lastControlUsed;
    }

    private Control addRepositoryChoice(Composite subComposite, Control lastControl, int numInRow, int nbInRow, int top,
            IElementParameter param) {
        FormData data;

        IElementParameter repositoryParameter = param.getChildParameters().get(getRepositoryChoiceParamName());

        Text labelText;

        final DecoratedField dField = new DecoratedField(subComposite, SWT.BORDER, new SelectAllTextControlCreator());
        if (param.isRequired()) {
            FieldDecoration decoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                    FieldDecorationRegistry.DEC_REQUIRED);
            dField.addFieldDecoration(decoration, SWT.RIGHT | SWT.TOP, false);
        }
        Control cLayout = dField.getLayoutControl();
        labelText = (Text) dField.getControl();

        labelText.setData(PARAMETER_NAME, param.getName());

        cLayout.setBackground(subComposite.getBackground());
        labelText.setEditable(false);
        if (elem instanceof Node) {
            labelText.setToolTipText(VARIABLE_TOOLTIP + param.getVariableName());
        }

        // *********************
        data = new FormData();
        data.left = new FormAttachment(lastControl, 0);
        data.right = new FormAttachment(lastControl, STANDARD_REPOSITORY_WIDTH, SWT.RIGHT);
        // data.right = new FormAttachment((numInRow * MAX_PERCENT) / nbInRow, 0);
        data.top = new FormAttachment(0, top);
        cLayout.setLayoutData(data);

        Button btn;
        Point btnSize;

        btn = getWidgetFactory().createButton(subComposite, "", SWT.PUSH); //$NON-NLS-1$
        btnSize = btn.computeSize(SWT.DEFAULT, SWT.DEFAULT);

        btn.setImage(ImageProvider.getImage(CorePlugin.getImageDescriptor(DOTS_BUTTON)));

        btn.addSelectionListener(listenerSelection);
        btn.setData(NAME, REPOSITORY_CHOICE);
        btn.setData(PARAMETER_NAME, param.getName());

        data = new FormData();
        data.left = new FormAttachment(cLayout, 0);
        data.right = new FormAttachment(cLayout, STANDARD_BUTTON_WIDTH, SWT.RIGHT);
        data.top = new FormAttachment(0, top);
        data.height = STANDARD_HEIGHT - 2;
        btn.setLayoutData(data);

        // **********************

        hashCurControls.put(param.getName() + ":" + repositoryParameter.getName(), labelText);

        return btn;
    }

    SelectionListener listenerSelection = new SelectionListener() {

        public void widgetDefaultSelected(SelectionEvent e) {
            // do nothing.
        }

        public void widgetSelected(SelectionEvent e) {
            Command cmd = createCommand(e);
            if (cmd != null) {
                getCommandStack().execute(cmd);
            }
        }
    };

    private Command createCommand(SelectionEvent selectionEvent) {
        if (selectionEvent.getSource() instanceof Button) {
            return createButtonCommand((Button) selectionEvent.getSource());
        }
        if (selectionEvent.getSource() instanceof CCombo) {
            return createComboCommand((CCombo) selectionEvent.getSource());
        }
        return null;
    }

    protected abstract Command createButtonCommand(Button button);

    protected abstract Command createComboCommand(CCombo combo);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.editor.properties.controllers.AbstractElementPropertySectionController#estimateRowSize(org.eclipse.swt.widgets.Composite,
     * org.talend.core.model.process.IElementParameter)
     */
    @Override
    public int estimateRowSize(Composite subComposite, IElementParameter param) {
        CCombo combo = new CCombo(subComposite, SWT.BORDER);
        Point initialSize = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        combo.dispose();

        return initialSize.y + ITabbedPropertyConstants.VSPACE;
    }

    private ConnectionItem lastItemUsed;

    private void fastRepositoryUpdateProperty() {
        IElementParameter param = elem.getElementParameterFromField(EParameterFieldType.PROPERTY_TYPE, dynamicProperty
                .getSection());

        if (param == null) {
            return;
        }

        param = param.getChildParameters().get(EParameterName.REPOSITORY_PROPERTY_TYPE.getName());

        if (param != null && param.getValue() != null) {
            IProxyRepositoryFactory factory = DesignerPlugin.getDefault().getProxyRepositoryFactory();
            String linkedRepository = (String) param.getValue();
            // value stored is the id, so we can get this item directly
            Item item;
            try {
                IRepositoryObject object = factory.getLastVersion(linkedRepository);
                if (object == null) {
                    return;
                }
                item = object.getProperty().getItem();
                Assert.isTrue(item instanceof ConnectionItem);
                lastItemUsed = (ConnectionItem) item;
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            }
            String displayName = dynamicProperty.getRepositoryAliasName(lastItemUsed) + ":" //$NON-NLS-1$
                    + lastItemUsed.getProperty().getLabel();

            param.setListItemsDisplayName(new String[] { displayName });
            param.setListItemsValue(new String[] { (String) param.getValue() });
        }

    }

    private void fastRepositoryUpdateSchema(IElementParameter schemaParam) {
        IElementParameter param = schemaParam.getChildParameters().get(EParameterName.REPOSITORY_SCHEMA_TYPE.getName());
        if (param != null && param.getValue() != null) {
            String[] names = ((String) param.getValue()).split(" - ");
            if (names.length != 2) {
                return;
            }
            String linkedRepository = names[0];
            if (lastItemUsed != null) {
                if (!linkedRepository.equals(lastItemUsed.getProperty().getId())) {
                    lastItemUsed = null;
                }
            }
            if (lastItemUsed == null) {
                IProxyRepositoryFactory factory = DesignerPlugin.getDefault().getProxyRepositoryFactory();
                Item item;
                try {
                    IRepositoryObject object = factory.getLastVersion(linkedRepository);
                    if (object == null) {
                        return;
                    }
                    item = object.getProperty().getItem();
                    Assert.isTrue(item instanceof ConnectionItem);
                    lastItemUsed = (ConnectionItem) item;
                } catch (PersistenceException e) {
                    ExceptionHandler.process(e);
                }
            }
            String tableName = names[1];
            // EList<MetadataTable> tableList = lastItemUsed.getConnection().getTables();
            // for (MetadataTable table )

            String displayName = dynamicProperty.getRepositoryAliasName(lastItemUsed) + ":" //$NON-NLS-1$
                    + lastItemUsed.getProperty().getLabel() + " - " + tableName; //$NON-NLS-1$

            param.setListItemsDisplayName(new String[] { displayName });
            param.setListItemsValue(new String[] { (String) param.getValue() });
        }
    }

    private void fastRepositoryUpdateQuery() {
        IElementParameter param = elem.getElementParameterFromField(EParameterFieldType.QUERYSTORE_TYPE, dynamicProperty
                .getSection());
        if (param == null) {
            return;
        }
        param = param.getChildParameters().get(EParameterName.REPOSITORY_QUERYSTORE_TYPE.getName());
        if (param != null && param.getValue() != null) {
            String[] names = ((String) param.getValue()).split(" - ");
            if (names.length != 2) {
                return;
            }
            String linkedRepository = names[0];
            String queryName = names[1];
            if (lastItemUsed != null) {
                if (!linkedRepository.equals(lastItemUsed.getProperty().getId())) {
                    lastItemUsed = null;
                }
            }
            if (lastItemUsed == null) {
                IProxyRepositoryFactory factory = DesignerPlugin.getDefault().getProxyRepositoryFactory();
                Item item;
                try {
                    IRepositoryObject object = factory.getLastVersion(linkedRepository);
                    if (object == null) {
                        return;
                    }
                    item = object.getProperty().getItem();
                    lastItemUsed = (ConnectionItem) item;
                } catch (PersistenceException e) {
                    ExceptionHandler.process(e);
                }
            }
            Assert.isTrue(lastItemUsed instanceof DatabaseConnectionItem);
            QueriesConnection queriesConnection = ((DatabaseConnection) lastItemUsed.getConnection()).getQueries();
            EList<Query> queries = queriesConnection.getQuery();

            String repositoryAliasName = dynamicProperty.getRepositoryAliasName(lastItemUsed);
            for (Query currentQuery : queries) {
                if (currentQuery.getLabel().equals(queryName)) {
                    String displayName = repositoryAliasName + ":" //$NON-NLS-1$
                            + lastItemUsed.getProperty().getLabel() + " - " + currentQuery.getLabel(); //$NON-NLS-1$

                    param.setListItemsDisplayName(new String[] { displayName });
                    param.setListItemsValue(new String[] { (String) param.getValue() });

                    dynamicProperty.getRepositoryQueryStoreMap().clear();
                    dynamicProperty.getRepositoryQueryStoreMap().put((String) param.getValue(), currentQuery);
                }
            }
        }
    }

    private void fastInitializeRepositoryNames() {
        lastItemUsed = null;

        fastRepositoryUpdateProperty();
        for (IElementParameter curParam : elem.getElementParameters()) {
            if (curParam.getField().equals(EParameterFieldType.SCHEMA_TYPE)) {
                String value = (String) curParam.getChildParameters().get(EParameterName.SCHEMA_TYPE.getName()).getValue();
                if (curParam.isShow(elem.getElementParameters())) {
                    if (value.equals(EmfComponent.REPOSITORY)) {
                        // load repository connections only if needed
                        fastRepositoryUpdateSchema(curParam);
                    }
                }
            }
        }
        fastRepositoryUpdateQuery();
    }

    protected String getDisplayNameFromValue(IElementParameter param, String value) {
        // to load informations from repository only if needed.

        if (param.getListItemsDisplayName().length == 0) {

            fastInitializeRepositoryNames();
            // if even after the initialize there is nothing, just return an empty string
            if (param.getListItemsDisplayName().length == 0) {
                return "";
            }
        }

        int index = param.getIndexOfItemFromList(value);
        return param.getListItemsDisplayName()[index];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.editor.properties.controllers.AbstractElementPropertySectionController#refresh(org.talend.core.model.process.IElementParameter,
     * boolean)
     */
    @Override
    public void refresh(IElementParameter param, boolean check) {
        IElementParameter propertyTypeParameter = param.getChildParameters().get(getRepositoryTypeParamName());
        CCombo combo = (CCombo) hashCurControls.get(param.getName() + ":" + propertyTypeParameter.getName());

        if (combo == null || combo.isDisposed()) {
            return;
        }
        Object value = propertyTypeParameter.getValue();

        if (value instanceof String) {
            String strValue = ""; //$NON-NLS-1$
            int nbInList = 0, nbMax = propertyTypeParameter.getListItemsValue().length;
            String name = (String) value;
            while (strValue.equals(new String("")) && nbInList < nbMax) { //$NON-NLS-1$
                if (name.equals(propertyTypeParameter.getListItemsValue()[nbInList])) {
                    strValue = propertyTypeParameter.getListItemsDisplayName()[nbInList];
                }
                nbInList++;
            }
            String[] paramItems = propertyTypeParameter.getListItemsDisplayName();
            String[] comboItems = combo.getItems();
            if (!paramItems.equals(comboItems)) {
                combo.setItems(paramItems);
            }
            combo.setText(strValue);
        }

        IElementParameter repositoryChoiceParameter = param.getChildParameters().get(getRepositoryChoiceParamName());
        Text text = (Text) hashCurControls.get(param.getName() + ":" + repositoryChoiceParameter.getName());

        if (text == null || text.isDisposed()) {
            return;
        }
        value = repositoryChoiceParameter.getValue();

        if (value instanceof String) {
            String toDisplay = getDisplayNameFromValue(repositoryChoiceParameter, (String) value);
            if (toDisplay == null || "".equals(value)) {
                text.setText("");
            } else {
                text.setText(toDisplay);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub

    }

}
