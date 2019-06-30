package org.gpsmaster.filehub;

import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Interface to be implemented by classes receiving {@link GPXFile}s
 * 
 * @author rfu
 *
 */
public interface IItemTarget {
	
	/**
	 * Get a short, human readable name 
	 * @return
	 */
	String getName();
	
	/**
	 * 
	 * @return
	 */
	boolean isEnabled();
	
	/**
	 * 
	 * @param gpxFile
	 */
	void addGpxFile(GPXFile gpxFile);

	// for later versions:
	
	/**
	 * 
	 */
	// void getSupportedExtension();
}
