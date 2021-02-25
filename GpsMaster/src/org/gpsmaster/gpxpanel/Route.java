package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.tree.TreeNode;
import java.math.*;

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
    protected double estHikeHours = 0;

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
        this.minMaxExtensions = new HashMap<String, ExtensionMeta>(source.getMinMaxExtensions());
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

    public double getEstHikeHours() {
    	return estHikeHours;
    }

    public String getEstHikeHoursString() {
    	long hrs = Math.round(Math.floor(estHikeHours));
    	long mns = Math.round((estHikeHours - hrs) * 60);
    	return String.valueOf(hrs) + ":" + String.format("%02d", mns) + " h";
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
        if (0 != path.minMaxExtensions.size()) {
            for (String key : path.getMinMaxExtensions().keySet()) {
                ExtensionMeta pathMeta = path.getMinMaxExtensions().get(key);
                ExtensionMeta meta = minMaxExtensions.get(key);
                if (null == meta) {
                    meta = new ExtensionMeta();
                    meta.name = key;
                    minMaxExtensions.put(key, meta);
                }
                meta.values.addAll(pathMeta.values);
            }
        }
        
        // calculate the estimated duration of a hike and system out it if it has changed.
        // formula according to alpenverein austria
        // https://www.alpenverein.at/portal/news/aktuelle_news_kurz/2018/2018_06_14_wie-berechnet-man-die-gehzeit-auf-wanderwegen.php
        // todo: parameters (300, 500, 4) -> config file so one can adjust to own fitness level and experience 
        // todo: presets for strolling, hiking, fast hiking, running...
        // todo: show this value in property table of routes
        String compStr = getEstHikeHoursString();
        double vertTime = (grossRiseMeters / 300) + (grossFallMeters / 500);
        double horzTime = (lengthMeters / 1000) / 4;
        if (horzTime >= vertTime) {
        	estHikeHours = horzTime + (vertTime / 2);
        }
        else {
        	estHikeHours = vertTime + (horzTime / 2);
        }
        if (!getEstHikeHoursString().equals(compStr)) {
        	System.out.println("estHikeHours: " + getEstHikeHoursString());
        }

        extToColor();
    }

	// Methods implementing TreeNode interface

	public Enumeration<TreeNode> children() {
		return null;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	public int getChildCount() {
		return 0;
	}

	public int getIndex(TreeNode node) {
		return 0;
	}

	public boolean isLeaf() {
		return true;
	}
}
