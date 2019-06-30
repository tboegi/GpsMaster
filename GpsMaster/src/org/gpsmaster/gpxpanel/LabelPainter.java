package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.gpsmaster.Const;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.util.XTime;

/**
 * Class providing functionality to paint extras along a Track Segment
 * (progress labels, directional arrows etc.)
 * 
 * @author rfu
 *
 */
public class LabelPainter {

	private JMapViewer mapViewer = null;
	private UnitConverter uc = null;
    private Polygon arrowHead = new Polygon();
    private Polygon parallelArrow = new Polygon();
	
    private ArrowType arrowType = ArrowType.NONE;
	private ProgressType progressType = ProgressType.NONE;
	
	private double multiplier = 2.7f; // label distance = label width * multiplier
	private double minLabelDist = 0f; // distance between two labels (in meters)
	private double arrowOffset = -12.0f;
	
	
	/*
	 * Default Constructor
	 */
	public LabelPainter(JMapViewer viewer, UnitConverter converter) {
		
		mapViewer = viewer;
		uc = converter;
        
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

	public ProgressType getProgressType() {
		return progressType;
	}

	public void setProgressType(ProgressType type) {
		progressType = type;
	}
	
	public Polygon getArrowHead() {
		return arrowHead;
	}
	
	public void setArrowHead(Polygon arrow) {
		arrowHead = arrow;
	}
	
	/*
	 * Methods
	 */

	/**
	 * Paint a directed arrow parallel to the track
	 * @param g2d
	 * @param color {@link Color} of the arrow
	 * @param wptFrom start point of track section 
	 * @param wptTo end point of track section 
	 */
	private void paintParallelArrow(Graphics2D g2d, Color color, Waypoint wptFrom, Waypoint wptTo) {
    	
		Point from = mapViewer.getMapPosition(wptFrom.getLat(), wptFrom.getLon(), false);
		Point to = mapViewer.getMapPosition(wptTo.getLat(), wptTo.getLon(), false);
		
		AffineTransform saveTransform = g2d.getTransform();
    	AffineTransform transform = new AffineTransform();	    	
    	transform.setToIdentity();
    	double angle = Math.atan2(to.y - from.y, to.x - from.x);
    	transform.translate(to.x, to.y);
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
	private void paintTrackArrow(Graphics2D g2d, Color color, Waypoint wptFrom, Waypoint wptTo) {
    	
		Point from = mapViewer.getMapPosition(wptFrom.getLat(), wptFrom.getLon(), false);
		Point to = mapViewer.getMapPosition(wptTo.getLat(), wptTo.getLon(), false);
		
		AffineTransform saveTransform = g2d.getTransform();
    	AffineTransform transform = new AffineTransform();	    	
    	transform.setToIdentity();
    	double angle = Math.atan2(to.y - from.y, to.x - from.x);
    	transform.translate(to.x, to.y);
    	transform.rotate((angle-Math.PI/2d));
    	transform.translate(0, -0.5f * from.distance(to));
    	g2d.setColor(color);
    	g2d.transform(transform);
    	g2d.fill(arrowHead);
    	g2d.setTransform(saveTransform);		
    }
    

	/**
     * paint progress label
     * @param wpt location of the label
     * @param distance
     */
    private void paintLabel(Graphics2D g2d, Waypoint wpt, Waypoint start, double distance) {
    		
			String timeString = "";
			Point point = mapViewer.getMapPosition(wpt.getLat(), wpt.getLon(), false);
			switch(progressType) {
			case ABSOLUTE:
					if (wpt.getTime() == null) {
						timeString = "--:--:--";
					} else {
						timeString = String.format("%tT", wpt.getTime());
					}
				break;
			case RELATIVE:
				long duration = start.getDuration(wpt);
				timeString = XTime.getDurationString(duration);
				break;
			default:
				break;  					
			}
			
			// String distString = String.format(distFormat, uc.dist(distance));
			String distString = uc.dist(distance, Const.FMT_DIST);
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D box = null;
			if (timeString.length() > distString.length()) {
				box = metrics.getStringBounds(timeString, g2d);						
			} else {
				box = metrics.getStringBounds(distString, g2d);	
			}				
			
			g2d.setColor(new Color(255, 255, 255, 155)); // R,G,B,Opacity
			g2d.fillRoundRect(
					point.x - 3, 
					point.y - (int) box.getHeight() - 3, 
					(int) box.getWidth()+6, 
					(int) (box.getHeight() + 4) * 2 - 1, 
					5, 5);
						
			g2d.setColor(Color.BLACK);
			g2d.drawString(timeString, point.x, point.y - 1);
			g2d.drawString(distString, point.x, point.y + (int) box.getHeight()); // TODO apply SoM
			minLabelDist = mapViewer.getMeterPerPixel() * ((int) box.getWidth() + 6) * multiplier;
			// setMinLabelDistance(point, (int) box.getWidth() + 6);
    }
    
    
    /**
     * paints distance & elapsed time along path
     * TODO consider start of new day at midnight
     * TODO paint labels for active track/segment only
     * @author rfuegen
     */
    private void doPaint(Graphics2D g2d, WaypointGroup wptGrp)
    {
    	double distance = 0;
    	double labelDist = 0;
    	double arrowDist = 0;

    	// Date startTime = wptGrp.getStart().getTime();    
    	
    	g2d.setColor(Color.BLACK);
    	Waypoint prev = wptGrp.getStart();    	
    	
    	double minArrowDist = minLabelDist / 2;
    	
    	// offset = minLabelDist / 2; // paint arrows halfway between labels
    
    	if (progressType != ProgressType.NONE) {
    		// always paint first label
    		paintLabel(g2d, wptGrp.getStart(), wptGrp.getStart(), distance);
    	}
    	
    	for (Waypoint curr: wptGrp.getWaypoints() ) {
 
   			// do not paint a label if distance to last label is less than (x)
   			if ((labelDist >= minLabelDist) && (progressType != ProgressType.NONE)) {	
   			    paintLabel(g2d, curr, wptGrp.getStart(), distance);
   			    labelDist = 0;
    		}
   			if ((arrowDist >= minArrowDist) && (arrowType != ArrowType.NONE)) {
   				// -- paintTrackArrow(g2d, wptGrp.getColor(), prev, curr);
   				switch(arrowType) {
   				case ONTRACK:
   					paintTrackArrow(g2d, Color.BLACK, prev, curr);
   					break;
   				case PARALLEL:
   					paintParallelArrow(g2d, wptGrp.getColor(), prev, curr);
   					break;
				default:
					break;
   				}
   				arrowDist = 0;
   				minArrowDist = minLabelDist * 2;  //
   			}
   			
    		double increment = curr.getDistance(prev);
    		if (!Double.isNaN(increment)) {
    		    distance += increment;
    		    labelDist += increment;
    		    arrowDist += increment;
    		}		 			
   			prev = curr;            
    	}
    	if (progressType != ProgressType.NONE) {
    		// paint label on endpoint
    		// TODO: don't paint second-to-last waypoint if to close
    		paintLabel(g2d, wptGrp.getEnd(), wptGrp.getStart(), distance);
    	}
    	// TODO prevent overlapping labels
     }

    /**
     * Main entry method
     * @param g2d
     * @param waypointGroup
     */
    public void paint(Graphics2D g2d, WaypointGroup waypointGroup) {
    	if (waypointGroup.getNumPts() > 1) {
	    	if ((progressType != ProgressType.NONE) || (arrowType != ArrowType.NONE)) {
	    		minLabelDist = mapViewer.getMeterPerPixel() * 50 * multiplier;
	    		doPaint(g2d, waypointGroup);
	    	}    	
    	}
    }
}
