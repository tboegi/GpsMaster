package org.gpsmaster.filehub;

/**
 * Possible data types an item source or target can accept
 * @author rfu
 *
 */
public enum DataType {

	UNKNOWN,
	STREAM,
	GPXFILE,
	STREAMGPX // target requires both GPX and stream

}
