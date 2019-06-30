package org.gpsmaster;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * 
 * @author rfu
 *
 * TODO Debug. index springt unvermittelt um größenordnungen
 */
public class TrackSlider extends JSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 332307178698570815L;
	private int myValue = 0;
	
	private PropertyChangeListener changeListener = null;
	/**
	 * 
	 */
	public TrackSlider() {
		super();
		init();
	}
	
	/**
	 * 
	 */
	private void init() {
		setOrientation(JSlider.HORIZONTAL);
        setMinimum(0);
        setMaximum(0);
        setPaintLabels(false);
        setPaintTicks(false);
        setAlignmentX(0.0f); 
        addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int idx = getValue();
				if (idx > -1) {
					GpsMaster.active.setTrackpoint(idx);
				}
			}
		});
        
        /**
         * receive notifications about active GPXObjects
         */
        changeListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(Const.PCE_ACTIVEGPX)) {					
					setValue(-1); // means: do not set active trackpoint					
					if (GpsMaster.active.getGpxObject() == null) {
						setEnabled(false);
					} else {
						setMaximum(GpsMaster.active.getNumWaypoints() - 1);
						setEnabled(true);
					}
				} else if (e.getPropertyName().equals(Const.PCE_ACTIVE_TRKPT)) {
					Waypoint activeTrkpoint = GpsMaster.active.getTrackpoint(); 
					if ( activeTrkpoint != null) {
						int idx = GpsMaster.active.getTotalIndexOf(activeTrkpoint);
						int current = getValue();
						if (Math.abs(idx - current) > 5) {					
							setValue(idx);
						}
					}
				}				
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);
	}
	
	@Override 
	public void setValue(int newValue) {
		myValue = newValue;
		super.setValue(newValue);
	}
	
	@Override
	public int getValue() {
		return myValue;
	}
}
