package org.gpsmaster.timeshift;

import java.util.Date;
import java.util.Hashtable;

import org.gpsmaster.Core;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * Remove all timestamps
 * (aka set {@link Waypoint} time to null
 *  
 * @author rfu
 *
 */
public class ClearTimestamps extends TimeshiftAlgorithm {

	private Hashtable<Waypoint, Date> undoLog = new Hashtable<Waypoint, Date>();
	
	/**
	 * 
	 */
	public ClearTimestamps() {
		super();
		name = "Clear";
		description = "remove all timestamps";
	}

	@Override
	public boolean isApplicable() {
		
		return (waypointGroups.size() > 0);
	}

	@Override
	public void apply() {

		for (WaypointGroup group : waypointGroups) {
			// save timestamps to undo log
			for(Waypoint wpt : group.getWaypoints()) {
				Date timestamp = wpt.getTime();
				if (timestamp != null) { undoLog.put(wpt, timestamp); }
			}
			Core.clearTimestamps(group);
		}			
	}

	@Override
	public String getUndoDescription() {
		return description;
	}

	@Override
	public void undo() {
		for (WaypointGroup group : waypointGroups) {
			// save timestamps to undo log
			for(Waypoint wpt : group.getWaypoints()) {
				if(undoLog.containsKey(wpt)) {
					wpt.setTime(undoLog.get(wpt));
				}
			}		
		}
	}
}
