package eu.fuegenstein.util;

/**
 * 
 * @author rfu
 *
 */
public interface IProgressReporter {

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title);
	
	/**
	 * 
	 * @param footer
	 */
	public void setFooter(String footer);

	/**
	 * 
	 * @param item
	 */
	public void addProgressItem(ProgressInfo item);

	/**
	 * 
	 * @param item
	 */
	public void removeProgressItem(ProgressInfo item);
	

	/**
	 * display message that cancelling is in progress.
	 * 
	 */
	public void cancel();
	
	/**
	 * 
	 * @return {@link true} if cancelled by user, false otherwise
	 */
	public boolean isCancelled();
	
	/**
	 * update the graphical representation of the progress
	 */	
	public void update();
	
	/**
	 * reset all {@link ProgressInfo}s to zero
	 */
	public void reset();
	
	/**
	 * 
	 */
	public void clear();


	
}
