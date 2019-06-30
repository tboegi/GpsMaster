package org.gpsmaster.chart;

import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYAreaRenderer;

import eu.fuegenstein.unit.UnitConverter;

/**
 * An axis providing access to the GPS-specific numeric
 * values within a {@link Waypoint}, like PDOP, HDOP, ...
 * 
 * @author rfu
 *
 */
public class GpsDataAxis extends ChartYAxis {

	// constants for fields
	public static final byte HDOP = 0;
	public static final byte PDOP = 1;
	public static final byte VDOP = 2;
	public static final byte SAT = 3;
	public static final byte MAGVAR = 4;
	
	public static final byte FIRST = HDOP; // to allow for() loop over all constants above
	public static final byte LAST = MAGVAR;
	
	private byte field = 0;
	
	/**
	 * Constructor
	 * @param field numeric identifier of the field this axis represents 
	 * (see constants) HDOP, PDOP, ...
	 */
	public GpsDataAxis(byte field) {
		super(null);
		this.field = field;
		switch(field) {
			case 0:
				title = "HDOP";
				break;
			case 1:
				title = "PDOP";
				break;
			case 2:
				title = "VDOP";
				break;
			case 3:
				title = "SAT";
				break;
			case 4:
				title = "MAGVAR";
				break;
			default:
				throw new IllegalArgumentException();
		}
					
		iconFile = "axis_gps.png";
		valueAxis = new NumberAxis();
		valueAxis.setLabel(title);
		renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
		super.setDefaults();
	}

	/**
	 * get the numeric identifier of the field this axis represents
	 * @return
	 */
	public byte getField() {
		return field;
	}
	
	@Override
	public double getValue(Waypoint wpt) {
		switch(field) {
			case 0:
				return wpt.getHdop();
			case 1:
				return wpt.getPdop();
			case 2:
				return wpt.getVdop();
			case 3:
				return wpt.getSat();
			case 4:
				return wpt.getMagvar();
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public void reset() {
		// nothing to do

	}

}
