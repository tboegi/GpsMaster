package org.gpsmaster.timeshift;

import java.util.Date;
import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * Reverse the direction of a track segment
 * 
 * @author rfu
 *
 * TODO support more than one waypoint group
 * 
 */
public class Reverse extends TimeshiftAlgorithm {

	/**
	 * Constructor
	 */
	public Reverse() {
		super();
		name = "Reverse";
		description = "Reverse direction of track(s)";
	}
	
	@Override
	public void apply() {
		
		reverse(waypointGroups.get(0).getWaypoints()); // tmp
	}

	@Override
	public String getUndoDescription() {		
		return "Timeshift - Reverse";
	}

	/**
	 * Undo by just reversing the track again.
	 * attention: does not check if segment was actually reversed before 
	 */
	@Override
	public void undo() {		
		
		reverse(waypointGroups.get(0).getWaypoints());
	}

	/**
	 * 
	 * @param waypoints
	 */
	private void reverse(List<Waypoint> waypoints) {
		int i = 0;
		int j = waypoints.size() - 1;
		
		while (i < j) {
			
			Waypoint wpti = waypoints.get(i);
			Waypoint wptj = waypoints.get(j);
			Date tmp = wptj.getTime();
			wptj.setTime(wpti.getTime());
			wpti.setTime(tmp);
			waypoints.set(i, wptj);
			waypoints.set(j, wpti);

			i++;
			j--;
		}
	}

}
