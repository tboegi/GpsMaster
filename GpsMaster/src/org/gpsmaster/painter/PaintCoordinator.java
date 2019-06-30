package org.gpsmaster.painter;

/**
 * this class helps the individual painter  
 * to prevent mutually overlapping labels
 * 
 * @author rfu
 *
 */
public class PaintCoordinator {

	private double distanceInterval = 0;
	private double distanceOffset = 0;
	
	/**
	 * 
	 */
	public void clear() {
	
		distanceInterval = 0;
		distanceOffset = 0;
			
	}

	/**
	 * @return the distanceInterval
	 */
	public double getDistanceInterval() {
		return distanceInterval;
	}

	/**
	 * @param distanceInterval the distanceInterval to set
	 */
	public void setDistanceInterval(double distanceInterval) {
		this.distanceInterval = distanceInterval;
	}

	/**
	 * @return the distanceOffset
	 */
	public double getDistanceOffset() {
		return distanceOffset;
	}

	/**
	 * @param distanceOffset the distanceOffset to set
	 */
	public void setDistanceOffset(double distanceOffset) {
		this.distanceOffset = distanceOffset;
	}



}
