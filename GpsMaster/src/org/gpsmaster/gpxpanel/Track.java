package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;
import javax.xml.bind.annotation.XmlElement;

import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;

/**
 * 
 * The GPX "trk" element.
 * 
 * @author Matt Hoover
 *
 */
public class Track extends GPXObjectCommon implements Comparable<Track> {
    
    private List<WaypointGroup> tracksegs = new ArrayList<WaypointGroup>();
    
    /**
     * Constructs a {@link Track} with the chosen color.
     * 
     * @param color     The color.
     */
    public Track(Color color) {
        super(color); 
    }
    
    /**
     * Constructs a {@link Track} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link Track} to be cloned
     */
    public Track(Track source) {
    	super(source);
    	for (WaypointGroup wptGrp : source.tracksegs) {
    		this.tracksegs.add(new WaypointGroup(wptGrp));
    	}
    }
    
    /**
     * 
     */
    public String toString() {
        String str = "Track";
        if (this.name != null && !this.name.equals("")) {
            str = str.concat(" - " + this.name);
        }
        return str;
    }
    
    /**
     * 
     */
    public void setColor(Color color) {
        super.setColor(color);
        for (WaypointGroup trackseg : tracksegs) {
            trackseg.setColor(color);
        }
    }
    
    @XmlElement(name = "trkseg")
    public List<WaypointGroup> getTracksegs() {
        return tracksegs;
    }
    
    /***
     * add a new, empty trackseg
     * @return the newly added, empty tracj segment
     */
    public WaypointGroup addTrackseg() {
        WaypointGroup trackseg = new WaypointGroup(this.color, WptGrpType.TRACKSEG);
        trackseg.setParent(this);
        tracksegs.add(trackseg);
        return trackseg;
    }

    public WaypointGroup addTrackseg(WaypointGroup trackSeg) {        
        trackSeg.setParent(this);
        tracksegs.add(trackSeg);
        return trackSeg;
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
        maxSpeedMps = 0;
        duration = 0;
        eleMinMeters = Integer.MAX_VALUE;
        eleMaxMeters = Integer.MIN_VALUE;
        riseTime = 0;
        fallTime = 0;
        grossRiseMeters = 0;
        grossFallMeters = 0;
        minLat =  86;
        maxLat = -86;
        minLon =  180;
        maxLon = -180;
        
        for (WaypointGroup trackseg : tracksegs) {
            trackseg.updateAllProperties();
            
            duration += trackseg.getDuration();
            exStop += trackseg.getDurationExStop();
            maxSpeedMps = Math.max(maxSpeedMps, trackseg.getMaxSpeedMps());
            lengthMeters += trackseg.getLengthMeters();
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
            if (0 != trackseg.minMaxExtensions.size()) {
                for (String key : trackseg.getMinMaxExtensions().keySet()) {
                    ExtensionMeta segMeta = trackseg.getMinMaxExtensions().get(key);
                    ExtensionMeta meta = minMaxExtensions.get(key);
                    if (null == meta) {
                        meta = new ExtensionMeta();
                        meta.name = key;
                        minMaxExtensions.put(key, meta);
                    }
                    meta.values.addAll(segMeta.values);
                }
            }
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
        extToColor();
    }

	@Override
	public int compareTo(Track o) {
		if ((getStartTime() == null) || (o.getStartTime() == null)) {
			return 0;
		}
		return getStartTime().compareTo(o.getStartTime());
	}

	// Methods implementing TreeNode interface
	
	public Enumeration<WaypointGroup> children() {
		return Collections.enumeration(tracksegs);
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {
		return tracksegs.get(childIndex);
	}

	public int getChildCount() {
		return tracksegs.size();
	}

	public int getIndex(TreeNode node) {
		return tracksegs.indexOf(node);
	}

	public boolean isLeaf() {
		return (tracksegs.size() == 0);
	}

}
