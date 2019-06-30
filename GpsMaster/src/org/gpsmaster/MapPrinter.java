package org.gpsmaster;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import org.gpsmaster.gpxpanel.GPXPanel;


public class MapPrinter implements Printable {

	private GPXPanel mapPanel = null;
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex > 0) {
			return NO_SUCH_PAGE;
		}
		
		if (mapPanel != null) {
			Graphics2D g2d = (Graphics2D) graphics;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			mapPanel.printAll(graphics);
		}

		return PAGE_EXISTS;
	}

	public GPXPanel getMapPanel() {
		return mapPanel;
	}

	public void setMapPanel(GPXPanel mapPanel) {
		this.mapPanel = mapPanel;
	}

	
}
