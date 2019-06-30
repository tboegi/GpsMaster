package org.gpsmaster.undo;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * Undo addition of {@link Waypoint} to {@link WaypointGroup}
 * 
 * @author rfu
 *
 */
public class UndoAddWaypoint implements IUndoable {

	protected Waypoint wpt = null;
	protected WaypointGroup group = null;
	
	/**
	 * 
	 * @param wpt waypoint to be removed
	 * @param waypointGroup {@link WaypointGroup] containing {@link Waypoint} to be removed
	 */
	public UndoAddWaypoint(Waypoint wpt, WaypointGroup waypointGroup) {
		this.wpt = wpt;
		this.group = waypointGroup;
	}
	
	@Override
	public String getUndoDescription() {
		String desc = String.format("Add Point (%.6f, %.6f)", wpt.getLat(), wpt.getLon());
		if (group.getName().isEmpty() == false) {
			desc.concat(" to " + group.getName());
		}
		return desc;
	}

	@Override
	public void undo() {
		if (wpt != null && group != null) {
			if (group.getWaypoints().contains(wpt)) {
				group.getWaypoints().remove(wpt);
			}
		}
	}

}
