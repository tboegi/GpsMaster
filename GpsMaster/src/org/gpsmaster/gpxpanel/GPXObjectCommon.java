package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.topografix.gpx._1._1.LinkType;

/**
 * a GPXObject with members common to track & route
 * 
 * @author rfu
 *
 */
public abstract class GPXObjectCommon extends GPXObjectND {

    protected int number;
    private String cmt;
    private String src;	
    protected String type;
    
    private List<LinkType> link = null;
    

    public GPXObjectCommon() {
    	super();
    }
    
    public GPXObjectCommon(GPXObjectCommon source) {
    	super(source);
    	this.cmt = source.cmt;
    	this.src = source.src;
    	this.number = source.number;
    	this.type = source.type;    	
    }
    
    public GPXObjectCommon(boolean randomColor) {
    	super(randomColor);
    }
    
    public GPXObjectCommon(Color color) {
    	super(color);
    }
    
    @XmlElement
    public String getCmt() {
		return cmt;
	}

	public void setCmt(String cmt) {
		this.cmt = cmt;
	}

	@XmlElement
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @XmlElement
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

    public List<LinkType> getLink() {
        if (link == null) {
            link = new ArrayList<LinkType>();
        }
        return this.link;
    }

}
