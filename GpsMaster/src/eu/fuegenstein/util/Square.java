package eu.fuegenstein.util;

/**
 * 
 * @author rfu
 *
 */
public class Square {

	private DPoint center = new DPoint(0,  0);
	private DPoint ulc = new DPoint(0, 0); // upper left corner
	private DPoint lrc = new DPoint(0, 0); // lower right corner
	
	private double sideLength = 0.0f; // in meters
	
	/**
	 * 
	 * @return
	 */
	public DPoint getCenter() {
		return center;
	}
	public void setCenter(DPoint center) {
		this.center = center;
	}
	
	public void setCenter(double x, double y) {
		center.x = x;
		center.y = y;
	}
	
	public double getSideLength() {
		return sideLength;
	}
	public void setSideLength(double sideLength) {
		this.sideLength = sideLength;
	}
	
	public boolean contains(double x, double y) {
		if (x > ulc.x && x < lrc.x && y < ulc.y && y > lrc.y){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 */
	private void calculateCorners() {
		
	}
}
