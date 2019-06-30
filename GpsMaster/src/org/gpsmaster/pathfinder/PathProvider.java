package org.gpsmaster.pathfinder;

import java.io.IOException;
import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;


/**
 * 
 * Interface to be implemented by any pathfinders.
 * 
 * @author Matt Hoover
 * 
 * OBSOLETED by RouteProvider
 *
 */
public interface PathProvider {
    
    /**
     * The different types of pathfinding.
     */
    public enum PathFindType {
        FOOT,
        BIKE,
        CAR
    }
    
    /**
     * Make the HTTP request for a pathfinding query.  Return the result as an XML string.
     * @throws IOException 
     * @throws Exception 
     */
    public abstract String getXMLResponse(PathFindType type, double lat1, double lon1, double lat2, double lon2) throws Exception;
    
    /**
     * Parse the XML string and return a List of {@link Waypoint}s.
     */
    public abstract List<Waypoint> parseXML(String xml) throws Exception;
    
    /**
     * get the maximum distance this routing provider can cover
     * (in kilometers)
     * 
     * @return
     */
    public abstract long getMaxDistance();
    
}
