package org.gpsmaster.gpxpanel;

import java.awt.Color;

import javax.xml.bind.annotation.XmlElement;

/**
 * extends GPXObject with Name & Description members
 * 
 * @author rfu
 *
 */
public abstract class GPXObjectND extends GPXObject {

    protected String name;
    protected String desc;
    

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
    
    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
