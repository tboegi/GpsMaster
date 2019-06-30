package org.gpsmaster.markers;

import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.Waypoint;

import com.drew.metadata.Tag;

/***
 * 
 */
public class PhotoMarker extends Marker {

	private List<Tag> tags = new ArrayList<Tag>();
	private String directory = "";
	private int orientation = -1;
	
	/**
	 * 
	 * @param lat
	 * @param lon
	 */
	public PhotoMarker(double lat, double lon) {
		super(lat, lon);
		setup();
	}

	/**
	 * 
	 * @param wpt
	 */
	public PhotoMarker(Waypoint wpt) {
		super(wpt);
		setup();
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * 
	 */
	protected void setup() {
		setIcon("photo.png");
		type = "Photo";
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * 
	 * @param directory
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public List<Tag> getExifTags() {
		return tags;
	}
	/**
	 * 
	 * @param tag
	 */
	public void addExifTag(Tag tag) {
		
		if (tag.getTagName().equals("Make")) {
			desc = tag.getDescription();
	    }
        if (tag.getTagName().equals("Model")) {
        	desc = desc + " " + tag.getDescription();
        }
        if (tag.getTagName().equals("Orientation") && tag.getDirectoryName().equals("Exif IFD0")) {
        	if (tag.getDescription().contains("Rotate 90 CW")) {
        		orientation = 6;
        	}
        }

		tags.add(tag);
	}
}
