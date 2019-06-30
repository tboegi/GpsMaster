package org.gpsmaster.marker;

import java.awt.event.MouseEvent;

/**
 * Interface to be implemented by all classes 
 * that want to be notified of a click on a marker
 * 
 * @author rfu
 *
 */
public interface IMarkerCallback {

	void Callback(Marker marker, MouseEvent evt);
	
}
