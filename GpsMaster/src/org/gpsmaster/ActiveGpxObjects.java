package org.gpsmaster;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * Class providing easy access to the active {@link GPXObject}
 * and its sub-objects. Also provides some some dynamics via
 * Property Change events.
 * 
 * Main purpose of this class is to eventually get rid of the
 * updateActive[...] code mess in {@link GpsMaster}
 * 
 * @author rfu
 *
 */
public class ActiveGpxObjects {

	private int idx = -1;
	private Core core = null;
	private Waypoint activeWaypoint = null;
	private WaypointGroup activeGroup = null;
	private GPXObject gpxObject = null;
	private List<WaypointGroup> allGroups = new ArrayList<WaypointGroup>(); 
	
	private PropertyChangeSupport pcs = null;
	private PropertyChangeListener propertyListener = null;

	public final String PCE_ACTIVEWPT = "activeWpt"; // set active waypoint
	
	/**
	 * Default constructor
	 */
	public ActiveGpxObjects() {
		core = new Core();
		
		pcs = new PropertyChangeSupport(this);
		propertyListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				handleEvent(evt);				
			}
		};
	}
	
	/**
	 * Get the active {@link GPXObject}
	 * @return {@link GPXObject} or null if none set
	 */
	public GPXObject getGpxObject() {
		return gpxObject;
	}
	
	/**
	 * Set the active {@link GPXObject}
	 * @param gpxObject active {@link GPXObject} 
	 */
	public void setGpxObject(GPXObject gpxObject) {
		setActiveGpx(gpxObject);
		pcs.firePropertyChange("activeGpx", null, gpxObject);
	}
	
	/**
	 * Get the Root object of the active {@link GPXObject}
	 * @return Parent/Root {@link GPXFile} or null of none set
	 */
	public GPXFile getGpxFile() {
		GPXFile gpx = null;
		if (gpxObject != null) {
			if (gpxObject instanceof GPXFile) {
				gpx = (GPXFile) gpxObject;
			} else {
				// find in tree
				// set tree as parameter
			}
		}
		return gpx;
	}
	
	/**
	 * 
	 * @return
	 */
	public WaypointGroup getActiveGroup() {
		return activeGroup;
	}
	
	/**
	 * 
	 * @param grp
	 */
	public void setActiveGroup(WaypointGroup grp) {
		activeGroup = grp;
		idx = -1; // TODO
	}
	
	/**
	 * Get all active {@link WaypointGroup}s, regardless of type 
	 * @return active {@link WaypointGroup}s or empty {@link List}
	 */
	public List<WaypointGroup> getWaypointGroups() {
		return allGroups;
	}
	
	public Waypoint getActiveWaypoint() {
		return activeWaypoint;
	}
	
	/**
	 * Set the active {@link Waypoint} and send a change notification
	 * @param wpt
	 */
	public void setActiveWaypoint(Waypoint wpt) {
		activeWaypoint = wpt;
		setActiveWpt(activeWaypoint);
		pcs.firePropertyChange("activeWpt", null, activeWaypoint);
	}
	
	/**
	 * Get the total number of waypoints in all active Waypointgroups
	 * @return Number of waypoints, 0 if none.
	 */
	public int getNumWaypoints() {
		int count = 0;
		for (WaypointGroup group : allGroups) {
			count += group.getNumPts();
		}
		return count;
	}
	
	/**
	 * Get the index in the active {@link WaypointGroup}
	 * @param wpt {@link Waypoint} to search for
	 * @return the index of the first occurrence of the specified {@link Waypoint} 
	 * in the active {@link WaypointGroup}, or -1 if this list does not contain the {@link Waypoint}
	 *  
	 */
	public int getIndexOf(Waypoint wpt) {
		int idx = -1;
		if ((activeGroup != null) && (wpt != null)) {
			idx = activeGroup.getWaypoints().indexOf(wpt);
		}
		return idx;
	}
	
	/**
	 * Get the position of the given {@link Waypoint} within all active {@link WaypointGroup}s
	 * @param wpt {@link Waypoint} to search for
	 * @return
	 */
	public int getTotalIndexOf(Waypoint wpt) {
		int totalIdx = -1;
		if (wpt != null) {
			for (WaypointGroup group : allGroups) {
				int idx = group.getWaypoints().indexOf(wpt);
				if (idx > -1) {
					totalIdx += idx;
					return totalIdx;
				} else {
					totalIdx += group.getNumPts();
				}
			}
		}
		return -1;
	}
	/**
	 * 
	 * @return
	 */
	public PropertyChangeListener getPropertyChangeListener() {
		return propertyListener;		
	}

	/**
	 * 
	 * @param listener
	 */
	public void AddPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void RemovePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	/**
	 * 
	 */
	public void clear() {
		idx = -1;
		gpxObject = null;
		activeWaypoint = null;
		activeGroup = null;
		allGroups.clear();
	}
	
	/**
	 * Handle event received externally
	 * @param evt
	 */
	private void handleEvent(PropertyChangeEvent evt) {
	
		String propertyName = evt.getPropertyName();
		if (propertyName.equals("activeGpxObject")) {
			setActiveGpx((GPXObject) evt.getNewValue());
		} else if (propertyName.equals("activeWpt")) {
			setActiveWpt((Waypoint) evt.getNewValue());
		}		
	}

	/**
	 * 
	 * @param newValue
	 */
	private void setActiveWpt(Waypoint wpt) {
		activeWaypoint = wpt;
		idx = -1;
		if (wpt == null) {			
			return;
		}
		for (WaypointGroup group : allGroups) {
			if (group.getWaypoints().contains(activeWaypoint)) {
				idx = group.getWaypoints().indexOf(activeWaypoint);
				activeGroup = group;  // sinnvoll?
			}
		}
		if (idx == -1) {
			activeGroup = null;
		}
	}


	private void setActiveGpx(GPXObject gpxObject) {
		if (gpxObject == null) {
			clear();
		} else {
			this.gpxObject = gpxObject;
			allGroups = core.getSegments(gpxObject, core.SEG_ALL);
		}
	}


}
