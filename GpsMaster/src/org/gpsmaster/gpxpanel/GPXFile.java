package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;
import javax.xml.bind.annotation.XmlElement;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;

import com.topografix.gpx._1._1.BoundsType;

/**
 * 
 * Top level GPX file element.  Contains all other GPX element types.
 * 
 * @author Matt Hoover
 * @author rfu
 *
 */
public class GPXFile extends GPXObject /* implements TreeNode */ {
    
    private String creator;
    private GpxMetadata metadata;
    private WaypointGroup waypointGroup;
    private List<Route> routes = new ArrayList<Route>();
    private List<Track> tracks = new ArrayList<Track>();
    // List of sourceFmt key prefixes used in this file.
    private List<String> extPrefixes = new ArrayList<String>();
    
    private long dbId = -1;
    
    /**
     * Creates an empty {@link GPXFile}.
     */
    public GPXFile() {
        super(true);    	
        // this.name = "UnnamedFile";
        this.metadata = new GpxMetadata();
        this.metadata.setBounds(new BoundsType());
        this.trackPtsVisible = false;
        this.creator = GpsMaster.ME;         
        this.waypointGroup = new WaypointGroup(color, WptGrpType.WAYPOINTS); 
        this.waypointGroup.setParent(this);
        
        // register sourceFmt prefix "gpsm:" anyway:
        extPrefixes.add(Const.EXT_GPSM_PRE);
    }
    
    /**
     * Creates an empty {@link GPXFile}.
     * 
     * @param name      The name of the route. 
     */
    public GPXFile(String name) {
        this();
        if (!name.equals("")) {
            metadata.setName(name);
        }
    }
    
    /**
     * Constructs a {@link GPXFile} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link GPXFile} to be cloned
     */
    public GPXFile(GPXFile source) {
    	super(source);
    	this.creator = source.creator;
    	this.metadata = source.metadata;
    	this.waypointGroup = new WaypointGroup(source.waypointGroup);
    	for (Track track : source.tracks) {
    		this.tracks.add(new Track(track));    		
    	}
    	for (Route route : source.routes) {
    		this.routes.add(new Route(route));
    	}    	
    }
    
    /**
     * 
     * @return
     */
    public long getNumTrackPts() {
    	long ctr = 0;
    	for (Track track : tracks) {
    		ctr += track.getNumPts();
    	}
    	return ctr;
    }

    /**
     * 
     * @return
     */
    public long getNumRoutePts() {
    	long ctr = 0;
    	for (Route route : routes) {
    		ctr += route.getNumPts();
    	}
    	return ctr;
    }

    /**
     * 
     * @return
     */
    public long getNumWayPts() {

    	return waypointGroup.getNumPts();
    }

        
    public void setColor(Color color) {
        super.setColor(color);
        waypointGroup.setColor(color);
        for (Route route : routes) {
            route.setColor(color);
        }
        for (Track track : tracks) {
            track.setColor(color);
        }
    }

    public String getCreator() {
        return creator;
    }
    
    public void setCreator(String cr) {
        this.creator = cr;
    }

    @XmlElement
    public GpxMetadata getMetadata() {
    	return metadata;
    }

    public String getName() {
    	return metadata.getName();
    }
    
    public void setName(String name) {
    	metadata.setName(name);
    }
    
    public String getDesc() {
    	return metadata.getDesc();
    }
    
    public void setDesc(String desc) {
    	metadata.setDesc(desc);
    }

    public WaypointGroup getWaypointGroup() {
        return waypointGroup;
    }

    @XmlElement(name = "rte")
    public List<Route> getRoutes() {
        return routes;
    }
    
    public void addTrack(Track track) {
    	track.setParent(this);
    	tracks.add(track);
    }
    
    // obsolete this
    public Route addRoute() {
        Route route = new Route(color);
        route.setParent(this);
        route.setName(metadata.getName());
        routes.add(route);
        return route;
    }
    
    public void addRoute(Route route) {
    	route.setParent(this);
    	routes.add(route);
    }
    
    @XmlElement(name = "trk")
    public List<Track> getTracks() {
        return tracks;
    }

    @Override
    public double getMinLat() {
        return metadata.getBounds().getMinlat().doubleValue();
    }

    @Override
    public double getMinLon() {
        return metadata.getBounds().getMinlon().doubleValue();
    }

    @Override
    public double getMaxLat() {
    	return metadata.getBounds().getMaxlat().doubleValue();
    }
    
    @Override
    public double getMaxLon() {
    	return metadata.getBounds().getMaxlon().doubleValue();
    }

    public String toString() {
        return metadata.getName();
    }
  
    /**
     * Register a prefix that is used in any sourceFmt key in this file,
     * i.e. for sourceFmt key "hrm:hr", the prefix is "hrm".
     * this allows the GPX writer to create the proper XML namespace attributes.
     * @param prefix
     * 
     * NOTE this could also be achieved by scanning all extensions when needed,
     * but is avoided here (for performance reasons?)
     */
    public void addExtensionPrefix(String prefix) {
    	if (extPrefixes.contains(prefix) == false) {
    		extPrefixes.add(prefix);
    	}
    }
    
    /**
     * 
     * @return
     */
    public List<String> getExtensionPrefixes() {
    	return extPrefixes;
    }
    
