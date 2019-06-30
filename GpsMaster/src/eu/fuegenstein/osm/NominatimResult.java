package eu.fuegenstein.osm;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a result as returned from Nominatim.
 * Used for both "forward" and reverse lookups.
 * 
 * @author rfu
 *
 */
public class NominatimResult {


	private double lat = 0;
	private double lon = 0;
	
	private List<NominatimPlace> places = new ArrayList<NominatimPlace>();
	
	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}

	public List<NominatimPlace> getPlaces() {
		return places;
	}
	
}
