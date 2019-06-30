package org.gpsmaster.pathfinder;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Waypoint;
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
 * TODO better add {@link RoutePointMarker} to {@link Route} instead of {@link RoutePointMarker}
 */
public class PathFinder {

	private RouteProvider routeProvider = null;
	private Route activeRoute = null;
	private PropertyChangeListener changeListener = null;
	private GPXFile gpxFile = null;
	private List<RouteRequestInfo> requests = null; 

	private PathFinderWidget widget = null;
	private MessageCenter msg = null;
	private SwingWorker<Void, Void> pathFindWorker = null;

	
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
		
		requests = Collections.synchronizedList(new ArrayList<RouteRequestInfo>());
		makePathFindWorker();		
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
		
		marker = new RoutePointMarker(destination);
		marker.setRouteProvider(routeProvider);
		marker.setRoute(activeRoute);
		gpxFile.getWaypointGroup().getWaypoints().add(marker);			
		
		RouteRequestInfo request = new RouteRequestInfo();
		request.setState(RouteRequestInfo.STATE_PENDING);
		request.destination = destination;
		request.route = activeRoute;
		request.gpxFile = gpxFile;
		request.provider = routeProvider;
		request.transport = routeProvider.getTransportType();
		request.routeMarker = marker;
		GpsMaster.active.addUndoOperation(request);
		GpsMaster.active.repaintMap();
		
		// empty route - set first point
		if (activeRoute.getNumPts() == 0) {
			activeRoute.getPath().addWaypoint(destination);
			request.setState(RouteRequestInfo.STATE_FINISHED); // workaround
			return;
		}
		requests.add(request);
		
		// if pathFindWorker is idle, create a new instance and start it.
		if (pathFindWorker.getState() == StateValue.DONE) {
			makePathFindWorker();
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
			gpxFile = GpsMaster.active.getGpxFile();
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
		for (RouteRequestInfo r : requests) {
			gpxFile.getWaypointGroup().getWaypoints().remove(r.routeMarker);
		}		
		requests.clear();
	}
	
	/**
	 * 
	 * TODO if GPXFile contains multiple routes, this method removes
	 * 		the RoutePoints for ALL routes! 
	 */
	public void clear() {
		/*
		List<Waypoint> waypoints = gpxFile.getWaypointGroup().getWaypoints();
		for (Waypoint wpt : waypoints) {
			if (wpt instanceof RoutePointMarker) {
				waypoints.remove(wpt);
			}
		}
		*/
		requests.clear();
	}
	
	/**
	 * A {@link SwingWorker} can only run once. 
	 * use this method to re-instantiate it when needed  
	 */
	private void makePathFindWorker() {
		pathFindWorker = new SwingWorker<Void, Void>() {

			RouteRequestInfo request = null;
			List<Waypoint> newSegment = new ArrayList<Waypoint>();
			MessagePanel infoPanel = null;
		
			@Override
			protected Void doInBackground() throws Exception {
				widget.setBusy(true);
				if (msg != null) {
					infoPanel = msg.infoOn("", new Cursor(Cursor.WAIT_CURSOR));
				}
				int size = requests.size(); // sync
				while (size > 0) {
					
					request = requests.get(0);
					if (request.getState() == RouteRequestInfo.STATE_PENDING) {
						request.setState(RouteRequestInfo.STATE_PROCESSING);
						try {
							List<Waypoint> routePoints = request.route.getPath().getWaypoints(); 
							Waypoint start = routePoints.get(routePoints.size() - 1);
							Waypoint dest = request.destination;
							infoPanel.setText(String.format("finding path to %.6f, %.6f (%s) ... ", dest.getLat(), dest.getLon(), request.provider.getName()));
							request.provider.findRoute(newSegment, start.getLat(), start.getLon(), dest.getLat(), dest.getLon()); //transport !!
							if ((isCancelled() == false) && (request.getState() != RouteRequestInfo.STATE_CANCELLED)) {
								request.setStartIdx(request.route.getPath().getWaypoints().size() - 1);
								request.route.getPath().getWaypoints().addAll(newSegment);
								request.route.updateAllProperties();
								GpsMaster.active.repaintMap();
								GpsMaster.active.refresh();
							}
							request.setState(RouteRequestInfo.STATE_FINISHED);
						} catch (Exception e) {
							if (msg != null) {
								msg.error(e);
							}
						}
					}
					requests.remove(request);
					newSegment.clear();
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
