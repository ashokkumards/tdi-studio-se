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
package org.talend.designer.core.ui.editor.properties.connections;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.editor.MetadataTableEditor;
import org.talend.core.model.process.EComponentCategory;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.Element;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.ui.metadata.dialog.CustomTableManager;
import org.talend.core.ui.metadata.dialog.MetadataDialog;
import org.talend.core.ui.metadata.editor.MetadataTableEditorView;
import org.talend.designer.core.ui.editor.connections.Connection;
import org.talend.designer.core.ui.editor.properties.controllers.AbstractElementPropertySectionController;
import org.talend.designer.core.ui.views.properties.DynamicComposite;

/**
 * DOC yzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class MainConnectionComposite extends DynamicComposite {

    private MetadataTableEditorView metadataTableEditorView;

    private MetadataTableEditor metadataTableEditor;

    public MainConnectionComposite(Composite parentComposite, int styles, EComponentCategory section, Element element) {
        super(parentComposite, styles, section, element);
    }

    @Override
    public void addComponents(boolean forceRedraw) {
        if (conSchema()) {
            disposeChildren();
            curRowSize = 0;

            List<? extends IElementParameter> listParam = ((Connection) elem).getSource().getElementParameters();

            generator.initController(this);
            for (IElementParameter cur : listParam) {

                if (cur.getCategory() == this.section) {

                    if ((cur.getField() == EParameterFieldType.SCHEMA_TYPE)
                            && (cur.getContext().equals(((Connection) elem).getConnectorName()))) {
                        AbstractElementPropertySectionController contorller = generator.getController(
                                EParameterFieldType.SCHEMA_TYPE, this);
                        contorller.createControl(composite, cur, 0, 1, 0, null);
                    }
                }
            }
            if (((Connection) elem).getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                FormData data = new FormData();
                data.left = new FormAttachment(0, ITabbedPropertyConstants.HSPACE);
                data.right = new FormAttachment(100, -ITabbedPropertyConstants.HSPACE);
                data.top = new FormAttachment(0, curRowSize + ITabbedPropertyConstants.VSPACE);
                data.width = 300; // to correct bug of table growing indefinitly

                IMetadataTable outputMetaTable = ((Connection) elem).getMetadataTable();
                if (outputMetaTable != null) {
                    // Composite compositeEditorView = new Composite(composite, SWT.BORDER);
                    // compositeEditorView.setLayoutData(data);

                    metadataTableEditor = new MetadataTableEditor(outputMetaTable, "Schema from "
                            + ((Connection) elem).getSource().getElementName() + " output ");
                    metadataTableEditorView = new MetadataTableEditorView(composite, SWT.NONE, metadataTableEditor, true, false,
                            true, false);
                    MetadataDialog.initializeMetadataTableView(metadataTableEditorView, ((Connection) elem).getSource(),
                            outputMetaTable);
                    metadataTableEditorView.initGraphicComponents();
                    metadataTableEditorView.getExtendedTableViewer().setCommandStack(getCommandStack());
                    CustomTableManager.addCustomManagementToTable(metadataTableEditorView, true);
                    Composite compositeEditorView = metadataTableEditorView.getMainComposite();
                    compositeEditorView.setLayoutData(data);
                    // compositeEditorView.getParent().layout();

                    Table table = metadataTableEditorView.getTable();
                    int currentHeightEditor = table.getHeaderHeight() + outputMetaTable.getListColumns().size()
                            * table.getItemHeight() + table.getItemHeight() + 50;
                    curRowSize = currentHeightEditor + ITabbedPropertyConstants.VSPACE + curRowSize;
                }
            }
            super.addComponents(forceRedraw, false, curRowSize);
        } else if (conIf()) {
            super.addComponents(forceRedraw);
        } else {
            disposeChildren();
        }
    }

    private boolean conIf() {
        Connection connection = (Connection) elem;
        return connection.getLineStyle() == EConnectionType.RUN_IF;
    }

    private boolean conSchema() {
        Connection connection = (Connection) elem;
        return connection.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA);
    }
}
