package eu.fuegenstein.util;

import org.joda.time.DateTime;

/**
 * 
 * @author rfu
 *
 */
public class LogEntry {

	public static final int UNDEFINED = 0;
	public static final int INFO = 1; // also: OK
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	
	private DateTime timeStamp = DateTime.now();
	private int level = UNDEFINED;
	private String location = "";
	private String message = "";
	private Exception exception = null;
	
	/**
	 * 
	 * @return
	 */
	public DateTime getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}
	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String toString() {
		String text = "";
		switch(level) {
		case LogEntry.INFO:
			text = "INFO";
			break;
		case LogEntry.WARNING:
			text = "WARNING";
			break;
		case LogEntry.ERROR:
			text = "ERROR";
			break;			
		}
		text += "@" + location + ": " + message + " " + exception.getMessage();
		
		return text;
	}
}
