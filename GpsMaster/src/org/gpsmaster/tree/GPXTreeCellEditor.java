package org.gpsmaster.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 
 * @author rfu
 *
 */
public class GPXTreeCellEditor extends DefaultTreeCellEditor  {

	/**
	 * Constructor
	 * @param tree
	 * @param treeCellRenderer
	 */
	public GPXTreeCellEditor(JTree tree, DefaultTreeCellRenderer treeCellRenderer) {
		super(tree, treeCellRenderer);

	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree,
            Object value,
            boolean isSelected,
            boolean expanded,
            boolean leaf,
            int row) {
		
		return null;
		
	}
}
