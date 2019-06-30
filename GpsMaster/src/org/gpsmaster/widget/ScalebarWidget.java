package org.gpsmaster.widget;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import org.gpsmaster.gpxpanel.GPXPanel;

import eu.fuegenstein.swing.Widget;
import eu.fuegenstein.swing.WidgetLayout;
import eu.fuegenstein.unit.Unit;
import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.unit.UnitFactory;


/**
 * Widget representing a scale bar on the map.
 * 
 * @author rfu
 * 
 */
public class ScalebarWidget extends Widget {

	private GPXPanel mapPanel = null;
	// JMapViewer returns "meters per pixel" for the current zoom level:
	private Unit mapUnit = UnitFactory.METER;
	
	private UnitConverter uc = null;

	private final int[] scales = {20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1}; 
		
	private final int barHeight = 12;
	private final int textOffset = 5; // pixels between bar and text
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1491190815019113326L;
	
	/**
	 * 
	 * @param msgPanel
	 */
	public ScalebarWidget(GPXPanel panel, UnitConverter unit) {
		super(WidgetLayout.BOTTOM_LEFT);
		mapPanel = panel;
		uc = unit;
				
		Dimension d = new Dimension(120, 40);
		setMinimumSize(new Dimension(100, barHeight));  // TODO consider font height + offset
		setPreferredSize(d);
		setSize(d);
	}
		
	/**
	 * Paint the scalebar
	 */
	@Override
	public void paint(Graphics g) {
		 
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
						
		// prepare zoomlevel text
		String zoom = Integer.toString(mapPanel.getZoom());
		Rectangle2D zoomRect = g2d.getFontMetrics().getStringBounds(zoom, g2d);
		int zoomWidth = (int) zoomRect.getWidth();
		double mpp = mapPanel.getMeterPerPixel();
		// available width for bar (in pixels):
		int barWidth = getPreferredSize().width - 2 * textOffset - zoomWidth; 
		// max. distance (in meters) the bar can represent:
		Unit barUnit = uc.getTargetSet().getDistanceUnit();
		double maxLength = uc.convert(mapUnit, barUnit, mpp * barWidth);
		Unit actualUnit = barUnit.getNextUnit(maxLength);
		String barText = "?"; // if this shows up, we are in trouble
		String unitText;
						
		// determine bar width according to scale
		unitText = actualUnit.getSymbol();
		int barLength = (int) uc.convert(barUnit, actualUnit, maxLength);
		// round down to the closest scale
		for (int i = 0; i < scales.length; i++) {
			if (scales[i] <= barLength) {
				barText = Integer.toString(scales[i]) + " " + unitText;
				barWidth = (int) (uc.convert(actualUnit, mapUnit, scales[i]) / mpp);				
				break;
			}
		}
		// TODO handle case when no matching scale was found
		
		// paint bar
		g2d.drawLine(0, getHeight() - barHeight, 0, getHeight()); // left vertical
		g2d.drawLine(0, getHeight(), barWidth, getHeight()); // horizontal
		g2d.drawLine(barWidth, getHeight() - barHeight, barWidth, getHeight()); // right vertical

		// paint text
		g2d.drawString(barText, textOffset, getHeight() - textOffset);
		// paint zoom level
		g2d.drawString(zoom, barWidth + textOffset, getHeight() - textOffset);
		
		
	}
}
