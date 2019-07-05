package org.gpsmaster.dialogs;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Widget extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -453248357188458175L;

	protected Color transparentWhite = new Color(255, 255, 255, 192);

	public Widget() {

		setBorder(new EmptyBorder(10, 0, 5, 10));

	}

}
