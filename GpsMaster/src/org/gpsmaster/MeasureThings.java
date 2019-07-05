package org.gpsmaster;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXPanel;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.markers.Marker;
import org.gpsmaster.markers.MeasureMarker;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.util.XTime;

/**
 * The Measure Of All Things
 *
 * @author rfu
 *
 * TODO future versions: display measure results in widget or table
 * for distances between multiple measureMarkers (painted in different colors)
 * TODO if chart is open, set measureMarkers by clicking on chart
 * (also show marker lines on chart)
 */
public class MeasureThings {

	// Listener to receive clicks on mapMarkers from GPXPanel
	private PropertyChangeListener clickListener = null;
	// Listener to receive active gpxobject updates
	private PropertyChangeListener changeListener = null;
	private List<WaypointGroup> groups = null;
	private List<MeasurePoint> points = new ArrayList<MeasurePoint>();

	private List<Marker> mapMarkers = null;
	private MessageCenter msg = null;
	private MessagePanel msgMeasure = null;
	private UnitConverter uc = null;

	private final String emptyText = "select two Trackpoins";
	private String distFormat = "%.2f";
	private String speedFormat = "%.2f";

	/**
	 * Constructor
	 * @param msg
	 * @param mapMarkers list of {@link Markers} from {@link GPXPanel}
	 */
	public MeasureThings(MessageCenter msg, UnitConverter converter, List<Marker> markers) {

		if ((msg == null) || (markers == null)) {
			throw new NullPointerException();
		}

		this.msg = msg;
		this.uc = converter;
		this.mapMarkers = markers;

		// Listener for clicks on Trackpoints & Markers
        clickListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("1click")) {
					Object o = evt.getNewValue();
					if (o instanceof MeasureMarker) {
						handleClick(null, (MeasureMarker) o);
					} else if (o instanceof Waypoint) {
						handleClick((Waypoint) o, null);
					}
				}
			}
		};

		// Listener for active GPX Objects
		changeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(GpsMaster.active.PCE_ACTIVEGPX)) {
					setActiveGpxObject();
				}
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);
		msgMeasure = msg.infoOn(emptyText);
		setActiveGpxObject();
	}

	/**
	 * set initial {@link WaypointGroup}. All others
	 * will be set via PropertyChange events
	 * @param grp Current {@link WaypointGroup}
	 */
	public void setActiveGpxObject() {
		msgMeasure.setText(emptyText);
		clearMarkers();
		points.clear();
		if (GpsMaster.active.getGpxObject() == null) {
			groups = null;
		} else {
			groups = GpsMaster.active.getGroups(ActiveGpxObjects.SEG_ROUTE_TRACK);
			if (groups.size() == 0) {
				msgMeasure.setText("Please select a track, route or segment");
			}
		}
	}

	public UnitConverter getUnitConverter() {
		return uc;
	}

	public void setUnitConverter(UnitConverter uc) {
		this.uc = uc;
	}

	/**
	 *
	 * @return
	 */
	public PropertyChangeListener getPropertyChangeListener() {
		return clickListener;
	}

	/**
	 * to be called before destruction
	 */
	public void clear() {
		clearMarkers();
		points.clear();
		msg.infoOff(msgMeasure);
	}

	/**
	 * Remove measure markers from {@link GPXPanel}
	 */
	private void clearMarkers() {
		for(MeasurePoint point : points) {
			mapMarkers.remove(point.getMarker());
		}
	}


    /**
     * handles the click on either a {@link Waypoint} or a {@link MeasureMarker}
     * for measuring purposes. Only one of the arguments may be specified, the
     * other has to be null
     *
     * @param wpt {@link Waypoint} selected (clicked) on the {@link GPXPanel
     * @param marker {@link MeasureMarker} selected (clicked) on the {@link GPXPanel}
     */
    private void handleClick(Waypoint clickedWpt, MeasureMarker clickedMarker) {
    	if ((groups == null) || (groups.size() == 0)) {
    		return;
    	}

    	if (clickedMarker != null) {
    		// existing marker was clicked. remove it.
    		for (MeasurePoint point : points) {
    			if (point.getMarker().equals(clickedMarker)) {
    				points.remove(point);
    				mapMarkers.remove(clickedMarker);
    				break;
    			}
    		}
    	}

    	if (clickedWpt != null) { // a trackpoint was clicked
    		boolean addnew = true;
    		// if we know it - remove it
    		for (MeasurePoint point : points) {
    			if (point.getWaypoint().equals(clickedWpt)) {
    				mapMarkers.remove(point.getMarker());
    				points.remove(point);
    				addnew = false;
    				break;
    			}
    		}

    		if (addnew && points.size() < 2) { // tmp
    			MeasurePoint point = new MeasurePoint(clickedWpt, new MeasureMarker(clickedWpt));
    			try {
					point.setIndexesFrom(groups);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			points.add(point);
    			mapMarkers.add(point.getMarker());
    		}
    	}
    	GpsMaster.active.repaintMap();

    	// do the calculation
    	if (points.size() == 2) {
    		doMeasure();
    	} else {
    		msgMeasure.setText(emptyText);
    	}
    }

    /*
     * Calculate distance & related data for/between set MeasurePoints
     */
    private void doMeasure() {

    	Collections.sort(points);
    	if (points.size() >= 2) {
    		double distance = 0.0f;
	    	MeasurePoint mp1 = points.get(0);
	    	for (int i = 1; i < points.size(); i++) {
	    		MeasurePoint mp2 = points.get(i);
	    		int gIdx = mp1.getGroupIdx(); // start group
	    		int gEnd = mp2.getGroupIdx(); // start group
	    		int idx = mp1.getPointIdx(); // index of start point in gStart
	    		Waypoint wptEnd = groups.get(gEnd).getWaypoints().get(mp2.getPointIdx());
	    		WaypointGroup g = groups.get(gIdx);
	    		Waypoint wptStart = g.getWaypoints().get(idx);
	    		Waypoint prev = wptStart;
	    		Waypoint curr = null;
	    		do {
	    			idx++;
	    			curr = g.getWaypoints().get(idx);
	    			distance += curr.getDistance(prev);
	    			prev = curr;
	    			if ((idx == g.getWaypoints().size() - 1) && (gIdx < gEnd)) {
	    				gIdx++;
	    				g = groups.get(gIdx);
	    				idx = -1;
	    			}
	    		} while (curr.equals(wptEnd) == false);

	    		// show result
	        	String out = String.format("distance "+uc.dist(distance, distFormat)
	        				+", direct " + 	uc.dist(wptStart.getDistance(wptEnd), distFormat));

    			// duration
    			long duration = Math.abs(wptStart.getDuration(wptEnd));
    			if (duration > 0) {
	    			out += ", duration " + XTime.getDurationString(duration);
	    			// avg speed
	            	out += ", avg speed " + uc.speed(distance / duration, speedFormat);
    			}
    			msgMeasure.setText(out);
	    		mp1 = mp2;
	    	}
    	}
    }
}