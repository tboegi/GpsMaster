package org.gpsmaster.dialogs;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;

/**
 * A transparent {@link JLabel} with an icon on top
 * and the activity name below.
 * Icons are retrieved from gpsmaster.icons.activities
 *
 *  
 *  CURRENTLY NOT USED.
 *  intended as popup-tooltip for "Activity" column in {@link DBDialog}
 *  
 * @author rfu
 *
 */
public class ActivityPanel extends JPanel {

	private ImageIcon icon = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -469139267713553169L;

	
	/**
	 * 
	 * @param activity
	 */
	public ActivityPanel(String activity) {
		
		JLabel lblIcon = new JLabel();
		JLabel lblText = new JLabel();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		// button with icon
		lblIcon.setOpaque(false);
		lblIcon.setVisible(true);
		lblIcon.setBackground(Color.WHITE);
		lblIcon.setBorder(new EmptyBorder(0, 0, 0, 0));
		lblIcon.setAlignmentX(CENTER_ALIGNMENT);	
		add(lblIcon);
		
		// text
		lblText.setOpaque(true);
		lblText.setVisible(true);
		lblText.setForeground(Color.BLACK);
		lblText.setBackground(Const.TRANSPARENTWHITE);
		lblText.setAlignmentX(CENTER_ALIGNMENT);
		lblText.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(lblText);
		
		String resource = Const.ICONPATH_ACTIVITIES + activity + ".png";		
		try {
			icon = new ImageIcon(GpsMaster.class.getResource(resource));
			lblIcon.setIcon(icon);
			if (activity.equals("_notset")) {
				lblText.setText("(Activity not set)");
			} else {
				lblText.setText(activity);
			}
		} catch (NullPointerException e) {
			// not found, set "missing" icon
			icon = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_ACTIVITIES.concat("_noicon.png")));
			lblIcon.setIcon(icon);
			lblText.setText(activity);			
		}	
		setVisible(true);
		validate();
		
	}
}
