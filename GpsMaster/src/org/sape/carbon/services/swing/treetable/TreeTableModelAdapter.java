package org.sape.carbon.services.swing.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/*
 * The contents of this file are subject to the Sapient Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://carbon.sf.net/License.html.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is The Carbon Component Framework.
 *
 * The Initial Developer of the Original Code is Sapient Corporation
 *
 * Copyright (C) 2003 Sapient Corporation. All Rights Reserved.
 */


/*
 * @(#)TreeTableModelAdapter.java    1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


/**
 * This is a wrapper class takes a TreeTableModel and implements
 * the table model interface. The implementation is trivial, with
 * all of the event dispatching support provided by the superclass:
 * the AbstractTableModel.
 *
 * @version 1.2 10/27/98
 *
 * @author Philip Milne
 * @author Scott Violet
 */
 class TreeTableModelAdapter extends AbstractTableModel
{
    JTree tree;
    TreeTableModel treeTableModel;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;

    tree.addTreeExpansionListener(new TreeExpansionListener() {
        // Don't use fireTableRowsInserted() here; the selection model
        // would get updated twice.
        public void treeExpanded(TreeExpansionEvent event) {
          fireTableDataChanged();
        }
            public void treeCollapsed(TreeExpansionEvent event) {
          fireTableDataChanged();
        }
    });

    // Install a TreeModelListener that can update the table when
    // tree changes. We use delayedFireTableDataChanged as we can
    // not be guaranteed the tree will have finished processing
    // the event before us.
    treeTableModel.addTreeModelListener(new TreeModelListener() {
        public void treeNodesChanged(TreeModelEvent e) {
        delayedFireTableDataChanged();
        }

        public void treeNodesInserted(TreeModelEvent e) {
        delayedFireTableDataChanged();
        }

        public void treeNodesRemoved(TreeModelEvent e) {
        delayedFireTableDataChanged();
        }

        public void treeStructureChanged(TreeModelEvent e) {
        delayedFireTableDataChanged();
        }
    });
    }

    // Wrappers, implementing TableModel interface.

    public int getColumnCount() {
    return treeTableModel.getColumnCount();
    }

    public String getColumnName(int column) {
    return treeTableModel.getColumnName(column);
    }

    public Class getColumnClass(int column) {
    return treeTableModel.getColumnClass(column);
    }

    public int getRowCount() {
    return tree.getRowCount();
    }

    protected Object nodeForRow(int row) {
    TreePath treePath = tree.getPathForRow(row);
    return treePath.getLastPathComponent();
    }

    public Object getValueAt(int row, int column) {
    return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    public boolean isCellEditable(int row, int column) {
         return treeTableModel.isCellEditable(nodeForRow(row), column);
    }

    public void setValueAt(Object value, int row, int column) {
    treeTableModel.setValueAt(value, nodeForRow(row), column);
    }

    /**
     * Invokes fireTableDataChanged after all the pending events have been
     * processed. SwingUtilities.invokeLater is used to handle this.
     */
    protected void delayedFireTableDataChanged() {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
        fireTableDataChanged();
        }
    });
    }
}