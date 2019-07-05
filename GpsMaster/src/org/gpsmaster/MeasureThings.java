package org.gpsmaster;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gpsmaster.UnitConverter.UNIT;
import org.gpsmaster.gpxpanel.GPXPanel;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.markers.Marker;
import org.gpsmaster.markers.MeasureMarker;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;

/**
 * The Measure Of All Things
 *
 * @author rfu
 *
 * TODO future versions: display measure results in widget or table
 * for distances between multiple measure mapMarkers (painted in different colors)
 *
 */
public class MeasureThings {

	// Listener to receive clicks on mapMarkers from GPXPanel
	private PropertyChangeListener propertyListener = null;
	private WaypointGroup activeWptGrp = null;
	private TreeMap<Waypoint, MeasureMarker> current = new TreeMap<Waypoint, MeasureMarker>();
	private List<Marker> mapMarkers = null;
	private MessageCenter msg = null;
	private MessagePanel msgMeasure = null;
	private UnitConverter uc = null;

	/**
	 * Constructor
	 * @param msg
	 * @param mapMarkers list of {@link Markers} from {@link GPXPanel}
	 */
	public MeasureThings(MessageCenter msg, List<Marker> markers) {

		if ((msg == null) || (markers == null)) {
			throw new NullPointerException();
		}

		this.msg = msg;
		this.mapMarkers = markers;
		uc = new UnitConverter();

        propertyListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals("1click")) {
					Object o = e.getNewValue();
					if (o instanceof MeasureMarker) {
						handleMeasure(null, (MeasureMarker) o);
					} else if (o instanceof Waypoint) {
						handleMeasure((Waypoint) o, null);
					}
				} else if (e.getPropertyName().equals("activeWptGrp")) {
					activeWptGrp = (WaypointGroup) e.getNewValue();
				}
			}
		};

		msgMeasure = msg.infoOn("...");
	}

	/**
	 * set initial {@link WaypointGroup}. All others
	 * will be set via PropertyChange events
	 * @param grp Current {@link WaypointGroup}
	 */
	public void setWaypointGroup(WaypointGroup grp) {
		activeWptGrp = grp;
	}

	public UnitConverter getUnit() {
		return uc;
	}

	public void setUnit(UnitConverter uc) {
		this.uc = uc;
	}

	/**
	 *
	 * @return
	 */
	public PropertyChangeListener getPropertyChangeListener() {
		return propertyListener;
	}

	/**
	 * to be called before destruction
	 */
	public void clear() {
		// remove markers from mapPanel
		for(Map.Entry<Waypoint, MeasureMarker> entry : current.entrySet()) {
			mapMarkers.remove(entry.getValue());
		}
		msg.infoOff(msgMeasure);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	private Waypoint getKey(MeasureMarker value) {
		for(Map.Entry<Waypoint, MeasureMarker> entry : current.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

    /**
     * handles the click on either a {@link Waypoint} or a {@link MeasureMarker}
     * for measuring purposes. Only one of the arguments may be specified, the
     * other has to be null
     *
     * @param wpt {@link Waypoint} selected (clicked) on the {@link GPXPanel
     * @param marker {@link MeasureMarker} selected (clicked) on the {@link GPXPanel}
     */
    private void handleMeasure(Waypoint clickedWpt, MeasureMarker clickedMarker) {

    	if (clickedMarker != null) {
    		// existing marker was clicked. remove it.
    		if (current.containsValue(clickedMarker)) {
    			current.remove(getKey(clickedMarker));
    			mapMarkers.remove(clickedMarker);
    		}
    	}

    	if (clickedWpt != null) { // a trackpoint was clicked
    		// if we know it - remove it
    		if (current.containsKey(clickedWpt)) {
    			mapMarkers.remove(current.get(clickedWpt));
    			current.remove(clickedWpt);
    		} else { // no. add a new one
    			if (current.size() < 2) {  // for now: limited to measuring between two waypoints
    				MeasureMarker m = new MeasureMarker(clickedWpt);
    				current.put(clickedWpt, m);
    				mapMarkers.add(m);
    			}
    		}
    	}

    	// do the calculation
    	if ((current.size() == 2) && (activeWptGrp != null)) {
    		doMeasure();
    	} else {
    		msgMeasure.setText("select two Trackpoins");
    	}
    }

    /*
     * handles the selection of a {@link Waypoint}
     * when the {@link tglMeasure} button is enabled
     */
    private void doMeasure() {
      	// TODO calculate distance & duration across track segments

    	Waypoint wpt1 = (Waypoint) current.keySet().toArray()[0];
    	Waypoint wpt2 = (Waypoint) current.keySet().toArray()[1];
    	double distance = 0;

    	String unit = uc.getUnit(UNIT.KM);
    	String format = "distance %1$.2f "+unit+", direct %2$.2f "+unit+", duration ";
    	// String format = "distance %f "+unit+", direct %f "+unit+", duration ";

    	// TODO DEBUG nullpointer here

    		int start = activeWptGrp.getWaypoints().indexOf(wpt1);
    		int end = activeWptGrp.getWaypoints().indexOf(wpt2);
    		if (start == -1 || end == -1)
    		{
    			msg.error("internal error: waypoint not in WaypointGroup");
    		} else {
    			// distance

    			Waypoint prev = activeWptGrp.getWaypoints().get(start);
    			for (int i = start; i <= end; i++) {
    				Waypoint curr = activeWptGrp.getWaypoints().get(i);
    	    		distance += curr.getDistance(prev);
    	    		prev = curr;
    			}
    			// as the crow flies
    			double direct = wpt1.getDistance(wpt2);

				DateTime startTime = new DateTime(wpt1.getTime());
				DateTime endTime = new DateTime(wpt2.getTime());
				Period period = new Duration(startTime, endTime).toPeriod();
				String timeString = String.format("%02d:%02d:%02d",
						period.getHours(), period.getMinutes(), period.getSeconds());
				if (period.getDays() > 0) {
					 timeString = String.format("%dd ", period.getDays()).concat(timeString);
				}
    			String text = String.format(format, uc.dist(distance, UNIT.KM), uc.dist(direct, UNIT.KM)).concat(timeString);
    			msgMeasure.setText(text);
    		}


    }

}