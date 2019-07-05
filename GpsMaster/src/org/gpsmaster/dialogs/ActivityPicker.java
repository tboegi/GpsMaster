package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.util.ClassUtils;

// obsolete, to be deleted
public class ActivityPicker extends JDialog {

	// private MessageCenter msg = null;

	private Container contentPane = null;
	private ActionListener pickListener = null;

	/**
	 *
	 */
	private static final long serialVersionUID = -3621797428482268606L;

	/**
	 *
	 * @param Panel set position relative to this panel
	 * @param msg {link MessageCenter} for error messages
	 *
	 * TODO retrieve icons from website and store locally in cache
	 */
	public ActivityPicker(JPanel panel, MessageCenter msg) {

		// this.msg = msg;

		// set bounds
        int width = 400;
        int x_offset = (panel.getWidth() - width) / 2;
        int height = 250;
        int y_offset = (panel.getHeight() - height) / 2;
        setBounds(panel.getX() + x_offset, panel.getY() + y_offset, width, height);
        setPreferredSize(new Dimension(width, height));
        setTitle("Select Activity");

        contentPane = getContentPane();
        contentPane.setBackground(Color.white);
        contentPane.setLayout(new BorderLayout());

        JPanel iconPane = new JPanel();
        iconPane.setBackground(Color.WHITE);
        iconPane.setLayout(new FlowLayout(FlowLayout.LEADING));
        iconPane.setSize(new Dimension(220, 400));
        iconPane.setMaximumSize(new Dimension(220, 400));
        iconPane.setPreferredSize(new Dimension(220, 400));
        iconPane.setMinimumSize(new Dimension(220, 400));

        /**
         * called when widget (icon) is picked (clicked) by user
         */
        pickListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e != null) {
					JButton button = (JButton) e.getSource();
					ActivityWidget widget = (ActivityWidget) button.getParent();
					firePropertyChange("picked", null, widget.getActivity());
				}
			}
		};

		/**
		 * notify ActivityHandler when window is closed.
		 */
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				firePropertyChange("closed", null, null);

			}

		});

        try {
			for (String iconFile : ClassUtils.getResources("org/gpsmaster/icons/activities/")) {
				if (iconFile.startsWith("_") == false) {
					ActivityWidget widget = new ActivityWidget();

					widget.setActivity(iconFile.replace(".png", ""));
					widget.addActionListener(pickListener);
					iconPane.add(widget);
				}
			}
		} catch (Exception e) {
			msg.error("Unable to get icon list", e);
		}

        contentPane.add(iconPane, BorderLayout.PAGE_START);

        JButton btnClose = new JButton();
        btnClose.setText("Close");

        contentPane.add(btnClose, BorderLayout.PAGE_END);
        pack();
	}

	/**
	 *
	 * @param listener
	 */
	public void addPickerListener(ActionListener listener) {
		// this.widgetListener = listener;

	}
}
