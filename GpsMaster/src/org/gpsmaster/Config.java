package org.gpsmaster;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.gpsmaster.db.DBConfig;

import eu.fuegenstein.swing.NamedColor;
import eu.fuegenstein.swing.NamedConfigColor;
import eu.fuegenstein.unit.UnitSystem;

@XmlRootElement
public class Config {

	// initial position of the map
	private boolean showWarning = false;
	private boolean showZoomControls = false;
	private boolean useExtensions = true;
	private boolean activitySupport = true;
	private boolean showScalebar = true;
	private float trackWidth = 3f;
	private double displayPositionLatitude = 48; // Europe
	private double displayPositionLongitude = 14;
	private int displayPositionZoom = 5;
	private int screenTime = 30;  // default on-screen time for volatile messages
	private String proxyHost = "";
	private int proxyPort = 0;
	private String lastOpenDirectory = "";
	private String lastSaveDirectory = "";
	private String tempDirectory = "";
	private String defaultExt = "gpx";
	private UnitSystem unitSystem = UnitSystem.METRIC;
	private String gpsiesUsername = "";
	private boolean showStartEnd = true;
	
	private List<DeviceConfig> deviceLoaders = new ArrayList<DeviceConfig>();
	private List<NamedConfigColor> configColors = new ArrayList<NamedConfigColor>();
	private List<NamedColor> namedColors = new ArrayList<NamedColor>();
	private List<OnlineTileSource> tileSources = new ArrayList<OnlineTileSource>();
	
	private DBConfig dbConfig = new DBConfig();
	
	/*
	 * Constructor
	 */
	public Config() {
	}
	
    public double getLat() {
        return this.displayPositionLatitude;
    }

    public void setLat(double lat) {
        this.displayPositionLatitude = lat;
    }
    
    public double getLon() {
        return this.displayPositionLongitude;
    }

    public void setLon(double lng) {
        this.displayPositionLongitude = lng;
    }

    public int getPositionZoom() {
        return this.displayPositionZoom;
    }

    public void setPositionZoom(int zoom) {
        this.displayPositionZoom = zoom;
    }

    public boolean getZoomControls() {
        return this.showZoomControls;
    }

    public void setZoomControls(boolean ctl) {
        this.showZoomControls = ctl;
    }

    @XmlTransient
	public List<NamedColor> getPalette() {
		return namedColors;
	}

	/**
     * only used for marshalling
     * @return
     */
    public List<NamedConfigColor> getColors() {
    	namedToConfig();
    	return configColors;
    }
    
    /**
     * Set list of colors in {@link NamedConfigColor} format (classes) 
     * Only to be used for unmarshalling purposes
     * @param colors
     */
    public void setColors(List<NamedConfigColor> colors) {
    	this.configColors = colors;
    	configToNamed();
    }
    
	public String getLastOpenDirectory() {
        return this.lastOpenDirectory;
    }

	/**
	 * 
	 * @return
	 */
	public List<OnlineTileSource> getOnlineTileSources() {
		return tileSources;
	}
	
	public void setOnlineTileSources(List<OnlineTileSource> onlineTileSources) {
		this.tileSources = onlineTileSources;
	}
	
    public void setLastOpenDirectory(String dir) {
        this.lastOpenDirectory = dir;
    }

    public String getLastSaveDirectory() {
		return lastSaveDirectory;
	}

	public void setLastSaveDirectory(String lastSaveDirectory) {
		this.lastSaveDirectory = lastSaveDirectory;
	}

	public String getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	public String getDefaultExt() {
		return defaultExt;
	}

	public void setDefaultExt(String defaultExt) {
		this.defaultExt = defaultExt;
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @param proxyHost the proxyHost to set
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public UnitSystem getUnitSystem() {
        return this.unitSystem;
    }

    public void setUnitSystem(UnitSystem som) {
        this.unitSystem = som;
    }

    public boolean getShowWarning() {
        return this.showWarning;
    }

    public void setShowWarning(boolean warning) {
        this.showWarning = warning;
    }

    public boolean useExtensions() {
		return useExtensions;
	}

    /**
     * define if attributes in <extensions> should be
     * written and applied, if applicable
     * @param useExtensions
     */
	public void setUseExtensions(boolean useExtensions) {
		this.useExtensions = useExtensions;
	}

	public List<DeviceConfig> getDeviceLoaders() {
		return deviceLoaders;
	}

	public int getScreenTime() {
		return screenTime;
	}

	public void setScreenTime(int screenTime) {
		this.screenTime = screenTime;
	}

	public float getTrackLineWidth() {
		return trackWidth;
	}

	public void setTrackLineWidth(float trackWidth) {
		this.trackWidth = trackWidth;
	}

	public boolean getActivitySupport() {
		return activitySupport;
	}

	public void setActivitySupport(boolean activitySupport) {
		this.activitySupport = activitySupport;
	}
 
	/**
	 * @return the showStartEnd
	 */
	public boolean isShowStartEnd() {
		return showStartEnd;
	}

	/**
	 * show start/end marker of track segments
	 * 
	 * @param showStartEnd the showStartEnd to set
	 */
	public void setShowStartEnd(boolean showStartEnd) {
		this.showStartEnd = showStartEnd;
	}

	public boolean isShowScalebar() {
		return showScalebar;
	}

	public void setShowScalebar(boolean showScalebar) {
		this.showScalebar = showScalebar;
	}

	public String getGpsiesUsername() {
		return gpsiesUsername;
	}

	public void setGpsiesUsername(String gpsiesUsername) {
		this.gpsiesUsername = gpsiesUsername;
	}

	/**
	 * @return the dbConfig
	 */
	public DBConfig getDbConfig() {
		return dbConfig;
	}

	/**
	 * @param dbConfig the dbConfig to set
	 */
	public void setDbConfig(DBConfig dbConfig) {
		this.dbConfig = dbConfig;
	}

	/**
	 * convert list of {@link NamedConfigColor}s to {@link NamedColor}s.  
	 */
	private void configToNamed() {
		namedColors.clear();
		for (NamedConfigColor configColor : configColors) {
			namedColors.add(configColor.getNamedColor());
		}
	}
	
	/**
	 * convert list of {@link NamedColor}s to {@link NamedConfigColor}s.  
	 */
	private void namedToConfig() {
		configColors.clear();
		for (NamedColor namedColor : namedColors) {			
			NamedConfigColor color = new NamedConfigColor(namedColor);
			configColors.add(color);
		}
	}

}

