package org.gpsmaster.markers;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * more or less a stub class for geo-referenced wikipedia articles
 *
 * @author rfu
 *
 */
public class WikipediaMarker extends Marker {

	/**
	 * Constructor
	 * @param lat
	 * @param lon
	 */
	public WikipediaMarker(double lat, double lon) {
		super(lat, lon);
	}

	/**
	 *
	 * @param wpt
	 */
	public WikipediaMarker(Waypoint wpt) {
		super(wpt);
	}

	protected void setup() {
		setIcon("wikipedia.png");
		type = "Wikipedia";
	}
}
