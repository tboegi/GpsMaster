package org.gpsmaster.pathfinder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.marker.RoutePointMarker;
import org.gpsmaster.undo.IUndoable;

/**
 * 
 * Helper class to hold all parameters required for finding a route (segment)
 * 
 * @author rfu
 *
 */
public class RouteRequestInfo implements IUndoable {
	
	public static final int STATE_PENDING = 0;
	public static final int STATE_PROCESSING = 1;
	public static final int STATE_FINISHED = 2;
	public static final int STATE_CANCELLED = 3;
	
	// TODO encapsulate the following
	public Waypoint destination = null;			// destination to find to
	public Route route = null;					// the route to add the found segment to
	public GPXFile gpxFile = null; 	// waypointGroup to hold RoutePoint Marker 
	public RouteProvider provider = null;
	public Transport transport = null;
	public RoutePointMarker routeMarker = null; 
	
	private final long lockTimeout = 5;
	private ReentrantLock lock = new ReentrantLock(); 
	
	private int startIdx = -1;
	private int state = STATE_PENDING;

	/**
	 * @return the startIdx
	 */
	public int getStartIdx() {
		return startIdx;
	}

	/**
	 * @param startIdx the startIdx to set
	 */
	public void setStartIdx(int startIdx) {
		this.startIdx = startIdx;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		// lock() ?
		return state;
	}

	/**
	 * @param state the state to set
	 * @throws InterruptedException 
	 */
	public void setState(int state) throws InterruptedException {
		lock();
		this.state = state;
		unlock();
	}

	@Override
	public String getUndoDescription() {
		return String.format("Find Path to (%.6f, %.6f) using %s (%s)", 
				destination.getLat(), destination.getLon(),
				provider.getName(), transport.getName());	}

	@Override
	public void undo() throws InterruptedException {
		lock();
		switch(state) {
			case STATE_PENDING:
				// route finding has not started for this segment.
				// just remove the RoutePointMarker
				removeRoutePointMarker();
				state = STATE_CANCELLED;
				break;
				
			case STATE_PROCESSING:
				// route finding for this segment is underway.
				removeRoutePointMarker();
				state = STATE_CANCELLED;
				break;
				
			case STATE_FINISHED:
				// pathfinder task is no longer running. no need to set state.
				List<Waypoint> waypoints = route.getPath().getWaypoints(); // shortcut
				int last = waypoints.size() - 1;
				for (int i = last; i > startIdx; i--) {
					waypoints.remove(i);
				}
				removeRoutePointMarker();
				state = STATE_CANCELLED; // not necessary since pathfinder is no longer running
				break;
				
			default:
				throw new IllegalArgumentException("State " + state);				
					
		}
		unlock();
	}
	
	/**
	 * lock access to all calling methods 
	 * @throws InterruptedException 
	 */
	private void lock() throws InterruptedException {
		lock.tryLock(lockTimeout, TimeUnit.SECONDS);

	}
	
	/**
	 * unlock access to all calling methods
	 */
	private void unlock() {
		lock.unlock();
	}
	
	/**
	 * 
	 */
	private void removeRoutePointMarker() {
		gpxFile.getWaypointGroup().getWaypoints().remove(routeMarker);
	}
}
