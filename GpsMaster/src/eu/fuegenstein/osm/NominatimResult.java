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
	private String moreUrl = null;
	private String attribution = "";
	
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

	/**
	 * @return the attribution
	 */
	public String getAttribution() {
		return attribution;
	}

	/**
	 * @param attribution the attribution to set
	 */
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	public List<NominatimPlace> getPlaces() {
		return places;
	}

	/**
	 * @return the moreUrl
	 */
	public String getMoreUrl() {
		return moreUrl;
	}

	/**
	 * @param moreUrl the moreUrl to set
	 */
	public void setMoreUrl(String moreUrl) {
		this.moreUrl = moreUrl;
	}

	public void clear() {
		places.clear();
		moreUrl = null;
		attribution = "";
		
	}
}
