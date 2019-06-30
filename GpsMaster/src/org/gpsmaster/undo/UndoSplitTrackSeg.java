package org.gpsmaster.undo;

import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.WaypointGroup;

public class UndoSplitTrackSeg implements IUndoable {
	
	protected Track track = null;
	protected WaypointGroup seg1 = null;
	protected WaypointGroup seg2 = null;
	
	
	/**
	 * 
	 * @param seg1
	 * @param seg2
	 */
	public UndoSplitTrackSeg(Track track, WaypointGroup seg1, WaypointGroup seg2) {
		this.track = track;
		this.seg1 = seg1;
		this.seg2 = seg2;
	}
	
	@Override
	public String getUndoDescription() {
		
		return "Split Track";
	}

	// does not work updateAllProperties required?
	@Override
	public void undo() throws Exception {
		seg1.getWaypoints().addAll(seg2.getWaypoints());
		track.getTracksegs().remove(seg2);		
	}

}
