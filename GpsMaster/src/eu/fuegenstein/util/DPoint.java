package eu.fuegenstein.util;

/**
 * a point with coordinates of type double 
 * @author rfu
 *
 */
public class DPoint {
		
	public double x = 0.0;
	public double y = 0.0;
	
	/**
	 * Constructor
	 * @param x
	 * @param y
	 */
	public DPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * 
	 * @param p
	 * @return
	 */
	public double getDistance(DPoint p) {
		double dx = p.x - x;
		double dy = p.y - y;				
		return Math.sqrt(dx * dx + dy * dy);
	}
}
