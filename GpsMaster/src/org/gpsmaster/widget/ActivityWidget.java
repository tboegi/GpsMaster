package org.gpsmaster.widget;


import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;

import eu.fuegenstein.swing.Widget;
import eu.fuegenstein.swing.WidgetLayout;

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
	
	private final String unknown = "Unknown";
	private String activity = unknown;   
	
	/**
	 * Default constructor
	 */
	public ActivityWidget() {
		super(WidgetLayout.TOP_RIGHT);
		setup();
	}

	public ActivityWidget(int corner) {
		super(corner);
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
		setOpaque(false);

		// button with icon
		btnActivity.setOpaque(false);
		btnActivity.setVisible(true);
		btnActivity.setBackground(Color.WHITE);
		btnActivity.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnActivity.setAlignmentX(CENTER_ALIGNMENT);
		btnActivity.setToolTipText("Click to set activity");
	
		add(btnActivity);
		
		// text
		lblActivity.setOpaque(true);
		lblActivity.setVisible(true);
		lblActivity.setForeground(Color.BLACK);
		lblActivity.setBackground(BACKGROUNDCOLOR);
		lblActivity.setAlignmentX(CENTER_ALIGNMENT);
		lblActivity.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(lblActivity);
		
		setActivity(activity);
	}
	
		
    /* PUBLIC METHODS
     * -------------------------------------------------------------------------------------------------------- */        

	/**
	 * 
	 * @return
	 */
	public String getActivity() {
		return activity;
	}
	
	public void setEnabled(boolean enabled) {
		btnActivity.setEnabled(enabled);
	}
	/**
	 * 
	 * @param activity
	 */
	public void setActivity(String activity) {
		this.activity = activity;
		String resource = Const.ICONPATH_ACTIVITIES.concat(activity).concat(".png");
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(GpsMaster.class.getResource(resource));
			btnActivity.setIcon(icon);
			if (activity.equals("_notset")) {
				lblActivity.setText("(Activity not set)");
			} else {
				lblActivity.setText(activity);
			}
		} catch (NullPointerException e) {
			// not found, set "missing" icon
			icon = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_ACTIVITIES.concat("_noicon.png")));
			btnActivity.setIcon(icon);
			lblActivity.setText(activity);			
		}						
		validate();
	}

	/**
	 * 
	 * @param propertyListener
	 */
	public void addActionListener(ActionListener listener) {
		if (listener != null) {
			btnActivity.addActionListener(listener);
		}
	}

}
