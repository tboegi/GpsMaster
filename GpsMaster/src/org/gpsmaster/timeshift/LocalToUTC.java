package org.gpsmaster.timeshift;

import java.util.Date;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.joda.time.DateTimeZone;
import org.gpsmaster.Core;

/**
 * 
 * This algorithm assumes that the GPX timestamps are in local time
 * instead of UTC and converts them to UTC.
 * 
 * TODO figure out proper timezone based on GPS coordinates
 * TODO pass local time zone as parameter
 * 
 * for now,  
 * 
 * @author rfu
 *
 */
public class LocalToUTC extends TimeshiftAlgorithm {

	/**
	 * Constructor
	 */
	public LocalToUTC() {
		super();
		name = "Local to UTC";
		description = "assumed that the timestamps are in local time, this algorithm"
				+ " converts the timestamps to UTC, as required by the GPX definition.";
		
		// no params required
		
	}
	
	@Override
	public boolean isApplicable() {
		
		return (waypointGroups.size() > 0);
	}


	/**
	 * 
	 */
	@Override
	public void apply() {
		
		DateTimeZone localZone = DateTimeZone.getDefault();		
		for (WaypointGroup group : waypointGroups) {			
			for (Waypoint wpt : group.getWaypoints()) {
				Date localtime = wpt.getTime();
				if (localtime != null) {
					wpt.setTime(new Date(localZone.convertLocalToUTC(localtime.getTime(), false)));
				}
			}
		}		
	}

	@Override
	public String getUndoDescription() {
		
		return "UTC to Local";
	}

	/**
	 * Undo - just convert back from utc to local. 
	 */
	@Override
	public void undo() {
		DateTimeZone localZone = DateTimeZone.getDefault();		
		for (WaypointGroup group : waypointGroups) {			
			for (Waypoint wpt : group.getWaypoints()) {
				Date localtime = wpt.getTime();
				if (localtime != null) {
					wpt.setTime(new Date(localZone.convertUTCToLocal(localtime.getTime())));
				}
			}
		}		

		
	}

	

}
