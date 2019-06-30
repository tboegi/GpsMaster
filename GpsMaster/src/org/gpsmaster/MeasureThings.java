package org.gpsmaster;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;

import org.gpsmaster.UnitConverter.UNIT;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
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
import eu.fuegenstein.util.XTime;

/**
 * The Measure Of All Things
 * 
 * @author rfu
 *
 * TODO future versions: display measure results in widget or table
 * for distances between multiple measureMarkers (painted in different colors)
 * 
 */
public class MeasureThings {

	// Listener to receive clicks on mapMarkers from GPXPanel
	private PropertyChangeListener propertyListener = null;
	private WaypointGroup waypointGroup = null;
	private Hashtable<Waypoint, MeasureMarker> current = new Hashtable<Waypoint, MeasureMarker>();
	private List<Marker> mapMarkers = null;
	private MessageCenter msg = null;
	private MessagePanel msgMeasure = null;
	private UnitConverter uc = null;
	private Core core = new Core();
	
	private static String emptyText = "select two Trackpoins";
	
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
				} else if (e.getPropertyName().equals("activeGpxObject")) {
					System.out.println("activeGpxObject");
					setActiveGpxObject((GPXObject) e.getNewValue());
				}
			}
		};
		
		msgMeasure = msg.infoOn(emptyText);
	}

	/**
	 * set initial {@link WaypointGroup}. All others 
	 * will be set via PropertyChange events
	 * @param grp Current {@link WaypointGroup}
	 */
	public void setActiveGpxObject(GPXObject gpxObject) {
		if (gpxObject == null) {			
			waypointGroup = null;
			msgMeasure.setText(emptyText);
			clearMarkers();
		} else {
			List<WaypointGroup> groups = core.getSegments(gpxObject, core.SEG_TRACK_ROUTE_WAYPOINTS);
			if (groups.size() == 1) {
				waypointGroup = groups.get(0);
			} else {
				msgMeasure.setText("Please select a track segment or route segment");
			}
		}
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
		clearMarkers();
		msg.infoOff(msgMeasure);		
	}
	
	/**
	 * Remove measure markers from {@link GPXPanel}
	 */
	private void clearMarkers() {
		for(Map.Entry<Waypoint, MeasureMarker> entry : current.entrySet()) {
			mapMarkers.remove(entry.getValue());
		}			
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
    	if (waypointGroup == null) {
    		return;
    	}
    	
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
    	if ((current.size() == 2) && (waypointGroup != null)) {
    		doMeasure();
    	} else {
    		msgMeasure.setText(emptyText);
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
    	String out = ""; // format = "distance %1$.2f "+unit+", direct %2$.2f "+unit+", duration ";
    	// String format = "distance %f "+unit+", direct %f "+unit+", duration ";
    	
    	// TODO if distance is less than (i.e.) 1km, display it in meters
    	//		(and accordingly for other unit systems)
    	// TODO DEBUG nullpointer here
    	
    		int start = waypointGroup.getWaypoints().indexOf(wpt1);
    		int end = waypointGroup.getWaypoints().indexOf(wpt2);
    		if (start > end) {
    	    	int x = start; start = end; end = x;    			
    		}
    		if (start == -1 || end == -1)
    		{
    			msg.error("internal error: waypoint not in WaypointGroup");
    		} else {
    			// distance
    			Waypoint prev = waypointGroup.getWaypoints().get(start);
    			for (int i = start; i <= end; i++) {
    				Waypoint curr = waypointGroup.getWaypoints().get(i);
    	    		distance += curr.getDistance(prev);
    	    		prev = curr;
    			}
    			// as the crow flies
    			out = String.format("distance %1$.2f "+unit+", direct %2$.2f "+unit, 
    					uc.dist(distance, UNIT.KM),    					
    					uc.dist(wpt1.getDistance(wpt2), UNIT.KM));
    			System.out.println(distance);
    			// duration
    			long duration = Math.abs(wpt1.getDuration(wpt2));
    			if (duration > 0) {
	    			out += ", duration " + XTime.getDurationString(duration);
	    			// avg speed
	            	double avgSpeed = uc.speed((distance / duration * 3600), UNIT.KMPH);
	            	out += String.format(", avg speed %.2f " + uc.getUnit(UNIT.KMPH), avgSpeed);
    			}
    			msgMeasure.setText(out);
    		}   		
    	
    	
    }

}