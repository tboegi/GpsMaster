package org.gpsmaster;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * Class providing easy access to the active {@link GPXObject}
 * and its sub-objects. Notifies registered classes of change
 * of active objects via Property Change events.
 *
 * also (for now) intended to act as kind of message/event broker
 *
 * @author rfu
 *
 */
public class ActiveGpxObjects {

	private JTree tree = null;
	private Core core = null;
	private Waypoint activeWaypoint = null;
	private WaypointGroup activeGroup = null;
	private GPXObject gpxObject = null;
	private GPXFile gpxFile = null;
	private DefaultMutableTreeNode currSelection = null;
	private List<WaypointGroup> allGroups = new ArrayList<WaypointGroup>();

	private PropertyChangeSupport pcs = null;
	private PropertyChangeListener propertyListener = null;

	// Property names for PropertyChangeEvents
	public final String PCE_NEWGPX = "newGpx"; // add new GPXFile
	public final String PCE_REFRESHGPX = "updateGpx"; // refresh active GpxObject
	public final String PCE_ACTIVEGPX = "activeGpxObject"; // set active GpxObject
	public final String PCE_ACTIVEWPT = "activeWpt"; // set active waypoint
	public final String PCE_REPAINTMAP = "repaintMapPanel"; // repaint map on mapPanel

	/**
	 * all track segments
	 */
	public static final int SEG_TRACK = Core.SEG_TRACK;

	/**
	 * all route segments
	 */
	public static final int SEG_ROUTE = Core.SEG_ROUTE;

	/**
	 * Waypoints only
	 */
	public static final int SEG_WAYPOINTS = Core.SEG_WAYPOINTS;

	/**
	 * all track & route segments
	 */
	public static final int SEG_ROUTE_TRACK = Core.SEG_ROUTE_TRACK;

	/**
	 * waypoint group and all track & route segments
	 */
	public static final int SEG_TRACK_ROUTE_WAYPOINTS = Core.SEG_TRACK_ROUTE_WAYPOINTS;

	/**
	 * waypoint group and all track segments
	 */
	public static final int SEG_TRACK_WAYPOINTS = Core.SEG_TRACK_WAYPOINTS;

	/**
	 * waypoint group and all route segments
	 */
	public static final int SEG_ROUTE_WAYPOINTS = Core.SEG_ROUTE_WAYPOINTS;

	/**
	 * waypoint group and all track & route segments
	 */
	public static final int SEG_ALL = Core.SEG_ALL;


	/**
	 * Default constructor
	 * @param tree The GUI Tree containing all gpx objects for selection by user
	 */
	public ActiveGpxObjects(JTree tree) {
		this.tree = tree;
		core = new Core();

		pcs = new PropertyChangeSupport(this);
		makeListeners();
	}

	/**
	 * Get the active {@link GPXObject}
	 * @return {@link GPXObject} or null if none set
	 */
	public GPXObject getGpxObject() {
		return gpxObject;
	}

	/**
	 * Set the active {@link GPXObject}
	 * @param gpxObject active {@link GPXObject}
	 */
	public void setGpxObject(GPXObject gpxObject) {
		setActiveGpx(gpxObject);
	}

	/**
	 * Get the Root object of the active {@link GPXObject}
	 * @return Parent/Root {@link GPXFile} or null of none set
	 */
	public GPXFile getGpxFile() {
		return gpxFile;
	}

	/**
	 * Get the active {@link WaypointGroup}. This is usually the one that
	 * contains the active {@link Waypoint} (see setWaypoint())
	 * @return active {@link WaypointGroup} or {@link null} if none set
	 */
	public WaypointGroup getGroup() {
		return activeGroup;
	}

	/**
	 * Set the active {@link WaypointGroup}
	 * @param grp
	 * @throws {@link IllegalArgumentException} if given group
	 * is not one of the current {@link GPXObject}s groups
	 */
	public void setGroup(WaypointGroup grp) {
		if ((grp != null) && (allGroups.contains(grp) == false)) {
			throw new IllegalArgumentException();
		}
		activeGroup = grp;
	}

