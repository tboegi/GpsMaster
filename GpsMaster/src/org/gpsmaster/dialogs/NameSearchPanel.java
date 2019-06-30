package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gpsmaster.GpsMaster;

public class NameSearchPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1503013835813804411L;

	public NameSearchPanel() {
		
		setup();
	}

	private void setup() {
		setLayout(new BorderLayout());
		final String tooltip ="Search place by name";
		JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(5, 15));
		textField.setToolTipText(tooltip);
		add(textField, BorderLayout.CENTER);
		
		ImageIcon searchIcon = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/dialogs/namesearch.png"));
		JButton searchButton = new JButton();
		searchButton.setIcon(searchIcon);
		searchButton.setToolTipText(tooltip);
		Dimension size = new Dimension(searchIcon.getIconWidth() + 14, searchIcon.getIconHeight() + 6);
		searchButton.setMinimumSize(size);
		searchButton.setPreferredSize(size);
		searchButton.setMaximumSize(size);
		add(searchButton, BorderLayout.EAST);
		
	}
}
