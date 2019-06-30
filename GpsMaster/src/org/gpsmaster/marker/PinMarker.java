package org.gpsmaster.marker;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * A Marker represented by a pin on the map.
 * 
 * Caveat: the pin marker/icon needs an offset so that
 * the tip of needle points to the given coordinates. 
 * 
 * @author rfu
 *
 */
public class PinMarker extends Marker {

	/**
	 * 
	 * @param lat
	 * @param lon
	 */
	public PinMarker(double lat, double lon) {
		super(lat, lon);		
	}

	/**
	 * 
	 * @param wpt
	 */
	public PinMarker(Waypoint wpt) {
		super(wpt);		
	}
	
	/**
	 * 
	 */
	protected void setup() {
		setIcon("pin.png");
		iconXOffset = icon.getIconWidth() / 2;  // lower left corner is reference point		
	}
}