	/**
	 * Get all active {@link WaypointGroup}s, regardless of type
	 * @return active {@link WaypointGroup}s or empty {@link List}
	 */
	public List<WaypointGroup> getGroups() {
		return allGroups;
	}

	/**
	 * Get a subset of the active {@link GPXObject}s groups
	 * @param which
	 * @return List of {@link WaypointGroup}s of given type or empty list
	 */
	public List<WaypointGroup> getGroups(int which) {
		return core.getSegments(gpxObject, which);
	}

	/**
	 * Get the active {@link Waypoint}
	 * @return {@link Waypoint} or {@link null} if none set
	 */
	public Waypoint getWaypoint() {
		return activeWaypoint;
	}

	/**
	 * Set the active {@link Waypoint} and send a change notification.
	 * The {@link WaypointGroup} containing the given {@link Waypoint}
	 * is set as active {@link WaypointGroup} or to {@link null} if
	 * no group contains the given {@link Waypoint}.
	 * @param wpt {@link Waypoint} to set as active
	 */
	public void setWaypoint(Waypoint wpt) {
		setActiveWpt(wpt, true);
	}

	/**
	 * Set the active {@link Waypoint} and send a change notification
	 * @param wpt {@link Waypoint} to set as active
	 * @param autoSetGroup if true, the {@link WaypointGroup} containing
	 * the given {@link Waypoint} is set as active {@link WaypointGroup}.
	 * if false or no group contains the given {@link Waypoint}, the active
	 * {@link WaypointGroup} is set to {@link null}
	 */
	public void setWaypoint(Waypoint wpt, boolean autoSetGroup) {
		setActiveWpt(wpt, autoSetGroup);
	}

	/**
	 * Get the number of active {@link WaypointGroup}s
	 * @return
	 */
	public int getNumWaypointGroups() {
		return allGroups.size();
	}

	/**
	 * Get the total number of waypoints in all active Waypointgroups
	 * @return Number of waypoints, 0 if none.
	 */
	public int getNumWaypoints() {
		int count = 0;
		for (WaypointGroup group : allGroups) {
			count += group.getNumPts();
		}
		return count;
	}

	/**
	 * Get index of {@link Waypoint} in the active {@link WaypointGroup}
	 * @param wpt {@link Waypoint} to search for
	 * @return the index of the first occurrence of the specified {@link Waypoint}
	 * in the active {@link WaypointGroup}, or -1 if this list does not contain the {@link Waypoint}
	 *
	 */
	public int getIndexOf(Waypoint wpt) {
		int idx = -1;
		if ((activeGroup != null) && (wpt != null)) {
			idx = activeGroup.getWaypoints().indexOf(wpt);
		}
		return idx;
	}

	/**
	 * Get the position of the given {@link Waypoint} within all active {@link WaypointGroup}s
	 * @param wpt {@link Waypoint} to search for
	 * @return
	 */
	public int getTotalIndexOf(Waypoint wpt) {
		int offset = 0;
		if (wpt != null) {
			for (WaypointGroup group : allGroups) {
				int idx = group.getWaypoints().indexOf(wpt);
				if (idx > -1) { // this group contains the waypoint
					return offset + idx;
				}
				offset += group.getNumPts();
			}
		}
		return -1;
	}

	/**
	 * Set the {@link Waypoint} at given index position active. Index is
	 * the overall position within ALL active WaypointGroups.
	 * @param idx Total index of the Waypoint to set active. If out of bounds,
	 * the active {@link Waypoint} is set to {@link null}
	 */
	public void setWaypoint(int totalIdx) {
		Waypoint wpt = null;
		activeGroup = null;
		int start = 0;
		int end = 0;
		for (WaypointGroup group : allGroups) {
			end += group.getNumPts();
			if (totalIdx <= end) {
				int idx = totalIdx - start - 1;	// bug here somewhere
				try {
					wpt = group.getWaypoints().get(idx);
				} catch (Exception e) {
					System.out.println("idx="+idx+" size="+group.getNumPts());
				}
				activeGroup = group;
				break;
			}
			start += group.getNumPts();
		}
		setActiveWpt(wpt, false);
	}

