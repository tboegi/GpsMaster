package org.gpsmaster;

import java.util.Hashtable;


public class DeviceConfig {

	private String name = "";
	private String description = "";
	private String deviceLoader = "";
	private Hashtable<String, String> params = new Hashtable<String, String>();
	
	/**
	 * Constructor
	 */
	public DeviceConfig() {
		
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoaderClass() {
		return deviceLoader;
	}

	public void setLoaderClass(String deviceLoader) {
		this.deviceLoader = deviceLoader;
	}

	public Hashtable<String, String> getConnectionParams() {
		return params;
	}

	public void setConnectionParams(Hashtable<String, String> params) {
		this.params = params;
	}
	
	
}
