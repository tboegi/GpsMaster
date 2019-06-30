package org.gpsmaster.markers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.gpxpanel.Waypoint;

/**
 * 
 * @author rfu
 * 
 * ABANDONED, for now
 * 
 */
public class ClickableMarker extends Marker {

	private JPanel marker = new JPanel();
	private JButton button = new JButton();
	// private JLabel label = new JLabel();
	
	private ImageIcon icon = null;
	
	/**
	 * Constructor
	 * @param lat
	 * @param lon
	 */
	public ClickableMarker(double lat, double lon) {
		super(lat, lon);

		marker.setLayout(new BorderLayout());
		marker.setOpaque(false);
		marker.setVisible(true);
		
		button.setOpaque(false);
		button.setVisible(true);
		button.setBackground(Color.WHITE);
		button.setBorder(new EmptyBorder(0, 0, 0, 0));

		label.setOpaque(true);
		label.setBackground(backgroundColor); // in paint()?
		label.setForeground(foregroundColor); // in paint()?
		
		marker.add(button, BorderLayout.CENTER);
		marker.add(label, BorderLayout.SOUTH); // TODO determine in paint() based on labelPosition
		
	}
	
	/**
	 * Constructor
	 * @param waypoint
	 */
	public ClickableMarker(Waypoint waypoint) {
		super(waypoint);
	}
	
	/**
	 * 
	 */
	public void setIcon(ImageIcon icon) {
		button.setIcon(icon);
	}
	
	
	public void addActionListener(ActionListener listener) {
		button.addActionListener(listener);
	}

	/**
	 * 
	 * @param panel
	 * @param location
	 */
	public void paint(JPanel panel, Point location) {
		
	}
}
