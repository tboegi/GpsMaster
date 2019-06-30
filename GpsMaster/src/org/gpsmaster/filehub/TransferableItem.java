package org.gpsmaster.filehub;

/**
 * Abstract Class containing methods common to (all) transferable items
 *  
 * @author rfu
 *
 */
public abstract class TransferableItem implements ITransferableItem {

	protected Exception ex = null;
	protected int state = ITransferableItem.STATE_PENDING;
	
	/**
	 * 
	 */
	public void setException(Exception e) {
		ex = e;		
	}

	/**
	 * 
	 */
	public Exception getException() {
		return ex;
	}

	/**
	 * 
	 */
	public int getState() {
		return state;
	}

	/**
	 * 
	 */
	public void setState(int state) {
		this.state = state;
	}
}
