package org.gpsmaster.device;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.online.OnlineTrack;


/**
 * Base class for device-specific transfer classes
 * 
 * @author rfuegen
 *
 */
public abstract class GpsDevice {

	protected Hashtable<String, String> connectionParams = new Hashtable<String, String>();	
	
	protected String key = "";
	protected String name = "";
	protected String description = "";
		
	protected boolean isConnected = false;
	protected boolean getExtended = false; // get extended GPS attributes for track/waypoints?
	protected boolean canDelete = false;  // possible to delete tracks from the GPS device?
	protected boolean canUpload = false;  // uploading tracks to device is supported?
	
	/**
	 * Constructor
	 */	
	public GpsDevice() {
		
	}
	
	/**
	 * establish connection to device.
	 */
	public void connect() throws Exception { }
	
	/**
	 * Get a list of tracks stored on the device
	 */
	public List<OnlineTrack> getTracklist() throws Exception {
		List<OnlineTrack> directory = new ArrayList<OnlineTrack>();
		
		
		return(directory);
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public GPXFile load(OnlineTrack entry) throws Exception {	
		GPXFile gpxFile = new GPXFile();
		
		return gpxFile;
	}
	
	/**
	 * Delete track/file from device
	 * @param entry {@link DeviceTrack} to delete
	 */
	public void delete(OnlineTrack entry) {
		
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
	public Hashtable<String, String> getConnectionParams() {
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
