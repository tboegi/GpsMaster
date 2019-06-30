package org.gpsmaster.dialogs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.GPXExtension;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import com.topografix.gpx._1._1.LinkType;

import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.util.XTime;

/**
 * Table model containing properties of the specified {@link GPXObject} 
 * @author rfu
 * (new version for self-contained {@link GpxPropertiesPanel})
 */
public class GpxPropertiesTableModel extends DefaultTableModel {

	/*
	 * This TableModel holds three values for each row:  
	 * 	1	Property Name as displayed in table (string)
	 * 	2	Property Value (object)
	 * 	3	is editable (boolean)
	 * 3rd column isn't displayed, for future (internal) use only
	 */

	private static final long serialVersionUID = -2702982954383747924L;
	private GPXObject gpxObject = null;
	private DateFormat sdf = null;
	private UnitConverter uc = null;
		     
	/**
	 * Default Constructor
	 */
	public GpxPropertiesTableModel(UnitConverter converter) {
		super(new Object[]{"Name", "Value"}, 0);
		setColumnCount(2);
		uc = converter;
		sdf = new SimpleDateFormat(Const.SDF_STANDARD);					    
	}
		
	/**
	 * 
	 * @param gpx
	 */
	public synchronized void setGpxObject(GPXObject gpx) {
		gpxObject = gpx;
		update();	
	}

	/**
	 * 
	 * @param trackpoint
	 */
	public void setTrackpoint(Waypoint trackpoint) {
		propsDisplayTrackpoint(trackpoint, -1);
	}	

	/**
	 * Show trackpoint properties
	 * @param trackpoint trackpoint to show properties of
	 * @param indexOf position (index) of trackpoint in parent list
	 */
	public void setTrackpoint(Waypoint trackpoint, int indexOf) {
		propsDisplayTrackpoint(trackpoint, indexOf);
	}	

    /**
     * 
     * @param links
     */
    private void propsDisplayLink(List<LinkType> links) {
    	for (LinkType link : links) {
    		// URL url = null;
    		String text = "link";
			if (link.getText() != null) {
				text = link.getText();
			}
			addRow(new Object[]{text, link.getHref()});
			/*
			try {
				url = new URL(link.getHref());
				addRow(new Object[]{text, url});
			} catch (MalformedURLException e) {
				addRow(new Object[]{text, "<malformed URL>"});
			}
			*/
    	}
    }
            
