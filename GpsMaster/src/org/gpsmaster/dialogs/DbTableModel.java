package org.gpsmaster.dialogs;

import java.awt.Color;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.db.DbLayer;
import org.gpsmaster.db.GpsRecord;
import org.gpsmaster.online.GpsiesTableModel;

import eu.fuegenstein.swing.PlainColorIcon;
import eu.fuegenstein.util.XTime;

/**
 * 
 * @author rfu
 * TODO unify with / inherit from {@link GpsiesTableModel}
 */
public class DbTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 5273731050613887670L;

	private final String[] colNames = {" ", // Color
            "Start Date",
            "Name",
            "Distance",
            "Duration",
            "Activity"}; 
	
	private final int COLUMNCOUNT = 6;
	
	private DbLayer db = null;
	
	private SimpleDateFormat sdf = new SimpleDateFormat(Const.SDF_DATETIME);
	private List<GpsRecord> gpsEntries = new ArrayList<GpsRecord>();

	private Hashtable<String, ImageIcon> iconCache = new Hashtable<String, ImageIcon>();
	private Hashtable<Color, Icon> colorCache = new Hashtable<Color, Icon>();
	
	/**
	 * 
	 * @param gpsList
	 * @param uc
	 */
	public DbTableModel(DbLayer db) {
		this.db = db;				
	}

	/**
	 * 
	 */
	@Override
	public int getColumnCount() {
		
		return COLUMNCOUNT;
	}

	@Override
	public int getRowCount() {

		return gpsEntries.size();
	}

	@Override
	public String getColumnName(int col) {
	    return colNames[col];
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		
		GpsRecord gpsEntry = gpsEntries.get(row);
		switch (col) {
			case 0: 
				return getColorIcon(gpsEntry.getColor());
			case 1:
				return sdf.format(gpsEntry.getStartTime());
			case 2:
				return gpsEntry.getName();
			case 3:
				return gpsEntry.getDistance();
			case 4:
				return XTime.getDurationString(gpsEntry.getDuration());
			case 5:
				return getActivityIcon(gpsEntry.getActivity());
			default:
				return "?";
		}		
	}

	/**
	 * 
	 */
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:            	
            case 5:
            	return Icon.class;
            case 3:
            	return Long.class;            	
            default: return String.class;
        }
    }
    
    /**
     * 
     * @param idx
     * @return
     */
    public GpsRecord get(int idx) {
    	return gpsEntries.get(idx);
    }
    
	/**
	 * @return the gpsEntries
	 */
	public List<GpsRecord> getGpsRecords() {
		return gpsEntries;
	}

	/**
	 * @param gpsEntries the gpsEntries to set
	 */
	public void setGpsRecords(List<GpsRecord> gpsEntries) {
		this.gpsEntries = gpsEntries;
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void refresh() throws SQLException {
		gpsEntries.clear(); // TODO fill delta
		db.getGpsRecords(gpsEntries);
		fireTableDataChanged();
	}
	
	/**
	 * 
	 */
	public void clear() {
		gpsEntries.clear();
		fireTableDataChanged();
	}

	/**
	 * 
	 * @param activity
	 * @return
	 */
	private ImageIcon getActivityIcon(String activity) {
		ImageIcon icon = null;
		if (activity.length() > 0) {
			if (iconCache.containsKey(activity)) {
				icon = iconCache.get(activity);
			} else {
				icon = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_ACTIVITIES + activity + ".png"));
				iconCache.put(activity, icon);
			}
		}
			
		return icon;
	}
	
	/**
	 * 
	 * @param color
	 * @return
	 */
	private Icon getColorIcon(Color color) {
		Icon icon = null;	
			if (colorCache.containsKey(color)) {
				icon = colorCache.get(color);
			} else {
				icon = new PlainColorIcon(color);
				colorCache.put(color, icon);
			}
					
		return icon;
	}
	
}
