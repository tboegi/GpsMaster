package org.gpsmaster.markers;

import org.gpsmaster.gpxpanel.Waypoint;



/**
 * 
 * @author rfu
 * 
 * Class representing a marker for distance measuring
 * 
 */
public class MeasureMarker extends Marker {
	
	/**
	 * Constructor
	 * @param lat
	 * @param lon
	 */
	public MeasureMarker(double lat, double lon) {
		super(lat, lon);
		setup();		
	}
	
	/**
	 * 
	 * @param wpt
	 */
	public MeasureMarker(Waypoint wpt) {
		super(wpt);
		setup();
	}
	
	protected void setup() {
		setIcon("measure.png");
	}
}
