package org.gpsmaster.livefeed;

import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;


/**
 * Base class for (near) realtime GPS sources
 * 
 * @author rfuegen
 * 
 * http://stackoverflow.com/questions/336714/reading-serial-port-in-java
 * 
 *
 */
public class GpsFeed {

	private List<Waypoint> waypoints;
	private int updateInterval = 1000;
	private double minDistance = 5;
	
	/**
	 * Constructor
	 * 
	 */
	public GpsFeed() {
		waypoints = new ArrayList<Waypoint>();
		
	}
		
	/**
	 * 
	 * @param interval Interval seconds
	 */
	public void SetUpdateInterval(int interval) {
		updateInterval = interval;
	}
	
	public int GetUpdateInterval() {
		return updateInterval;
	}
	
	/**
	 * @param min minimum distance between two successive GPS points
	 * (in meters)
	 * GPS points below this distance will not be logged.
	 */
	public void SetMinDistance(double min) {
		minDistance = min;
	}
	
	public double GetMinDistance() {
		return minDistance;
	}
	
	public List<Waypoint> GetWayPoints()
	{
		return waypoints;
	}
	
	/**
	 * start gathering of GPS points
	 */
	public void Start() {
		// start timer
	
	}

	/**
	 * stop gathering of GPS points
	 */
	public void Stop() {
		// stop timer
		
	}
}
