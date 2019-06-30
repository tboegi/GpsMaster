package org.gpsmaster.filehub;

import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Class representing an {@link ITransferableItem} already containing the {@link GPXFile} to transfer
 * @author rfu
 *
 */
public class GpxFileItem extends TransferableItem {

	private GPXFile gpxFile = null;
	
	/**
	 * Constructor
	 * @param gpx
	 */
	public GpxFileItem(GPXFile gpx) {
		this.gpxFile = gpx;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return gpxFile.getName();
	}
	
	public GPXFile getGpxFile() {
		return gpxFile;
	}

}
