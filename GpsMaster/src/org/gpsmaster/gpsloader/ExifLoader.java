package org.gpsmaster.gpsloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.marker.PhotoMarker;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDescriptor;
import com.drew.metadata.exif.GpsDirectory;

/**
 * Loader Class for geotagged images
 * @author rfu
 *
 */
public class ExifLoader extends GpsLoader {

	GPXFile gpx = null;
	
	/**
	 * Default Constructor
	 */
	public ExifLoader() {
		super();
		extensions.add("jpg");
		extensions.add("jpeg");
		isAdding = true;	
	}
	
	@Override
	public void loadCumulative(InputStream inputStream) throws ImageProcessingException, IOException, MetadataException {
		if (gpx == null) {
			setupGpx();
		}
		readExif(inputStream);		
	}

	@Override
	public GPXFile load(InputStream inputStream, String format) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void save(GPXFile gpx, OutputStream out) {
		throw new UnsupportedOperationException();		
	}

	public boolean canValidate() {
		return false;
	}

	@Override
	public void validate(InputStream inStream) {
		// DUMMY nothing to validate. (yet)
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}


	private void readExif(InputStream inStream) throws ImageProcessingException, IOException, MetadataException {
		PhotoMarker marker = null;
		String timeString = "";
		String dateString = "";
		Date timestamp = null;
		String device = "";
		
		// Metadata metadata = ImageMetadataReader.readMetadata(file);
		Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(inStream), true);

		GpsDirectory gpsDirectory = null;
		GpsDescriptor gpsDescriptor = null;
		GeoLocation location = null;
		try {
			gpsDirectory = metadata.getDirectory(GpsDirectory.class);
			gpsDescriptor = new GpsDescriptor(gpsDirectory);
			location = gpsDirectory.getGeoLocation();
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("File does not contain GPS data");
		}
		
		marker = new PhotoMarker(location.getLatitude(), location.getLongitude());
		marker.setName(file.getName());
		marker.setDirectory(file.getPath());
		if (gpsDirectory.containsTag(GpsDirectory.TAG_GPS_ALTITUDE)) {
			marker.setEle(gpsDirectory.getDouble(GpsDirectory.TAG_GPS_ALTITUDE));
		}

		for (Directory directory : metadata.getDirectories()) {
		    for (Tag tag : directory.getTags()) {
		        marker.addExifTag(tag);
		    }
		}

		timeString = gpsDescriptor.getGpsTimeStampDescription();		

	    for (Tag tag : gpsDirectory.getTags()) {
	       if (tag.getTagName().equals("GPS Date Stamp")) {
	    	   dateString = tag.getDescription();
	       }
	       if (tag.getTagName().equals("GPS Time Stamp") || tag.getTagName().equals("GPS Time-Stamp")) {
	    	   timeString = tag.getDescription();
	       }
	    }
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd k:mm:ss Z");
	    try {
			timestamp = sdf.parse(dateString + " " + timeString);
		} catch (ParseException e) { }
	    

	    ExifIFD0Directory ifd0Directory = metadata.getDirectory(ExifIFD0Directory.class);
	    for (Tag tag : ifd0Directory.getTags()) {
	       if (tag.getTagName().equals("Date/Time") && (timestamp == null)) {
	    	   sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	    	   try {
				timestamp = sdf.parse(tag.getDescription());
			} catch (ParseException e) { }		    	   
	       }
	    }
	    
	    if (timestamp != null) {
	    	marker.setTime(timestamp);
	    }
	    if (device.isEmpty() == false) {
	    	marker.setCmt(device);
	    }
			
		gpx.getWaypointGroup().getWaypoints().add(marker);
	}
	
	private void setupGpx() {
		// set up gpx file
		gpx = new GPXFile();
		gpx.getMetadata().setName("Geo-referenced Images");
		gpxFiles.put(new File("exif"), gpx); // dummy file
	}
	
	public void clear() {
		super.clear();
		gpx = null;
	}

}
