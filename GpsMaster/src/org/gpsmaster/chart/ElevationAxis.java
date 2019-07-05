package org.gpsmaster.chart;
import org.gpsmaster.UnitConverter;
import org.gpsmaster.UnitConverter.UNIT;
import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYAreaRenderer;

public class ElevationAxis extends ChartYAxis {


	/**
	 *
	 * @param label
	 */
	public ElevationAxis(UnitConverter uc) {
		super(uc);
		title = "Elevation";
		iconFile = "axis_elevation.png";
		valueAxis = new NumberAxis();
		valueAxis.setLabel(uc.getUnit(UNIT.M));
		renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
		super.setDefaults();
	}


	public double getValue(Waypoint wpt) {
		if (wpt == null) {
			return 0.0f;
		}
		double ele = wpt.getEle();
		if (uc != null) {
			ele = uc.dist(ele, UNIT.M);
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
