package org.gpsmaster.widget;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.gpsmaster.MeasurePoint;

public class DistanceTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6500837718518411378L;

	private List<MeasurePoint> points = new ArrayList<MeasurePoint>();
	
	/**
	 * @return the points
	 */
	public List<MeasurePoint> getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(List<MeasurePoint> points) {
		this.points = points;
	}

	/**
	 * create table content from MeasurePoints list
	 */
	private void update() {

		if (points.size() < 2) {
			return;
		}
		
		for (int i = 0; i < points.size() - 1; i++) {
			MeasurePoint curr = points.get(i);
			MeasurePoint next = points.get(i + 1);
		}
	}
}
