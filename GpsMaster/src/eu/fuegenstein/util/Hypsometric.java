package eu.fuegenstein.util;

import java.awt.Color;

/**
 * class providing colors for hypsometric tinting
 * @author rfu
 *
 */
public class Hypsometric extends Tinting {

	public Hypsometric() {
		super();    	
    	colorTable.put( 100, new Color(  0, 100,  70));  //     0 -  100m blue green
    	colorTable.put( 200, new Color( 20, 120,  50));  //   100 -  200m yellow green
    	colorTable.put( 500, new Color(230, 210, 130));  //   200 -  500m yellow
    	colorTable.put(1000, new Color(210, 165,  95));  //   500 - 1000m light brown
    	colorTable.put(2000, new Color(130,  40,   0));  //  1000 - 2000m brown
    	colorTable.put(4000, new Color(150,  30,  20));  //  2000 - 4000m red brown
    	colorTable.put(5000, new Color(125,  10,   5));  //  4000 - 5000m brown red
    	colorTable.put(8848, Color.WHITE);  			  //  5000 - 8848m white
	}
	
	/**
	 * elevation in meters
	 */
	public Color getColor(int elevation) {
		return super.getColor(elevation);
	}
}
