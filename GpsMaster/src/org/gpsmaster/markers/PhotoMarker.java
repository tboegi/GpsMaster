package org.gpsmaster.markers;

import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.Waypoint;

import com.drew.metadata.Tag;

/***
 * Class representing a geotagged image
 * TODO move EXIF reading code here
 *
 * */
public class PhotoMarker extends Marker {

	private List<Tag> tags = new ArrayList<Tag>();
	private int orientation = -1;

	/**
	 *
	 * @param lat
	 * @param lon
	 */
	public PhotoMarker(double lat, double lon) {
		super(lat, lon);
	}

	/**
	 *
	 * @param wpt
	 */
	public PhotoMarker(Waypoint wpt) {
		super(wpt);
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
	}

	/**
	 *
	 * @return
	 */
	public String getDirectory() {
		if (getExtensions().containsKey(Const.EXT_FILE)) {
			return getExtensions().get(Const.EXT_FILE);
		}
		return "";
	}

	/**
	 *
	 * @param directory
	 */
	public void setDirectory(String directory) {
		if (getExtensions().containsKey(Const.EXT_FILE)) {
			getExtensions().remove(Const.EXT_FILE);
		}
		getExtensions().put(Const.EXT_FILE, directory);
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