    /**
     * Display properties which are common to all GPX objects 
     * @param o
     */
    private void propsDisplayEssentials(GPXObject o) {
    	
    	Date startTime = o.getStartTime();
    	Date endTime = o.getEndTime();
    	
        if (startTime != null && endTime != null) {
            String startTimeString = "";
            String endTimeString = "";
            startTimeString = sdf.format(startTime);
            endTimeString = sdf.format(endTime);
            addRow(new Object[]{"start time", startTimeString, false});
            addRow(new Object[]{"end time", endTimeString, false});
        }
        
        if (o.getDuration() != 0) {
        	addRow(new Object[]{"duration", XTime.getDurationText(o.getDuration()), false});
        }        
        /* don't display while still buggy
        if (o.getDurationExStop() != 0) {
        	addRow(new Object[]{"duration ex stop", getTimeString(o.getDurationExStop())});
        }        
        */
        double distance = o.getLengthMeters();
        if (distance > 0) {
            addRow(new Object[]{"distance", uc.dist(distance, Const.FMT_DIST), false});
            
            addRow(new Object[]{"max speed", uc.speed(o.getMaxSpeedMps(), Const.FMT_SPEED), false});
            
            if (o.getDuration() > 0) {
            	double avgSpeed = distance / o.getDuration(); // meters per second	
            	addRow(new Object[]{"avg speed", uc.speed(avgSpeed, Const.FMT_SPEED), false});
            }
            /* don't display while still buggy
            if (o.getDurationExStop() > 0) {
            	double avgSpeedEx = (dist / o.getDurationExStop() * 3600000);	
            	addRow(new Object[]{"avg speed ex stop", String.format(speedFormat, avgSpeedEx)});
            }        	
			*/
        }
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayRiseFall(GPXObject o) {
    	       
    	double grossRise = o.getGrossRiseMeters();
    	double grossFall = o.getGrossFallMeters();
    	
        addRow(new Object[]{"gross rise", uc.ele(grossRise, Const.FMT_ELE), false});
        addRow(new Object[]{"gross fall", uc.ele(grossFall, Const.FMT_ELE), false});
        
		long riseTime = o.getRiseTime();
		if (riseTime > 0) {
			addRow(new Object[]{"rise time", XTime.getDurationText(riseTime), false});
		}
		long fallTime = o.getFallTime();
		if (fallTime > 0) {
			addRow(new Object[]{"fall time", XTime.getDurationText(fallTime), false});
		}
                		
        double avgRiseSpeed = grossRise / riseTime;
        if (Double.isNaN(avgRiseSpeed) || Double.isInfinite(avgRiseSpeed)) {
            avgRiseSpeed = 0;
        }
        if (avgRiseSpeed != 0) {
            addRow(new Object[]{"avg rise speed", uc.vertSpeed(avgRiseSpeed, Const.FMT_ELESPEED), false});
        }        
        double avgFallSpeed = grossFall / riseTime;
        if (Double.isNaN(avgFallSpeed) || Double.isInfinite(avgFallSpeed)) {
            avgFallSpeed = 0;
        }
        if (avgFallSpeed != 0) {
            addRow(new Object[]{"avg fall speed", uc.vertSpeed(avgFallSpeed, Const.FMT_ELESPEED), false});
        }
    }
    

    /**
     * 
     * @param o
     */
    private void propsDisplayElevation(GPXObject o) {

    	double eleStart = o.getEleStartMeters();
    	if (eleStart > 0) {
    		addRow(new Object[]{"elevation (start)", uc.ele(eleStart, Const.FMT_ELE), false});    		
    	}
    	double eleEnd = o.getEleEndMeters();
    	if (eleEnd > 0) {
    		addRow(new Object[]{"elevation (end)", uc.ele(eleEnd, Const.FMT_ELE), false});    		
    	}
    	
    	double eleMin = o.getEleMinMeters();
    	if (eleMin != Integer.MAX_VALUE) {
    		addRow(new Object[]{"min elevation", uc.ele(eleMin, Const.FMT_ELE), false});
    	}
    	double eleMax = o.getEleMaxMeters();
    	if (eleMax != Integer.MIN_VALUE) {    	
    		addRow(new Object[]{"max elevation", uc.ele(eleMax, Const.FMT_ELE), false});
    	}
    }

	/**
	 *
	 * @param o
	 */
	private void propsDisplayMinMaxExtensions(GPXObject o) {

		// There should always be a min AND max container with an identical key set
		if (null != o.getMinMaxExtensions()) {
			for (String key : o.getMinMaxExtensions().keySet()) {
				GPXObject.ExtensionMeta meta = o.getMinMaxExtensions().get(key);
				addRow(new Object[]{"min " + key, meta.getMin(), false});
				addRow(new Object[]{"max " + key, meta.getMax(), false});
				addRow(new Object[]{"avg " + key, String.format("%.2f", meta.getMean()), false});
				addRow(new Object[]{"med " + key, String.format("%.2f", meta.getMedian()), false});
				addRow(new Object[]{"sdv " + key, String.format("%.2f", meta.getStandardDeviation()), false});
			}
		}
	}

    /**
	 * displays the properties of a trackpoint 
	 * 
	 * @param wpt
	 */
	private void propsDisplayTrackpoint(Waypoint wpt, int indexOf) {
				
		if (wpt != null) {
			clear();
			// mandatory
			if (indexOf > -1) {
				addRow(new Object[]{"trackpoint #", indexOf, false});
			}
			addRow(new Object[]{"latitude", wpt.getLat(), false});
			addRow(new Object[]{"longitude", wpt.getLon(), false});
			addRow(new Object[]{"elevation", uc.ele(wpt.getEle(), Const.FMT_ELE), false});
			Date time = wpt.getTime();
			
			// optional
			if (time != null) {
				addRow(new Object[]{"time", sdf.format(time), false});
			}			
			if (wpt.getSat() > 0) { addRow(new Object[]{"sat", wpt.getSat(), false}); }
			if (wpt.getHdop() > 0) { addRow(new Object[]{"hdop", wpt.getHdop(), false}); }
			if (wpt.getVdop() > 0) { addRow(new Object[]{"vdop", wpt.getVdop(), false}); }
			if (wpt.getPdop() > 0) { addRow(new Object[]{"pdop", wpt.getPdop(), false}); }
			if (wpt.getName().isEmpty() == false) {
				addRow(new Object[]{"name", wpt.getName(), true});
			}
			if (wpt.getDesc().isEmpty() == false) {
				addRow(new Object[]{"desc", wpt.getDesc(), true});
			}
			if (wpt.getType().isEmpty() == false) {
				addRow(new Object[]{"type", wpt.getType(), true});
			}
			if (wpt.getCmt().isEmpty() == false) {
				addRow(new Object[]{"cmt", wpt.getCmt(), true});
			}
			if (wpt.getSrc().isEmpty() == false) {
				addRow(new Object[]{"src", wpt.getSrc(), true});
			}
			if (wpt.getSym().isEmpty() == false) {
				addRow(new Object[]{"sym", wpt.getSym(), true});
			}
			if (wpt.getFix().isEmpty() == false) {
				addRow(new Object[]{"fix", wpt.getFix(), true});
			}
			propsDisplayLink(wpt.getLink());
			if (wpt.getMagvar() > 0) { addRow(new Object[]{"magvar", wpt.getMagvar(), false}); }
			if (wpt.getGeoidheight() > 0) { addRow(new Object[]{"geoidheight", wpt.getGeoidheight(), false}); }
			if (wpt.getAgeofdgpsdata() > 0) { addRow(new Object[]{"ageofdgpsdata", wpt.getAgeofdgpsdata(), false}); }
			if (wpt.getDgpsid() > 0) { addRow(new Object[]{"dgpsid", wpt.getDgpsid(), false}); }
			if (wpt.getExtension().getExtensions() != null) {
				propsDisplayExtension(wpt.getExtension());
			}
		}
	}

	private void propsDisplayExtension(GPXExtension extension) {
		if (extension != null) {
			for (GPXExtension sub : extension.getExtensions()) {
				if (sub.getValue() != null) {
					String[] split = sub.getKey().split(":");
					addRow(new Object[]{split[split.length - 1], sub.getValue(), true});
//					addRow(new Object[]{sub.getKey(), sub.getValue(), true});
				}
				propsDisplayExtension(sub);
			}
		}
	}

	/**
     * 
     * @param o
     */
    private void propsDisplayWaypointGrp(GPXObject o) {
    	WaypointGroup wptGrp = (WaypointGroup) o;
    	addRow(new Object[]{"name", wptGrp.getName(), true});
        addRow(new Object[]{"# of pts", wptGrp.getWaypoints().size(), false});
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayRoute(GPXObject o) {
    	Route route = (Route) o;
    	addRow(new Object[]{"name", route.getName(), true});
        addRow(new Object[]{"# of pts", route.getNumPts(), false});
    }
    /**
     * 
     * @param o
     */
    private void propsDisplayTrack(GPXObject o) {
    	
    	Track track = (Track) o;
    	if (track.getName() != null) {
    		addRow(new Object[]{"track name", track.getName(), true});
    	}
    	if (track.getDesc() != null) {
    		addRow(new Object[]{"desc", track.getDesc(), true});
    	}
    	if (track.getType() != null) {
    		addRow(new Object[]{"type", track.getType(), true});
    	}

    	if (track.getTracksegs().size() > 0) {
    		addRow(new Object[]{"segments", track.getTracksegs().size(), false});
    	}
    	if (track.getNumPts() > 0) {
    		addRow(new Object[]{"# of pts", track.getNumPts(), false});
    	}
        if (track.getNumber() != 0) {
            addRow(new Object[]{"track number", track.getNumber(), true}); // editable?
        }    	
        propsDisplayLink(track.getLink());
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayGpxFile(GPXObject o) {
    	
    	GPXFile gpxFile = (GPXFile) o;
        addRow(new Object[]{"GPX name", gpxFile.getMetadata().getName(), true});
        if (gpxFile.getMetadata().getDesc() != null) {
            addRow(new Object[]{"GPX desc", gpxFile.getMetadata().getDesc(), true});
        }
        if (!gpxFile.getCreator().isEmpty()) {
        	addRow(new Object[]{"creator", gpxFile.getCreator()});
        }
        
        // if (!gpxFile.getMetadata().getLink().isEmpty()) {
        // addRow(new Object[]{"link", gpxFile.getLink()});
        // }
        String timeString = "";
        if (gpxFile.getMetadata().getTime() != null) {
            Date time = gpxFile.getMetadata().getTime();
            timeString = sdf.format(time);
        }
        addRow(new Object[]{"GPX time", timeString, false}); // show even if empty
        if (gpxFile.getRoutes().size() > 0) {
        	addRow(new Object[]{"# of routes", gpxFile.getRoutes().size(), false});
        }
        if (gpxFile.getTracks().size() > 0) {
        	addRow(new Object[]{"# of tracks", gpxFile.getTracks().size(), false});
        }
        if (gpxFile.getNumWayPts() > 0) {
        	addRow(new Object[]{"# of waypoints", gpxFile.getNumWayPts(), false});
        }
        if (gpxFile.getNumTrackPts() > 0) {
        	addRow(new Object[]{"# of trackpoints", gpxFile.getNumTrackPts(), false});
        }        
        
    }
    
    /**
     * show properties of current GPX object in properties table   
     */
    private void update() {
    	clear();
    	if (gpxObject != null) {
    		if (gpxObject.isGPXFile()) {
	    		propsDisplayGpxFile(gpxObject);
	            propsDisplayEssentials(gpxObject);
	            propsDisplayElevation(gpxObject);
	            propsDisplayRiseFall(gpxObject);
				propsDisplayMinMaxExtensions(gpxObject);
	    	} else if (gpxObject.isTrack()) {
	    		propsDisplayTrack(gpxObject);
	            propsDisplayEssentials(gpxObject);
	            propsDisplayElevation(gpxObject);            
	            propsDisplayRiseFall(gpxObject);
				propsDisplayMinMaxExtensions(gpxObject);
	    	} else if (gpxObject.isRoute()) {
	    		propsDisplayRoute(gpxObject);
	    		propsDisplayEssentials(gpxObject);
	    		propsDisplayElevation(gpxObject);
				propsDisplayMinMaxExtensions(gpxObject);
	    	} else if (gpxObject.isTrackseg()) {
	    		propsDisplayWaypointGrp(gpxObject);
	    		propsDisplayEssentials(gpxObject);
	    		propsDisplayElevation(gpxObject);
	    		propsDisplayRiseFall(gpxObject);
				propsDisplayMinMaxExtensions(gpxObject);
	    	} else if (gpxObject.isWaypointGroup()) {
	    		propsDisplayWaypointGrp(gpxObject);
	    		propsDisplayElevation(gpxObject);
	    	}
    	}    	
    }
        
    /**
     * Clear properties table
     */
    public void clear() {
    	setRowCount(0);    	
    }
}