	/**
	 *
	 * @return
	 */
	public DefaultMutableTreeNode getTreeNode() {
		return currSelection;
	}

	/**
	 *
	 * @return
	 */
	public PropertyChangeListener getPropertyChangeListener() {
		return propertyListener;
	}

	/**
	 *
	 * @param propertyListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 *
	 * @param propertyListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Notify all subscribers that the current GpxObject has been updated
	 * TODO provide some "hint" about what has changed
	 */
	public void refresh() {
		if (gpxObject != null) {
			gpxObject.updateAllProperties(); // done here centrally, as courtesy
		}
		pcs.firePropertyChange(PCE_REFRESHGPX, null, null);
	}

	/**
	 * Notify all subscribers that the map panel needs to be repainted
	 */
	public void repaintMap() {
		pcs.firePropertyChange(PCE_REPAINTMAP, null, null);
	}
	/**
	 *
	 */
	public void clear() {
		gpxObject = null;
		gpxFile = null;
		activeWaypoint = null;
		activeGroup = null;
		allGroups.clear();
	}

	/**
	 *
	 */
	private void makeListeners() {

		/*
		 * Handle change event received from the outside
		 */
		propertyListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if (propertyName.equals(PCE_ACTIVEGPX)) {
					setActiveGpx((GPXObject) evt.getNewValue());
				} else if (propertyName.equals(PCE_ACTIVEGPX)) {
					setActiveWpt((Waypoint) evt.getNewValue(), false);
				}
			}
		};

		/*
		 * Listener called when user selects a node in the tree
		 */
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
            	currSelection = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (currSelection != null) {
                    setGpxFromTree(currSelection);
                 	setActiveGpx((GPXObject) currSelection.getUserObject());
                } else {
                	setActiveGpx(null);
                }
            }
        });
	}


	/**
	 *
	 * @param newValue
	 */
	private void setActiveWpt(Waypoint wpt, boolean autoSetGroup) {
		boolean equals = true; // if the new waypoint equals the old

		if ((activeWaypoint == null) && (wpt == null)) {
			// nothing to do
			return;
		}
		// either one is not null
		if (activeWaypoint != null) {
			equals = activeWaypoint.equals(wpt);
		} else {
			equals = wpt.equals(activeWaypoint);
		}

		if (equals == false) { // the new one is different
			activeWaypoint = wpt;

			if (autoSetGroup) {
				activeGroup = null;
				for (WaypointGroup group : allGroups) {
					if (group.getWaypoints().contains(activeWaypoint)) {
						activeGroup = group;
						break;
					}
				}
			}
			pcs.firePropertyChange(PCE_ACTIVEWPT, null, activeWaypoint);
		}
	}

	/**
	 *
	 * @param newObject the new active {@link GPXObject}
	 */
	private void setActiveGpx(GPXObject newObject) {
		if (newObject == null) {
			clear();
		} else {
			gpxObject = newObject;
			gpxObject.setVisible(true); // current object is always visible
			allGroups = core.getSegments(newObject, SEG_ROUTE_TRACK);
		}
		// it is discouraged to use the gpxObject in event.getNewValue()
		// it is provided here just for backward compatibility
		pcs.firePropertyChange(PCE_ACTIVEGPX, null, gpxObject);
	}

    /**
     * find the top level {@link GPXFile} that contains the specified node
     * @param node
     * @return
     */
    private void setGpxFromTree(DefaultMutableTreeNode node) {
        while (!((GPXObject) node.getUserObject()).isGPXFile()) {
            node = (DefaultMutableTreeNode) node.getParent();
        }
    	gpxFile = (GPXFile) node.getUserObject();
    }

}
