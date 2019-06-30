package org.gpsmaster.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.gpsmaster.gpxpanel.ArrowType;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;

/**
 * Painter class for directional arrows
 * 
 * @author rfu
 *
 */
public class ArrowPainter extends Painter {

	private final double arrowOffset = -12.0f;
    private Polygon arrowHead = new Polygon();
    private Polygon parallelArrow = new Polygon();
    private final Color color = Color.BLACK; // color for on-track arrow 
    private final int check = 2; // number of points before and after current waypoint to consider for arrow angle 
    
    private ArrowType arrowType = ArrowType.NONE;
    
    public ArrowPainter() {
    	super();
    	order = 7;
    	
	    arrowHead.addPoint(0, 8);
	    arrowHead.addPoint(-5,  -5);
	    arrowHead.addPoint(5, -5);
	    
	    parallelArrow.addPoint(0, -14);
	    parallelArrow.addPoint(0, 8);
	    parallelArrow.addPoint(-4, -4);
	    parallelArrow.addPoint(4, -4);
	    parallelArrow.addPoint(0, 8);
	    
	}
	
	/*
	 * Properties
	 */
	public ArrowType getArrowType() {
		return arrowType;
	}
	
	public void setArrowType(ArrowType paintArrows) {
		this.arrowType = paintArrows;
	}

	@Override
	public void paint(Graphics2D g2d, GPXFile gpx) {
		
		if (coordinator.getDistanceInterval() == 0.0f) {
			
			coordinator.setDistanceInterval(mapViewer.getMeterPerPixel() * 125f); 
			// TODO get default() factor (here: 125) from paintcoordinator 
						
		}

		if ((arrowType != ArrowType.NONE) && enabled && gpx.isVisible()) {			
			for (Track track : gpx.getTracks()) {
				for (WaypointGroup grp : track.getTracksegs()) {
					if (track.isVisible() && grp.isVisible()) {
						paintGroup(g2d, grp);
					}
				}
			}
			for (Route route : gpx.getRoutes()) {
				if (route.isVisible()) {
					paintGroup(g2d, route.getPath());
				}
			}

		}
		

	}

	@Override
	public void paint(Graphics2D g2d, List<Marker> markerList) {
		// nothing to do
		
	}

	/**
	 * paint arrows for a single waypoint group
	 * @param grp
	 */
	private void paintGroup(Graphics2D g2d, WaypointGroup grp) {
		double distance = 0;
		double minDistance = coordinator.getDistanceInterval() / 2.0f;

		long count = grp.getWaypoints().size();
		for (int i = 1; i < count; i++ ) {
			Waypoint prev = grp.getWaypoints().get(i - 1);
			Waypoint curr = grp.getWaypoints().get(i);
			int af = i - check; // angle calculation - from waypoint
			int at = i + check; // angle calculation - to waypoint
			
			distance += curr.getDistance(prev);
			
			if ((distance >= minDistance) && (af >= 0) && (at < count)) {
				switch(arrowType) {
				case PARALLEL:
					paintParallelArrow(g2d, grp.getColor(), curr, grp.getWaypoints().get(af), grp.getWaypoints().get(at));
					break;
				case ONTRACK:
					paintTrackArrow(g2d, color, curr, grp.getWaypoints().get(af), grp.getWaypoints().get(at));
					break;
				default:
					break;
				}
				
				distance = 0;
				minDistance = coordinator.getDistanceInterval();				
			}			
		}			
	}
	
	/**
	 * Paint a directed arrow parallel to the track
	 * @param g2d
	 * @param color {@link Color} of the arrow
	 * @param wptFrom start point of track section 
	 * @param wptTo end point of track section 
	 */
	private void paintParallelArrow(Graphics2D g2d, Color color, Waypoint curr, Waypoint wptFrom, Waypoint wptTo) {
    			
		Point from = mapViewer.getMapPosition(wptFrom.getLat(), wptFrom.getLon(), false);
		Point to = mapViewer.getMapPosition(wptTo.getLat(), wptTo.getLon(), false);
		Point wpt = mapViewer.getMapPosition(curr.getLat(), curr.getLon(), false); // where the arrow is painted
		
		AffineTransform saveTransform = g2d.getTransform();
    	AffineTransform transform = new AffineTransform();	    	
    	transform.setToIdentity();
    	double angle = Math.atan2(to.y - from.y, to.x - from.x);
    	transform.translate(wpt.x, wpt.y);
    	transform.rotate((angle-Math.PI/2d));
    	transform.translate(arrowOffset, -0.5f * from.distance(to));
    	g2d.setColor(color);
    	g2d.transform(transform);
    	g2d.drawPolygon(parallelArrow);
    	g2d.setTransform(saveTransform);		
    }

	/**
	 * Paint a directional arrow directly on track. arrow will be located
	 * halfway between two waypoints.
	 * @param g2d
	 * @param wptFrom 1stt {@link Waypoint} determining arrow position
	 * @param wptTo 2nd {@link Waypoint} determining arrow position
	 */
	private void paintTrackArrow(Graphics2D g2d, Color color, Waypoint curr, Waypoint wptFrom, Waypoint wptTo) {
    	
		Point from = mapViewer.getMapPosition(wptFrom.getLat(), wptFrom.getLon(), false);
		Point to = mapViewer.getMapPosition(wptTo.getLat(), wptTo.getLon(), false);
		Point wpt = mapViewer.getMapPosition(curr.getLat(), curr.getLon(), false); // where the arrow is painted
		
		AffineTransform saveTransform = g2d.getTransform();
    	AffineTransform transform = new AffineTransform();	    	
    	transform.setToIdentity();
    	double angle = Math.atan2(to.y - from.y, to.x - from.x);
    	transform.translate(wpt.x, wpt.y);
    	transform.rotate((angle-Math.PI/2d));
    	transform.translate(0, -0.5f * from.distance(to));
    	g2d.setColor(color);
    	g2d.transform(transform);
    	g2d.fill(arrowHead);
    	g2d.setTransform(saveTransform);		
    }


}
