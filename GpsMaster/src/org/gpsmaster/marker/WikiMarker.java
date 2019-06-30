package org.gpsmaster.marker;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * more or less a stub class for geo-referenced wikipedia articles
 * 
 * @author rfu
 *
 */
public class WikiMarker extends Marker {

	/**
	 * Constructor
	 * @param lat
	 * @param lon
	 */
	public WikiMarker(double lat, double lon) {
		super(lat, lon);
	}

	/**
	 * 
	 * @param wpt
	 */
	public WikiMarker(Waypoint wpt) {
		super(wpt);
	}

	protected void setup() {
		setIcon("wikipedia.png");
		showWebIcon = false;
	}
}
