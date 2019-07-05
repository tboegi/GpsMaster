package org.gpsmaster.chart;

import java.util.ArrayList;

import org.gpsmaster.gpxpanel.WaypointGroup;
import org.jfree.data.xy.XYSeries;

/**
 * Class representing a {@link XYDataset} by gathering of
 * values from provided {@link WaypointGroup}s according to
 * the type of chart requested by the user
 *
 * @author rfu
 *
 */
public class ChartDataSet extends XYSeries {

	/**
	 *
	 */
	private static final long serialVersionUID = -4024683002474831475L;

	public ChartDataSet(Comparable key) {
		super(key);
		// TODO Auto-generated constructor stub
	}

	private ArrayList<WaypointGroup> trackpointGroups = new ArrayList<WaypointGroup>();

	/**
	 *
	 * @param groups
	 */
	public void setWaypointGroups(ArrayList<WaypointGroup> groups) {
		trackpointGroups = groups;
	}

	/**
	 *
	 * @param group
	 */
	public void addWaypointGroup(WaypointGroup group) {
		trackpointGroups.add(group);
	}
}
