package org.gpsmaster.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.List;

import org.gpsmaster.ActiveGpxObjects;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 *
 * @author rfu
 *
 */
public class TrackPainter extends Painter {

	private float trackLineWidth = 3;
	private boolean paintBorder = true;

	private final int ORDER = 0;

	public static ActiveGpxObjects active = null;
	/**
	 *
	 */
	public TrackPainter() {
		super();
		order = ORDER;
	}
	/**
	 *
	 * @param viewer
	 */
	public TrackPainter(JMapViewer viewer) {
		super(viewer);
		order = ORDER;
	}


	/**
	 * @return the trackLineWidth
	 */
	public float getLineWidth() {
		return trackLineWidth;
	}

	/**
	 * @param trackLineWidth the trackLineWidth to set
	 */
	public void setLineWidth(float trackLineWidth) {
		this.trackLineWidth = trackLineWidth;
	}

	@Override
	public void paint(Graphics2D g2d, GPXFile gpx) {

		final Stroke bgStroke = new BasicStroke(trackLineWidth + 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		final Stroke trackStroke = new BasicStroke(trackLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		final float dash1[] = {trackLineWidth * 3.0f, trackLineWidth};
        final Stroke routeStroke = new BasicStroke(trackLineWidth + 2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f);

        Stroke saveStroke = g2d.getStroke();

		// paint route & tracks
		if (gpx.isVisible() && enabled) {
			for (Route route : gpx.getRoutes()) {
			   if (route.isVisible()) {
                   paintPath(g2d, route.getPath(), routeStroke, null);
               }
           }
           for (Track track : gpx.getTracks()) {
               if (track.isVisible()) {
                   for (WaypointGroup path : track.getTracksegs()) {
                       if (path.isVisible()) {
                           paintPath(g2d, path, trackStroke, bgStroke);
                       }
                   }
               }
           }

           // highlight points on tracks/routes
           if (gpx.isTrackPtsVisible()) {
               for (Route route : gpx.getRoutes()) {
                   if (route.isTrackPtsVisible() && route.isVisible()) {
                       paintPathpoints(g2d, route.getPath());
                   }
               }
               for (Track track : gpx.getTracks()) {
                   if (track.isTrackPtsVisible() && track.isVisible()) {
                       for (WaypointGroup wptGrp : track.getTracksegs()) {
                           paintPathpoints(g2d, wptGrp);
                       }
                   }
               }
           }
		}
		g2d.setStroke(saveStroke);

	}

	@Override
	public void paint(Graphics2D g2d, List<Marker> markerList) {
		// we don't paint marker.

	}

    /**
     * Paints a single path contained in a {@link WaypointGroup}.
     */
	/**
	 *
	 * @param g2d Canvas to paint on
	 * @param waypointPath {@link WaypointGroup} to paint
	 * @param stroke Stroke for path
	 * @param bgStroke Stroke for path background
	 */
    private void paintPath(Graphics2D g2d, WaypointGroup waypointPath, Stroke stroke, Stroke bgStroke) {
    	// TODO don't check this here, but in caller
        Point maxXY = mapViewer.getMapPosition(waypointPath.getMinLat(), waypointPath.getMaxLon(), false);
        Point minXY = mapViewer.getMapPosition(waypointPath.getMaxLat(), waypointPath.getMinLon(), false);
        if (maxXY.x < 0 || maxXY.y < 0 || minXY.x > mapViewer.getWidth() || minXY.y > mapViewer.getHeight()) {
            return; // don't paint paths that are completely off screen
        }

        g2d.setColor(waypointPath.getColor());
        if (waypointPath.getNumPts() >= 2) {
            List<Waypoint> waypoints = waypointPath.getWaypoints();
            GeneralPath path;
            Waypoint rtept;
            Point point;

            path = new GeneralPath();
            rtept = waypointPath.getStart();
            point = mapViewer.getMapPosition(rtept.getLat(), rtept.getLon(), false);
            path.moveTo(point.x, point.y);
            Point prev = point;
            for (int i = 1; i < waypoints.size(); i++) {
                rtept = waypoints.get(i);
                point = mapViewer.getMapPosition(rtept.getLat(), rtept.getLon(), false);
                if (point.equals(prev) == false) { // performance improvement?
                	path.lineTo(point.x, point.y);
                }
                prev = point;
            }

            // don't paint track background (border) when segment color is transparent
            if (paintBorder && waypointPath.getColor().getAlpha() == 255 && bgStroke != null) {
                // draw black border
            	g2d.setStroke(bgStroke);
                g2d.setColor(Color.BLACK);
            	g2d.draw(path);
            }

            // draw colored route
            g2d.setStroke(stroke);
            g2d.setColor(waypointPath.getColor());
            g2d.draw(path);

        }
    }

    /**
     * Paints the pathpoints for a path in {@link WaypointGroup}.
     */
    private void paintPathpoints(Graphics2D g2d, WaypointGroup wptGrp) {
        
        if (wptGrp.isVisible() && wptGrp.isTrackPtsVisible()) {
            Stroke saveStroke = g2d.getStroke();
        	g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        	g2d.setColor(Color.DARK_GRAY);

            for (Waypoint wpt : wptGrp.getWaypoints()) {
                Point point = mapViewer.getMapPosition(wpt.getLat(), wpt.getLon(), false);
            	if (wpt == active.getRoutepoint()) {g2d.setColor(Color.BLUE);}
            	else {g2d.setColor(Color.DARK_GRAY);}
                if (mapViewer.getBounds().contains(point)) {
                	g2d.drawOval(point.x-2, point.y-2, 4, 4);
                }
            }
            g2d.setStroke(saveStroke);
        }
        // System.out.println(String.format("%d %d %d %d", getBounds().x, getBounds().y, getBounds().width, getBounds().y));
    }


}
