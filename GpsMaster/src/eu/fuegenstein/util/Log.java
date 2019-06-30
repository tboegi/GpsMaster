package eu.fuegenstein.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class containing a list of {@link LogEntry}s and some helper methods
 * 
 * @author rfu
 *
 */
public class Log {

	private List<LogEntry> logEntries = new ArrayList<LogEntry>();
	
	public void addEntry(LogEntry entry) {
		logEntries.add(entry);
	}
	
	public void addEntry(int level, String text, Exception e) {
		LogEntry entry = new LogEntry();
		entry.setLevel(level);
		entry.setMessage(text);
		entry.setException(e);
		logEntries.add(entry);		
	}

	/**
	 * 
	 * @return LogEntries or empty list
	 */
	public List<LogEntry> getEntries() {
		return logEntries;
	}
	
	/**
	 * Get the number of log entries containing an Error or Warning
	 * @return
	 */
	public int getFailureCount() {
		int count = 0;
		for (LogEntry logEntry : logEntries) {
			switch (logEntry.getLevel()) {
			case LogEntry.WARNING:
				count++;
				break;
			case LogEntry.ERROR:
				count++;
				break;
			}
		}
		return count;
	}
	
	/**
	 * get the "worst" state of all log entries.
	 * if no error / warning occured, {@link LogEntry}.INFO is returned
	 * @return
	 */
	public int getFailureState() {
		int ret = LogEntry.INFO;
		for (LogEntry logEntry : logEntries) {
			ret = Math.max(ret, logEntry.getLevel());
		}
		return ret;
	}
	
	/**
	 * Get the number of entries with the given LogLevel contained in this log
	 * @param level
	 * @return
	 */
	public int getCount(int level) {
		int count = 0;
		for (LogEntry logEntry : logEntries) {
			if (logEntry.getLevel() == level) {
				count++;
			}
		}
		return count;
	}
}
