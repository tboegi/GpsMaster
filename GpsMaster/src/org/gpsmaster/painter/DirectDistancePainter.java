package org.gpsmaster.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.marker.Marker;
import org.gpsmaster.marker.MeasureMarker;

/**
 * class for painting a direct line between to measure markers
 * (as the crow flies)
 * 
 * @author rfu
 * 
 * TODO BUG sort order of measure points in MeasureThings table is different 
 * 			from sort order of markers in markerList
 *
 */
public class DirectDistancePainter extends Painter {

	private List<MeasureMarker> marker = new ArrayList<MeasureMarker>();
	private final Color lineColor = Color.RED;
	
	@Override
	public void paint(Graphics2D g2d, GPXFile gpx) {
		// ignored

	}

	@Override
	public void paint(Graphics2D g2d, List<Marker> markerList) {
		
		GeneralPath path = new GeneralPath();
		
		marker.clear();
		for (Marker m : markerList) {
			if (m instanceof MeasureMarker) {
				marker.add((MeasureMarker) m);
			}
		}
		
		if (marker.size() > 1) {
			MeasureMarker m = marker.get(0);
            Point point = mapViewer.getMapPosition(m.getLat(), m.getLon(), false);
            path.moveTo(point.x, point.y);

            final float dash1[] = {2.0f};
            BasicStroke stroke = new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);
            g2d.setStroke(stroke);
            g2d.setColor(lineColor);
            
			for (int i = 1; i < marker.size(); i++) {				
				m = marker.get(i);
				point = mapViewer.getMapPosition(m.getLat(), m.getLon(), false);
				path.lineTo(point.x, point.y);
											
            	g2d.draw(path);
				
			}
		}
	}

}
