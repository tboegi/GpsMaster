package org.gpsmaster.dialogs;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import eu.fuegenstein.util.Log;
import eu.fuegenstein.util.LogEntry;

/**
 * {@link JPanel} containing {@link Log} entries
 * @author rfu
 * 
 * TODO show as table with status icon and text
 *
 */
public class TransferableItemLogPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7005801299680700253L;	

	private JTextArea statusText = null;
	private boolean autoHide = false;
	
	/**
	 * Constructor
	 */
	public TransferableItemLogPanel() {
		super();		
		setLayout(new BorderLayout());
		
		statusText = new JTextArea();
		statusText.setEditable(false);
		statusText.setLineWrap(true);		
		add(statusText, BorderLayout.CENTER);		
	}

	/**
	 * @return the autoHide
	 */
	public boolean isAutoHide() {
		return autoHide;
	}

	/**
	 * Show / hide this panel based on current {@link Log}
	 * @param true: hide if {@link Log} == null or {@link Log} is empty
	 */
	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}

	/**
	 * 
	 * @param log
	 */
	public void setLog(Log log) {
		if (autoHide && ((log == null) || (log.getEntries().size() == 0))) {
			this.setVisible(false);
		} else {
			setStatusText(log);
			this.setVisible(true);
		}
	}
	
	/**
	 * 
	 * @param item
	 * TODO consistently handle LogEntry.Location
	 */
	private void setStatusText(Log log) {
		String text = "";
		if (log != null) {
			for (LogEntry logEntry : log.getEntries()) {
				text += logEntry.toString() + "\n";			
			}
		}
		statusText.setText(text);
		// setPreferredSize(statusText.getPreferredSize());
	}
}
