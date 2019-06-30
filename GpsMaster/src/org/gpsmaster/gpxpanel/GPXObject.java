package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.swing.tree.TreeNode;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;


/**
 * 
 * Contains fields and methods common to all GPX element types (files, routes, tracks, waypointgroups, etc). 
 * 
 * @author Matt Hoover
 *
 */
public abstract class GPXObject implements TreeNode {
    /**
     * Updates the relevant properties of the subclass.
     */
    public abstract void updateAllProperties();
    
    protected boolean visible = true;
    protected boolean trackPtsVisible = false;
    protected Color color = Color.white;
    protected GPXExtension extension = null;
    
    // TODO get colors from config
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
    protected HashMap<String, ExtensionMeta> minMaxExtensions;
    protected GPXObject parent = null;
    
    public static class ExtensionMeta {
        public String name;
        public ArrayList<Double> values = new ArrayList<>();
        private boolean sorted = false;
        private void sort() {
            if (!sorted) {
                Collections.sort(values);
                sorted = true;
            }
        }
        public double getMin() {
            sort();
            if (0 < values.size()) {
                return values.get(0);
            } else {
                return 0.0;
            }
        }
        public double getMax() {
            sort();
            if (0 < values.size()) {
                return values.get(values.size() - 1);
            } else {
                return 0.0;
            }
        }
        double getSum() {
            if (0 < values.size()) {
                double sum = 0.0;
                for (Double value : values) {
                    sum += value;
                }
                return sum;
            } else {
                return 0.0;
            }
        }
        public double getMean() {
            if (0 < values.size()) {
                return getSum() / (values.size() * 1.0);
            } else {
                return 0.0;
            }
        }
        public double getMedian() {
            sort();
            if (0 < values.size()) {
                int middle = values.size() / 2;
                if (values.size() % 2 == 1) {
                    return values.get(middle);
                } else {
                    return (values.get(middle - 1) + values.get(middle)) / 2.0;
                }
            } else {
                return 0.0;
            }
        }
        public double getStandardDeviation() {
            double sum = 0.0;
            double mean = getMean();
            for (Double value : values) {
                sum += Math.pow(value - mean, 2.0);
            }
            if (values.size() > 1000) {
                return Math.sqrt(sum / values.size());
            } else {
                return Math.sqrt(sum / (values.size() - 1));
            }
        }
    }
    
    /**
     * Default superclass constructor.
     */
    public GPXObject() {
        this.visible = true;
        this.trackPtsVisible = true;
        this.color = Color.white;
        
        this.minLat =  86;
        this.maxLat = -86;
        this.minLon =  180;
        this.maxLon = -180;
        this.startTime = null;
        this.endTime = null;
        this.extension = new GPXExtension(Const.TAG_EXTENSIONS);
        this.minMaxExtensions = new HashMap<String, ExtensionMeta>(256);
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
    	this.trackPtsVisible = source.trackPtsVisible;
    	this.extension = new GPXExtension(source.extension);
        this.minMaxExtensions = new HashMap<String, ExtensionMeta>(source.getMinMaxExtensions());
    }
    
    public abstract void setName(String name);
    public abstract String getName();
    
    public abstract void setDesc(String desc);
    public abstract String getDesc();
    
    /**
     * 
     * @return
     */
    public GPXExtension getExtension() {
    	return extension;
    }
    
    public void setExtension(GPXExtension extension) {
    	this.extension = extension;
    }
    
    public boolean isVisible() {
    	/*
    	if (parent != null) {
    		return (visible && parent.isVisible());
    	}
    	*/
        return this.visible;
    }
    
    public void setVisible(boolean visible) {    	
        this.visible = visible;
    }
    
    public boolean isTrackPtsVisible() {
    	if (parent != null) {
    		return (trackPtsVisible && parent.isTrackPtsVisible());
    	}
        return this.trackPtsVisible;
    }

    public void setTrackPtsVisible(boolean visible) {
        this.trackPtsVisible = visible;
    }

    public Color getColor() {
        return color;
    }

    public Color getColor(int slot) {
    	return colors[slot % colors.length];
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

    public HashMap<String, ExtensionMeta> getMinMaxExtensions() {
        return minMaxExtensions;
    }

    protected void setParent(GPXObject parent) {
    	this.parent = parent;
    }
        
    public GPXObject getParent() {
    	return parent;
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
     * Save the current color as an sourceFmt
     */
    private void colorToExt() {    	
    	if (extension.containsKey(Const.EXT_COLOR)) {
    		extension.remove(Const.EXT_COLOR);
    	}
    	String colorString = String.format("%02x%02x%02x%02x", 
    			color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    	extension.add(Const.EXT_COLOR, colorString);    	
    }
    
    /**
     * Set color according to sourceFmt, if it exists
     */
    protected void extToColor() {
    	if (extension.containsKey(Const.EXT_COLOR)) {
    		String colorString = extension.getSubValue(Const.EXT_COLOR);
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
