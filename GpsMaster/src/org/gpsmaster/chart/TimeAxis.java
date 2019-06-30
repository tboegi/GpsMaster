package org.gpsmaster.chart;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.axis.DateAxis;

import eu.fuegenstein.unit.UnitConverter;

/**
 * An axis with the absolute (local) time
 * @author rfu
 *
 */
public class TimeAxis extends ChartXAxis {

	/**
	 * Default Constructor
	 * @param uc
	 */
	public TimeAxis(UnitConverter uc) {
		super(uc);
		title = "Time";
		iconFile = "axis_time.png";
		valueAxis = new DateAxis();
		// TODO use timezone/daylight saving of first track date instead of current timezone 
		valueAxis.setLabel(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT, Locale.getDefault()));
		super.setDefaults();
	}
	
	@Override
	public double getValue(Waypoint wpt) {
		if (wpt.getTime() != null) {			
			return wpt.getTime().getTime();
		}
		return 0;
	}

	/**
	 * 
	 * @param wpt
	 * @return
	 */
	public Date getDateValue(Waypoint wpt) {
		return wpt.getTime();
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * test
	 */
/*
	public void plotOverTime() {
				
		double yMin = Double.MAX_VALUE;
		double yMax = 0;
		
		TimeSeriesCollection collection = new TimeSeriesCollection();
		
		long numPoints = 0;
		for (WaypointGroup group : dataset.getWaypointGroups()) {
			int idx = dataset.getWaypointGroups().indexOf(group);
			numPoints += group.getNumPts();
			TimeSeries timeSeries = new TimeSeries(idx);
			Waypoint prev = group.getStart();
			for (Waypoint wpt : group.getWaypoints()) {
				double ele = uc.dist(wpt.getEle(), UNIT.M);
				yMin = Math.min(yMin, ele);
				yMax = Math.max(yMax, ele);
				timeSeries.addOrUpdate(new Minute(wpt.getTime()), ele);
				
				prev = wpt;
			}
			collection.addSeries(timeSeries);			
		}
				
		JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, null, collection);
		// chart = ChartFactory.createXYAreaChart(null, "Distance", "Elevation", collection);
		chart.removeLegend();
		chart.addProgressListener(new ChartProgressListener() {
			
			@Override
			public void chartProgress(ChartProgressEvent e) {
		        XYPlot xyPlot = (XYPlot) chartPanel.getChart().getPlot();
		        System.out.println(e.getType()
		            + ": " + new Date((long) xyPlot.getDomainCrosshairValue()).toString()
		            + ", " + xyPlot.getRangeCrosshairValue());
			
			}
		});
		// modifications for smaller screen footprint 
		RectangleInsets inset = new RectangleInsets(0, 0, 0, 0);
		
		// chart.
		// setup plot
		XYPlot plot = chart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		// RectangleEdge edge = plot.getRangeAxisEdge();
		
		// if no gpxdata: show empty chart
        if (numPoints > 0) {
        	ValueAxis domainAxis = plot.getDomainAxis();
            domainAxis.setLowerMargin(0.0D);
            domainAxis.setUpperMargin(0.0D);
            domainAxis.setTickLabelInsets(inset);
            Font current = domainAxis.getTickLabelFont();
            domainAxis.setTickLabelFont(new Font(current.getFontName(), current.getStyle(), current.getSize() - 2));
            
            ValueAxis rangeAxis = plot.getRangeAxis();
            double offset = (yMax - yMin) / 10D;
        	rangeAxis.setRange(yMin - offset, yMax + offset);
        	rangeAxis.setTickLabelInsets(inset);
        	rangeAxis.setTickLabelFont(new Font(current.getFontName(), current.getStyle(), current.getSize() - 2));        	
        }
        chartPanel.setChartTitle("Elevation Chart");
		chartPanel.setChart(chart);
		chartPanel.setMaximumDrawHeight(99999);
		chartPanel.setMaximumDrawWidth(99999);
		chartPanel.setMinimumDrawHeight(1);
		chartPanel.setMinimumDrawWidth(1);
		
		// set colors
		// TODO receive notification when GPXObject.color changes
		for (WaypointGroup group : dataset.getWaypointGroups()) {
			int idx = dataset.getWaypointGroups().indexOf(group);
			plot.getRenderer().setSeriesPaint(idx, group.getColor()); 
		}
	}
*/
	
}
