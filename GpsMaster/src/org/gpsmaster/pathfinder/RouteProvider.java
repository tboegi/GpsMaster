package org.gpsmaster.pathfinder;

import java.util.List;

import org.gpsmaster.ConnectivityType;
import org.gpsmaster.gpxpanel.Waypoint;

/**
 * 
 * @author rfu
 *
 */
public abstract class RouteProvider {

	protected Transport transport = null;
	
	/**
	 * Display Name
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * 
	 * @return
	 */
	public abstract String getDescription();
	
	/**
	 * 
	 * @return
	 */
	public abstract String getAttribution();
	
	/**
	 * get the maximum distance this provider can cover (in kilometers)
	 * 
	 * @return
	 */
	public abstract long getMaxDistance();

	/**
	 * 
	 * @return
	 */
	public abstract ConnectivityType getConnectivityType();
	
	/**
	 * 
	 * @param transport
	 */
	public void setRouteType(Transport routeType) {
		this.transport = routeType;
	}
	
	/**
	 * 
	 * @return
	 */
	public Transport getTransportType() {
		return transport;
	}
	
	/**
	 * Get supported route types
	 * @return supported route types or empty list
	 */
	public abstract List<Transport> getTransport();
	
	/**
	 * 
	 * @param resultRoute
	 * @param start
	 * @param end
	 */
	public void findRoute(List<Waypoint> resultRoute, Waypoint start, Waypoint end) throws Exception {
		findRoute(resultRoute, start.getLat(), start.getLon(), end.getLat(), end.getLon());
	}

	/**
	 * 
	 * @param resultRoute
	 * @param startLat
	 * @param startLon
	 * @param endLat
	 * @param endLon
	 */
	public abstract void findRoute(List<Waypoint> resultRoute, double startLat, double startLon, double endLat, double endLon) throws Exception;
	
	
}
