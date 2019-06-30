package org.gpsmaster.painter;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.ProgressType;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;

import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.util.XTime;

public class ProgressPainter extends Painter {

	private double multiplier = 2.7f; // label distance = label width * multiplier
	private double labelDistance = 0f; // distance between two labels (in meters)

	private double distanceInterval = 0.0f;
	private long timeInterval = 0;
	
	private UnitConverter uc = null;
	
	private ProgressType progressType = ProgressType.NONE;

	/**
	 * 
	 * @param unitConverter
	 */
	public ProgressPainter(UnitConverter unitConverter) {
		super();
		uc = unitConverter;		
	}
	
	/**
	 * @return the progressType
	 */
	public ProgressType getProgressType() {
		return progressType;
	}

	/**
	 * @param progressType the progressType to set
	 */
	public void setProgressType(ProgressType progressType) {
		this.progressType = progressType;
	}

	/**
	 * @return the distanceInterval
	 */
	public double getDistanceInterval() {
		return distanceInterval;
	}

	/**
	 * paint progress labels at the given distance interval
	 * (unit is meters)
	 *  
	 * @param distanceInterval the distanceInterval to set
	 */
	public void setDistanceInterval(double distanceInterval) {
		this.distanceInterval = distanceInterval;
	}

	/**
	 * paint progress labels at the given time interval
	 * (unit is seconds)
	 * @return the timeInterval
	 */
	public long getTimeInterval() {
		return timeInterval;
	}

	/**
	 * @param timeInterval the timeInterval to set
	 */
	public void setTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
	}

	/**
	 * 
	 */
	@Override
	public void paint(Graphics2D g2d, GPXFile gpx) {
		
		// if no fixed label distance is set by user, use default value		
		if (distanceInterval != 0.0f) {
			labelDistance = distanceInterval;
		}
		
		if ((progressType != ProgressType.NONE) && enabled && gpx.isVisible()) {			
			for (Track track : gpx.getTracks()) {
				for (WaypointGroup grp : track.getTracksegs()) {
					if (grp.isVisible() && grp.getNumPts() > 2) {
						paintSegment(g2d, grp);
					}
				}
			}
			
			for (Route route : gpx.getRoutes()) {
				if (route.getPath().isVisible() && route.getNumPts() > 2) {
					paintSegment(g2d, route.getPath());
				}
			}			
		}
		
    	// leave a hint for next painter regarding label distances
    	coordinator.setDistanceInterval(labelDistance);
	}

	@Override
	public void paint(Graphics2D g2d, List<Marker> markerList) {
		// ignored
		
	}

	/**
	 * paint labels on a single {@link WaypointGroup}
	 * @param g2d
	 * @param grp
	 * TODO prevent overlapping labels
	 */
	private void paintSegment(Graphics2D g2d, WaypointGroup grp) {
	  	double distance = 0;
    	double labelDist = 0;
    	
    	g2d.setColor(Color.BLACK);
    	Waypoint prev = grp.getStart();    	    	
    	
    	// always paint first label
    	paintLabel(g2d, grp.getStart(), grp.getStart(), distance);
    	
    	for (Waypoint curr: grp.getWaypoints() ) {
 
   			// do not paint a label if distance to last label is less than (x)
   			if (labelDist >= labelDistance) {	
   			    paintLabel(g2d, curr, grp.getStart(), distance);
   			    labelDist = 0;
    		}
   			
    		double increment = curr.getDistance(prev);
    		if (!Double.isNaN(increment)) {
    		    distance += increment;
    		    labelDist += increment;
    		}		 			
   			prev = curr;            
    	}
    	// paint label on endpoint
    	// TODO: don't paint second-to-last waypoint if to close
    	paintLabel(g2d, grp.getEnd(), grp.getStart(), distance);
    	  
	}
	
	/**
     * paint progress label
     * @param wpt location of the label
     * @param distance
     */
    private void paintLabel(Graphics2D g2d, Waypoint wpt, Waypoint start, double distance) {
    		
			String timeString = "";
			if (wpt == null) {
				System.out.println("NULL wpt");
			}
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
			g2d.drawString(distString, point.x, point.y + (int) box.getHeight()); 

			if (distanceInterval == 0.0f) {
				labelDistance = mapViewer.getMeterPerPixel() * ((int) box.getWidth() + 6) * multiplier;
			}
			
			// TODO fill some data structure to let other painters know where
			// 		they can / can not paint their stuff
    }
    

	
}
