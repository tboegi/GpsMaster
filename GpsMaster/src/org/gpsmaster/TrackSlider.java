package org.gpsmaster;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
				GpsMaster.active.setWaypoint(idx);
				
			}
		});
        
        /**
         * receive notifications about active GPXObjects
         */
        changeListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(GpsMaster.active.PCE_ACTIVEGPX)) {
					setValue(0);
					if (GpsMaster.active.getGpxObject() == null) {
						setEnabled(false);
					} else {
						setMaximum(GpsMaster.active.getNumWaypoints() - 1);
						setEnabled(true);
					}
				} else if (e.getPropertyName().equals(GpsMaster.active.PCE_ACTIVEWPT)) {
					if (GpsMaster.active.getWaypoint() != null) {
						int idx = GpsMaster.active.getTotalIndexOf(GpsMaster.active.getWaypoint());
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
}
