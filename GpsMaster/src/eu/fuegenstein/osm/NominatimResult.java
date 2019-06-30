package eu.fuegenstein.osm;

import java.util.Hashtable;

/**
 * 
 * @author rfu
 *
 */
public class NominatimResult {

	private String country = "";
	private String countryCode = "";
	private String state = "";
	private String county = "";
	private String village = "";
	private String road = "";
	
	private Hashtable<String, String> all = new Hashtable<String, String>();
	
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
	
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	
	public String getVillage() {
		return village;
	}
	public void setVillage(String village) {
		this.village = village;
	}
	
	public String getRoad() {
		return road;
	}
	public void setRoad(String road) {
		this.road = road;
	}

	public Hashtable<String, String> getAll() {
		return all;
	}
	
}
