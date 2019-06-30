package org.gpsmaster.tree;

import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * 
 * @author rfu
 *
 */
public class GPXTreeModel implements TreeModel {

	List<GPXFile> gpxFiles = null;
	
	/**
	 * 
	 * @param gpxFiles
	 */
	public GPXTreeModel(List<GPXFile> gpxFiles) {
		this.gpxFiles = gpxFiles;
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent.equals(gpxFiles)) {
			return gpxFiles.get(index);
		}
		if (parent instanceof GPXFile) {
			// TBI
		}
		if (parent instanceof Track) {
			return ((Track) parent).getTracksegs().get(index);
		}
		if (parent instanceof WaypointGroup) {
			return ((WaypointGroup) parent).getWaypoints().get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		int count = 0;
		if (parent.equals(gpxFiles)) {
			return gpxFiles.size();
		}		
		if (parent instanceof GPXFile) {
			GPXFile gpx = (GPXFile) parent;
			if (gpx.getWaypointGroup().getNumPts() > 0) {
				count++;
			}
			count += gpx.getTracks().size() + gpx.getRoutes().size();
			return count;
		}
		// if (parent instanceof WaypointGroup)
		if (parent instanceof Track) {
			return ((Track) parent).getTracksegs().size();			
		}
		if (parent instanceof WaypointGroup) {
			WaypointGroup group = (WaypointGroup) parent;
			if (group.isWaypoints()) {
				return group.getNumPts();
			}
			return 0;
		}
		// route = 0
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return 0;
	}

	/**
	 * 
	 * more or less a dummy, since the root node is hidden in the displayed tree
	 */
	@Override
	public Object getRoot() {
		
		return gpxFiles;
	}

	@Override
	public boolean isLeaf(Object node) {
		if (node instanceof Waypoint) return true;
		if (node instanceof GPXFile) return false;
		if (node instanceof Track) return false;
		if (node instanceof Route) return true;
		if (node instanceof WaypointGroup) {
			WaypointGroup group = (WaypointGroup) node;
			if (group.isWaypoints() && group.getWaypoints().size() > 0) {
				return false;				
			}
		}
		return true;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
		
	}

}
