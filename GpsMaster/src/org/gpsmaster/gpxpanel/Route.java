package org.gpsmaster.gpxpanel;

import java.awt.Color;

import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;


/**
 * 
 * The GPX "rte" element.
 * 
 * @author Matt Hoover
 *
 */
public class Route extends GPXObjectCommon {

    protected int number;
    protected String type;
    
    private WaypointGroup path;
    
    /**
     * Constructs a {@link Route} with the chosen color.
     * 
     * @param color     The color.
     */
    public Route(Color color) {
        super(color);
        this.path = new WaypointGroup(this.color, WptGrpType.ROUTE);
        this.path.setParent(this);
    }
    
    /**
     * Constructs a {@link Route} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link Route} to be cloned
     */
    public Route(Route source) {
    	this.path = new WaypointGroup(source.path);    
    	this.path.setParent(this);
    }
    
    public String toString() {
        String str = "Route";
        if (this.name != null && !this.name.equals("")) {
            str = str.concat(" - " + this.name);
        }
        return str;
    }
    
    public void setColor(Color color) {
        super.setColor(color);
        path.setColor(color);
    }

    public WaypointGroup getPath() {
        return path;
    }

    public int getNumPts() {
    	return path.getNumPts(); 
    }
    
    /* (non-Javadoc)
     * @see org.gpsmaster.gpxpanel.GPXObject#updateAllProperties()
     */
    @Override
    public void updateAllProperties() {
        path.updateAllProperties();
        
        duration = path.getDuration();
        maxSpeedMps = path.getMaxSpeedMps();
        lengthMeters = path.getLengthMeters();
        eleStartMeters = path.getEleStartMeters();
        eleEndMeters = path.getEleEndMeters();
        eleMinMeters = path.getEleMinMeters();
        eleMaxMeters = path.getEleMaxMeters();
        grossRiseMeters = path.getGrossRiseMeters();
        grossFallMeters = path.getGrossFallMeters();
        fallTime = path.getFallTime();
        riseTime = path.getRiseTime();
        minLat = path.getMinLat();
        minLon = path.getMinLon();
        maxLat = path.getMaxLat();
        maxLon = path.getMaxLon();
        
        extToColor();
    }
}
