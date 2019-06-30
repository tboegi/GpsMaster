package org.gpsmaster.marker;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * Basically a Marker without an icon, just the label is painted.
 * 
 * @author rfu
 *
 */
public class NullMarker extends Marker {

	public NullMarker(Waypoint wpt) {
		super(wpt);
	}

	public NullMarker(double lat, double lon) {
		super(lat, lon);
	}

}
