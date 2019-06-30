package org.gpsmaster.filehub;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.OutputStream;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Map msgPanel target for {@link ITransferableItem}s
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
	public String getDescription() {
		return "Show on Map";
	}
	
	/**
	 * 
	 */
	public DataType getDataType() {

		return DataType.GPXFILE;
	}

	/**
	 * Advise GUI not to show a progress text,
	 * since adding to the map is usually quite fast.
	 */
	public boolean doShowProgressText() {
	
		return false;
	}
	
	/**
	 * 
	 */
	public boolean isEnabled() {	
		return enabled;
	}
	
	/**
	 * 
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void AddPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	/**
	 * 
	 */
	public void addGpxFile(GPXFile gpxFile, TransferableItem item) {
		if (gpxFile.getMetadata().getName().isEmpty()) {
			gpxFile.getMetadata().setName(item.getName());
		}
		pcs.firePropertyChange(Const.PCE_NEWGPX, null, gpxFile);		
	}

		
	/**
	 * get required format
	 */
	public String getRequiredFormat() {
		// not applicable
		return null;
	}

	public void open(TransferableItem transferableItem) {
		// silently ignore
		
	}

	public OutputStream getOutputStream() {
		throw new UnsupportedOperationException();
	}

	public void close() throws Exception {
		// silently ignore
		
	}



}
