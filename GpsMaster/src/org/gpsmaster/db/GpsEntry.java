package org.gpsmaster.db;

import java.awt.Color;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Class representing a record in the dat_gps table
 *  
 * @author rfu
 *
 */
public class GpsEntry {

	// metadata for user
	private String name;
	private Date startTime;
	private Date endTime;
	private long distance = 0; // in meters
	private long duration = 0; // in seconds
	private Hashtable<String, String> tags = null;
	
	private double minLat = 0.0f;
	private double minLon = 0.0f;
	private double maxLat = 0.0f;
	private double maxLon = 0.0f;
	private String activity = "";
	
	private int rgbColor = 0xFFFFFF; // white
	
	private String sourceUrn;
	
	// internal metadata
	private long id = 0;
	private String loaderClass;
	private String progVersion;
	private byte[] gpsData;
	private long userId = 0;
	private boolean compressed = false;
	private Date entryDate;
	private String checksum = "";
	
	
	
	/**
	 * Default Constructor
	 */
	public GpsEntry() {
		
	}
	
	/**
	 * Create this entry with metadata from {@link GPXFile}
	 * @param gpx
	 */
	public GpsEntry(GPXFile gpx) {
		setFields(gpx);
	}

	/**
	 * fill this {@link GpsEntry} with information from {@link GPXFile) 
	 * @param gpx
	 */
	public void setFields(GPXFile gpx) {
		
		name = gpx.getName();
		setColor(gpx.getColor());
		startTime = gpx.getStartTime();
		endTime = gpx.getEndTime();
		distance = (long) gpx.getLengthMeters();
		duration = gpx.getDuration();
		minLat = gpx.getMinLat();
		maxLat = gpx.getMaxLat();
		minLon = gpx.getMinLon();
		maxLon = gpx.getMaxLon();
		if (gpx.getExtensions().containsKey(Const.EXT_ACTIVITY)) {
			setActivity(gpx.getExtensions().get(Const.EXT_ACTIVITY));
		}
		
		loaderClass = "";
		progVersion = GpsMaster.ME;
		gpsData = null;
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
	 * @return the minLat
	 */
	public double getMinLat() {
		return minLat;
	}

	/**
	 * @param minLat the minLat to set
	 */
	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	/**
	 * @return the minLon
	 */
	public double getMinLon() {
		return minLon;
	}

	/**
	 * @param minLon the minLon to set
	 */
	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}

	/**
	 * @return the maxLat
	 */
	public double getMaxLat() {
		return maxLat;
	}

	/**
	 * @param maxLat the maxLat to set
	 */
	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	/**
	 * @return the maxLon
	 */
	public double getMaxLon() {
		return maxLon;
	}

	/**
	 * @param maxLon the maxLon to set
	 */
	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
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
	public String getLoaderClass() {
		return loaderClass;
	}

	/**
	 * @param loaderClass the loaderClass to set
	 */
	public void setLoaderClass(String loaderClass) {
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
	 * @return the gpsData
	 */
	public byte[] getGpsData() {
		return gpsData;
	}

	/**
	 * @param gpsData the gpsData to set
	 */
	public void setGpsData(byte[] gpsData) {
		this.gpsData = gpsData;
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
