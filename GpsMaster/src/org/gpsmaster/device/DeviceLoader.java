package org.gpsmaster.device;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;


/**
 * Base class for device-specific loader classes
 * 
 * @author rfuegen
 *
 */
public abstract class DeviceLoader {

	protected Hashtable<String, String> connectionParams = new Hashtable<String, String>();	
	
	protected String key = "";
	protected String name = "";
	protected String description = "";
		
	protected boolean isConnected = false;
	protected boolean getExtended = false; // get extended GPS attributes for track/waypoints?
	protected boolean canDelete = false;  // possible to delete tracks from the GPS device?
	
	/**
	 * Constructor
	 */	
	public DeviceLoader() {
		
	}
	
	/**
	 * establish connection to device.
	 */
	public void connect() throws Exception { }
	
	/**
	 * Get a list of tracks stored on the device
	 */
	public List<TrackEntry> getTracklist() throws Exception {
		List<TrackEntry> directory = new ArrayList<TrackEntry>();
		
		
		return(directory);
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public GPXFile load(TrackEntry entry) throws Exception {	
		GPXFile gpxFile = new GPXFile();
		
		return gpxFile;
	}
	
	/**
	 * Delete track/file from device
	 * @param entry {@link TrackEntry} to delete
	 */
	public void delete(TrackEntry entry) {
		
	}
	
	/**
	 * Disconnect from device
	 */
	public void disconnect() throws Exception {
		
	}
	
	// properties
	
	/**
	 * 
	 * @return unique identifier for this device class
	 */
	public String getDeviceKey() {
		return key;
	}

	/**
	 * 
	 * @return short, human readable name for this device
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return description of this device
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param params
	 */
	public void setConnectionParams(Hashtable<String, String> params) {
		connectionParams = params;
	}
	
	/**
	 * 
	 * @return
	 */
	public Hashtable getConnectionParams() {
		return connectionParams;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * 
	 * @param ext
	 */
	public void setExtended(boolean ext) {
		getExtended = ext;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getExtended() {
		return getExtended;
	}
	
	/**
	 * 
	 * @return true if tracks can be deleted from the device
	 */
	public boolean getCanDelete() {
		return canDelete;
	}
	
	/**
	 * check if connection to the device is established
	 */
	protected void checkConnection() {
		
		if (isConnected == false) {
			// throw new NotConnected() exception 
		}
	
	}
}
