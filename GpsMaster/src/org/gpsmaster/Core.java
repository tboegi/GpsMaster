package org.gpsmaster;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointComparator;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


/*
 * Class providing some basic functionality (stateless). 
 * to be used from GUI or commandline batch processing
 * TODO static?
 */
public class Core {

	private int requestChunkSize = 200;
	private boolean isCancelled = false;

	/**
	 * all track segments
	 */
	public static final int SEG_TRACK = 0;
	
	/**
	 * all route segments
	 */	
	public static final int SEG_ROUTE = 1;

	/**
	 * Waypoints only
	 */	
	public static final int SEG_WAYPOINTS = 2;

	/**
	 * all track & route segments
	 */
	public static final int SEG_ROUTE_TRACK = 3;
	
	/**
	 * waypoint group and all track & route segments
	 */
	public static final int SEG_TRACK_ROUTE_WAYPOINTS = 4;
	
	/**
	 * waypoint group and all track segments
	 */
	public static final int SEG_TRACK_WAYPOINTS = 5;
	
	/**
	 * waypoint group and all route segments
	 */
	public static final int SEG_ROUTE_WAYPOINTS = 6;

	/**
	 * all groups
	 */
	public static final int SEG_ALL = 7;

	/**
	 * Default Constructor
	 */
	public Core() {
		
	}

	/**
	 * get size of chunks for requests to web services like mapquest
	 * @return
	 */
	public int getRequestChunkSize() {
		return requestChunkSize;
	}

