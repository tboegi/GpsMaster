package org.gpsmaster.chart;

import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * 
 * @author rfu
 *
 */
public class ChartDataset {
	
	private List<WaypointGroup> groups = null;
	private XYSeriesCollection collection = null;
	private ChartXAxis xAxis = null;
	private ChartYAxis yAxis = null;
	
	/**
	 * Default constructor
	 */
	public ChartDataset() {
		collection = new XYSeriesCollection();
		groups = new ArrayList<WaypointGroup>();
	}
	
	public ChartXAxis getXAxis() {
		return xAxis;
	}


	public void setXAxis(ChartXAxis xAxis) {
		this.xAxis = xAxis;
	}


	public ChartYAxis getYAxis() {
		return yAxis;
	}


	public void setYAxis(ChartYAxis yAxis) {
		this.yAxis = yAxis;
	}

	public ValueAxis getDomainAxis() {
		return xAxis.getValueAxis();
	}

	public ValueAxis getRangeAxis() {
		return yAxis.getValueAxis();
	}

	/**
	 * 
	 * @param group
	 */
	public void addWaypointGroup(WaypointGroup group) {
		groups.add(group);		
	}
	
	/**
	 * 
	 * @param groups
	 */
	public void addWaypointGroups(List<WaypointGroup> groups) {
		this.groups.addAll(groups);
	}

	/**
	 * 
	 * @param group
	 */
	public void removeWaypointGroup(WaypointGroup group) {
		if (groups.contains(group)){
			groups.remove(group);
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<WaypointGroup> getWaypointGroups() {
		return groups;
	}
	
	/**
	 * find the {@link WaypointGroup} containing the given waypoint
	 * @param wpt
	 * @return {@link WaypointGroup} containing wpt, NULL if not found
	 */
	public WaypointGroup findGroup(Waypoint wpt) {
		for (WaypointGroup grp : groups) {
			if (grp.getWaypoints().contains(wpt)) {
				return grp;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public XYSeriesCollection getCollection() {
			
		fillDataset();
		
		return collection;
	}
	
	/**
	 * Find the waypoint that relates to the given value on the X-Axis
	 * @param xValue
	 * @return
	 */
	public Waypoint getWaypointForX(double xValue) {
		Waypoint wpt = null;
		int wptIdx = -1;
		for (int i = 0; i < groups.size(); i++) {
			int[] idx = DatasetUtilities.findItemIndicesForX(collection, i, xValue);
			if (idx[0] != -1) {
				wptIdx = idx[0];
				return groups.get(i).getWaypoints().get(wptIdx);
			}
		}
		return wpt;
	}
	/**
	 * 
	 */
	public void clear() {
		collection.removeAllSeries();
		groups.clear();
	}
	
	/**
	 * 
	 */
	public void refresh() {
		collection.removeAllSeries();
		fillDataset();
	}
	
	/**
	 * 
	 */
	private void fillDataset() {
 
		if (xAxis == null) {
			throw new NullPointerException("X-Axis not set, use setXAxis()");
		}

		if (yAxis == null) {
			throw new NullPointerException("Y-Axis not set, use setYAxis()");
		}
		
		collection.removeAllSeries();
		xAxis.reset();
		yAxis.reset();
		
		for (WaypointGroup group : groups) {
			int idx = groups.indexOf(group);
// 			numPoints += group.getNumPts();
			XYSeries xySeries = new XYSeries(idx);
			// set plot.color!!
			for (Waypoint wpt : group.getWaypoints()) {
				double xValue = xAxis.getValue(wpt);
				double yValue = yAxis.getValue(wpt);
				xySeries.add(xValue, yValue);								
			}
			collection.addSeries(xySeries);	
		}
		
	}

	public double lookupXValue(Waypoint wpt) {
		return xAxis.lookupValue(wpt);
	}
}
