package org.gpsmaster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.gpsmaster.dialogs.ActivityWidget;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.util.ClassUtils;

/**
 * Handles the display of the {@link ActivityWidget} based on
 * the content of GPX file extensions
 * Also handles the selection of a new activity by the user
 * @author rfu
 *
 * TODO: generalize this to be a handler for all kind of widgets (icons)
 * based on GPXFile extensions
 *
 */
public class ActivityHandler {

	private MessageCenter msg = null;
	private JPanel panel = null; // Panel on which the widget is to be displayed
	private GPXFile gpx = null;

	private JToolBar pickerBar = null;
	private JScrollPane scrollPane = null; // ScrollPane for pickerBar
	private Container pickerContainer = null;
	private ActivityWidget widget = new ActivityWidget();
	private ActionListener widgetListener = null; // called when widget is clicked
	private ActionListener pickerListener = null;

	// private boolean msgDisplayed = false;

	/**
	 * Constructor
	 * @param widgetPanel JPanel holding the ActivityWidget
	 * @param pickerContainer Container to attach the Widget Picker Toolbar to
	 */
	public ActivityHandler(JPanel widgetPanel, Container pickerContainer, MessageCenter msg) {
		this.panel = widgetPanel;
		this.msg = msg;
		this.pickerContainer = pickerContainer;

		/**
		 * Listener called when user clicks on activity widget on MapPanel
		 */
		widgetListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				checkPicker();
			}
		};

		/**
		 * Listener called when user selects an activity from the sidebar (pickerBar)
		 */
		pickerListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("pickerListener");
				JButton button = (JButton) e.getSource();
				ActivityWidget widget = (ActivityWidget) button.getParent();
				System.out.println(widget.getActivity());
				setActivity(widget.getActivity());
				if (gpx.getExtensions().containsKey("gpsm:activity")) {
					gpx.getExtensions().remove("gpsm:activity");
				}
				if (gpx.getExtensions().containsKey("activity")) { // legacy support
					gpx.getExtensions().remove("activity");
				}
				gpx.getExtensions().put("gpsm:activity", widget.getActivity());

				closePicker();
			}
		};

		/**
		 * Listener called from __ActivityPicker
		 */
		widget.addActionListener(widgetListener);
		widget.setVisible(false);
	}

	/**
	 * Sets the current GPX file. if NULL, the activity widget is disabled.
	 * @param gpxFile
	 */
	public void setGpxFile(GPXFile gpxFile) {
		this.gpx = gpxFile;
		checkGpxFile();
	}

	public GPXFile getGpxFile() {
		return gpx;
	}

	public ActivityWidget getWidget() {
		return widget;
	}

	/**
	 *
	 * @return
	 */
	public String getActivity() {
		return widget.getActivity();
	}

	/**
	 *
	 * @param activity
	 */
	public void setActivity(String activity) {
		if (activity.equals(widget.getActivity()) == false) {
			widget.setActivity(activity);
		}
		widgetOn();
	}

	/**
	 * called by ActionListener when user clicks on activity widget:
	 * open ActionPicker dialog
	 * TODO better make in Constructor()
	 */
	private void checkPicker() {
		if (scrollPane == null) {
			makePicker();
		} else {
			closePicker();
		}
	}

	/**
	 * Open/display the activity picker toolbar
	 */
	private void makePicker() {
		pickerBar = new JToolBar(JToolBar.VERTICAL);
		pickerBar.setFloatable(false);
		pickerBar.setBackground(Color.WHITE);
		scrollPane = new JScrollPane(pickerBar);

        try {
			for (String iconFile : ClassUtils.getResources("/org/gpsmaster/icons/activities/")) {
				if (iconFile.startsWith("_") == false) {
					ActivityWidget widget = new ActivityWidget();

					widget.setActivity(iconFile.replace(".png", ""));
					widget.addActionListener(pickerListener);
					pickerBar.add(widget);
				}
			}
			pickerContainer.add(scrollPane, BorderLayout.EAST);
			pickerContainer.validate();
/*
			if (msgDisplayed == false) {
				msg.volatileInfo("Can you help with additional free activity icons and/or icon sets? if yes: info@gpsmaster.org");
				msgDisplayed = true;
			}
*/
		} catch (Exception e) {
			msg.error("Unable to get icon list", e);
		}
	}

	/**
	 *
	 */
	private void closePicker() {
		pickerContainer.remove(scrollPane);
		pickerContainer.validate();
		scrollPane = null;
	}

	/**
	 * display widget if it isn't already displaying
	 */
	private void widgetOn() {
		if (widget.isVisible() == false) {
			widget.setVisible(true);
			panel.add(widget);
		}
	}

	/**
	 * hide widget if it isn't already hidden
	 */
	private void widgetOff() {
		if (widget.isVisible()) {
			widget.setVisible(false);
			panel.remove(widget);
		}
	}

	/**
	 * check if active GPX file contains an activity
	 *
	 */
	private void checkGpxFile() {

		if (gpx != null) {
			if (gpx.getExtensions().containsKey("gpsm:activity")) {
				setActivity(gpx.getExtensions().get("gpsm:activity"));
				widgetOn();
			} else if (gpx.getExtensions().containsKey("activity")) { // legacy support
				setActivity(gpx.getExtensions().get("activity"));
				widgetOn();
			} else {
				// display dummy/unknown icon!
				setActivity("_notset");
			}
		} else {
			// if no GPX file is selected and widget is
			// displaying, remove it from parent panel
			widgetOff();
		}
	}
}