	/**
	 * set size of chunks for requests to web services like mapquest
	 * 
	 * @param requestChunkSize
	 */
	public void setRequestChunkSize(int requestChunkSize) {
		this.requestChunkSize = requestChunkSize;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public void cancel() {
		isCancelled = true;
	}

	
    /* TIMESHIFT METHODS
     * -------------------------------------------------------------------------------------------------------- */        
	
	/**
	 * Assume that time in waypoints is in local time
	 * and convert it to UTC
	 * @param wptGrp
	 */
	public void localToUtc(WaypointGroup wptGrp) {
		for (Waypoint wpt : wptGrp.getWaypoints()) {
			if (wpt.getTime() != null) {
				DateTime utcDate = new DateTime(wpt.getTime()).toDateTime(DateTimeZone.UTC);
				wpt.setTime(utcDate.toDate());
			}
		}
	}
	
	/**
	 * 
	 * @param waypointGroups
	 */
	public static void clearTimestamps(WaypointGroup waypointGroup) {
		for (Waypoint wpt : waypointGroup.getWaypoints()) {
			wpt.setTime(null);
			
		}
	}
		
	/**
	 * Remove all timestamps (set to NULL) in specified waypointGroups
	 * @param waypointGroups
	 */
	public static void clearTimestamps(List<WaypointGroup> waypointGroups) {
		for (WaypointGroup waypointGroup : waypointGroups) {
			clearTimestamps(waypointGroup);
		}
	}
	
	/**
	 * shift waypoint time by a given interval
	 * @param group {@link WaypointGroup} containing {@link Waypoint}s to be shifted 
	 * @param delta interval in milliseconds, signed. use negative numbers to shift back in time.
	 */
	public static void timeShiftDelta(WaypointGroup group, long delta) {
    	if (group != null) {
    		for (Waypoint wpt : group.getWaypoints()) {
    			if (wpt.getTime() != null) {
    				Date newDate = new Date(wpt.getTime().getTime() + delta);
    				wpt.setTime(newDate);
    			}
    		}    		
    	}
	}

	/**
	 * Get Group of Waypoints from given {@link GPXObject} 
	 * @param gpxObject
	 * @return List with one element (WaypointGroup) or NULL if none within gpxObject
	 */
	public List<WaypointGroup> getWaypointGroup(GPXObject gpxObject) {
		List<WaypointGroup> groups = new ArrayList<WaypointGroup>();
		
		if (gpxObject instanceof GPXFile) {
			GPXFile gpx = (GPXFile) gpxObject;
			if (gpx.getWaypointGroup().getWaypoints().size() > 0) {
				groups.add(gpx.getWaypointGroup());
			}
		} else if (gpxObject instanceof WaypointGroup) {
			WaypointGroup group = (WaypointGroup) gpxObject;
			if ((group.getWptGrpType() == WptGrpType.WAYPOINTS) && (group.getWaypoints().size() > 0)) {
				groups.add(group);
			}
		}
		return groups;
	}

	/**
	 * Collect all Track Segments from given {@link GPXObject} 
	 * @param gpxObject
	 * @return List of Track Segments, empty list if none within gpxObject
	 */
	public List<WaypointGroup> getTrackSegments(GPXObject gpxObject) {
		List<WaypointGroup> groups = new ArrayList<WaypointGroup>();
		
		if (gpxObject instanceof GPXFile) {
			GPXFile gpx = (GPXFile) gpxObject;
			for (Track track : gpx.getTracks()) {
				groups.addAll(track.getTracksegs());
			}
		} else if (gpxObject instanceof Track) {
			groups.addAll(((Track) gpxObject).getTracksegs());
		} else if (gpxObject instanceof WaypointGroup) {
			WaypointGroup group = (WaypointGroup) gpxObject;
			if (group.getWptGrpType() == WptGrpType.TRACKSEG) {
				groups.add(group);
			}
		}
		return groups;
	}

	/**
	 * Collect all Route Segments from given {@link GPXObject} 
	 * @param gpxObject
	 * @return List of Route Segments, empty list if none within gpxObject
	 */
	public List<WaypointGroup> getRouteSegments(GPXObject gpxObject) {
		List<WaypointGroup> groups = new ArrayList<WaypointGroup>();
		
		if (gpxObject instanceof GPXFile) {
			GPXFile gpx = (GPXFile) gpxObject;
			for (Route route : gpx.getRoutes()) {
				groups.add(route.getPath());
			}
		} else if (gpxObject instanceof Route) {
			groups.add(((Route) gpxObject).getPath());
		} else if (gpxObject instanceof WaypointGroup) {
			WaypointGroup group = (WaypointGroup) gpxObject;
			if (group.getWptGrpType() == WptGrpType.ROUTE) {
				groups.add(group);
			}
		}
		return groups;
	}
	
	/**
	 * Collect {@link WaypointGroup}s from a {@link GPXFile} 
	 * @param GPXFile to get {@link WaypointGroup}s from
	 * @param which which types to collect. see SEG_*
	 * @return list of selected {@link WaypointGroup}s 
	 * TODO GPXObject instead of GPXFile
	 */
	public List<WaypointGroup> getSegments(GPXObject gpxObject, int which) {
		List<WaypointGroup> groups = new ArrayList<WaypointGroup>();
		
		switch(which) {
		case SEG_TRACK:
			groups.addAll(getTrackSegments(gpxObject));
			break;
		case SEG_ROUTE:
			groups.addAll(getRouteSegments(gpxObject));
			break;
		case SEG_ROUTE_TRACK:
			groups.addAll(getTrackSegments(gpxObject));
			groups.addAll(getRouteSegments(gpxObject));
			break;
		case SEG_WAYPOINTS:
			groups.addAll(getWaypointGroup(gpxObject));
			break;
		case SEG_TRACK_WAYPOINTS:
			groups.addAll(getTrackSegments(gpxObject));
			groups.addAll(getWaypointGroup(gpxObject));
			break;
		case SEG_ROUTE_WAYPOINTS:
			groups.addAll(getRouteSegments(gpxObject));
			groups.addAll(getWaypointGroup(gpxObject));
			break;
		case SEG_ALL:
		case SEG_TRACK_ROUTE_WAYPOINTS:
			groups.addAll(getTrackSegments(gpxObject));
			groups.addAll(getRouteSegments(gpxObject));
			groups.addAll(getWaypointGroup(gpxObject));
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		return groups;
	}
	
    /* MERGING METHODS
     * -------------------------------------------------------------------------------------------------------- */        

	/**
	 * add trackpoints from source {@link WaypointGroup} to target {@link WaypointGroup} 
	 * @param target
	 * @param source
	 */
	private void addTrackpoints(WaypointGroup target, WaypointGroup source) {
		for (Waypoint wpt : source.getWaypoints()) {
			target.getWaypoints().add(new Waypoint(wpt));
		}		
	}

	/**
	 * add waypoints from source {@link WaypointGroup} to target {@link WaypointGroup} 
	 * @param target
	 * @param source
	 */
	private void addWaypoints(WaypointGroup target, WaypointGroup source) {
		target.getWaypoints().addAll(source.getWaypoints());
		// for (Waypoint wpt : source.getWaypoints()) {
		//	target.getWaypoints().add(new WaypointMarker(wpt));
		// }		
	}
	
	/**
	 * add all {@link Waypoint}s contained in sourceSegments to target 
	 * @param target
	 * @param sourceSegments 
	 */
	private void copyVisibleSegments(WaypointGroup target, List<WaypointGroup> sourceSegments) {
		for (WaypointGroup wptGrp : sourceSegments) {
			if (wptGrp.isVisible()) {
				addTrackpoints(target, wptGrp);
			}
		}
	}

	/**
	 * Merge visible tracks and segments into a single GPX file, keep track and segments 1:1
	 * @param gpxFiles
	 * @return
	 */
	public GPXFile mergeIntoTracks(List<GPXFile> gpxFiles) {
		GPXFile newGpx = new GPXFile();
		newGpx.getMetadata().setName("Merged GPX");

		int trackNumber = 0;
		
		for (GPXFile gpx : gpxFiles) {
			if (gpx.isVisible()) {
    		   for (Track track : gpx.getTracks()) {
				   if (track.isVisible()) {
					   trackNumber++;
					   Track newTrack = new Track(track);
					   newTrack.setNumber(trackNumber);
					   newGpx.addTrack(newTrack);					   
				   }
    		  }
    		   // TODO same for routes
    		  if (gpx.getWaypointGroup().isVisible()) {
    			  addWaypoints(newGpx.getWaypointGroup(), gpx.getWaypointGroup());
    		  }
			}
		}
		Collections.sort(newGpx.getWaypointGroup().getWaypoints(), new WaypointComparator());
		return newGpx;		
	}
	
	/**
	 * Merge visible track segments into a single track, keep multiple track segments
	 * @param gpxFiles
	 * @return
	 */
	public GPXFile mergeIntoMulti(List<GPXFile> gpxFiles) {		
		GPXFile newGpx = new GPXFile();
		newGpx.getMetadata().setName("Merged GPX");
		Track newTrack = new Track(newGpx.getColor());
		
		for (GPXFile gpx : gpxFiles) {
			if (gpx.isVisible()) {
    		   for (Track track : gpx.getTracks()) {
    			   // merge into multiple track segments
    			   if (track.isVisible()) {
    				   for (WaypointGroup trackSeg: track.getTracksegs()) {
    					   if (trackSeg.isVisible()) {
    						   newTrack.addTrackseg(new WaypointGroup(trackSeg));
    					   }
    				   }
    			   }
    		  }
    		   // TODO same for routes
     		  if (gpx.getWaypointGroup().isVisible()) {
    			  addWaypoints(newGpx.getWaypointGroup(), gpx.getWaypointGroup());
    		  }
			}
		}
		if (newTrack.getTracksegs().size() > 0) {
			newGpx.addTrack(newTrack);
		}
		// Collections.sort(newGpx.getWaypointGroup().getWaypoints(), new WaypointComparator());
		return newGpx;		
	}

	
	/**
	 * Merge visible track segments into a single track with a single track segment
	 * @param gpxFiles
	 * @return
	 */
	public GPXFile mergeIntoSingle(List<GPXFile> gpxFiles) {		
		GPXFile newGpx = new GPXFile();
		newGpx.getMetadata().setName("Merged GPX");
		Track newTrack = new Track(newGpx.getColor());
		WaypointGroup newSegment = new WaypointGroup(newGpx.getColor(), WptGrpType.TRACKSEG);
		// WaypointGroup newWaypoints = new WaypointGroup(newGpx.getColor(), WptGrpType.WAYPOINTS);
				
		for (GPXFile gpx : gpxFiles) {
			if (gpx.isVisible()) {
    		   for (Track track : gpx.getTracks()) {
    			   if (track.isVisible()) {
    				   copyVisibleSegments(newSegment, track.getTracksegs());
    			   }
    		  }
    		   // TODO same for routes
    		  if (gpx.getWaypointGroup().isVisible()) {
    			  // TODO treat waypoints as markers!
    			  addWaypoints(newGpx.getWaypointGroup(), gpx.getWaypointGroup());
    		  }
			}
		}
		
		if (newSegment.getWaypoints().size() > 0) {
			newGpx.addTrack(newTrack);
			Collections.sort(newSegment.getWaypoints(), new WaypointComparator());
			newTrack.addTrackseg(newSegment);
		}
		// Collections.sort(newGpx.getWaypointGroup().getWaypoints(), new WaypointComparator());
		return newGpx;		
	}
	
	/**
	 * 
	 * @param gpxFiles
	 * @return
	 */
	public GPXFile mergeParallel(List<GPXFile> gpxFiles) {		
		GPXFile newFile = new GPXFile();
		
		return newFile;		
	}

    /* ELEVATION CORRECTION METHODS
     * TODO sauber implementieren mit events nach aussen (cancel, progress)
     * -------------------------------------------------------------------------------------------------------- */        

}
