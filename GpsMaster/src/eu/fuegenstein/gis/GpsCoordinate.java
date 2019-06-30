package eu.fuegenstein.gis;

// http://www.movable-type.co.uk/scripts/latlong.html

public class GpsCoordinate {

	private double lat = 0.0f;
	private double lon = 0.0f;
	
	long r = 6371; // earth radius in meters
	
	/**
	 * Constructor 
	 * @param lat
	 * @param lon
	 */
	public GpsCoordinate(double lat, double lon) {
		this.setLat(lat);
		this.setLon(lon);
	}
	
	
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
	 * 
	 * @param coord
	 * @return
	 */
	public double distance(GpsCoordinate coord) {
			
		double phi1 = degToRad(lat);
		double phi2 = degToRad(coord.getLat());
		double dPhi = degToRad(coord.getLat() - lat);
		double dLambda = degToRad(coord.getLon() - lon);
		
		double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2) + 
				Math.cos(phi1) * Math.cos(phi2) *
				Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return r * c;
	}
	
	/**
	 * 
	 * @param bearing
	 * @param d
	 * @return
	 */
	public GpsCoordinate destination(double bearing, double d) {
		
		double phi1 = lat;
		double lambda1 = lon;
		double b = degToRad(bearing);
		double phi2 = Math.asin(Math.sin(phi1) * Math.cos(d/r) +
				Math.cos(phi1) * Math.sin(d/r) * Math.cos(b));
		
		double lambda2 = lambda1 + Math.atan2(Math.sin(b) * Math.sin(d/r) * Math.cos(phi1),
								Math.cos(d/r) - Math.sin(phi1) * Math.sin(phi2));
		
		return new GpsCoordinate(phi2, lambda2);
	}
	
	private double degToRad(double deg) {
		return deg * Math.PI / 180;
	}
}
