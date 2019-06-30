package org.gpsmaster.timeshift;

import java.util.Date;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import eu.fuegenstein.parameter.DurationParameter;
import eu.fuegenstein.parameter.RadioButtonParameter;

/**
 * 
 * @author rfu
 *
 */
public class ShiftTime extends TimeshiftAlgorithm {

	private DurationParameter durationParameter = new DurationParameter(60 * 60); // 1 hour
	private RadioButtonParameter direction = new RadioButtonParameter();
	
	private final String DIR_PAST = "past";
	private final String DIR_FUTURE = "future";
	private long undoDelta = 0;
	
	private String dateFormat = "HH:mm:ss"; 
	
	/**
	 * 
	 */
	public ShiftTime() {
		super();
		
		setName("Shift Time");
		setDescription("Shift trackpoint times forward or backward");
				
		durationParameter.setFormat(dateFormat);
		durationParameter.setName("Delta");
		durationParameter.setDescription("shift time by");
		params.add(durationParameter);
		
		direction.setName("Direction");
		direction.setDescription("Direction");
		direction.addValue(DIR_PAST);
		direction.addValue(DIR_FUTURE);
		direction.setValue(DIR_PAST); // set current value
		params.add(direction);
		
	}
	
	@Override
	public boolean isApplicable() {
		
		return (waypointGroups.size() > 0);
	}
	
	@Override
	public void apply() {
		long delta = durationParameter.getValue() * 1000;
		if (direction.getValueString().equals(DIR_PAST)) {
			delta = delta * -1;
		}
		
		undoDelta = delta * -1; // undo by shifting in opposite direction
		doShift(delta);
	}

	@Override
	public String getUndoDescription() {
	
		return getName() + " " + durationParameter.getValueString() + " " + direction.getValueString();
	}

	
	@Override
	public void undo() {

		doShift(undoDelta);
		
	}

	/**
	 * perform the actual time shift
	 * @param delta time difference in milliseconds
	 */
	private void doShift(long delta) {

		for (WaypointGroup grp : waypointGroups) {
			for (Waypoint wpt : grp.getWaypoints()) {
				Date oldDate = wpt.getTime();
    			if (oldDate != null) {
    				Date newDate = new Date(oldDate.getTime() + delta);
    				wpt.setTime(newDate);
    			}
			}
		}		
		
	}
}
