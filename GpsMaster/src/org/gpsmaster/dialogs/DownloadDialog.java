package org.gpsmaster.dialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;

import eu.fuegenstein.messagecenter.MessageCenter;

/**
 * Base class for download dialog
 * to be inherited by service-specific classes
 *  
 * @author rfu
 *
 */
public class DownloadDialog {

	protected MessageCenter msg = null;
	
	protected JDialog dialog = null;
	protected JPanel filterPanel = null;
	private JButton loadButton = null;
	private JButton cancelButton = null;
	private JTextArea descriptionArea = null;
	

	// table model
	protected JTable trackTable = null;
	
	
	protected boolean cancelled = false;
	
	/**
	 * Constructor
	 * @param msg
	 */
	public DownloadDialog(MessageCenter msg) {
		super();
		this.msg = msg;
	}
	
	/**
	 * Set up dialog
	 */
	private void setup() {
		
	}
}
