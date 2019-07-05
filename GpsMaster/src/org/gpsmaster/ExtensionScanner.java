package org.gpsmaster;

import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * Scan Waypoint extensions for properties containing numerical values
 *
 * @author rfu
 *
 */
public class ExtensionScanner {

	private List<String> properties = new ArrayList<String>();

	// TODO recognize & report type (double, integer, long) of discovered numerical properties
	// TODO determine type from XSD?

	/**
	 *
	 * @param group
	 * @return
	 */
	public List<String> scan(WaypointGroup group) {
		properties.clear();

		return properties;
	}
}
