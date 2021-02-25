package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.gpsmaster.marker.Marker;

// import org.joda.time.DateTime; // TODO change date&time handling to joda.time

/**
 *
 * An ad-hoc element representing a group of waypoint elements.<br />
 * In a group of top-level "wpt" elements, these points will be represented discretely.<br />
 * In a "rte" or "trk" element, these points will be represented as paths.
 *
 * @author Matt Hoover
 *
 */
public class WaypointGroup extends GPXObjectND implements Comparable<WaypointGroup>, TreeNode {

    /**
     * The different types of {@link WaypointGroup}.
     */
    public enum WptGrpType {
        WAYPOINTS,
        ROUTE,
        TRACKSEG
    }

    private WptGrpType wptGrpType;
    private List<Waypoint> waypoints = new ArrayList<Waypoint>();

    // every distance below this value is considered a stop
    // (for ex.stop calculation)

    private final double exMinDist = 0.2f;

    /**
     * Default constructor.
     *
     * @param color     The color.
     * @param type      The type of {@link WaypointGroup}.
     */
    public WaypointGroup(Color color, WptGrpType type) {
        super(color);
        switch (type) {
            case WAYPOINTS:
                this.name = "Waypoints";
                break;
            case ROUTE:
                this.name = "Route";
                break;
            case TRACKSEG:
                this.name = "Track segment";
                break;
        }
        this.wptGrpType = type;
    }

    /**
     * Constructs a {@link WaypointGroup} by cloning the specified object
     * ATTENTION - updateAllProperties() has to be called
     * externally after cloning.
     * @param source {@link WaypointGroup} to be cloned
     */
    public WaypointGroup(WaypointGroup source) {
    	super(source);
    	this.wptGrpType = source.wptGrpType;
    	for (Waypoint wpt : source.waypoints) {
    		waypoints.add(new Waypoint(wpt));
    	}
    }

