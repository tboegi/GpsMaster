package eu.fuegenstein.osm;

import java.util.Hashtable;

/**
 * 
 * @author rfu
 *
 */
public class NominatimPlace {

	private double lat = 0.0f;
	private double lon = 0.0f;
	
	private String displayName = "";
	private String country = "";
	private String countryCode = "";
	private String state = "";
	private String county = "";
	private String village = "";
	private String road = "";

	private Hashtable<String, String> all = new Hashtable<String, String>();

	
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
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getVillage() {
		return village;
	}
	public void setVillage(String village) {
		this.village = village;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getRoad() {
		return road;
	}
	public void setRoad(String road) {
		this.road = road;
	}


	/**
	 * get a list of all place elements returned by nominatim
	 * @return list of elements or empty list
	 */
	public Hashtable<String, String> getAll() {
		return all;
	}
	
}
