package org.gpsmaster;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Config {

	// initial position of the map
	private boolean showWarning = false;
	private boolean showZoomControls = false;
	private boolean useExtensions = true;
	private boolean activitySupport = true;
	private double cleaningDistance = 0.2f;
	private float trackWidth = 4f;
	private double displayPositionLatitude = 48; // Europe
	private double displayPositionLongitude = 14;
	private int displayPositionZoom = 5;
	private int screenTime = 30;  // default on-screen time for warnings
	private String lastOpenDirectory = "";
	private String lastSaveDirectory = "";
	private String tempDirectory = "";
	private String defaultExt = "gpx";
	private UnitSystem unitSystem = UnitSystem.METRIC;

	private List<DeviceConfig> deviceLoaders = new ArrayList<DeviceConfig>();


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

    /**
     *
     * @return
     */
    public double getCleaningDistance() {
		return cleaningDistance;
	}

    /**
     * sets the distance below which trackpoints will
     * be removed when cleaning the track
     * @param cleaningDistance
     */
	public void setCleaningDistance(double cleaningDistance) {
		this.cleaningDistance = cleaningDistance;
	}

	public String getLastOpenDirectory() {
        return this.lastOpenDirectory;
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



}

