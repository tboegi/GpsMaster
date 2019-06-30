package org.gpsmaster.db;

import java.awt.Color;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.filehub.TransferableItem;
import org.gpsmaster.gpxpanel.GPXFile;

import com.topografix.gpx._1._1.BoundsType;

/**
 * Class representing a record in the dat_gps table
 *
 * this record does not contain actual GPS data!
 *   
 * @author rfu
 */
public class GpsRecord extends TransferableItem {

	// GPS metadata for user
	private String name;
	private Date startTime;
	private Date endTime;
	private long distance = 0; // in meters
	private long duration = 0; // in seconds
	private Hashtable<String, String> tags = null;
	
	private BoundsType bounds = null;
	private String activity = "";
	
	private int rgbColor = 0xFFFFFF; // white
	
	private String sourceUrn;
	
	// internal metadata
	private long id = -1;
	private String loaderClass;
	private String progVersion;
	private long userId = 0;
	private boolean compressed = false;
	private Date entryDate;
	private String checksum = "";
		
	/**
	 * Default Constructor
	 */
	public GpsRecord() {
		
	}
	
	/**
	 * Create this entry with metadata from {@link GPXFile}
	 * @param gpx
	 */
	public GpsRecord(GPXFile gpx) {
		setFields(gpx);
	}

	/**
	 * fill this {@link GpsRecord} with information from {@link GPXFile) 
	 * @param gpx
	 */
	public void setFields(GPXFile gpx) {
		id = gpx.getDbId();
		setName(gpx.getName());
		setColor(gpx.getColor());
		startTime = gpx.getStartTime();
		endTime = gpx.getEndTime();
		distance = (long) gpx.getLengthMeters();
		duration = gpx.getDuration();
		bounds = gpx.getMetadata().getBounds();
		if (gpx.getExtension().containsKey(Const.EXT_ACTIVITY)) {
			setActivity(gpx.getExtension().getSubValue(Const.EXT_ACTIVITY));
		}
		
		loaderClass = "";
		progVersion = GpsMaster.ME;
		sourceUrn = "";
		userId = 0;
		compressed = false;
		entryDate = new Date(); // now
		checksum = "";
		
	}

	/**
	 * @return the record id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @param id the record id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */	
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return distance in meters
	 */
	public long getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(long distance) {
		this.distance = distance;
	}

	/**
	 * @return duration in seconds
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	
	/**
	 * @return the bounds
	 */
	public BoundsType getBounds() {
		if (bounds == null) {
			bounds = new BoundsType();
		}
		return bounds;
	}

	/**
	 * @param bounds the bounds to set
	 */
	public void setBounds(BoundsType bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return new Color(rgbColor);
	}

	/**
	 * @param color the color to set
	 * ATTENTION transparency/alpha channel is lost
	 */
	public void setColor(Color color) {
		rgbColor = color.getRGB();
	}

	/**
	 * 
	 * @return
	 */
	public int getRgbColor() {
		return rgbColor;
	}
	
	/**
	 * 
	 * @param rgb
	 */
	public void setRgbColor(int rgb) {
		rgbColor = rgb;
	}
	
	/**
	 * @return the sourceUrn
	 */
	public String getSourceUrn() {
		return sourceUrn;
	}

	/**
	 * @param sourceUrn the sourceUrn to set
	 */
	public void setSourceUrn(String sourceUrn) {
		this.sourceUrn = sourceUrn;
	}

	/**
	 * @return the loaderClass
	 */
	public String getLoaderClassName() {
		return loaderClass;
	}

	/**
	 * @param loaderClass the loaderClass to set
	 */
	public void setLoaderClassName(String loaderClass) {
		this.loaderClass = loaderClass;
	}

	/**
	 * @return the progVersion
	 */
	public String getProgVersion() {
		return progVersion;
	}

	/**
	 * @param progVersion the progVersion to set
	 */
	public void setProgVersion(String progVersion) {
		this.progVersion = progVersion;
	}
		
	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return the compressed
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * @param compressed the compressed to set
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/**
	 * @return the entryDate
	 */
	public Date getEntryDate() {
		return entryDate;
	}

	/**
	 * @param entryDate the entryDate to set
	 */
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	/**
	 * @return the tags
	 */
	private Hashtable<String, String> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	private void setTags(Hashtable<String, String> tags) {
		this.tags = tags;
	}

	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * 
	 * @param checksum
	 */
	public void setChecksum(byte[] checksum) {
		String hex = (new HexBinaryAdapter()).marshal(checksum); 
		this.checksum = hex;
	}

}
