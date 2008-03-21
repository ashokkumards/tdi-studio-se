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
package org.talend.designer.core.ui.editor.properties.controllers.generator;

import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.eclipse.swt.widgets.Composite;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.builder.connection.Query;
import org.talend.core.model.process.EComponentCategory;
import org.talend.core.model.process.Element;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.designer.core.ui.AbstractMultiPageTalendEditor;

/**
 * DOC nrousseau class global comment. Detailled comment <br/>
 * 
 */
public interface IDynamicProperty {

    public BidiMap getHashCurControls();

    public Element getElement();

    public AbstractMultiPageTalendEditor getPart();

    public EComponentCategory getSection();

    public Composite getComposite();

    public Map<String, IMetadataTable> getRepositoryTableMap();

    public void setCurRowSize(int i);

    public int getCurRowSize();

    public Map<String, ConnectionItem> getRepositoryConnectionItemMap();

    public Map<String, Query> getRepositoryQueryStoreMap();

    public Map<String, String> getTableIdAndDbTypeMap();

    public Map<String, String> getTableIdAndDbSchemaMap();

    public void refresh();

    public String getRepositoryAliasName(ConnectionItem connectionItem);

    public void updateContextList(IElementParameter param);
}
