package org.gpsmaster.painter;

import java.awt.Graphics2D;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.marker.Marker;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Paint a label at fixed intervals
 * 
 * @author rfu
 *
 */
public class IntervalPainter extends Painter {

	public IntervalPainter(JMapViewer viewer) {
		super(viewer);		
	}

	private double interval = 1000; // in meters

	/**
	 * @return the interval
	 * (in meters)
	 */
	public double getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set (in meters)
	 * 
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}

	@Override
	public void paint(Graphics2D g2d, GPXFile gpx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paint(Graphics2D g2d, List<Marker> markerList) {
		// TODO Auto-generated method stub
		
	}
	
}
