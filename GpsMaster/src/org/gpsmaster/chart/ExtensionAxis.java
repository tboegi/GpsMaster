package org.gpsmaster.chart;

import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYAreaRenderer;

/**
 * Y Axis holding generic (numerical) values 
 * stored in {@link Waypoint} extensions.
 *  
 * @author rfu
 *
 */
public class ExtensionAxis extends ChartYAxis {

	private String key = null;
	
	/**
	 * Constructor
	 * @param extensionKey key of extension holding the value.
	 * the value (of type string) has to be castable to {@link double} 
	 */
	public ExtensionAxis(String extensionKey) {
		super(null);
		key = extensionKey;
		title = extensionKey;
		// title = "Extension::" + key;
		iconFile = "axis_extension.png";
		valueAxis = new NumberAxis();
		valueAxis.setLabel(key);
		renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
		super.setDefaults();
	}

	/**
	 * get the extension's numeric value from the given {@link Waypoint} 
	 */
	@Override
	public double getValue(Waypoint wpt) {
		double value = 0.0f;
		if ((wpt != null) && wpt.getExtensions().containsKey(key)) {
			try {
				value = Double.parseDouble(wpt.getExtensions().get(key));
			} catch (NumberFormatException e) {};
		}
		return value;
	}

	@Override
	public void reset() {
		// not needed

	}

}
