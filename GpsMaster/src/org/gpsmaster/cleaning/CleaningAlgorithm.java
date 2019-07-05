package org.gpsmaster.cleaning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.dialogs.CleaningStats;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.markers.RemoveMarker;
import org.gpsmaster.markers.Marker;

import eu.fuegenstein.util.CommonParameter;

/**
 * Base class implementing track cleaning
 * functionality by removing certain points from tracks
 *
 * @author rfu
 *
 * Inspired by GpsPrune
 *
 * TODO support List<WaypointGroup> instead of just one {@link WaypointGroup}
 */
public abstract class CleaningAlgorithm {

	private JPanel algorithmPanel = null;
	private CleaningStats statPanel = null;
	protected List<CommonParameter> parameters = new ArrayList<CommonParameter>();

	protected Hashtable <WaypointGroup, List<Waypoint>> allGroups = new Hashtable<WaypointGroup, List<Waypoint>>();
	protected List<Marker> markerList = null;

	/**
	 * Default constructor
	 */
	public CleaningAlgorithm() {
		// makePanel();
	}

	/**
	 * set list of active {@link WaypointGroup}s
	 * @param groups
	 */
	public void setWaypointGroups(List<WaypointGroup> groups) {
		allGroups.clear();
		for (WaypointGroup group : groups) {
			List<Waypoint> toDelete = new ArrayList<Waypoint>();
			allGroups.put(group, toDelete);
		}
	}

	/**
	 * Waypoints to be deleted will be added as Markers to this list
	 * for preview on the {@link GPXPanel}
	 * @param markers List of markers from {@link GPXPanel}
	 */
	public void setMarkerList(List<Marker> markers) {
		markerList = markers;
	}

	/**
	 * perform cleaning - remove obsolete trackpoints
	 */
	public void doClean() {

		if (getNumDelete() == 0) {
			applyAll();
		}

		Enumeration<WaypointGroup> keys = allGroups.keys();
		while (keys.hasMoreElements()) {
			WaypointGroup group = keys.nextElement();
			List<Waypoint> toDelete = allGroups.get(group);
			for (Waypoint wpt : toDelete) {
				group.getWaypoints().remove(wpt);
			}
			group.updateAllProperties();
		}
		clear();
	}

	/**
	 * determine all Trackpoints to be deleted
	 * and add them to markerList for preview
	 */
	public void preview() {
		clear();
		applyAll();
		populateMarkerList();

		if (statPanel != null) {
			int totalPts = 0;
			for (WaypointGroup group : allGroups.keySet()) {
				totalPts += group.getNumPts();
			}
			statPanel.setStats(getAffected(), totalPts);
		}
	}

	/**
	 *
	 * @return number of trackpoints affected by this algorithm
	 */
	public long getAffected() {
		int numDelete = getNumDelete();
		if (numDelete == 0) {
			applyAll();
			numDelete = getNumDelete();
		}
		return numDelete;
	}

	/**
	 *
	 * @return String containing the name of this algorithm
	 */
	public abstract String getName();

	/**
	 *
	 * @return String containing a short description of this algorithm
	 */
	public abstract String getDescription();

	public List<CommonParameter> getParameters() {
		return parameters;
	}


	/**
	 * Create a {@link JPanel} containing all GUI elements for this algorithm
	 * @param backGroundColor panel's Background color
	 * @return {@link JPanel} containing GUI elements
	 */
	public JPanel getPanel(Color backGroundColor) {
		if (algorithmPanel == null) {
			algorithmPanel = new JPanel();
			makePanel(backGroundColor);
		}
		return algorithmPanel;
	}

	/**
	 *
	 * @return {@link JPanel} containing all GUI elements for this algorithm
	 */
	public JPanel getPanel() {
		if (algorithmPanel != null) {
			return algorithmPanel;
		}
		return getPanel(Color.WHITE);
	}

	/**
	 * reset class
	 */
	public void clear() {
		clearMarkerList();
		for (List<Waypoint> toDelete : allGroups.values()) {
			toDelete.clear();
		}
		if (statPanel != null) {
			statPanel.clear();
		}
	}

	/**
	 * find all obsolete Trackpoints
	 */
	protected abstract void applyAlgorithm(WaypointGroup group, List<Waypoint> toDelete);

	/**
	 * add all Waypoints to be deleted as Markers to MarkerList
	 */
	protected void populateMarkerList() {
		if (markerList != null) {
			for (List<Waypoint> toDelete : allGroups.values()) {
				for (Waypoint wpt : toDelete) {
					RemoveMarker marker = new RemoveMarker(wpt);
					marker.setMarkerPosition(Marker.POSITION_CENTER);
					markerList.add(marker);
				}
			}
		}
	}

	/**
	 * remove preview markers from markerList
	 */
	protected void clearMarkerList() {
		if (markerList != null) {
			List<Marker> deleteList = new ArrayList<Marker>();
			for (Marker marker : markerList) {
				// caveat: remove only markers generated by this.subclass
				if (marker instanceof RemoveMarker) {
					deleteList.add(marker);
				}
			}
			for (Marker marker : deleteList) {
				markerList.remove(marker);
			}
		}
	}

	/**
	 * Create the {@link JPanel} containing all GUI components for this algorithm
	 */
	protected void makePanel(Color backgroundColor) {

		// algorithmPanel.setPreferredSize(new Dimension(10,  10));
		algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.Y_AXIS));
		algorithmPanel.setBackground(backgroundColor);

		Font nameFont = new Font(algorithmPanel.getFont().getFamily(), Font.BOLD, algorithmPanel.getFont().getSize() + 2);
		JLabel nameLabel = new JLabel();
		nameLabel.setFont(nameFont);
		nameLabel.setText(getName());
		nameLabel.setAlignmentX(0.0f);
		algorithmPanel.add(nameLabel);

		if (getDescription().isEmpty() == false) {
			JTextArea descLabel = new JTextArea();
			descLabel.setFont(algorithmPanel.getFont());
			descLabel.setPreferredSize(new Dimension(220, 120));
			descLabel.setText(getDescription());
			descLabel.setEditable(false);
			descLabel.setLineWrap(true);
			descLabel.setWrapStyleWord(true);
			descLabel.setAlignmentX(0.0f);
			algorithmPanel.add(descLabel);
			// algorithmPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		}

		for (CommonParameter p : parameters) {
			JPanel paramPanel = p.getGuiComponent(new Dimension(40, 20));
			paramPanel.setAlignmentX(0.0f);
			algorithmPanel.add(paramPanel);
		}

		statPanel = new CleaningStats();
		statPanel.setBackground(backgroundColor);
		statPanel.setBorder(new EmptyBorder(3, 0,  0, 0));
		statPanel.setAlignmentX(0.0f);
		algorithmPanel.add(statPanel);

	}

	/**
	 * Apply algorithm to all active {@link WaypointGroup}s
	 */
	private void applyAll() {
		for (WaypointGroup group : allGroups.keySet()) {
			List<Waypoint> toDelete = allGroups.get(group);
			applyAlgorithm(group, toDelete);
		}
	}

	/**
	 *
	 * @return number of waypoints marked for deletion
	 */
	private int getNumDelete() {
		int toDelete = 0;

		for (List<Waypoint> list : allGroups.values()) {
			toDelete += list.size();
		}
		return toDelete;
	}
}
