package org.gpsmaster.dialogs;

/**
 * class representing interim results sent to progress widget
 * @author rfu
 *
 */

public class Progress {
	
	private int waypoints = 0;
	private int items = 0;
	
	public Progress(int items, int waypoins) {
		this.setItems(items);
		this.setWaypoints(waypoins);			
	}

	public int getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(int waypoints) {
		this.waypoints = waypoints;
	}

	public int getItems() {
		return items;
	}

	public void setItems(int items) {
		this.items = items;
	}
	
}
