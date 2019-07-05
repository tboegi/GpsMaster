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
public class Route extends GPXObject {

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
        this.type = "";
        this.path = new WaypointGroup(this.color, WptGrpType.ROUTE);
    }

    /**
     * Constructs a {@link Route} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link Route} to be cloned
     */
    public Route(Route source) {
    	this.number = source.number;
    	this.type = source.type;
    	this.path = new WaypointGroup(source.path);
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        maxSpeedKmph = path.getMaxSpeedKmph();
        lengthMeters = path.getLengthMeters();
        eleStartMeters = path.getEleStartMeters();
        eleEndMeters = path.getEleEndMeters();
        eleMinMeters = path.getEleMinMeters();
        eleMaxMeters = path.getEleMaxMeters();
        grossRiseMeters = path.getGrossRiseMeters();
        grossFallMeters = path.getGrossFallMeters();
        fallTime = path.getFallTime();
        minLat = path.getMinLat();
        minLon = path.getMinLon();
        maxLat = path.getMaxLat();
        maxLon = path.getMaxLon();
    }
}
