package org.gpsmaster.cleaning;

import java.util.List;
import org.gpsmaster.gpxpanel.Waypoint;

import eu.fuegenstein.util.CommonParameter;
import eu.fuegenstein.util.DoubleParameter;

/**
 * Cleans all points within a mininum distance to its neighbours
 * @author rfu
 *
 */
public class MinDistance extends CleaningAlgorithm {

	private DoubleParameter minDistance = null;

	/**
	 *
	 * @param waypointGroup
	 */
	public MinDistance() {
		super();
		minDistance = new DoubleParameter(1.0f);
		minDistance.setDescription("Minimum Distance");
		minDistance.setName("minDistance");
		minDistance.setFormat("%.1f");
		parameters.add((CommonParameter) minDistance);
	}

	@Override
	public String getName() {
		return "MinDistance";
	}

	@Override
	public String getDescription() {
		return "Remove all trackpoints within a certain distance to their neighbours";
	}

	/**
	 *
	 */
	@Override
	protected void applyAlgorithm() {

		double distance = 0f;

		if (waypointGroup.getWaypoints().size() > 2) {
	    	List<Waypoint> waypoints = waypointGroup.getWaypoints(); // shortcut
		    Waypoint prev = waypoints.get(0);

	    	for (int i = 1; i < waypoints.size(); i++) {
	    		Waypoint wpt = waypoints.get(i);
	    		distance += wpt.getDistance(prev);
	    		if (distance < minDistance.getValue()) {
	    			toDelete.add(wpt);
	    		} else {
	    			distance = 0;
		    		prev = wpt;
	    		}
	    	}
		}
	}

}