    public WptGrpType getWptGrpType() {
        return wptGrpType;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    /**
     * Adds a waypoint to the group.
     */
    public void addWaypoint(Waypoint wpt) {
   		waypoints.add(wpt);
    }

    /**
     * Inserts a waypoint to the group right after the active waypoint
     */
  
    public void insertWaypoint(Waypoint actwpt, Waypoint wpt) {
    	int idx = -1;
    	if ((actwpt != null)) {
    		idx = waypoints.indexOf(actwpt) + 1;
    	}
    	
    	if (idx >= 0) {
    		waypoints.add(idx, wpt);
    	} 
    	else {
    		waypoints.add(wpt);
    	}
    }

    /**
     * Removes a waypoint from the group.
     */
    public void removeWaypoint(Waypoint wpt) {
        waypoints.remove(wpt);
        updateBounds();
    }

    public int getNumPts() {
        return waypoints.size();
    }

    public Waypoint getStart() {
        if (waypoints.size() > 0) {
            return waypoints.get(0);
        } else {
            return null;
        }
    }

    public Waypoint getEnd() {
        if (waypoints.size() > 0) {
            return waypoints.get(waypoints.size() - 1);
        } else {
            return null;
        }
    }



    /* (non-Javadoc)
     * @see org.gpsmaster.gpxpanel.GPXObject#updateAllProperties()
     */
    @Override
    public void updateAllProperties() {
        if (waypoints.size() > 0) {
            updateDuration();
            updateLength();
            updateEx();
            updateMaxSpeed();
            updateEleProps();
            updateBounds();
            updateStartEnd();
            updateExtensions();
            extToColor();
        }
    }

    private void updateExtensions() {
        for (Waypoint wpt : waypoints) {
            updateExtension(wpt.getExtension());
        }
    }

    private void updateExtension(final GPXExtension extension) {
        if (null != extension) {
            for (GPXExtension sub : extension.getExtensions()) {
                if (null != sub.getValue() && !sub.getValue().trim().isEmpty()) {
                    final String[] split = sub.getKey().split(":");
                    final String key = split[split.length - 1];
                    try {
                        double parseDouble = Double.parseDouble(sub.getValue());
                        ExtensionMeta meta = minMaxExtensions.get(key);
                        if (null == meta) {
                            meta = new ExtensionMeta();
                            meta.name = key;
                            minMaxExtensions.put(key, meta);
                        }
                        meta.values.add(parseDouble);
                    } catch (final NumberFormatException nfe) {
                        System.err.println("GPXExtension is not a number! " + sub.getKey() + " | " + sub.getValue());
                    }
                }
                updateExtension(sub);
            }
        }
    }

    private void updateDuration() {
    	duration = getStart().getDuration(getEnd());
    }

    // TODO merge functionality which iterates through all waypoints
    // 		into a single method (for performance reasons)

    /**
     * update all ex-stop related values
     */
    private void updateEx()
    {
    	exStop = 0;  // time ex stop
        Waypoint curr = getStart();
        Waypoint prev;
        for (Waypoint rtept : waypoints) {
            prev = curr;
            curr = rtept;
            double distance = curr.getDistance(prev);
           	if ((distance > exMinDist) && (curr.getTime() != null)) {
           		exStop += curr.getDuration(prev);
           	}
        }
    }

    /**
     *
     */
    private void updateLength() {

        lengthMeters = 0;
        Waypoint curr = getStart();
        Waypoint prev;
        for (Waypoint rtept : waypoints) {
            prev = curr;
            curr = rtept;
            lengthMeters += curr.getDistance(prev);
        }
    }

    private void updateMaxSpeed() {
        maxSpeedMps = 0;
        double length;
        long seconds;
                                 // TODO replace this cheap smoothing method below with a Kalman filter?
        int smoothingFactor = 5; // find max avg speed over this many segments to smooth unreliable data and outliers
        if (getNumPts() <= smoothingFactor) {
            return;
        }
        Waypoint segStart = getStart();
        Waypoint segEnd = waypoints.get(smoothingFactor);
        for (int i = smoothingFactor; i < getNumPts(); i++)  {
            segEnd = waypoints.get(i);
            segStart = waypoints.get(i - smoothingFactor);

            length = 0;
            for (int j = 0; j < smoothingFactor; j++) {
                Waypoint w1 = waypoints.get(i - j);
                Waypoint w2 = waypoints.get(i - j - 1);
                length += w1.getDistance(w2); // meters
            }

            Date startTime = segStart.getTime();
            Date endTime = segEnd.getTime();
            if (startTime != null && endTime != null) {
                seconds = (long) ((endTime.getTime() - startTime.getTime()) / 1000D);
                double candidateMax = length / seconds;
                if (!Double.isNaN(candidateMax) && !Double.isInfinite(candidateMax)) {
                    maxSpeedMps = Math.max(maxSpeedMps, length / seconds);
                }
            }
        }
    }

    public void updateEleProps() {
        eleStartMeters = getStart().getEle();
        eleEndMeters = getEnd().getEle();
        eleMinMeters = Integer.MAX_VALUE;
        eleMaxMeters = Integer.MIN_VALUE;
        grossRiseMeters = 0;
        grossFallMeters = 0;
        riseTime = 0;
        fallTime = 0;
        Waypoint curr = getStart();
        Waypoint prev;

        for (Waypoint rtept : waypoints) {
            prev = curr;
            curr = rtept;
            if (curr.getEle() > prev.getEle()) {
                grossRiseMeters += (curr.getEle() - prev.getEle());
                if (curr.getTime() != null && prev.getTime() != null) {
                    riseTime += curr.getTime().getTime() - prev.getTime().getTime();
                }
            } else if (curr.getEle() < prev.getEle()) {
                grossFallMeters += (prev.getEle() - curr.getEle());
                if (curr.getTime() != null && prev.getTime() != null) {
                    fallTime += curr.getTime().getTime() - prev.getTime().getTime();
                }
            }
            eleMinMeters = Math.min(eleMinMeters, curr.getEle());
            eleMaxMeters = Math.max(eleMaxMeters, curr.getEle());
        }
        fallTime = fallTime / 1000; // in seconds
        riseTime = riseTime / 1000; // in seconds
    }

    private void updateBounds() {
        minLat =  86;
        maxLat = -86;
        minLon =  180;
        maxLon = -180;
        for (Waypoint wpt : waypoints) {
            minLat = Math.min(minLat, wpt.getLat());
            minLon = Math.min(minLon, wpt.getLon());
            maxLat = Math.max(maxLat, wpt.getLat());
            maxLon = Math.max(maxLon, wpt.getLon());
        }
    }

    /**
     * update start and end time
     */
    private void updateStartEnd() {
    	startTime = waypoints.get(0).getTime();
    	if (waypoints.size() > 1) {
    		endTime = waypoints.get(waypoints.size()-1).getTime();
    	} else {
    		endTime = startTime;
    	}
    }

    /**
     * Sort waypoint groups by start time
     */
	@Override
	public int compareTo(WaypointGroup o) {
		if ((getStartTime() == null) || (o.getStartTime() == null)) {
			return 0;
		}

		return getStartTime().compareTo(o.getStartTime());
	}

	// Methods implementing TreeNode interface
	// future feature: return waypoints to be shown in tree
	public Enumeration<TreeNode> children() {
		List<TreeNode> markerList = null;
		if (wptGrpType == WptGrpType.WAYPOINTS) {
			markerList = new ArrayList<TreeNode>();
			for (int i = 0; i < waypoints.size(); i++) {
				markerList.add((Marker) waypoints.get(i));
			}
			return java.util.Collections.enumeration(markerList);
		}
		return null;
	}

	public boolean getAllowsChildren() {
		if (wptGrpType == WptGrpType.WAYPOINTS) {
			return true;
		}
		return false;
	}

	public TreeNode getChildAt(int pos) {
		return (Marker) waypoints.get(pos);
	}

	public int getChildCount() {
		if (wptGrpType == WptGrpType.WAYPOINTS) {
			return waypoints.size();
		}
		return 0;
	}

	public int getIndex(TreeNode node) {
		if (wptGrpType == WptGrpType.WAYPOINTS) {
			return waypoints.indexOf(node);
		}
		return 0;
	}

	public boolean isLeaf() {
		if (wptGrpType == WptGrpType.WAYPOINTS) {
			return false;
		}
		return true;
	}
}
