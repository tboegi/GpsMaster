package org.gpsmaster;

import java.util.Collections;
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


/*
 * Class providing core functionality
 * for modification of tracks. to be used
 * from GUI or commandline batch processing
 * TODO static?
 */
public class Core {

	private int requestChunkSize = 200;

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

	public void getObjectCount(GPXObject gpxObject) {

		int totalItems = 0;
		int totalWaypoints = 0;

	    if (gpxObject.isGPXFile()) { // correct all tracks and segments
			GPXFile gpx = (GPXFile) gpxObject;
			totalWaypoints = (int) (gpx.getNumTrackPts() + gpx.getNumWayPts() + gpx.getNumRoutePts());
			for (Track track : gpx.getTracks()) {
				totalItems += track.getTracksegs().size();
			}
			totalItems += gpx.getRoutes().size();
			if (gpx.getWaypointGroup().getWaypoints().size() > 0) {
				totalItems++;
			}
		} else if (gpxObject.isTrack()) {
			Track track = (Track) gpxObject;
			totalItems = track.getTracksegs().size();
			totalWaypoints = (int) track.getNumPts();
		} else if (gpxObject.isTrackseg()) {
			totalItems = 1;
			totalWaypoints = ((WaypointGroup) gpxObject).getNumPts();
		} else if (gpxObject.isRoute()) {
			totalItems = 1;
			totalWaypoints = ((Route) gpxObject).getNumPts();
		}

	    // return result object
	}

	/**
	 *
	 * @param gpxObject
	 * @param addWaypoints include Waypoints
	 * @param addRoutes include Routes
	 * @return
	 */
	private List<WaypointGroup> getWaypointGroups(GPXObject gpxObject, boolean addWaypoints, boolean addRoutes) {
		List<WaypointGroup> waypointGroups = new ArrayList<WaypointGroup>();
		if (gpxObject.isGPXFile()) { // correct and cleanse all tracks and segments
			GPXFile gpx = (GPXFile) gpxObject;
			for (Track track : gpx.getTracks()) {
				for (WaypointGroup trackSeg : track.getTracksegs()) {
					waypointGroups.add(trackSeg);
				}
			}
			if ((gpx.getWaypointGroup().getWaypoints().size() > 0) && addWaypoints) {
				waypointGroups.add(gpx.getWaypointGroup());
			}
		} else if (gpxObject.isTrack()) {
			Track track = (Track) gpxObject;
			for (WaypointGroup trackSeg : track.getTracksegs()) {
				waypointGroups.add(trackSeg);
			}
		} else if (gpxObject.isWaypointGroup() && addWaypoints) {
			waypointGroups.add((WaypointGroup) gpxObject);
		} else if (gpxObject.isRoute() || addRoutes) {
			waypointGroups.add((WaypointGroup) gpxObject);
		}

		return waypointGroups;
	}

	/**
	 *
	 * @param gpxObject
	 * @return all WaypointGroups contained in Tracks, Routes and Waypoints
	 */
	public List<WaypointGroup> getWaypointGroups(GPXObject gpxObject) {
		return getWaypointGroups(gpxObject, true, true);
	}

	/**
	 *
	 * @param gpxObject
	 * @return Track Segments only
	 */
	public List<WaypointGroup> getTrackSegments(GPXObject gpxObject) {
		return getWaypointGroups(gpxObject, false, false);
	}


    /* MERGING METHODS
     * -------------------------------------------------------------------------------------------------------- */

	/**
	 * add waypoints from source {@link WaypointGroup} to target {@link WaypointGroup}
	 * @param target
	 * @param source
	 */
	private void addWaypoints(WaypointGroup target, WaypointGroup source) {
		for (Waypoint wpt : source.getWaypoints()) {
			target.getWaypoints().add(new Waypoint(wpt));
		}
	}

	/**
	 * add all {@link Waypoint}s contained in sourceSegments to target
	 * @param target
	 * @param sourceSegments
	 */
	private void copyVisibleSegments(WaypointGroup target, List<WaypointGroup> sourceSegments) {
		for (WaypointGroup wptGrp : sourceSegments) {
			if (wptGrp.isVisible()) {
				addWaypoints(target, wptGrp);
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
					   newGpx.getTracks().add(newTrack);
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
    						   newTrack.getTracksegs().add(new WaypointGroup(trackSeg));
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
			newGpx.getTracks().add(newTrack);
		}
		Collections.sort(newGpx.getWaypointGroup().getWaypoints(), new WaypointComparator());
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
    			  addWaypoints(newGpx.getWaypointGroup(), gpx.getWaypointGroup());
    		  }
			}
		}

		if (newSegment.getWaypoints().size() > 0) {
			newGpx.getTracks().add(newTrack);
			Collections.sort(newSegment.getWaypoints(), new WaypointComparator());
			newTrack.getTracksegs().add(newSegment);
		}
		Collections.sort(newGpx.getWaypointGroup().getWaypoints(), new WaypointComparator());
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

}
