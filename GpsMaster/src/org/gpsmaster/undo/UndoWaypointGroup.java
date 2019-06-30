package org.gpsmaster.undo;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * Generic Undo operation:
 * Restore a {@link WaypointGroup} by cloning and copying back. 
 * 
 * Only the list of Waypoints is restored, 
 * the {@link WaypointGroup} properties are untouched.
 * 
 * @author rfu
 *
 */
public class UndoWaypointGroup implements IUndoable {

	protected String desc = "Track Segment";
	
	private WaypointGroup newGroup = null;
	private WaypointGroup oldGroup = null;
	
	/**
	 * Constructor
	 * @param description Short description of this undo operation
	 */
	public UndoWaypointGroup(String description) {
		desc = description;
	}
	
	@Override
	public String getUndoDescription() {
		
		return desc;
	}

	/**
	 * @return the newGroup
	 */
	public WaypointGroup getNewGroup() {
		return newGroup;
	}

	/**
	 * @param newGroup the newGroup to set
	 */
	public void setNewGroup(WaypointGroup newGroup) {
		this.newGroup = newGroup;
	}

	/**
	 * @return the oldGroup
	 */
	public WaypointGroup getOldGroup() {
		return oldGroup;
	}

	/**
	 * @param oldGroup the oldGroup to set
	 */
	public void setOldGroup(WaypointGroup oldGroup) {
		this.oldGroup = new WaypointGroup(oldGroup);
	}

	/**
	 * Restore the old list of waypoints 
	 */
	@Override
	public void undo() {
		
		newGroup.getWaypoints().clear();
		for (Waypoint wpt : oldGroup.getWaypoints()) {
			newGroup.addWaypoint(wpt);
		}
		newGroup.updateAllProperties();
	}

}
