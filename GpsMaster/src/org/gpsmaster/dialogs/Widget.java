package org.gpsmaster.dialogs;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Widget extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -453248357188458175L;

	
	public Widget() {
	
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setOpaque(false);
		setAlignmentY(Component.TOP_ALIGNMENT);
		
	}
	
}
