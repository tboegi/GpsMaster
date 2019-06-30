package org.gpsmaster.device;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class representing a Track entry for selection in the GUI
 * 
 * @author rfu
 *
 */
public class TrackEntry {

	private int id;
	private Date date;
	private String description;
	private String name;
	
	
	/**
	 * Constructor
	 */
	public TrackEntry(int id) {
		this.id = id;
	}
	
	/**
	 * 
	 */
	public int GetId() {
		return id;
	}
		
	/**
	 * Returns the date the track was recorded at
	 * @return
	 */
	public Date GetDate() {
		
		return date;
	}
	
	/**
	 * Sets the date the track was recorded at
	 * (usually the start date)
	 */
	public void SetDate(Date date) {
		this.date = date;
	}
	
	/**
	 * 
	 * @return
	 */
	public String GetName() {
		return name;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void SetName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String GetDescription() {
		return description;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void SetDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 */
	public String ToString() {
		
		// TODO: consider empty/existing members when building this string
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
		String out = df.format(date) + String.format("(%4d) ", id) + name;
	
		return out.trim();
	}
}
