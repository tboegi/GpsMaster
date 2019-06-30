package org.gpsmaster.gpxpanel;

import java.util.Comparator;
import java.util.Date;

/**
 * 
 * @author rfu
 *
 */
public class WaypointComparator implements Comparator<Waypoint> {
	@Override
	public int compare(Waypoint w1, Waypoint w2) {
		Date date1 = w1.getTime();
		Date date2 = w2.getTime();
		if ((date1 == null) || (date2 == null)) {
			return 0;
		}
		return date1.compareTo(date2);	
	}
}
