package org.gpsmaster.pathfinder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.marker.Marker;
import org.gpsmaster.marker.PathPointMarker;

/**
 * Class for finding routes by using path provider.
 * 
 * newly found route segment is added to the current
 * (active) route.
 * 
 * @author rfu
 *
 */
public class PathFinder {

	private PathProvider pathProvider = null;
	private Route activeRoute = null;
	private PropertyChangeListener changeListener = null;
	private List<Waypoint> userPointList = new ArrayList<Waypoint>();
	private List<Marker> markerList = null;
	
	/**
	 * default constructor
	 */
	public PathFinder() {
	
		setGpxObject();
		
		changeListener = new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName(); 
				if (propertyName.equals(Const.PCE_ACTIVEGPX) || propertyName.equals(Const.PCE_REFRESHGPX)) {
					setGpxObject();				
				}
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);
		
	}
	
	/**
	 * 
	 * @param provider
	 */
	public PathFinder(PathProvider provider) {
		this();
		pathProvider = provider;
	}
	
	/**
	 * @return the pathProvider
	 */
	public PathProvider getPathProvider() {
		return pathProvider;
	}

	/**
	 * @param pathProvider the pathProvider to set
	 */
	public void setPathProvider(PathProvider provider) {
		this.pathProvider = provider;
	}
	
	/**
	 * @return the markerList
	 */
	public List<Marker> getMarkerList() {
		return markerList;
	}

	/**
	 * @param markerList the markerList to set
	 */
	public void setMarkerList(List<Marker> markerList) {
		this.markerList = markerList;
	}

	/**
	 * find a path from the active route's last point to the given {@link Waypoint}
	 *  
	 * @param wpt
	 */
	public void findPath(Waypoint wpt) {
		findPath(wpt.getLat(), wpt.getLon());
	}

	/**
	 * find a path from the active route's last point to the given coordinates
	 * @param lat
	 * @param lon
	 */
	public void findPath(double lat, double lon) {
		if (markerList != null) {
			markerList.add(new PathPointMarker(lat, lon));
		}
		
	}
	
	/**
	 * 
	 */
	private void setGpxObject() {
		activeRoute = null;
		if (GpsMaster.active.getGpxObject() instanceof Route) {
			activeRoute = (Route) GpsMaster.active.getGpxObject();
		}
	}
	
	/**
	 * 
	 */
	public void clear() {
		if (markerList != null) {
			for (Marker marker : markerList) {
				if (marker instanceof PathPointMarker) {
					markerList.remove(marker);
				}
			}
		}
		
		userPointList.clear();
		
	}
}
