package org.gpsmaster.chart;

import javax.swing.ImageIcon;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
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
	 * @param extensionKey key of sourceFmt holding the value.
	 * the value (of type string) has to be castable to {@link double} 
	 */
	public ExtensionAxis(String extensionKey) {
		super(null);
		key = extensionKey;
		title = extensionKey;
		iconFile = "axis_extension.png";
		valueAxis = new NumberAxis();
		valueAxis.setLabel(key);
		renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
		super.setDefaults();
		
		// check if there is an icon defined for this sourceFmt
		// in /org/gpsmaster/icons/chart. name of the icon file 
		// is ext_{key}.png, where ":" is replaced by "_".
		
		try {
			String extIconFile = "ext_" + extensionKey.replaceAll(":", "_") + ".png";
			icon = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_CHART + extIconFile));
		} catch (NullPointerException e) { };
	}

	/**
	 * Key of sourceFmt this axis represents
	 * 
	 * @return
	 */
	public String getExtensionKey() {
		return key;
	}
	
	/**
	 * get the sourceFmt's numeric value from the given {@link Waypoint} 
	 */
	@Override
	public double getValue(Waypoint wpt) {
		double value = 0.0f;
		if ((wpt != null) && wpt.getExtension().containsKey(key)) {
			try {
				value = Double.parseDouble(wpt.getExtension().getSubValue(key));
			} catch (NumberFormatException e) {};
		}
		return value;
	}
	
	@Override
	public void reset() {
		// not needed

	}

}
