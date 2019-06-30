package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;


/**
 * 
 * Contains fields and methods common to all GPX element types (files, routes, tracks, waypoints, etc). 
 * 
 * @author Matt Hoover
 *
 */
public abstract class GPXObject {
    /**
     * Updates the relevant properties of the subclass.
     */
    public abstract void updateAllProperties();
    
    protected String name;
    protected String desc;
    protected boolean visible;
    protected boolean wptsVisible;
    protected Color color;
    // TODO get colors from config
    private static Color[] colors = { // some standard colors
    	new Color(255,180,  0), // orange
    	new Color(  0,  0,255), // blue 
    	new Color(255,  0,  0), // red
    	new Color(  0,255,  0), // green
        new Color(  0,102,102), // dark green
        new Color(255,255,  0), new Color(255,  0,255), new Color(  0,255,255),
        new Color(127,  0,255), new Color(255,127,  0), new Color(255,255,255)
    };
    private static int currentColor = 0;
    
    protected double minLat;
    protected double minLon;
    protected double maxLat;
    protected double maxLon;
    
    protected long duration;
    protected long exStop;
    protected double lengthMeters;
    protected double maxSpeedKmph;
    protected double eleStartMeters;
    protected double eleEndMeters;   
    protected double eleMinMeters;
    protected double eleMaxMeters;
    protected double grossRiseMeters;
    protected double grossFallMeters;
    protected long riseTime;
    protected long fallTime;
    protected Date startTime;
    protected Date endTime;
    
    protected Hashtable<String, String> extensions = new Hashtable<String, String>();
    /**
     * Default superclass constructor.
     */
    public GPXObject() {
        this.name = "";
        this.desc = "";        
        this.visible = true;
        this.wptsVisible = true;
        this.color = Color.white;
        
        this.minLat =  86;
        this.maxLat = -86;
        this.minLon =  180;
        this.maxLon = -180;
        this.startTime = null;
        this.endTime = null;
    }
    
    /**
     * Constructs a GPX object with a random color.
     * 
     * @param randomColor   If true, use a random color.  If false, use white.
     */
    public GPXObject(boolean randomColor) {
        this();
        if (randomColor) {
            this.color = colors[(currentColor++) % colors.length];
        }
    }
    
    /**
     * Constructs a GPX object with a chosen color.
     * 
     * @param color     The color.
     */
    public GPXObject(Color color) {
        this();
        this.color = color;
    }
    
    /**
     * Constructs a {@link GPXObject} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link GPXObject} to be cloned
     */
    public GPXObject(GPXObject source) {
    	this.name = source.name;
    	this.desc = source.desc;
    	this.color = source.color;
    	this.visible = source.visible;
    	this.wptsVisible = source.wptsVisible;
    	
		Iterator<String> i = source.extensions.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			this.extensions.put(key, source.extensions.get(key));
		}   	    	
    }

    public String toString() {
        return this.name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Hashtable<String, String> getExtensions() {
    	return extensions;
    }
    
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public boolean isWptsVisible() {
        return wptsVisible;
    }

    public void setWptsVisible(boolean wptsVisible) {
        this.wptsVisible = wptsVisible;
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public long getDuration() {
        return duration;
    }

    public long getDurationExStop() {
        return exStop;
    }

    public double getLengthMeters() {
        return lengthMeters;
    }

    public double getMaxSpeedKmph() {
        return maxSpeedKmph;
    }

    public double getEleStartMeters() {
        return eleStartMeters;
    }

    public double getEleEndMeters() {
        return eleEndMeters;
    }

    public double getEleMinMeters() {
        return eleMinMeters;
    }

    public double getEleMaxMeters() {
        return eleMaxMeters;
    }


    public double getGrossRiseMeters() {
        return grossRiseMeters;
    }

    public double getGrossFallMeters() {
        return grossFallMeters;
    }

    public long getRiseTime() {
        return riseTime;
    }

    public long getFallTime() {
        return fallTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public boolean isGPXFile() {
        return getClass().equals(GPXFile.class);
    }
    
    public boolean isGPXFileWithOneRoute() {
        return (isGPXFile() && ((GPXFile) this).getRoutes().size() == 1);
    }
    
    public boolean isGPXFileWithOneRouteOnly() {
        return (isGPXFile() && ((GPXFile) this).getRoutes().size() == 1 && ((GPXFile) this).getTracks().size() == 0);
    }
    
    public boolean isGPXFileWithNoRoutes() {
        return (isGPXFile() && ((GPXFile) this).getRoutes().size() == 0);
    }
    
    public boolean isGPXFileWithOneTrackseg() {
        return (isGPXFile() && ((GPXFile) this).getTracks().size() == 1
                && ((GPXFile) this).getTracks().get(0).getTracksegs().size() == 1);
    }
    
    public boolean isGPXFileWithOneTracksegOnly() {
        return (isGPXFile() && ((GPXFile) this).getTracks().size() == 1
                && ((GPXFile) this).getTracks().get(0).getTracksegs().size() == 1
                && ((GPXFile) this).getRoutes().size() == 0);
    }
    
    public boolean isGPXFileWithTracksegs() {
        return (isGPXFile() && ((GPXFile) this).getTracks().size() > 0
                && ((GPXFile) this).getTracks().get(0).getTracksegs().size() > 0);
    }
    
    public boolean isWaypoints() {
        return (isWaypointGroup() && ((WaypointGroup) this).getWptGrpType() == WptGrpType.WAYPOINTS);
    }
    
    public boolean isRoute() {
        return getClass().equals(Route.class);
    }
    
    public boolean isTrack() {
        return getClass().equals(Track.class);
    }
    
    public boolean isTrackWithOneSeg() {
        return (isTrack() && ((Track) this).getTracksegs().size() == 1);
    }
    
    public boolean isTrackseg() {
        return (isWaypointGroup() && ((WaypointGroup) this).getWptGrpType() == WptGrpType.TRACKSEG);
    }
    
    public boolean isWaypointGroup() {
        return getClass().equals(WaypointGroup.class);
    }

}
