package org.gpsmaster.filehub;

import java.util.ArrayList;
import java.util.List;

import eu.fuegenstein.util.Log;
import eu.fuegenstein.util.LogEntry;

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
	protected String targetFmt = null;
	private String internalId = null;
	protected String className = null;
	protected int state = STATE_UNKNOWN;
	protected Log log = new Log(); 
	
	/**
	 * short, human readable name 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Get the internal ID which uniquely identifies this item
	 * @return the internalId
	 */
	protected String getInternalId() {
		return internalId;
	}

	/**
	 * Set the internal ID which uniquely identifies this item
	 * @param internalId the internalId to set
	 */
	protected void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	/**
	 * 
	 */
	public int getTransferState() {
		return state;
	}

	/**
	 * 
	 */
	public void setTransferState(int state) {
		this.state = state;
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
		
	public Log getLog() {
		return log;
	}
}
