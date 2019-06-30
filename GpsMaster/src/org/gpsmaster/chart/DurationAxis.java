package org.gpsmaster.chart;

import java.util.Locale;
import java.util.TimeZone;

import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.axis.DateAxis;

import eu.fuegenstein.unit.UnitConverter;

public class DurationAxis extends ChartXAxis {

	private Waypoint first = null;
	
	/**
	 * 
	 * @param uc
	 */
	public DurationAxis(UnitConverter uc) {
		super(uc);
		title = "Duration";
		iconFile = "axis_duration.png";
		valueAxis = new DateAxis(title, TimeZone.getTimeZone("UTC"), Locale.getDefault());
		valueAxis.setLabel("hrs");
		super.setDefaults();
		
	}

	@Override
	public double getValue(Waypoint wpt) {
		long duration = 0;
		if ((wpt != null) && (wpt.getTime() != null)) { 
			if (first == null) {
				first = wpt;
			}
			duration = wpt.getTime().getTime() - first.getTime().getTime();
		}
		return duration;
	}

	@Override
	public void reset() {
		first = null;
	}
}
