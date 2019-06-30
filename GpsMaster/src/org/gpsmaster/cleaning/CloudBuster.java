package org.gpsmaster.cleaning;

import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import eu.fuegenstein.util.DoubleParameter;
import eu.fuegenstein.util.IntegerParameter;

/**
 * remove point clouds
 * 
 * @author rfu
 *
 */
public class CloudBuster extends CleaningAlgorithm {

	// vicinity of current point to check for other points: 
	// TODO replace circle with square
	private DoubleParameter radius = new DoubleParameter(2.0f);
	
	// consider it a cloud if a waypoint has more than {threshold} neighbours
	private IntegerParameter threshold = new IntegerParameter(5); 
	
	private IntegerParameter checkNeighbours = new IntegerParameter(25);

	
	public CloudBuster() {
		super();
		radius.setName("Radius");
		radius.setDescription("Radius to check");
		parameters.add(radius);
		
		threshold.setName("Threshold");
		threshold.setDescription("# of close points defining a cloud");
		parameters.add(threshold);
		
		checkNeighbours.setName("CheckNeighbours");
		checkNeighbours.setDescription("neighbours to check");
		parameters.add(checkNeighbours);
	}

	@Override
	public String getName() {
		String name = "CloudBuster";
		return name;
	}
	
	@Override
	public String getDescription() {
		String desc = "Detect and remove clouds of points"; 
		return desc;  			   
	}

	/**
	 * scan waypoint group for point clouds
	 */
	@Override
	protected void applyAlgorithm(WaypointGroup group, List<Waypoint> toDelete) {
		List<Waypoint> trackpoints = group.getWaypoints(); // shortcut
		for (int i = 0; i < trackpoints.size(); i++) {
			Waypoint curr = trackpoints.get(i);
			int cloudPoints = 0;
			int j = 0;
			while((j < checkNeighbours.getValue()) && ((i + j + 1) < trackpoints.size())) {
				Waypoint next = trackpoints.get(i + j + 1);
				if (curr.getDistance(next) < radius.getValue()) {
					cloudPoints++;
					if (!toDelete.contains(next)) {
						toDelete.add(next);
					}
					// increase "floating window average"
					// set cloud min/max lat/lon
				}
				j++;
			}
			if (cloudPoints > threshold.getValue()) {
				// add found points to current cloud / toDelete list
				// System.out.println(cloudPoints);
			}
		}
		
	}
	
}
