package org.gpsmaster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.gpsmaster.gpxpanel.GPXExtension;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.widget.ActivityWidget;

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
	private GPXFile gpxFile = null;
	
	private JToolBar pickerBar = null;
	private JScrollPane scrollPane = null; // ScrollPane for pickerBar
	private Container pickerContainer = null;
	private ActivityWidget widget = new ActivityWidget(); 
	private ActionListener widgetListener = null; // called when widget is clicked
	private ActionListener pickerListener = null;	
	private PropertyChangeListener changeListener = null; // called when active GpxObject is changed

	// private boolean msgDisplayed = false;
	
	/**
	 * Constructor
	 * @param widgetPanel JPanel holding this ActivityWidget
	 * @param pickerContainer Container to attach the org.gpsmaster.widget Picker Toolbar to
	 */
	public ActivityHandler(JPanel widgetPanel, Container pickerContainer, MessageCenter msg) {
		this.panel = widgetPanel;
		this.msg = msg;
		this.pickerContainer = pickerContainer;
		
		/*
		 * Listener called when user clicks on activity widget on MapPanel 
		 */
		widgetListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				checkPicker();
			}
		};
	
		/*
		 * Listener called when user selects an activity from the sidebar (pickerBar)
		 */
		pickerListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				ActivityWidget widget = (ActivityWidget) button.getParent();	
				setActivity(widget.getActivity());

				GPXExtension activityElement = gpxFile.getExtension().getExtension(Const.EXT_ACTIVITY);
				if (activityElement != null) {
					activityElement.setValue(widget.getActivity());
				} else {
					gpxFile.getExtension().add(Const.EXT_ACTIVITY, widget.getActivity());
				}
				closePicker();
			}
		};
		
		/*
		 * Listener called when active GPX Object is set 
		 */
		changeListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// set active or update - doesn't matter
				setGpxFile(GpsMaster.active.getGpxFile());				
			}
		};		
		GpsMaster.active.addPropertyChangeListener(changeListener);
				
		 // Listener called from ActivityPicker		 
		widget.addActionListener(widgetListener);
		widget.setVisible(false);				
	}
	
	/**
	 * Sets the current GPX file. if NULL, the activity widget is disabled.
	 * @param newGpx
	 */
	private void setGpxFile(GPXFile newGpx) {
		if (gpxFile == null) { 
			gpxFile = newGpx;
			checkGpxFile();
		} else if (gpxFile.equals(newGpx) == false) {
			gpxFile = newGpx;
			checkGpxFile();			
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public GPXFile getGpxFile() {
		return gpxFile;
	}
	
	/**
	 * 
	 * @return
	 */
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
        	// attention! no leading "/...." !!
			for (String iconFile : ClassUtils.getResources("org/gpsmaster/icons/activities/")) {			
				if (iconFile.startsWith("_") == false) {
					ActivityWidget widget = new ActivityWidget();
					
					widget.setActivity(iconFile.replace(".png", ""));
					widget.addActionListener(pickerListener);
					pickerBar.add(widget);					
				}
			}
			pickerContainer.add(scrollPane, BorderLayout.EAST);
			pickerContainer.validate();
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
		
		if (gpxFile != null) {
			GPXExtension ext = gpxFile.getExtension().getExtension(Const.EXT_ACTIVITY); 
			if (ext != null) {
				setActivity(ext.getValue());
				widgetOn();
			} else {
				// display dummy/unknown icon!
				setActivity("_notset");
			}
		} else {
			// if no GPX file is selected and widget is 
			// displaying, remove it from parent msgPanel
			widgetOff();
		}
	}
}
