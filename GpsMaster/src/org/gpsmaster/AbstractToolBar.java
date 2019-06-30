package org.gpsmaster;


import java.util.List;

import javax.swing.JToolBar;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;

/**
 * base class encapsulating buttons within a toolbar
 * main purpose is to internally handle enabling/disabling
 * of buttons according to external data / events
 * 
 * TODO check if number of event handlers can be reduced
 *   
 * @author rfu
 *
 */
public abstract class AbstractToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3631733913595000770L;


	// TODO event handlers for 
	//		-	sending events: button clicked --> start method
	// 		-	receiving events: enable/disable buttons
	/**
	 * enable/disable buttons according to type of {@link GPXObject}
	 * @param gpx
	 */
	public abstract void setActiveGpxObject(GPXObject gpx);
			
	
	/**
	 * enable/disable buttons according to number of loaded GPX files
	 * (disable some buttons if list is empty) 
	 * @param gpxFiles
	 */
	public abstract void setGpxFiles(List<GPXFile> gpxFiles);
		

}
