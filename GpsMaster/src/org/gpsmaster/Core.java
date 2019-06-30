package org.gpsmaster;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.gpsmaster.dialogs.CleaningDialog;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointComparator;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;
import org.gpsmaster.markers.WaypointMarker;


/*
 * Class providing core functionality 
 * for modification of tracks. to be used
 * from GUI or commandline batch processing
 * TODO static?
 */
public class Core {

	private int requestChunkSize = 200;
	private boolean isCancelled = false;

	/**
	 * all track segments
	 */
	public final int SEG_TRACK = 0;
	
	/**
	 * all route segments
	 */	
	public final int SEG_ROUTE = 1;

	/**
	 * Waypoints only
	 */	
	public final int SEG_WAYPOINTS = 2;

	/**
	 * all track & route segments
	 */
	public final int SEG_ROUTE_TRACK = 3;
	
	/**
	 * waypoint group and all track & route segments
	 */
	public final int SEG_TRACK_ROUTE_WAYPOINTS = 4;
	
	/**
	 * waypoint group and all track segments
	 */
	public final int SEG_TRACK_WAYPOINTS = 5;
	
	/**
	 * waypoint group and all route segments
	 */
	public final int SEG_ROUTE_WAYPOINTS = 6;

	/**
	 * waypoint group and all route segments
	 */
	public final int SEG_ALL = 7;

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

	/**
	 * TODO finish
	 * @param gpxObject
	 */
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

	
    /* TIMESHIFT METHODS
     * -------------------------------------------------------------------------------------------------------- */        
	
	/**
	 * Remove all timestamps for given GPXObject and all subobjects
	 * @param gpx
	 */
	public void removeTimestamps(GPXObject gpx) {
		removeTimestamps(getSegments(gpx, SEG_ALL));
	}
	
	/**
	 * 
	 * @param waypointGroups
	 */
	public void removeTimestamps(WaypointGroup waypointGroup) {
		for (Waypoint wpt : waypointGroup.getWaypoints()) {
			wpt.setTime(null);
			
		}
	}
		
