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
	public void addProgressItem(ProgressItem item);

	/**
	 * 
	 * @param item
	 */
	public void removeProgressItem(ProgressItem item);
	
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
	 * 
	 */
	public void clear();
	
}
