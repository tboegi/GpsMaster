package org.gpsmaster.gpxpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 * Class representing the JTree root node containing all currently loaded {@link GPXFile}s
 * 
 * @author rfu
 * 
 *
 */
public class GPXRoot extends GPXObject implements TreeNode {

	private List<GPXFile> gpxFiles = Collections.synchronizedList(new ArrayList<GPXFile>());
	
	/**
	 * Constructor
	 */
	public GPXRoot() {

	}
	
	public void addGpxFile(GPXFile gpx) {
		gpx.setParent(null); // let upward recursions stop at GPXFile level
		gpxFiles.add(gpx);		
	}
	
	/**
	 * 
	 * @param gpx
	 */
	public void removeGpxFile(GPXFile gpx) {
		gpx.setParent(null);
		gpxFiles.remove(gpx);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<GPXFile> getGpxFiles() {
		return gpxFiles;
	}
	
	/**
	 * TreeNode methods
	 */
	
	public Enumeration<GPXFile> children() {
		return Collections.enumeration(gpxFiles);
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int pos) {
		return gpxFiles.get(pos);
	}

	public int getChildCount() {
		return gpxFiles.size();
	}

	public int getIndex(TreeNode gpxFile) {
		return gpxFiles.indexOf(gpxFile);
	}

	public GPXObject getParent() {
		return null;
	}

	public boolean isLeaf() {
		return false;
	}

	/**
	 * GPXObject methods
	 * not intended to be displayed since this root node is hidden in the tree
	 */
	
	public void setDesc(String desc) {
		
	}

	@Override
	public void updateAllProperties() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}
}
