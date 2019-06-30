package org.gpsmaster.elevation;

import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * 
 * @author rfu
 *
 */
public interface ElevationProvider {

	/**
	 * Get the name of this provider
	 * @return 
	 */
	public String getName();
	
	/**
	 * 
	 * @return
	 */
	public String getAttribution();
	
	/**
	 * determine if missing elevation information is to be interpolated
	 * 
	 * @param interpolate
	 */
	public void setInterpolation(boolean interpolate);
	
	/**
	 * 
	 * @return
	 */
	public boolean isInterpolation();
	
	/**
	 * get max. allowed number of {@link Waypoint}s per request 
	 * @return number of {@link Waypoint}s or 0 if no limit 
	 */
	public int getChunkSize();

	/**
	 * 
	 * @return
	 */
	int getFailed();
	
	/**
	 * correct elevation of given {@link Waypoint}
	 * 
	 * @param waypoint
	 */
	public void correctElevation(Waypoint waypoint);
	
	/**
	 * correct elevation of given {@link Waypoint}s
	 * 
	 * @param waypoints
	 * @throws Exception 
	 */
	public void correctElevation(List<Waypoint> waypoints) throws Exception;

	
		
	
}
