package org.gpsmaster.marker;

import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.pathfinder.RouteProvider;

/**
 * 
 * Marker to mark start / end / intermediate points
 * on a route added by the user
 * 
 * @author rfu
 *
 */
public class RoutePointMarker extends Marker {

	private Route route = null;
	protected RouteProvider routeProvider = null;
		
	/**
	 * 
	 * @param lat
	 * @param lon
	 */
	public RoutePointMarker(double lat, double lon) {
		super(lat, lon);
	}

	public RoutePointMarker(Waypoint wpt) {
		super(wpt);
	}

	/**
	 * set the {@link RouteProvider} that created the segment following this marker
	 * @param provider
	 */
	public void setRouteProvider(RouteProvider provider) {
		routeProvider = provider;
		setName(routeProvider.getName() + " (" + routeProvider.getTransportType().getName()+")");
	}

	/**
	 * Get Route this RoutePoint belongs to
	 * @return the route
	 */
	public Route getRoute() {
		return route;
	}

	/**
	 * Set Route this RoutePoint belongs to
	 * @param route the route to set
	 */
	public void setRoute(Route route) {
		this.route = route;
	}

	/**
	 * 
	 */
	protected void setup() {
		setIcon("path-point.png");
		iconXOffset = icon.getIconWidth() / 2;  // lower left corner is reference point		
	}
	
}
