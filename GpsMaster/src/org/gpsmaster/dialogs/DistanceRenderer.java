package org.gpsmaster.dialogs;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.gpsmaster.Const;

import eu.fuegenstein.unit.UnitConverter;

/**
 * Renderer for {@link JTable} columns. 
 * 
 * @author rfu
 *
 */
public class DistanceRenderer extends DefaultTableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4716470880604010041L;
	private UnitConverter uc = null;
	
	/**
	 * Constructor 
	 * @param uc
	 */
	public DistanceRenderer(UnitConverter uc) {
		this.uc = uc;
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	/**
	 * Sets the text of the table cell to distance plus unit,
	 * converted to given Unit System
	 *  
	 * @param value Object of type Long, containing the distance in meters.
	 * 
	 */
    public void setValue(Object value) {
    	Long longDist = (Long) value;
    	String dist = uc.dist(longDist.longValue(), Const.FMT_DIST);
    	setText(dist);	    	
    }
}