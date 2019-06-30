package org.gpsmaster.fileloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.xml.bind.ValidationException;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.markers.ClickableMarker;
import org.gpsmaster.markers.Marker;

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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Loader Class for georeferenced images
 * @author rfu
 *
 */
public class ExifLoader extends FileLoader {

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
	public void open(File file) {
		this.file = file;
		
	}

	@Override
	public GPXFile load() throws Exception {
		if (gpx == null) {
			setupGpx();
		}
		readExif();
		return gpx;
	}

	@Override
	public void loadCumulative() throws ImageProcessingException, IOException, MetadataException {
		if (gpx == null) {
			setupGpx();
		}
		readExif();		
	}

	@Override
	public void save(GPXFile gpx, File file) throws FileNotFoundException {
		throw new NotImplementedException();		
	}

	@Override
	public void validate() throws ValidationException, NotBoundException {
		// DUMMY nothing to validate. (yet)
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}


	private void readExif() throws ImageProcessingException, IOException, MetadataException {
		Marker wpt = null;
		String timeString = "";
		String dateString = "";
		Date timestamp = null;
		String device = "";
		
		Metadata metadata = ImageMetadataReader.readMetadata(file);
		GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
		GpsDescriptor gpsDescriptor = new GpsDescriptor(gpsDirectory);
		ExifIFD0Directory ifd0Directory = metadata.getDirectory(ExifIFD0Directory.class);
		
		for (Directory directory : metadata.getDirectories()) {
		    for (Tag tag : directory.getTags()) {
		        System.out.println(tag);
		    }
		}
		
		GeoLocation location = gpsDirectory.getGeoLocation();
		wpt = new Marker(location.getLatitude(), location.getLongitude());
		wpt.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/markers/photo.png")));
		wpt.setEle(gpsDirectory.getDouble(GpsDirectory.TAG_GPS_ALTITUDE));

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
	    
	    for (Tag tag : ifd0Directory.getTags()) {
	       if (tag.getTagName().equals("Make")) {
	    	   device = tag.getDescription();
	       }
	       if (tag.getTagName().equals("Model")) {
	    	   device = device + " " + tag.getDescription();
	       }
	       if (tag.getTagName().equals("Date/Time") && (timestamp == null)) {
	    	   sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	    	   try {
				timestamp = sdf.parse(tag.getDescription());
			} catch (ParseException e) { }		    	   
	       }
	    }

	    if (timestamp != null) {
	    	wpt.setTime(timestamp);
	    }
	    if (device.isEmpty() == false) {
	    	wpt.setCmt(device);
	    }
		wpt.setName(file.getName());
			
		gpx.getWaypointGroup().getWaypoints().add(wpt);
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
