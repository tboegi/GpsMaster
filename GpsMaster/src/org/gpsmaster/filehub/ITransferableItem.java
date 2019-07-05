package org.gpsmaster.filehub;

/**
 * Interface to be implemented by classes representing
 * a transferable GPS data item (i.e. a file)
 *
 * @author rfu
 *
 */
public interface ITransferableItem {

	public final int STATE_PENDING = 0;
	public final int STATE_PROCESSING = 1;
	public final int STATE_FINISHED = 2;
	public final int STATE_ERROR = 3;
	public final int STATE_WARNING = 4;

	/**
	 * short, human readable name
	 * @return
	 */
	public String getName();

	/**
	 *
	 * @return
	 */
	public int getState();

	/**
	 *
	 * @param state
	 */
	public void setState(int state);

	/**
	 * 3 letter "extension", representing the file format
	 * of this resource.
	 * @return
	 */
	public String getExtension();

	/**
	 * Set the {@link Exception} that has been thrown
	 * in case of on error while processing this item
	 * @param ex
	 */
	public void setException(Exception ex);

	/**
	 * Get the {@link Exception} that has been thrown
	 * in case of on error while processing this item
	 * @return Exception or NULL if successful
	 */
	public Exception getException();


}
