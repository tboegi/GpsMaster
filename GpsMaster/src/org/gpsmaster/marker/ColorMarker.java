package org.gpsmaster.marker;

import java.awt.Color;

import org.gpsmaster.gpxpanel.Waypoint;


/**
 * A marker symbol with a color that
 * can be set at runtime. 
 * 
 * The marker symbol is defined in {@link org.gpsmaster.icons.marker.colormarker.png}
 * each black dot will be set to the specified color.
 * 
 * @author rfu
 *
 */
public class ColorMarker extends Marker {

	private Color color = Color.BLACK;
	
	/**
	 * 
	 * @param lat
	 * @param lon
	 */
	public ColorMarker(double lat, double lon) {
		super(lat, lon);		
	}

	/**
	 * 
	 * @param wpt
	 */
	public ColorMarker(Waypoint wpt) {
		super(wpt);		
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
		applyColor();
	}

	/**
	 * 
	 */
	private void applyColor() {
		// TBI
	}
	
}
