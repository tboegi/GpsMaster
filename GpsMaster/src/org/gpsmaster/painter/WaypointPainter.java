package org.gpsmaster.painter;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Class for painting GPXFile Waypoints and Markers on the map.
 * 
 * @author rfu
 *
 */
public class WaypointPainter extends Painter {

	private final int ORDER = 5;
	
	/**
	 * 
	 */
	public WaypointPainter() {
		order = ORDER;
	}
	
	/**
	 * 
	 * @param viewer
	 */
	public WaypointPainter(JMapViewer viewer) {
		super(viewer);
		order = ORDER;
		
	}

	/**
	 * paint all waypoints in the {@link GPXFile}s Waypoint group
	 */
	@Override
	public void paint(Graphics2D g2d, GPXFile gpx) {
		WaypointGroup grp = gpx.getWaypointGroup();
		if (gpx.isVisible() && grp.isVisible()) {        	         
			for (Waypoint wpt : grp.getWaypoints()) {            	
				paintMarker(g2d, (Marker) wpt);
			}
		}	
	}

	@Override
	public void paint(Graphics2D g2d, List<Marker> markerList) {
		for (Marker marker : markerList) {
			paintMarker(g2d, marker);
		}
	}

    /**
     * paint a single {@link Marker} on the map
     * @param g2d
     * @param marker
     */
    private void paintMarker(Graphics2D g2d, Marker marker) {
        Point point = mapViewer.getMapPosition(marker.getLat(), marker.getLon(), false);      
        g2d.drawOval(point.x - 2, point.y - 2, 4, 4); // TODO: draw circle only if enabled in tree
        marker.paint(g2d, point);
    }
}
