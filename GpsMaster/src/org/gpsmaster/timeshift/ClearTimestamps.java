package org.gpsmaster.timeshift;

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
			Core.clearTimestamps(group);
		}			
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}


}
