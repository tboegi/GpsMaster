package org.gpsmaster.cleaning;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import se.kodapan.osm.domain.Way;

/**
 * bereinigen nach gauß'scher normalverteilung:
 * badewannenkurve nach Distance(p1, p2)
 * unterste x% (kürzeste strecken) entfernen (idle points)
 * oberste x% (längste strecken) entfernen (ausreisser)
 * 
 * @author rfu
 *
 */
public class Gaussian extends CleaningAlgorithm {

	// TreeMap holding a Waypoint (value) and the distance to its neighbour (key)
	// 
	private TreeMap<Double, Waypoint> map = new TreeMap<Double, Waypoint>();
	private List<Waypoint> trackpoints = null;
	private List<Waypoint> toDelete = null;
	
	public Gaussian() {
		super();
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	private void populateMap() {
		
		// we will not remove start- and/or endpoint 
		if (trackpoints.size() > 2) {			
			int i = 1;
			while (i < trackpoints.size() - 2) {
				Waypoint p1 = trackpoints.get(i);
				Waypoint p2 = trackpoints.get(i+1);
				map.put(p1.getDistance(p2), p1);  // round(distance)?
				i++;
			}
		}
	}
	
	private void printMap() {
		for(Map.Entry<Double,Waypoint> entry : map.entrySet()) {
			  System.out.println(entry.getKey());
			}
	}
	@Override
	protected void applyAlgorithm(WaypointGroup group, List<Waypoint> toDelete) {
		trackpoints = group.getWaypoints();
		this.toDelete = toDelete;
			
		populateMap();
		printMap(); // debug
	}

}