	/**
	 * Remove all timestamps (set to NULL) in specified waypointGroups
	 * @param waypointGroups
	 */
	public void removeTimestamps(List<WaypointGroup> waypointGroups) {
		for (WaypointGroup waypointGroup : waypointGroups) {
			removeTimestamps(waypointGroup);
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
		for (Waypoint wpt : source.getWaypoints()) {
			target.getWaypoints().add(new WaypointMarker(wpt));
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
			newGpx.getTracks().add(newTrack);
			Collections.sort(newSegment.getWaypoints(), new WaypointComparator());
			newTrack.getTracksegs().add(newSegment);
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

    /**
     * Corrects the elevation of each {@link Waypoint} in the group and updates the aggregate group properties.<br />
     * (Optionally can do a "cleanse," attempting to fill missing data (SRTM voids) in the response.<br />)
     * Note: The MapQuest Open Elevation API has a bug with POST XML, and the useFilter parameter.
     *       Because of this, the request must be a POST KVP (key/value pair).  The useFilter parameter returns
     *       data of much higher quality. 
     * 
     * @return  The status of the response.
     */
	public void correctElevation(WaypointGroup waypointGroup) throws Exception {

		int totals = 0;
		int grpCtr = 0; // waypoint group counter
		
		List<Waypoint> waypoints = waypointGroup.getWaypoints();
		// itemCount++;
		// trackpointBar.setMaximum(waypoints.size());
		while(grpCtr < waypoints.size() && !isCancelled) {
			
			int blockCtr = 0;
			int firstInBlock = grpCtr;
		
			// build a chunk
	        Locale prevLocale = Locale.getDefault();
	        Locale.setDefault(new Locale("en", "US"));

			String latLngCollection = "";
			while((grpCtr < waypoints.size()) && (blockCtr < requestChunkSize)) {
				Waypoint wpt = waypoints.get(grpCtr);
		        latLngCollection += String.format("%.6f,%.6f,", wpt.getLat(), wpt.getLon());
						
				grpCtr++;
				blockCtr++;
				totals++;
			}
			latLngCollection = latLngCollection.substring(0, latLngCollection.length()-1);
			Locale.setDefault(prevLocale);
			
			// make request
	        String url = "http://open.mapquestapi.com/elevation/v1/profile";
	        String charset = "UTF-8";
	        String param1 = "kvp"; // inFormat
	        String param2 = latLngCollection;
	        String param3 = "xml"; // outFormat
	        String param4 = "true"; // useFilter
	        String query = null;
	        URLConnection connection = null;
	        OutputStream output = null;
	        InputStream response = null;
	        BufferedReader br = null;
	        StringBuilder builder = new StringBuilder();
	            query = "key=Fmjtd%7Cluub2lu12u%2Ca2%3Do5-96y5qz" + 
	                    String.format("&inFormat=%s" + "&latLngCollection=%s" + "&outFormat=%s" + "&useFilter=%s",
	                    URLEncoder.encode(param1, charset),
	                    URLEncoder.encode(param2, charset),
	                    URLEncoder.encode(param3, charset),
	                    URLEncoder.encode(param4, charset));
	            connection = new URL(url).openConnection();
	            connection.setDoOutput(true);
	            connection.setRequestProperty("Accept-Charset", charset);
	            connection.setRequestProperty(
	                    "Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
	            output = connection.getOutputStream();
	            output.write(query.getBytes(charset));
	            output.close();
	            response = connection.getInputStream();
	            br = new BufferedReader((Reader) new InputStreamReader(response, "UTF-8"));
	            for(String line=br.readLine(); line!=null; line=br.readLine()) {
	                builder.append(line);
	                builder.append('\n');
	            }

			// process response
	        String responseStr = builder.toString();
	        if (responseStr.contains("Given Route exceeds the maximum allowed distance")) {
	        	// should not happen since we process in chunks
	        	throw new IllegalArgumentException("Given Route exceeds the maximum allowed distance");
	        }
			// TODO check for error in response
            List<Double> eleList = getEleArrayFromXMLResponse(responseStr);
            if (eleList.size() != blockCtr) {
            	throw new IllegalArgumentException("Result size mismatch");
            }
            for (int i = 0; i < eleList.size(); i++) {
            	waypoints.get(firstInBlock+i).setEle(eleList.get(i));
            }
            // update progress bar
	        // publish(new Progress(itemCount, totals));

		}
	}

    /**
     * Cleanse the elevation data.  Any {@link Waypoint} with an elevation of -32768 needs to be interpolated.
     * 
     * @return  The status of the cleanse.
     */
    public void cleanseElevation(WaypointGroup wptGrp) {
  		
    	List<Waypoint> waypoints = wptGrp.getWaypoints();
    	double eleStart = wptGrp.getStart().getEle();
        double eleEnd = wptGrp.getEnd().getEle();

        if (eleStart == -32768) {
            for (int i = 0; i < waypoints.size(); i++) {
                if (waypoints.get(i).getEle() != -32768) {
                    eleStart = waypoints.get(i).getEle();
                    break;
                }
            }
        }
        
        if (eleEnd == -32768) {
            for (int i = waypoints.size() - 1; i >= 0; i--) {
                if (waypoints.get(i).getEle() != -32768) {
                    eleEnd = waypoints.get(i).getEle();
                    break;
                }
            }
        }
        
        if (eleStart == -32768 && eleEnd == -32768) {
        	// hopeless! (impossible to correct)
        	// cleanseFailed++;
        	// TODO set some kind of error flag
            return;
        }
        
        waypoints.get(0).setEle(eleStart);
        waypoints.get(waypoints.size() - 1).setEle(eleEnd);
        
        for (int i = 0; i < waypoints.size(); i++) {
            if (waypoints.get(i).getEle() == -32768) {
                Waypoint neighborBefore = null;
                Waypoint neighborAfter = null;
                double distBefore = 0;
                double distAfter = 0;
                
                Waypoint curr = waypoints.get(i);
                Waypoint prev = waypoints.get(i);
                for (int j = i - 1; j >= 0; j--) {
                    prev = curr;
                    curr = waypoints.get(j);
                    distBefore += curr.getDistance(prev);
                    if (waypoints.get(j).getEle() != -32768) {
                        neighborBefore = waypoints.get(j);
                        break;
                    }
                }
    
                curr = waypoints.get(i);
                prev = waypoints.get(i);
                for (int j = i + 1; j < waypoints.size(); j++) {
                    prev = curr;
                    curr = waypoints.get(j);
                    distAfter += curr.getDistance(prev); 
                    if (waypoints.get(j).getEle() != -32768) {
                        neighborAfter = waypoints.get(j);
                        break;
                    }
                }
                
                if ((neighborBefore != null) && (neighborAfter != null)) {
	                double distDiff = distBefore + distAfter;
	                double eleDiff = neighborAfter.getEle() - neighborBefore.getEle();
	                double eleCleansed = ((distBefore / distDiff) * eleDiff) + neighborBefore.getEle();
	                waypoints.get(i).setEle(eleCleansed);
                }
            }
        }
    }

    /**
     * Parses an XML response string.
     * 
     * @return  A list of numerical elevation values.
     * @throws XMLStreamException 
     */
    private List<Double> getEleArrayFromXMLResponse(String xmlResponse) throws XMLStreamException {
        List<Double> ret = new ArrayList<Double>();
        InputStream is = new ByteArrayInputStream(xmlResponse.getBytes());
        XMLInputFactory xif = XMLInputFactory.newInstance();
   
        XMLStreamReader xsr = xif.createXMLStreamReader(is, "ISO-8859-1");
        while (xsr.hasNext()) {
            xsr.next();
            if (xsr.getEventType() == XMLStreamReader.START_ELEMENT) {
                if (xsr.getLocalName().equals("height")) {
                    xsr.next();
                    if (xsr.isCharacters()) {
                        ret.add(Double.parseDouble(xsr.getText()));
                    }
                }
            }
        }
        xsr.close();
        return ret;
    }

}
