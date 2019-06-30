package org.gpsmaster.cleaning;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * Remove duplicate trackpoints
 * @author rfu
 *
 */
public class Duplicates extends CleaningAlgorithm {

	@Override
	public String getName() {
		String name = "Duplicates";
		return name;
	}

	@Override
	public String getDescription() {
		String desc = "Remove points with identical coordinates";
		return desc;
	}

	@Override
	protected void applyAlgorithm() {
		Waypoint prev = new Waypoint(270.0f,  270.0f);
		for (Waypoint curr : trackpoints) {
			if (prev.getLat() == curr.getLat() && prev.getLon() == curr.getLon()) {
				toDelete.add(curr);
			}
			prev = curr;
		}
		
	}

}
