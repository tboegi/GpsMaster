package org.gpsmaster.cleaning;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import eu.fuegenstein.util.DPoint;
import eu.fuegenstein.util.Square;

/**
 * remove point clouds
 *
 * @author rfu
 *
 */
public class CloudBuster extends CleaningAlgorithm {

	private Rectangle quadrant = new Rectangle();
	private int neighbours = 10; // how many neighbouring points to check
	private double sideLength = 2.0f;


	public CloudBuster() {
		super();
	}

	@Override
	public String getName() {
		return "CloudBuster";
	}

	@Override
	public String getDescription() {
		return "This algorithm detects and removes clouds of points";
	}

	@Override
	protected void applyAlgorithm() {
		Dimension d = new Dimension();

	}

	/**
	 * scan waypoint group for point clouds
	 */
	private void scan() {
		Square square = new Square();
		square.setSideLength(sideLength);
		List<Waypoint> waypoints = waypointGroup.getWaypoints();
		for (int i = 0; i < waypoints.size() - neighbours; i++) {
			Waypoint current = waypoints.get(i);
			square.setCenter(current.getLat(), current.getLon());
			// see how many neighbours are in this square
			for (int j = 0; j < neighbours; j++) {
				Waypoint wp = waypoints.get(i + j + 1);
				if (square.contains(wp.getLat(), wp.getLon())) {

				}
			}
		}

	}


}
