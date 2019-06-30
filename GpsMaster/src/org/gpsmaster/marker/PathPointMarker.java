package org.gpsmaster.marker;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * 
 * Marker to mark start / end / intermediate points
 * on a route added by the user
 * 
 * @author rfu
 *
 */
public class PathPointMarker extends Marker {

	/**
	 * 
	 * @param lat
	 * @param lon
	 */
	public PathPointMarker(double lat, double lon) {
		super(lat, lon);
	}

	public PathPointMarker(Waypoint wpt) {
		super(wpt);
	}
	
	/**
	 * 
	 */
	protected void setup() {
		setIcon("path-point.png");
		iconXOffset = icon.getIconWidth() / 2;  // lower left corner is reference point		
	}
}
