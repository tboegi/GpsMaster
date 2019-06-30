package org.gpsmaster.cleaning;

import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * Remove duplicate trackpoints
 * @author rfu
 *
 */
public class Duplicates extends CleaningAlgorithm {
	
	/**
	 * 
	 */
	public Duplicates() {
		super();
		setName("Duplicates");
		setDescription("Remove points with identical coordinates");
	}
	
		
	@Override
	protected void applyAlgorithm(WaypointGroup group, List<Waypoint> toDelete) {
		Waypoint prev = new Waypoint(270.0f,  270.0f);
		for (Waypoint curr : group.getWaypoints()) {
			if (prev.getLat() == curr.getLat() && prev.getLon() == curr.getLon()) {
				toDelete.add(curr);
			}
			prev = curr;
		}
		
	}

}
