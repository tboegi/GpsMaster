package eu.fuegenstein.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.gpsmaster.widget.ProgressWidget;

/**
 * Adapter class to allow non-threading classes & methods to
 * report progress to a {@link ProgressWidget} when running
 * in background
 * 
 * @author rfu
 * 
 * (glaub das geht so nicht)
 */

public class ProgressAdapter {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	/**
	 * Default Constructor
	 */
	public ProgressAdapter() {
		
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	
}
