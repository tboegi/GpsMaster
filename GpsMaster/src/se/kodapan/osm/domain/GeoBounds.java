package se.kodapan.osm.domain;

public class GeoBounds {

	private double e = 0.0f; // EAST
	private double w = 0.0f; // WEST
	private double n = 0.0f; // NORTH
	private double s = 0.0f; // SOUTH
	
	
	public GeoBounds() {
		
	}
	
	public GeoBounds(double east, double north, double south, double west) {
		this();
		e = east;
		w = west;
		s = south;
		n = north;		
	}
	
	// EAST
	public double getE() {
		return e;
	}
	public void setE(double e) {
		this.e = e;
	}
	
	// WEST
	public double getW() {
		return w;
	}
	public void setW(double w) {
		this.w = w;
	}
	
	// NORTH
	public double getN() {
		return n;
	}
	public void setN(double n) {
		this.n = n;
	}
	
	// SOUTH
	public double getS() {
		return s;
	}
	public void setS(double s) {
		this.s = s;
	}
	
	
}
