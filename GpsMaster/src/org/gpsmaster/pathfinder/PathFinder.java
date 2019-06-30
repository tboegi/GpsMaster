package org.gpsmaster.pathfinder;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;
import org.gpsmaster.marker.RoutePointMarker;
import org.gpsmaster.widget.PathFinderWidget;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;

/**
 * Class for finding routes via route provider.
 * 
 * newly found route segment is added to the current
 * (active) route.
 * 
 * @author rfu
 *
 */
public class PathFinder {

	private RouteProvider routeProvider = null;
	private Route activeRoute = null;
	private PropertyChangeListener changeListener = null;
	private List<Marker> markerList = null;
	private List<RouteRequest> requests = new ArrayList<PathFinder.RouteRequest>();

	private PathFinderWidget widget = null;
	private MessageCenter msg = null;
	private SwingWorker<Void, Void> pathFindWorker = null;

	/**
	 * Helper class to hold all parameters required for finding a route
	 * @author rfu
	 *
	 */
	private class RouteRequest {
		public Waypoint destination = null;
		public Route route = null;
		public RouteProvider provider = null;
		public Transport transport = null;
		public RoutePointMarker routeMarker = null;
	}
	
	/**
	 * default constructor
	 */
	public PathFinder() {
	
		changeListener = new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName(); 
				if (propertyName.equals(Const.PCE_ACTIVEGPX)) {
					setGpxObject();				
				}
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);

		makeSwingWorker();		
	}
	
	/** 
	 * Constructor
	 * 
	 * @param provider
	 */
	public PathFinder(RouteProvider provider) {
		this();
		routeProvider = provider;
	}
	
	/**
	 * @return the routeProvider
	 */
	public RouteProvider getRouteProvider() {
		return routeProvider;
	}
	
	/**
	 * @param routeProvider the routeProvider to set
	 */
	public void setRouteProvider(RouteProvider provider) {
		this.routeProvider = provider;
	}
	
	/**
	 * @param markerList the markerList to set
	 */
	public void setMarkerList(List<Marker> markerList) {
		this.markerList = markerList;
	}

	/**
	 * @param widget the widget to set
	 */
	public void setWidget(PathFinderWidget widget) {
		this.widget = widget;
	}

	/**
	 * 
	 * @param msg
	 */
	public void setMessageCenter(MessageCenter msg) {
		this.msg = msg;
	}
	
	/**
	 * find a path from the active route's last point to the given {@link Waypoint}
	 * @param destination
	 * @throws Exception
	 */
	public void findRoute(Waypoint destination) throws Exception {
		
		RoutePointMarker marker = null;
		
		if (activeRoute == null) {
			throw new IllegalArgumentException("No active route");
		}

		if (routeProvider == null) {
			throw new IllegalArgumentException("Route provider not set");
		}
		
		if (markerList != null) {
			marker = new RoutePointMarker(destination);
			marker.setRouteProvider(routeProvider);
			markerList.add(marker);			
			GpsMaster.active.repaintMap();
		}

		// empty route - set first point
		if (activeRoute.getNumPts() == 0) {
			activeRoute.getPath().addWaypoint(destination);			
			return;
		}

		RouteRequest request = new RouteRequest();
		request.destination = destination;
		request.route = activeRoute;
		request.provider = routeProvider;
		request.transport = routeProvider.getTransportType();
		request.routeMarker = marker;
		requests.add(request);
		
		// if pathFindWorker is idle, create a new instance and start it.
		if (pathFindWorker.getState() == StateValue.DONE) {
			makeSwingWorker();
		}
		pathFindWorker.execute();
		
	}
	
	/**
	 * Set the active route:
	 *  - if the current {@link GPXObject} is a {@link route}
	 *  - or the {@link GPXFile} contains just one route
	 *  otherwise, the route is set to null 
	 */
	public void setGpxObject() {
		activeRoute = null;
		if (GpsMaster.active.getGpxObject() != null) {
			if (GpsMaster.active.getGpxObject() instanceof Route) {
				activeRoute = (Route) GpsMaster.active.getGpxObject();
			} else {
				if (GpsMaster.active.getGpxFile().isGPXFileWithOneRoute()) {
					activeRoute = GpsMaster.active.getGpxFile().getRoutes().get(0);
				}
			}
		}
	}

	/**
	 * Cancel running pathFinder tasks
	 * it is assumed that the request list contains only items
	 * that have not been processed 
	 */
	public void Cancel() {
		pathFindWorker.cancel(true);
		// remove "unused" RoutePointMarker
		if (markerList != null) {
			for (RouteRequest r : requests) {
				markerList.remove(r.routeMarker);
			}
		}
		requests.clear();
	}
	
	/**
	 * 
	 */
	public void clear() {
		if (markerList != null) {
			for (Marker marker : markerList) {
				if (marker instanceof RoutePointMarker) {
					markerList.remove(marker);
				}
			}
		}
		requests.clear();
	}
	
	/**
	 * A {@link SwingWorker} can only run once. 
	 * use this method to re-instantiate it when needed  
	 */
	private void makeSwingWorker() {
		pathFindWorker = new SwingWorker<Void, Void>() {
			
			MessagePanel infoPanel = null;
			
			@Override
			protected Void doInBackground() throws Exception {
				widget.setBusy(true);
				if (msg != null) {
					infoPanel = msg.infoOn("", new Cursor(Cursor.WAIT_CURSOR));
				}
				int size = requests.size(); // sync
				while (size > 0) {
					
					RouteRequest request = requests.get(0);
					try {
						List<Waypoint> routePoints = request.route.getPath().getWaypoints(); 
						Waypoint start = routePoints.get(routePoints.size() - 1);
						Waypoint dest = request.destination;
						infoPanel.setText(String.format("finding path to %.6f, %.6f (%s) ... ", dest.getLat(), dest.getLon(), routeProvider.getName()));
						routeProvider.findRoute(routePoints, start.getLat(), start.getLon(), dest.getLat(), dest.getLon()); //transport !!
						request.route.updateAllProperties();
						GpsMaster.active.repaintMap();
					} catch (Exception e) {
						if (msg != null) {
							msg.error(e);
						}
					}
					requests.remove(request);
					size = requests.size();
				}

				return null;
			}
			
			@Override
			protected void done() {				
				if (msg != null) {
					msg.infoOff(infoPanel);
				}
				widget.setBusy(false);
			}			
		};
	}
}
