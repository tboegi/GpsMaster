package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.GpsMaster;

/**
 * Class displaying an Activity.
 * for each activity set via {@link setActivity()}, a .png file
 * with the same name has to exist in "/org/gpsmaster/icons/activities/"
 *
 * @author rfu
 *
 */
public class ActivityWidget extends Widget {

	private static final long serialVersionUID = 4245359012331910150L;

	private JButton btnActivity = new JButton();
	private JLabel lblActivity = new JLabel();
	private Color transparentWhite = new Color(255, 255, 255, 120);

	private final String basePath = "/org/gpsmaster/icons/activities/";
	private final String unknown = "Unknown";
	private String activity = unknown;

	/**
	 * Default constructor
	 * @param parent
	 */
	public ActivityWidget() {
		super();
		setup();
	}

	/**
	 *
	 * @param parent
	 * @param activity
	 */
	public ActivityWidget(String activity) {
		super();
		this.activity = activity;
		setup();
	}


	/**
	 *
	 */
	private void setup() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// button with icon
		btnActivity.setOpaque(false);
		btnActivity.setVisible(true);
		btnActivity.setBackground(Color.WHITE);
		btnActivity.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnActivity.setAlignmentX(CENTER_ALIGNMENT);
		add(btnActivity);

		// text
		lblActivity.setOpaque(true);
		lblActivity.setVisible(true);
		lblActivity.setForeground(Color.BLACK);
		lblActivity.setBackground(Color.ORANGE);
		lblActivity.setAlignmentX(CENTER_ALIGNMENT);
		lblActivity.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(lblActivity);

		setActivity(activity);
	}

	/**
	 *
	 *
	 */
	private void setIcon() {
		String resource = basePath.concat(activity).concat(".png");
		ImageIcon icon = null;
		// TRY/CATCH!
		icon = new ImageIcon(GpsMaster.class.getResource(resource));
		btnActivity.setIcon(icon);

	}


    /* PUBLIC METHODS
     * -------------------------------------------------------------------------------------------------------- */

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
		setIcon();
		lblActivity.setText(activity);

		int width = lblActivity.getPreferredSize().width;
		int height = lblActivity.getPreferredSize().height + btnActivity.getPreferredSize().height;
		// Dimension dim = new Dimension(width, height);
		Dimension dim = new Dimension(100, 100);
		setPreferredSize(dim);
		setMaximumSize(dim);
		setMinimumSize(dim);

	}


}
