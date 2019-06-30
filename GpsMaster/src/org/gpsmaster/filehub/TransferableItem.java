package org.gpsmaster.filehub;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Class containing methods common to (all) classes representing 
 * a transferable GPS data item (i.e. a file)
 *  
 * @author rfu
 *
 */
public abstract class TransferableItem {

	public static final int STATE_UNKNOWN = 0;		// none of the following
	public static final int STATE_QUEUED = 1;		// queued, waiting for transfer
	public static final int STATE_PROCESSING = 2;	// currently being transferred
	public static final int STATE_FINISHED = 3;	// transferred successfully
	
	protected String sourceFmt = null;
	private String targetFmt = null;
	protected String className = null;
	protected int state = STATE_UNKNOWN;
	protected List<TransferLogEntry> log = new ArrayList<TransferLogEntry>();
	
	/**
	 * short, human readable name 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * 
	 */
	public int getTransferState() {
		return state;
	}

	/**
	 * 
	 */
	protected void setTransferState(int state) {
		this.state = state;
	}
	
	/**
	 * get the "worst" state of all log entries.
	 * if no error / warning occured, {@link TransferLogEntry}.INFO is returned
	 * @return
	 */
	public int getFailureState() {
		int ret = TransferLogEntry.INFO;
		for (TransferLogEntry logEntry : log) {
			ret = Math.max(ret, logEntry.getLevel());
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public String getSourceFormat() {
		return sourceFmt;
	}
	
	/**
	 * 
	 */
	public void setSourceFormat(String ext) {
		this.sourceFmt = ext;
	}
	
	/**
	 * @return the targetFmt
	 */
	public String getTargetFormat() {
		return targetFmt;
	}

	/**
	 * @param targetFmt the targetFmt to set
	 */
	public void setTargetFormat(String targetFmt) {
		this.targetFmt = targetFmt;
	}

	/**
	 * 
	 */
	public String getLoaderClassName() {
		return className;
	}
	
	/**
	 * 
	 */
	public void setLoaderClassName(String className) {
		this.className = className;
	}
	
	protected void addLogEntry(int level, String text, Exception e) {
		TransferLogEntry entry = new TransferLogEntry();
		entry.setLevel(level);
		entry.setMessage(text);
		entry.setException(e);
		log.add(entry);		
	}
	
	public List<TransferLogEntry> getLog() {
		return log;
	}
}
