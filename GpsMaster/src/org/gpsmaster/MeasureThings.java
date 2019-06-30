package org.gpsmaster;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXPanel;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;
import org.gpsmaster.marker.MeasureMarker;
import org.gpsmaster.widget.DistanceWidget;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.util.XTime;

/**
 * The Measure Of All Things
 * 
 * @author rfu
 * 
 * TODO measureMarkers painted in different colors
 * TODO if chart is open, set measureMarkers by clicking on chart
 * (also show marker lines on chart) 
 */
public class MeasureThings {

	private DistanceWidget widget = null;
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
	private final String emptyText = "select two or more trackpoints.";
	

	private int c = 0;
	/**
	 * Constructor
	 * @param msg
	 * @param mapMarkers list of {@link Markers} from {@link GPXPanel}
	 */
	public MeasureThings(DistanceWidget widget, UnitConverter converter, List<Marker> markers) {
		
		this.widget = widget;
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
				if (evt.getPropertyName().equals(Const.PCE_ACTIVEGPX)) {
					setActiveGpxObject();
				}				
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);
		
		setActiveGpxObject();
	}

	/**
	 * set initial {@link WaypointGroup}. All others 
	 * will be set via PropertyChange events
	 * @param grp Current {@link WaypointGroup}
	 */
	public void setActiveGpxObject() {
		// msgMeasure.setText(emptyText);
		clearMarker();
		points.clear();
		widget.clear();
		c = 0;
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
	 * @return the msg
	 */
	public MessageCenter getMessageCenter() {
		return msg;
	}

	/**
	 * @param msg the {@link MessageCenter} to set
	 * optional. if set, shows a message to select trackpoints for measuring.
	 */
	public void setMessageCenter(MessageCenter msg) {
		this.msg = msg;
		msgMeasure = msg.infoOn(emptyText);
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
	public void dispose() {	
		clearMarker();
		points.clear();
		if (msg != null) {
			msg.infoOff(msgMeasure);
		}
	}
	
	/**
	 * Remove measure markers from {@link GPXPanel}
	 */
	private void clearMarker() {
		for(MeasurePoint point : points) {
			mapMarkers.remove(point.getMarker());
		}			
	}

	/**
	 * add all measure points as {@link MeasureMarker} to the map
	 */
	private void addMarker() {
		for(MeasurePoint point : points) {
			mapMarkers.add(point.getMarker());
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
    				clearMarker();
    				points.remove(point);
    				// mapMarkers.remove(clickedMarker);
    				addMarker();
    				break;
    			}
    		}
    	}
    	
    	if (clickedWpt != null) { // a trackpoint was clicked
    		boolean addnew = true;
    		// if we know it - remove it
    		for (MeasurePoint point : points) {
    			if (point.getWaypoint().equals(clickedWpt)) {
    				clearMarker();
    				// mapMarkers.remove(point.getMarker());
    				points.remove(point);    				
    				addMarker();
    				addnew = false;
    				break;
    			}
    		}    		
    		
    		if (addnew) {
    			c++;
    			MeasureMarker m = new MeasureMarker(clickedWpt);
    			m.setName("M"+Integer.toString(c));
    			MeasurePoint point = new MeasurePoint(clickedWpt, m);
    			try {
					point.setIndexesFrom(groups);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			clearMarker();
    			points.add(point);    			
    			Collections.sort(points);
    			// mapMarkers.add(point.getMarker());    			
    			addMarker();
    		}    		    		
    	}
    	GpsMaster.active.repaintMap();
    	
   		doMeasure();
    }
    
    /*
     * Calculate distance & related data for/between set MeasurePoints 
     */
    private void doMeasure() {

    	widget.clear();
    	// Collections.sort(points);
    	if (points.size() >= 2) {
    		double distance = 0.0f;
	    	MeasurePoint mp1 = points.get(0);
	    	for (int i = 1; i < points.size(); i++) {
	    		MeasurePoint mp2 = points.get(i);
	    		int gIdx = mp1.getGroupIdx(); // start group
	    		int gEnd = mp2.getGroupIdx(); // end group
	    		int idx = mp1.getPointIdx(); // index of start point in gStart	    		
	    		Waypoint wptEnd = groups.get(gEnd).getWaypoints().get(mp2.getPointIdx());
	    		WaypointGroup g = groups.get(gIdx);
	    		Waypoint wptStart = g.getWaypoints().get(idx);
	    		Waypoint prev = wptStart;
	    		Waypoint curr = null;
	    		distance = 0.0f;
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
	        	String dist = uc.dist(distance, Const.FMT_DIST);
	        	String direct = uc.dist(wptStart.getDistance(wptEnd), Const.FMT_DIST);
	        	String dur = "-";
	        	String avg = "-";
    			// duration
    			long duration = Math.abs(wptStart.getDuration(wptEnd));
    			if (duration > 0) {
	    			dur = XTime.getDurationString(duration);
	    			// avg speed
	            	avg = uc.speed(distance / duration, Const.FMT_SPEED);
    			}
    			widget.addValues(mp1.getMarker(), mp2.getMarker(), dist, direct, dur, avg);   	
	    		mp1 = mp2;
	    	}
    	}        	
    }
}