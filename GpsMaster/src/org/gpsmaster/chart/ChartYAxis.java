package org.gpsmaster.chart;

import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.ui.RectangleInsets;

import eu.fuegenstein.unit.UnitConverter;

/**
 * Base class for classes representing Y-Axes
 * additionally provides Min-, Max values and range padding
 * 
 * @author rfu
 *
 */
public abstract class ChartYAxis extends ChartAxis {

	/**
	 * 
	 * @param uc
	 */
	public ChartYAxis(UnitConverter uc) {
		super(uc);
	}

	protected double min = Double.MAX_VALUE;
	protected double max = Double.MIN_VALUE;
	protected double padding = 0;
	protected AbstractRenderer renderer = null;	

	/**
	 * get minimum value of all waypoints processed so far
	 * @return
	 */
	protected double getMin() {
		return min;
	}
	
	/**
	 * get maximum value of all waypoints processed so far
	 * @return
	 */
	protected double getMax() {
		return max;
	}
	
	/**
	 * 
	 */
	private void setPaddingRange() {
		if ((padding > 0) && (min < max)) {
			double offset = (max - min) * padding;
			valueAxis.setRange(min - offset, max + offset);
		}		
	}
	
	/**
	 * 
	 * @param value
	 */
	protected void setMinMax(double value) {
		if (value < min) {
			min = value;
			setPaddingRange();
		} else if (value > max) {			
			max = value;
			setPaddingRange();
		}		
	}
	
	/**
	 * set extra space above and below the axis data plot.
	 * percentage of actual value range (delta: max - min) 
	 * @param padding padding factor, 0.0f .. 1.0f
	 */
	public void setPadding(double padding) {
		this.padding = padding;
	}
	
	/**
	 * get the preferred renderer for this domain
	 * @return Subclass of {@link AbstractRenderer} or NULL
	 */
	public AbstractRenderer getPreferredRenderer() {
		return renderer;
	}
	
	/**
	 * set defaults specific for all Y-Axes
	 */
	protected void setDefaults() {
		super.setDefaults();
        valueAxis.setLabelLocation(AxisLabelLocation.HIGH_END);
        // valueAxis.setLabelPosition(AxisLabelPosition.INSIDE);
        valueAxis.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0)); // smaller onscreen footprint
        // valueAxis.setLabelAngle(Math.PI / 2.0f); // 90°        
	}

	
}
