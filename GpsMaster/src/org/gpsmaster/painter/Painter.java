package org.gpsmaster.painter;

import java.awt.Graphics2D;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.marker.Marker;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Base class for classes painting something on the map
 * 
 * @author rfu
 *
 */
public abstract class Painter implements Comparable<Painter> {

	protected int order = 10;
	protected boolean enabled = true;
	protected JMapViewer mapViewer = null;
	protected PaintCoordinator coordinator = null;
	

	/**
	 * 
	 */
	public Painter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * @param viewer
	 */
	public Painter(JMapViewer viewer) {
		mapViewer = viewer;
	}
	
	/**
	 * @return the mapViewer
	 */
	public JMapViewer getMapViewer() {
		return mapViewer;
	}

	/**
	 * @param mapViewer the mapViewer to set
	 */
	public void setMapViewer(JMapViewer mapViewer) {
		this.mapViewer = mapViewer;
	}

	/**
	 * @return the coordinator
	 */
	public PaintCoordinator getCoordinator() {
		return coordinator;
	}

	/**
	 * @param coordinator the coordinator to set
	 */
	public void setCoordinator(PaintCoordinator coordinator) {
		this.coordinator = coordinator;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * 
	 * @param g2d
	 * @param gpx
	 */
	public abstract void paint(Graphics2D g2d, GPXFile gpx);
	
	/**
	 * 
	 * @param g2d
	 * @param markerList
	 */
	public abstract void paint(Graphics2D g2d, List<Marker> markerList);
	
	@Override
	public int compareTo(Painter o) {
		// TODO Auto-generated method stub
		return (order - o.order);
	}
	
}
