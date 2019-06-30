package org.gpsmaster.chart;

import javax.swing.ImageIcon;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.ui.RectangleInsets;

import eu.fuegenstein.unit.UnitConverter;

/**
 * Base class representing a chart axis, containing
 * GpsMaster- and JFreeChart specific components.
 * 
 * @author rfu
 *
 */
public abstract class ChartAxis {

	protected UnitConverter uc = null;
	protected ValueAxis valueAxis = null;
	protected String title = null;
	
	public abstract double getValue(Waypoint wpt);	
	public abstract void reset();
	
	protected ImageIcon icon = null;
	protected String iconFile = null;	
	
	/**
	 * Default constructor
	 */	
	public ChartAxis(UnitConverter uc) {
		this.uc = uc;
	}
	
	/**
	 * 
	 * @return
	 */
	public UnitConverter getUnit() {
		return uc;
	}
	
	/**
	 * 
	 * @param uc
	 */
	public void setUnit(UnitConverter uc) {
		this.uc = uc;
		// TODO refresh needed? unit symbol on axis?
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 
	 * @return
	 */
	protected String getLabel() {
		return valueAxis.getLabel();
	}
	
	/**
	 * 
	 * @param label
	 */
	protected void setLabel(String label) {
		valueAxis.setLabel(label);
	}
	
	/**
	 * 
	 * @return
	 */
	public ValueAxis getValueAxis() {
		return valueAxis;
	}
	
	/**
	 * Get the icon that represents this axis.
	 * @return
	 */
	public ImageIcon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_CHART + iconFile));
		}
		return icon;
	}

	/**
	 * Set the icon that is represents this axis. 
	 * displayed in the axis selector combo box.
	 * @param icon
	 */
	public void setIcon(ImageIcon icon) {
		
	}
	
	/**
	 * set defaults specific for all subclasses/axes
	 */
	protected void setDefaults() {
		valueAxis.setLabelInsets(new RectangleInsets(5, 5, 5, 5));
                
	}
}
