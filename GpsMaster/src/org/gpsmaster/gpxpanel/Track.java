package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;


/**
 *
 * The GPX "trk" element.
 *
 * @author Matt Hoover
 *
 */
public class Track extends GPXObject {

    protected int number;
    protected String type;

    private List<WaypointGroup> tracksegs = new ArrayList<WaypointGroup>();

    /**
     * Constructs a {@link Track} with the chosen color.
     *
     * @param color     The color.
     */
    public Track(Color color) {
        super(color);
        this.type = "";

    }

    /**
     * Constructs a {@link Track} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link Track} to be cloned
     */
    public Track(Track source) {
    	super(source);
    	this.number = source.number;
    	this.type = source.type;
    	for (WaypointGroup wptGrp : source.tracksegs) {
    		this.tracksegs.add(new WaypointGroup(wptGrp));
    	}
    }

    public String toString() {
        String str = "Track";
        if (this.name != null && !this.name.equals("")) {
            str = str.concat(" - " + this.name);
        }
        return str;
    }

    public void setColor(Color color) {
        super.setColor(color);
        for (WaypointGroup trackseg : tracksegs) {
            trackseg.setColor(color);
        }
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

    public List<WaypointGroup> getTracksegs() {
        return tracksegs;
    }

    public WaypointGroup addTrackseg() {
        WaypointGroup trackseg = new WaypointGroup(this.color, WptGrpType.TRACKSEG);
        tracksegs.add(trackseg);
        return trackseg;
    }

    /**
     *
     * @return
     */
    public long getNumPts() {
        long ctr = 0;
        for (WaypointGroup wptGrp : tracksegs) {
        	ctr += wptGrp.getNumPts();
        }
        return ctr;
    }

    /* (non-Javadoc)
     * @see org.gpsmaster.gpxpanel.GPXObject#updateAllProperties()
     */
    @Override
    public void updateAllProperties() {
    	lengthMeters = 0;
        maxSpeedKmph = 0;
        eleMinMeters = Integer.MAX_VALUE;
        eleMaxMeters = Integer.MIN_VALUE;
        minLat =  86;
        maxLat = -86;
        minLon =  180;
        maxLon = -180;

        for (WaypointGroup trackseg : tracksegs) {
            trackseg.updateAllProperties();

            duration += trackseg.getDuration();
            exStop += trackseg.getDurationExStop();
            maxSpeedKmph = Math.max(maxSpeedKmph, trackseg.getMaxSpeedKmph());
            lengthMeters += trackseg.getLengthMeters();
            // lengthMiles += trackseg.getLengthMiles();
            eleMinMeters = Math.min(eleMinMeters, trackseg.getEleMinMeters());
            eleMaxMeters = Math.max(eleMaxMeters, trackseg.getEleMaxMeters());
            grossRiseMeters += trackseg.getGrossRiseMeters();
            grossFallMeters += trackseg.getGrossFallMeters();
            riseTime += trackseg.getRiseTime();
            fallTime += trackseg.getFallTime();

            minLat = Math.min(minLat, trackseg.getMinLat());
            minLon = Math.min(minLon, trackseg.getMinLon());
            maxLat = Math.max(maxLat, trackseg.getMaxLat());
            maxLon = Math.max(maxLon, trackseg.getMaxLon());
        }

        if (tracksegs.size() > 0) {
            eleStartMeters = tracksegs.get(0).getEleStartMeters();
            eleEndMeters = tracksegs.get(tracksegs.size() - 1).getEleEndMeters();
            startTime = tracksegs.get(0).getStartTime();
            endTime = tracksegs.get(tracksegs.size() - 1).getEndTime();
        } else {
            eleStartMeters = 0;
            eleEndMeters = 0;
        }
    }
}
