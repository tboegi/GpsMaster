package org.gpsmaster.marker;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * Standard Waypoint Marker
 * 
 * @author rfu
 *
 */
public class WaypointMarker extends Marker {

	public WaypointMarker(Waypoint waypoint) {
		super(waypoint);		
	}
	
	public WaypointMarker(double lat, double lon) {
		super(lat, lon);
	}

	@Override
	protected void setup() {
		setIcon("waypoint.png");	
		showWebIcon = true;
	}

}
