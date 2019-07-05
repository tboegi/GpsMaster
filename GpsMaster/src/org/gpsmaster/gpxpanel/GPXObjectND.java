package org.gpsmaster.gpxpanel;

import java.awt.Color;

/**
 * a GPXObject with name and description.
 *
 * @author rfu
 *
 */
public abstract class GPXObjectND extends GPXObject {

    protected String name = "";
    protected String desc = "";


    public GPXObjectND() {
    	super();
    }

    public GPXObjectND(GPXObjectND source) {
    	super(source);
    	this.name = source.name;
    	this.desc = source.desc;
    }

    public GPXObjectND(boolean randomColor) {
    	super(randomColor);
    }

    public GPXObjectND(Color color) {
    	super(color);
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


}
