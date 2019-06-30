package org.gpsmaster.chart;

import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYAreaRenderer;

import eu.fuegenstein.unit.UnitConverter;

public class ElevationAxis extends ChartYAxis {

	/**
	 * 
	 * @param uc
	 */
	public ElevationAxis(UnitConverter uc) {
		super(uc);
		title = "Elevation";
		iconFile = "axis_elevation.png";
		valueAxis = new NumberAxis();
		valueAxis.setLabel(uc.getTargetSet().getElevationUnit().getSymbol());
		renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
		super.setDefaults();
	}
		
	/**
	 * 
	 */
	public double getValue(Waypoint wpt) {
		if (wpt == null) {
			return 0.0f;
		}
		double ele = wpt.getEle();
		if (uc != null) {
			ele = uc.ele(ele);
		}
		setMinMax(ele);
		return ele;
	}

	@Override
	public void reset() {
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
	}

}
