package org.gpsmaster.undo;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;

/**
 * 
 * @author rfu
 *
 */
public class UndoAddRoute implements IUndoable {

	protected Route route = null;
	protected GPXFile gpx = null;
	
	/**
	 * 
	 * @param route 
	 * @param parent
	 */
	public UndoAddRoute(Route route, GPXFile parent) {
		this.route = route;
		this.gpx = parent;
	}
	
	@Override
	public String getUndoDescription() {
		String desc = "Add Route";
		if (route != null) {
			desc.concat(" " + route.getName());
		}
		return desc;
	}

	@Override
	public void undo() {
		if (route != null && gpx != null) {
			gpx.getRoutes().remove(route);
		}
		
	}

}
