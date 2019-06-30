package org.gpsmaster.chart;

import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYAreaRenderer;

import eu.fuegenstein.unit.UnitConverter;

public class SpeedAxis extends ChartYAxis {

	private int windowSize = 5;	
	private List<Waypoint> window = new ArrayList<Waypoint>();
		
	/**
	 * 
	 * @param label
	 */
	public SpeedAxis(UnitConverter uc) {
		super(uc);
		title = "Speed";
		iconFile = "axis_speed.png";
		valueAxis = new NumberAxis();
		valueAxis.setLabel(uc.getTargetSet().getSpeedUnit().getSymbol());
		renderer = new XYAreaRenderer(XYAreaRenderer.LINES);
		super.setDefaults();
	}
	
	@Override
	public double getValue(Waypoint wpt) {
		if ((wpt == null) || (wpt.getTime() == null)) {
			return 0;
		}
		window.add(wpt);
		if (window.size() < windowSize) {
			return 0;
		}
		// TODO implement something more performing & intelligent
		double speed = 0;
		double distance = 0;
		for (int i = 1; i < windowSize; i++) {
			distance += window.get(i).getDistance(window.get(i - 1));			
		}
		double duration = (window.get(windowSize - 1).getTime().getTime() - window.get(0).getTime().getTime()) / 1000D; // seconds
		speed = uc.speed(distance / duration);
		window.remove(0);
		return speed;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
