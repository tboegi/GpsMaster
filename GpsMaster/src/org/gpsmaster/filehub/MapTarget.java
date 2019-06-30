package org.gpsmaster.filehub;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Map panel target for {@link ITransferableItem}s
 * 
 * @author rfu
 *
 */
public class MapTarget implements IItemTarget {

	private PropertyChangeSupport pcs = null;
	private boolean enabled = true;
	
	/**
	 * Constructor
	 */
	public MapTarget() {
		pcs = new PropertyChangeSupport(this);
	}
	
	/**
	 * 
	 */
	public String getName() {
		return "Map";
	}
	
	/**
	 * 
	 */
	public boolean isEnabled() {	
		return enabled;
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void AddPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void addGpxFile(GPXFile gpxFile) {
		pcs.firePropertyChange(Const.PCE_NEWGPX, null, gpxFile);		
	}



}
