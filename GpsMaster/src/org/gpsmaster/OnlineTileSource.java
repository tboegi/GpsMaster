package org.gpsmaster;

/**
 * Config file entry for a templated online tile source
 * @author rfu
 *
 */
public class OnlineTileSource {

	private String name = "";
	private String url = "";
	private int maxZoom = 0;
	
	/**
	 * @return Name of this tile source
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name short descriptive name of this tilesource
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * get the templated URL 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the maxZoom
	 */
	public int getMaxZoom() {
		return maxZoom;
	}

	/**
	 * @param maxZoom the maxZoom to set
	 */
	public void setMaxZoom(int maxZoom) {
		this.maxZoom = maxZoom;
	}
	
	
}
