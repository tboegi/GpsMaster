package org.gpsmaster;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestDialog extends JDialog {

	JFrame parentFrame = null;

	/**
	 *
	 * @param frame
	 */
	public TestDialog(JFrame frame) {
		super(frame);
		parentFrame = frame;

		setLocationRelativeTo(parentFrame);

		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BorderLayout());

		frame.getContentPane().add(dialogPanel);
		pack();

		Point location = new Point();
		location.x = parentFrame.getLocation().x + parentFrame.getWidth() / 2 - getWidth() / 2;
		location.y = parentFrame.getLocation().y + parentFrame.getHeight() / 2 - getHeight() / 2;
		setLocation(location);

	}

}
