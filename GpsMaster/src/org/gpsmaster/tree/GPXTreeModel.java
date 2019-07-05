package org.gpsmaster.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


/**
 *
 * @author rfu
 *
 */
public class GPXTreeModel extends DefaultTreeModel {


	/**
	 *
	 */
	private static final long serialVersionUID = -2081646812594605999L;

	/**
	 *
	 * @param gpxFiles
	 */
	public GPXTreeModel(TreeNode rootNode) {
		super(rootNode);

	}

	public void refresh() {
		reload();

	}

}
