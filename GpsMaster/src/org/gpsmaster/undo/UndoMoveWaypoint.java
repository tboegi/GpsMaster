package org.gpsmaster.undo;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 *
 * @author wbeisser
 *
 */
public class UndoMoveWaypoint implements IUndoable {

	protected Waypoint wpt = null;
	protected WaypointGroup group = null;
	protected double lat;
	protected double lon;

	/**
	 *
	 * @param wpt waypoint to be re-added
	 * @param waypointGroup {@link WaypointGroup] containing {@link Waypoint} to be re-added
	 */
	public UndoMoveWaypoint(Waypoint wpt, WaypointGroup waypointGroup) {
		this.wpt = wpt;
		this.group = waypointGroup;
		this.lat = wpt.getLat();
		this.lon = wpt.getLon();
	}

	@Override
	public String getUndoDescription() {
		String desc = String.format("Move Point (%.6f, %.6f)", lat, lon);
		return desc;
	}

	@Override
	public void undo() {
		if (this.wpt != null) {
			this.wpt.setLat(this.lat);
			this.wpt.setLon(this.lon);
		}
	}

}
