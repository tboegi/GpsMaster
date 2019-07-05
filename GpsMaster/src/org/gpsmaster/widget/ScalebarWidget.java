package org.gpsmaster.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.gpsmaster.UnitConverter;
import org.gpsmaster.gpxpanel.GPXPanel;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;

import eu.fuegenstein.swing.Widget;
import eu.fuegenstein.swing.WidgetLayout;


/**
 *
 * @author rfu
 *
 */
public class ScalebarWidget extends Widget {

	private GPXPanel mapPanel = null;
	private UnitConverter uc = null;
	private Font font = null;
	private Color color = Color.BLACK;  // foreground color

	/**
	 *
	 */
	private static final long serialVersionUID = -1491190815019113326L;

	/**
	 *
	 * @param panel
	 */
	public ScalebarWidget(GPXPanel panel, UnitConverter unit) {
		super();
		mapPanel = panel;
		uc = unit;
		corner = WidgetLayout.BOTTOM_LEFT;
		setup();
	}

	/**
	 *
	 */
	private void setup() {

		// set preferred size
		Dimension size = new Dimension(200, 40);
		setPreferredSize(size);

		// listener for ZOOM events
    	JMapViewerEventListener listener = new JMapViewerEventListener() {

			@Override
			public void processCommand(JMVCommandEvent evt) {
				if (evt.getCommand() == JMVCommandEvent.COMMAND.ZOOM) {
					System.out.println("zoom");
				}

			}
		};
    	mapPanel.addJMVListener(listener);

    	// font for scalebar text
	}

	/**
	 * Paint the scalebar
	 */
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		int zoom = mapPanel.getZoom();
		int barWidth = getWidth(); // minus some offset
		double mpp = mapPanel.getMeterPerPixel();

		double total = mpp * barWidth;  // how many meters the bar represents
		if (total >= 1000) {

		}
		// to be continued

		// paint bar

		// paint text
	}
}
