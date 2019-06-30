package org.gpsmaster.dialogs;

import javax.swing.JLabel;

public class CleaningStats extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1210256682411783255L;

	/*
	 * Default constructor
	 */
	public CleaningStats() {
		super();					
		clear();
	}
	
	/**
	 * 
	 */
	public void clear() {
		setText("Points to delete: 0");
	}
	/**
	 * 
	 * @param affected
	 * @param total
	 */
	public void setStats(long affected, long total) {
		String text = String.format("Points to delete: %d (%.0f%%)", affected, (double) affected / (double) total * 100);
		setText(text);
	}
	
	
}
