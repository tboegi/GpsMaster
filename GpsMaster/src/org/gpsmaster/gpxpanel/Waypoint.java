package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.gpsmaster.Const;
import org.openstreetmap.gui.jmapviewer.OsmMercator;

import com.topografix.gpx._1._1.LinkType;

/**
 * 
 * The GPX "wpt" element.
 * 
 * @author Matt Hoover
 * @author rfu
 */
public class Waypoint implements Comparable<Waypoint> {

    private double lat;
    private double lon;
    private double ele;
    private double hdop;
    private double vdop;
    private double pdop;
    private double magvar;
    private double geoidheight;
    private double ageofdgpsdata;
    private int sat = 0;
    private int dgpsid = 0;
    private Date time = null;
    protected String name = "";
    protected String desc = "";
    protected String type = "";
    protected String cmt = "";
    protected String sym = "";
    protected String src = "";
    protected String fix = "";
        
    protected Color segmentColor = null;
    protected ArrayList<LinkType> links = null;
    
    private GPXExtension extension = null;
    
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
    	// TODO all others also

    	if (source.extension != null) {
    		extension = new GPXExtension(source.extension);
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

    /**
     * Gets the value of the cmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCmt() {
        return cmt;
    }

    /**
     * Sets the value of the cmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCmt(String value) {
        this.cmt = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the value of the src property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrc() {
        return src;
    }

    /**
     * Sets the value of the src property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrc(String value) {
        this.src = value;
    }

    /**
     * Gets the value of the link property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the link property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkType }
     * 
     * 
     */
    public List<LinkType> getLink() {
        if (links == null) {
            links = new ArrayList<LinkType>();
        }
        return this.links;
    }

    /**
     * Gets the value of the sym property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSym() {
        return sym;
    }

    /**
     * Sets the value of the sym property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSym(String value) {
        this.sym = value;
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
     * Gets the value of the magvar property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public double getMagvar() {
        return magvar;
    }

    /**
     * Sets the value of the magvar property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMagvar(double value) {
        this.magvar = value;
    }

    /**
     * Gets the value of the geoidheight property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public double getGeoidheight() {
        return geoidheight;
    }

    /**
     * Sets the value of the geoidheight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setGeoidheight(double value) {
        this.geoidheight = value;
    }

    /**
     * Gets the value of the fix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFix() {
        return fix;
    }

    /**
     * Sets the value of the fix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFix(String value) {
        this.fix = value;
    }

    /**
     * Gets the value of the ageofdgpsdata property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public double getAgeofdgpsdata() {
        return ageofdgpsdata;
    }

    /**
     * Sets the value of the ageofdgpsdata property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAgeofdgpsdata(double value) {
        this.ageofdgpsdata = value;
    }

    /**
     * Gets the value of the dgpsid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getDgpsid() {
        return dgpsid;
    }

    /**
     * Sets the value of the dgpsid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDgpsid(int value) {
        this.dgpsid = value;
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

    /**
     * 
     * @return
     */
    public GPXExtension getExtension() {
    	if (extension == null) {
    		extension = new GPXExtension(Const.TAG_EXTENSIONS);
    	}
    	return extension;
    }
    
    public void setExtension(GPXExtension extension) {
    	this.extension = extension;
    }
    
    /**
     * calculate distance from this waypoint 
     * to @param wpt
     * @return distance in meters
     */
    public double getDistance(Waypoint wpt) {
    	double distance = 0;
    	if (wpt != null) { 
    		distance = OsmMercator.MERCATOR_256.getDistance(this.getLat(), this.getLon(), wpt.getLat(), wpt.getLon());
        	if (Double.isNaN(distance)) {
        		distance = 0; 
        	}
    	}
       	return distance;
    }

    /**
     * calculate elapsed time between this waypoint 
     * and @param wpt (order matters!)
     * @return elapsed time in seconds
     */
    public long getDuration(Waypoint wpt) {
    	long duration = 0;
    	if ((wpt != null) && (this.getTime() != null) && (wpt.getTime() != null)) {
    		duration = wpt.getTime().getTime() - this.getTime().getTime();
    	}
    	return (long) (duration / 1000);
    }

	@Override
	public int compareTo(Waypoint o) {
		if ((getTime() == null) || (o.getTime() == null)) {
			return 0;
		}
		return getTime().compareTo(o.getTime());
	}


}
