package org.gpsmaster.undo;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * 
 * @author rfu
 *
 */
public class UndoRemoveWaypoint implements IUndoable {

	protected Waypoint wpt = null;
	protected WaypointGroup group = null;
	protected int idx = -1;
	
	/**
	 * 
	 * @param wpt waypoint to be re-added
	 * @param waypointGroup {@link WaypointGroup] containing {@link Waypoint} to be re-added
	 */
	public UndoRemoveWaypoint(Waypoint wpt, WaypointGroup waypointGroup) {
		this.wpt = wpt;
		this.group = waypointGroup;
		idx = waypointGroup.getWaypoints().indexOf(wpt);
	}
	
	@Override
	public String getUndoDescription() {
		String desc = String.format("Remove Point (%.6f, %.6f)", wpt.getLat(), wpt.getLon());
		return desc;
	}

	@Override
	public void undo() {
		if (wpt != null && group != null) {
			group.getWaypoints().add(idx, wpt);
		}
	}

}
