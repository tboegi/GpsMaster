package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;


/**
 * 
 * Contains fields and methods common to all GPX element types (files, routes, tracks, waypoints, etc). 
 * 
 * @author Matt Hoover
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class GPXObject {
    /**
     * Updates the relevant properties of the subclass.
     */
    public abstract void updateAllProperties();
    
    protected boolean visible;
    protected boolean wptsVisible;
    protected Color color;
    // TODO get colors from config
/*
    private static Color[] colors = { // some standard colors
    	new Color(255,180,  0), // orange
    	new Color(  0,  0,255), // blue 
    	new Color(255,  0,  0), // red
    	new Color(  0,255,  0), // green
        new Color(  0,102,102), // dark green
        new Color(255,255,  0), new Color(255,  0,255), new Color(  0,255,255),
        new Color(127,  0,255), new Color(255,127,  0), new Color(255,255,255)
    };
*/
/*        
    private static Color[] colors = { // RAL color palette 1
    	// TODO: rot-töne nicht unterscheidbar genug
    	new Color(0xF7, 0xBA, 0x0B), // RAL 1003 Signal Yellow  ALT: 1012
    	new Color(0xC8, 0x9F, 0x04), // RAL 1005 Honey Yellow  ALT: 1023
    	new Color(0xC6, 0x39, 0x27), // RAL 2002 Vermillion (Blutorange)
    	new Color(0xAB, 0x25, 0x24), // RAL 3000 Flame Red  ALT: 3004
        new Color(0xC1, 0x12, 0x1C), // RAL 3020 Traffic Red 
        new Color(0x82, 0x63, 0x9D), // RAL 4005 Blue liliac ALT: 4001 
        new Color(0x99, 0x25, 0x72), // RAL 4006 Traffic Purple  ALT: 4011 
        new Color(0x2B, 0x2C, 0x7C), // RAL 5002 Ultramarine Blue
        new Color(0x15, 0x48, 0x89), // RAL 5005 Signal Blue   ALT: 5022
        new Color(0x0E, 0x51, 0x8D), // RAL 5017 Traffic Blue
        new Color(0x4D, 0x66, 0x8E), // RAL 5023 Distant Blue  ALT: 5010
        new Color(0x28, 0x71, 0x3E), // RAL 6001 Emerald Green
        new Color(0x0F, 0x43, 0x36), // RAL 6005 British Racing Green
        new Color(0x68, 0x82, 0x5B), // RAL 6011 Reseda Green  ALT: 6020
        new Color(0x74, 0x66, 0x43), // RAL 7008 Khaki Grey
        new Color(0x9C, 0x6B, 0x30), // RAL 8001 Ochre Brown (Ocker Braun)
        new Color(0x6F, 0x4A, 0x2F)  // RAL 8007 Fawn Brown (Rehbraun)
    };
*/

    private static Color[] colors = {
        // RAL Signal Colors
    	new Color(0xF7, 0xBA, 0x0B), // RAL 1003 Signal Yellow 
    	new Color(0xD4, 0x65, 0x2F), // RAL 2010 Signal Orange
      	new Color(0xA0, 0x21, 0x28), // RAL 3001 Signal Red
      	new Color(0x90, 0x46, 0x84), // RAL 4008 Signal Violet
      	new Color(0x15, 0x48, 0x89), // RAL 5005 Signal Blue
      	new Color(0x0F, 0x85, 0x58), // RAL 6032 Signal Green
      	new Color(0x9E, 0xA0, 0xA1), // RAL 7004 Signal Grey
      	new Color(0x7B, 0x51, 0x41), // RAL 8002 Signal Brown
      	new Color(0xF4, 0xF8, 0xF4),  // RAL 9003 Signal White
      	// RAL Traffic Colors 
    	new Color(0xF0, 0xCA, 0x00), // RAL 1023 Traffic Yellow  
    	new Color(0xE1, 0x55, 0x01), // RAL 2009 Traffic Orange
      	new Color(0xC1, 0x12, 0x1C), // RAL 3020 Traffic Red
      	new Color(0x99, 0x25, 0x72), // RAL 4006 Traffic Purple
      	new Color(0x0E, 0x51, 0x8D), // RAL 5017 Traffic Blue
      	new Color(0x00, 0x87, 0x54), // RAL 6024 Traffic Green
      	new Color(0x8F, 0x96, 0x95), // RAL 7042 Traffic Grey A
      	new Color(0x4E, 0x54, 0x51), // RAL 7043 Traffic Grey B
      	new Color(0xF7, 0xFB, 0xF5)  // RAL 9016 Traffic White
    	// new Color(0x2A, 0x2D, 0x2F) // RAL 9017 Traffic Black
    };

    private static int currentColor = 0;
    
    protected double minLat;
    protected double minLon;
    protected double maxLat;
    protected double maxLon;
    
    protected long duration;
    protected long exStop;
    protected double lengthMeters;
    protected double maxSpeedMps;
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
     * @param autoColor  If true, use next available color. If false, use white.
     */
    public GPXObject(boolean autoColor) {
        this();
        if (autoColor) {
            this.color = colors[(currentColor++) % colors.length];
            colorToExt();
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
        colorToExt();
    }
    
    /**
     * Constructs a {@link GPXObject} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link GPXObject} to be cloned
     */
    public GPXObject(GPXObject source) {
    	this.color = source.color;
    	this.visible = source.visible;
    	this.wptsVisible = source.wptsVisible;
    	
		Iterator<String> i = source.extensions.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			this.extensions.put(key, source.extensions.get(key));
		}   	    	
    }

    public abstract void setName(String name);
    public abstract String getName();
    
    public abstract void setDesc(String desc);
    public abstract String getDesc();
    
    @XmlElement(name = "extensions")
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
        colorToExt();
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

    /**
     * Max. speed in meters per second
     * @return
     */
    public double getMaxSpeedMps() {
        return maxSpeedMps;
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

    /**
     * Save the current color as an extension
     */
    private void colorToExt() {
    	if (extensions.containsKey(Const.EXT_COLOR)) {
    		extensions.remove(Const.EXT_COLOR);
    	}
    	String colorString = String.format("%02x%02x%02x%02x", 
    			color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    	extensions.put(Const.EXT_COLOR, colorString);  
    }
    
    /**
     * Set color according to extension, if it exists
     */
    protected void extToColor() {
    	if (extensions.containsKey(Const.EXT_COLOR)) {
    		String colorString = extensions.get(Const.EXT_COLOR);
    		try {
				int r = Integer.parseInt(colorString.substring(0, 2), 16);
				int g = Integer.parseInt(colorString.substring(2, 4), 16);
				int b = Integer.parseInt(colorString.substring(4, 6), 16);
				int a = Integer.parseInt(colorString.substring(6, 8), 16);
				color = new Color(r, g, b, a);
    		} catch (NumberFormatException e) {
    			color = Color.WHITE;
    		}			
    	}
    }
}
