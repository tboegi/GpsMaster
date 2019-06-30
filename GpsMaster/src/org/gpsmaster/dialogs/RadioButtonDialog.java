package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import eu.fuegenstein.messagecenter.MessageCenter;

/**
 * Class implementing all panels for a dialog with radio buttons
 *  - radio buttons on the left
 *  - parameter / info msgPanel in the center
 *  - button msgPanel at bottom
 *  
 * @author rfu
 *
 */
public class RadioButtonDialog extends GenericDialog {

	protected JPanel radioPanel = null;  // for radio buttons on the left
	protected JPanel buttonPanel = null; // for action buttons at bottom
	protected JPanel infoPanel = null; // center msgPanel for info / params
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3688870762140855960L;

	/**
	 * 
	 * @param parentFrame
	 */
	public RadioButtonDialog(JFrame parentFrame) {
		super(parentFrame);
		setup();
	}

	/**
	 * 
	 * @param parentFrame
	 * @param msg
	 */
	public RadioButtonDialog(JFrame parentFrame, MessageCenter msg) {
		super(parentFrame, msg);
		setup();
	}

	/**
	 * 
	 * @param msgPanel
	 */
	protected void setInfoPanel(JPanel panel) {
		remove(infoPanel);
		add(panel, BorderLayout.CENTER);
		infoPanel = panel;
	}
	
	/**
	 * 
	 */
	private void setup() {
		
		setLayout(new BorderLayout());
		setBackground(backgroundColor);
		
		radioPanel = new JPanel();
		radioPanel.setBackground(backgroundColor);
		radioPanel.setLayout(new GridLayout(0, 1));
		radioPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		
		buttonPanel = new JPanel();
		buttonPanel.setBackground(backgroundColor);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		infoPanel = new JPanel();
		
		add(radioPanel, BorderLayout.WEST);
		add(buttonPanel, BorderLayout.SOUTH);
		add(infoPanel, BorderLayout.CENTER);

	}
	
	protected void setRadioButtonVisibility() {
		
	}
	
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void begin() {
		// TODO Auto-generated method stub
		
	}

}
