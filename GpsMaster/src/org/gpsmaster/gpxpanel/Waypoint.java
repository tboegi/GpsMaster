package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import org.openstreetmap.gui.jmapviewer.OsmMercator;

/**
 * 
 * The GPX "wpt" element.
 * 
 * @author Matt Hoover
 * @author rfu
 */
public class Waypoint {

    private double lat;
    private double lon;
    private double ele;
    private double hdop;
    private double vdop;
    private double pdop;
    private int sat;
    private Date time;
    protected String name;
    protected String desc;
    protected String type;
    protected Color segmentColor = null;

    private Hashtable<String, String> extensions =  new Hashtable<String, String>();
    
    /**
     * Constructs a {@link Waypoint}.
     * 
     * @param lat   Latitude.
     * @param lon   Longitude.
     */
    public Waypoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.time = null;
        this.name = "";
        this.desc = "";
        this.type = "";
        
    }

    /**
     * Constructs a {@link Waypoint} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link Waypoint} to be cloned
     */
    public Waypoint(Waypoint source) {
    	this.lat = source.lat;
    	this.lon = source.lon;
    	this.ele = source.ele;
    	this.hdop = source.hdop;
    	this.vdop = source.vdop;
    	this.pdop = source.pdop;
    	this.sat = source.sat;
    	this.time = source.time;
    	this.name = source.name;
    	this.desc = source.desc;
    	this.type = source.type;
		Iterator<String> i = source.extensions.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			this.extensions.put(key, source.extensions.get(key));
		}   	    	
    }
    
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getEle() {
        return ele;
    }

    public void setEle(double ele) {
        /*if (ele == -32768 && this.ele != 0) { // if SRTM data is missing, and GPS logged data exists, leave it as is
            return;
        }*/
        this.ele = ele;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public double getHdop() {
        return hdop;
    }

    public void setHdop(double hdop) {
        this.hdop = hdop;
    }

    public double getVdop() {
        return vdop;
    }

    public void setVdop(double vdop) {
        this.vdop = vdop;
    }

    public double getPdop() {
        return pdop;
    }

    public void setPdop(double pdop) {
        this.pdop = pdop;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }
    
    /**
     * get the color that is used for painting the track from this Waypoint onwards
     * @return
     */
    public Color getSegmentColor() {
    	return segmentColor;
    }
    
    /**
     * set the color that is used for painting the track from this Waypoint onwards
     * @param color
     */
    public void setSegmentColor(Color color) {
    	segmentColor = color;
    }
    
    public Hashtable<String, String> getExtensions() {
    	return extensions;
    }

    
    /**
     * calculate distance from this waypoint 
     * to @param wpt
     * @return distance in meters
     */
    public double getDistance(Waypoint wpt) {
    	double distance = 0;
    	if (wpt != null) { 
    		distance = OsmMercator.getDistance(this.getLat(), this.getLon(), wpt.getLat(), wpt.getLon());
        	if (Double.isNaN(distance)) {
        		distance = 0; 
        	}
    	}
       	return distance;
    }

    /**
     * calculate elapsed time between this waypoint 
     * and @param wpt (order matters!)
     * @return elapsed time in milliseconds
     */
    public long getDuration(Waypoint wpt) {
    	long duration = 0;
    	if ((wpt != null) && (this.getTime() != null) && (wpt.getTime() != null)) {
    		duration = this.getTime().getTime() - wpt.getTime().getTime();
    	}
    	return duration;
    }
}