    /* (non-Javadoc)
     * @see org.gpsmaster.gpxpanel.GPXObject#updateAllProperties()
     */
    @Override
    public void updateAllProperties() {
    	
    	lengthMeters = 0;
    	duration = 0;
    	maxSpeedMps = 0;
    	riseTime = 0;
    	fallTime = 0;
    	grossRiseMeters = 0;
    	grossFallMeters = 0;
    	
        if (waypointGroup.getWaypoints().size() > 1) {
            waypointGroup.updateAllProperties();
        }
        for (Route route : routes) {
            route.updateAllProperties();
        }
        for (Track track : tracks) {
            track.updateAllProperties();
        }
        
        minLat =  86;
        maxLat = -86;
        minLon =  180;
        maxLon = -180;
        eleMinMeters = Integer.MAX_VALUE;
        eleMaxMeters = Integer.MIN_VALUE;
        for (Route route : routes) {
            minLat = Math.min(minLat, route.getMinLat()); // vereinheitlichen
            minLon = Math.min(minLon, route.getMinLon());
            maxLat = Math.max(maxLat, route.getMaxLat());
            maxLon = Math.max(maxLon, route.getMaxLon());
        }
        for (Track track : tracks) {
            minLat = Math.min(minLat, track.getMinLat()); // vereinheitlichen
            minLon = Math.min(minLon, track.getMinLon());
            maxLat = Math.max(maxLat, track.getMaxLat());
            maxLon = Math.max(maxLon, track.getMaxLon());
            eleMinMeters = Math.min(eleMinMeters, track.getEleMinMeters());
            eleMaxMeters = Math.max(eleMaxMeters, track.getEleMaxMeters());
            lengthMeters += track.getLengthMeters();
            duration += track.getDuration();
            exStop += track.getDurationExStop();
            
            maxSpeedMps = Math.max(maxSpeedMps, track.getMaxSpeedMps());            
            grossRiseMeters += track.getGrossRiseMeters();
            grossFallMeters += track.getGrossFallMeters();
            riseTime += track.getRiseTime();
            fallTime += track.getFallTime();
            if (0 != track.minMaxExtensions.size()) {
                for (String key : track.getMinMaxExtensions().keySet()) {
                    ExtensionMeta trackMeta = track.getMinMaxExtensions().get(key);
                    ExtensionMeta meta = minMaxExtensions.get(key);
                    if (null == meta) {
                        meta = new ExtensionMeta();
                        meta.name = key;
                        minMaxExtensions.put(key, meta);
                    }
                    meta.values.addAll(trackMeta.values);
                }
            }
        }
        for (Waypoint waypoint : waypointGroup.getWaypoints()) {
            minLat = Math.min(minLat, waypoint.getLat()); // vereinheitlichen
            minLon = Math.min(minLon, waypoint.getLon());
            maxLat = Math.max(maxLat, waypoint.getLat());
            maxLon = Math.max(maxLon, waypoint.getLon());
        }
        if (tracks.size() > 0) {
        	startTime = tracks.get(0).getStartTime();
        	eleStartMeters = tracks.get(0).getEleStartMeters();
        	endTime = tracks.get(tracks.size()-1).getEndTime();
        	eleEndMeters = tracks.get(tracks.size()-1).getEleEndMeters();
        }
        
        // if time in GPX file is not specified, use time of first waypoint
        // TODO this actually belongs into the Track class ...
        if ((metadata.getTime() == null) && (tracks.size() > 0)) { 
        	Track track = tracks.get(0);
        	if (track.getTracksegs().size() > 0) {
        		Waypoint wpt = track.getTracksegs().get(0).getStart();
        		if (wpt != null) {
        			metadata.setTime(wpt.getTime());
        		}
        	}
        }
        extToColor();
        metadata.getBounds().setMaxlat(new BigDecimal(maxLat));
        metadata.getBounds().setMaxlon(new BigDecimal(maxLon));
        metadata.getBounds().setMinlat(new BigDecimal(minLat));
        metadata.getBounds().setMinlon(new BigDecimal(minLon));
        
    }

	/**
	 * link to the database record, in case this {@link GPXFile} 
	 * is stored in the database.
	 * 
	 * @return the dbId or 0 if not in database.
	 */
	public long getDbId() {
		return dbId;
	}

	/**
	 * @param dbId the dbId to set
	 */
	public void setDbId(long dbId) {
		this.dbId = dbId;
	}

	public Enumeration<TreeNode> children() {
		return Collections.enumeration(childList());
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {		
		return childList().get(childIndex);
	}

	public int getChildCount() {
		int count = 0;
		if (getWaypointGroup().getNumPts() > 0) {
			count++;
		}
		count = count + tracks.size() + routes.size();
		return count;
	}

	public int getIndex(TreeNode node) {
		return childList().indexOf(node);
	}

	public boolean isLeaf() {
		return false;
	}
	
	// helper function for TreeNode interface methods
	// poor performance, since this list is created on every TreeNode method call
	// TODO replace it by code figuring out the actual position within the GPXFile members
	private List<TreeNode> childList() {
		List<TreeNode> children = new ArrayList<TreeNode>();
		if (waypointGroup.getNumPts() > 0) {
			children.add(waypointGroup);
		}
		children.addAll(routes);
		children.addAll(tracks);		
		return children;
	}
}
