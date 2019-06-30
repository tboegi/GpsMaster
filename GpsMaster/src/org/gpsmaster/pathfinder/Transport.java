package org.gpsmaster.pathfinder;

/**
 * Class representing a means of transport for {@link RouteProvider}s
 * 
 * TODO generalize this for providers not requiring urlParams
 * TODO support additional features, if provided by the routing service:
 * 			- turn-by-turn directions
 * 			- fastest/shortest
 * 
 * @author rfu
 *
 */
public class Transport {

	protected String name = "<undef>";	
	protected String description = "<undef>";
	protected String urlParam = "";
	private TransportType transportType = TransportType.UNDEFINED;
	
	/**
	 * 
	 * @param displayName
	 * @param transportType
	 * @param param
	 */
	public Transport(String displayName, TransportType transportType, String param) {
		name = displayName;
		this.transportType = transportType;
		urlParam = param;
	}
	
	/**
	 * @return the urlParam
	 */
	public String getUrlParam() {
		return urlParam;
	}
	
	/**
	 * @param urlParam the urlParam to set
	 */
	public void setUrlParam(String urlParam) {
		this.urlParam = urlParam;
	}
	
	/**
	 * @return the text
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param text the text to set
	 */
	public void setName(String displayName) {
		this.name = displayName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the transportType
	 */
	protected TransportType getTransportType() {
		return transportType;
	}

	/**
	 * @param transportType the transportType to set
	 */
	protected void setTransportType(TransportType transportType) {
		this.transportType = transportType;
	}

	
}
