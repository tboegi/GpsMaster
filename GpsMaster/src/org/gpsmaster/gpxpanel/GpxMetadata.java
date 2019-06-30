package org.gpsmaster.gpxpanel;

import com.topografix.gpx._1._1.MetadataType;

/**
 * Class overriding [{@link MetadataType} from GPX schema with GpsMaster-specific sourceFmt type
 * @author rfu
 *
 */
public class GpxMetadata extends MetadataType {

	protected GPXExtension extension = new GPXExtension();
	
	/**
	 * Constructor
	 */
	public GpxMetadata() {
		super();
	}
	
   public GPXExtension getExtension() {
    	return extension;
    }
    
    public void setExtension(GPXExtension extension) {
    	this.extension = extension;
    }
    
}
