package org.gpsmaster.dialogs;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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

	private PropertyChangeSupport propertyChange = new PropertyChangeSupport(this);
	
	/**
	 * Default Constructor
	 */
	public ProgressAdapter() {
		
	}
	
	
}
