package org.gpsmaster.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.List;

import javax.swing.ImageIcon;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Class for painting the icons at the start and end of each track segment.
 * 
 * @author rfu
 *
 */
public class StartEndPainter extends Painter {

	private Image imgPathStart;
    private Image imgPathEnd;

    private final int ORDER = 5;
    
	/**
	 * 
	 * @param viewer
	 */
    public StartEndPainter() {
		super();		
		init();
	}

    /**
     * 
     * @param viewer
     */
    public StartEndPainter(JMapViewer viewer) {
    	super(viewer);
    	init();
    }
    
	@Override
	public void paint(Graphics2D g2d, GPXFile gpx) {
		// paint route & tracks
		if (gpx.isVisible() && enabled) {
			for (Route route : gpx.getRoutes()) {
			   if (route.isVisible()) {                   
                   paintStartAndEnd(g2d, route.getPath());                   
               }
           }
           for (Track track : gpx.getTracks()) {
               if (track.isVisible()) {
                   for (WaypointGroup path : track.getTracksegs()) {
                       if (path.isVisible()) { 
                           paintStartAndEnd(g2d, path);                           
                       }                           	                          
                   }
               }
           }
		}
	}

	@Override
	public void paint(Graphics2D g2d, List<Marker> markerList) {
		// we don't paint marker
	}

	/**
	 * 
	 */
	private void init() {
		order = ORDER;
        imgPathStart = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_MARKER + "path-start.png")).getImage();
        imgPathEnd = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_MARKER + "path-end.png")).getImage();

	}
	
    /**
     * Paints the start/end markers of a {@link Route} or {@link Track}.
     */
    private void paintStartAndEnd(Graphics2D g2d, WaypointGroup waypointPath) {
        if (waypointPath.getNumPts() >= 2) {
            Waypoint rteptEnd = waypointPath.getEnd(); 
            Point end = mapViewer.getMapPosition(rteptEnd.getLat(), rteptEnd.getLon(), false);
            g2d.setColor(Color.BLACK);
            g2d.drawImage(imgPathEnd, end.x - 9, end.y - 28, null);
        }
        
        if (waypointPath.getNumPts() >= 1) {
            Waypoint rteptStart = waypointPath.getStart(); 
            Point start = mapViewer.getMapPosition(rteptStart.getLat(), rteptStart.getLon(), false);
            g2d.setColor(Color.BLACK);
            g2d.drawImage(imgPathStart, start.x - 9, start.y - 28, null);
        }
    }

}
