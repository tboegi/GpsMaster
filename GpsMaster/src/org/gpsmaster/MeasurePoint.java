package org.gpsmaster;

import java.awt.Color;
import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.MeasureMarker;

/**
 * Class combining the association of {@link Waypoint} to {@link MeasureMarker}
 * and index positions for easier processing in {@link MeasureThings}. Also
 * provides a smaller marker icon for the distance table. Allows to set the
 * color of both markers.
 * 
 * @author rfu
 *
 */
public class MeasurePoint implements Comparable<MeasurePoint> {

	private Waypoint waypoint = null;
	private MeasureMarker marker = null;
	private int groupIdx = -1;
	private int pointIdx = -1;
	private Color color = Color.DARK_GRAY;
	
	/**
	 * Default constructor
	 */
	public MeasurePoint() {
		
	}
	
	/**
	 * Constructor
	 * @param wpt
	 */
	public MeasurePoint(Waypoint wpt, MeasureMarker marker) {
		waypoint = wpt;
		this.marker = marker;
	}
	
	public Waypoint getWaypoint() {
		return waypoint;
	}
	
	public void setWaypoint(Waypoint wpt) {
		waypoint = wpt;
	}

	public MeasureMarker getMarker() {
		return marker;
	}

	public void setMarker(MeasureMarker marker) {
		this.marker = marker;
	}

	/**
	 * Get the index of the group containing the {@link Waypoint} 
	 * @return zero based index or -1 if in no group
	 */
	public int getGroupIdx() {
		return groupIdx;
	}

	/**
	 * Set the index of the group containing the {@link Waypoint}
	 * @param groupIdx zero based index
	 */
	public void setGroupIdx(int groupIdx) {
		this.groupIdx = groupIdx;
	}

	/**
	 * Get the position of the {@link Waypoint} within the group 
	 * @return zero based index or -1 if in no group
	 */
	public int getPointIdx() {
		return pointIdx;
	}

	public void setPointIdx(int pointIdx) {
		this.pointIdx = pointIdx;
	}

	/**
	 * Set pointIdx
	 * @param groups
	 * @throws Exception 
	 */
	public void setIndexesFrom(List<WaypointGroup> groups) throws Exception {
		groupIdx = -1;
		pointIdx = -1;
		for (WaypointGroup group : groups) {
			if (group.getWaypoints().contains(waypoint)) {
				groupIdx = groups.indexOf(group);
				pointIdx = group.getWaypoints().indexOf(waypoint);
			}
		}
		if (groupIdx == -1) {
			throw new Exception(); // find more detailed exception
		}
	}
	
	@Override
	public int compareTo(MeasurePoint o) {
		if (groupIdx < o.groupIdx) {
			return -1;
		}
		if (groupIdx > o.groupIdx) {
			return 1;
		}
		// in the same group
		if (pointIdx < o.pointIdx) {
			return -1;
		}
		if (pointIdx > o.pointIdx) {
			return 1;
		}
		
		// should not happen:
		return 0;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

}
