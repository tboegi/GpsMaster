package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.gpsmaster.GpsMaster;

public class ActivityWidget extends JDialog {
	
	private Component parent = null;
	private BorderLayout layout = new BorderLayout();
	private JButton btnActivity = new JButton();
	
	private String activity = "00None";
	private final String basePath = "/org/gpsmaster/icons/activities/";
	private final String unknown = "Unknown";
	
	/**
	 * Default constructor
	 * @param parent
	 */
	public ActivityWidget(Component parent) {
		this.parent = parent;
		
	}

	/**
	 * 
	 * @param parent
	 * @param activity
	 */
	public ActivityWidget(Component parent, String activity) {
		this.activity = activity;
		// invoke constructor(parent);
	}
	
	
	/**
	 * 
	 */
	private void setup() {
		
        setLocation(parent.getLocationOnScreen().x+10, parent.getLocationOnScreen().y+10);
        setUndecorated(true);
		setBackground(Color.WHITE);
		setOpacity(0.80f);
		setLayout(layout);
		
		Container contentPane = getContentPane(); // new JPanel();
		btnActivity.setEnabled(true);
		btnActivity.setVisible(true);
		setIcon(unknown);
		contentPane.add(btnActivity, BorderLayout.CENTER);
		
		setAlwaysOnTop(true);
		setModalityType(ModalityType.MODELESS);
		setResizable(false);
		pack();
	}
	
	/**
	 * 
	 * @param activity
	 */
	private void setIcon(String activity) {
		String resource = basePath.concat(activity).concat(".png");
		ImageIcon icon = null;
//		try {
			icon = new ImageIcon(GpsMaster.class.getResource(resource));
			btnActivity.setIcon(icon);
			btnActivity.setToolTipText("Activity: ".concat(activity));
		//}
			// resize / relocate frame
	        btnActivity.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	// tell main application to open the ActivityPicker
	            	firePropertyChange("activity", null, "pick");
	            }
	            
	        });

	}
	
	
    /* PUBLIC METHODS
     * -------------------------------------------------------------------------------------------------------- */        

	public String getActivity() {
		return activity;
	}
	
	public void setActivity(String activity) {
		this.activity = activity;
		setIcon(activity);		
	}
		

}
