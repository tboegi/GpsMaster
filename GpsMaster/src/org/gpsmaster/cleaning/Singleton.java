package org.gpsmaster.cleaning;

import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import eu.fuegenstein.parameter.CommonParameter;
import eu.fuegenstein.parameter.DoubleParameter;

/**
 * Remove singleton points
 * 
 * based on code from GpsPrune
 * http://activityworkshop.net/
 * 
 * @author rfu
 *
 */
public class Singleton extends CleaningAlgorithm {

	private DoubleParameter parameter = null;
		
	public Singleton() {
		super();
		parameter = new DoubleParameter(2.0f);
		parameter.setDescription("factor");
		parameter.setName("avgFactor");
		parameter.setFormat("%.1f");
		params.add((CommonParameter) parameter);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Singleton";
	}
	@Override
	public String getDescription() {
		String desc = "Remove points away from their neighbours by (factor) times more than the average";
		return desc;
	}

	@Override
	protected void applyAlgorithm(WaypointGroup group, List<Waypoint> toDelete) {
		List<Waypoint> trackpoints = group.getWaypoints();
		// average distance between two trackpoints
		double meanDistance = group.getLengthMeters() / (group.getWaypoints().size() - 1);
		double threshold = meanDistance * parameter.getValue();
		// TODO use sliding window over (n) points to calculate average distance 
		// TODO besser: ausreisser finden per plötzlicher richtungs/winkel-änderung
		if (trackpoints.size() < 3) {
			// not enough points
			throw new IllegalArgumentException("Not enough trackpoints");
		}

		for (int i = 1; i < trackpoints.size() - 1; i++) {
			Waypoint prev = trackpoints.get(i - 1);
			Waypoint curr = trackpoints.get(i);
			Waypoint next = trackpoints.get(i + 1);
			double dPrev = curr.getDistance(prev);
			double dNext = curr.getDistance(next);
			if ((dPrev > threshold) && (dNext > threshold)) {
				toDelete.add(curr);
			}
		}
	}

}
