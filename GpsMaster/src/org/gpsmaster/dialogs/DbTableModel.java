package org.gpsmaster.dialogs;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.db.GpsEntry;

import eu.fuegenstein.swing.PlainColorIcon;
import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.util.XTime;

/**
 *
 * @author rfu
 *
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

	private SimpleDateFormat sdf = new SimpleDateFormat(Const.SDF_DATETIME);
	private List<GpsEntry> gpsEntries = null;
	private UnitConverter uc = null;

	private Hashtable<String, ImageIcon> iconCache = new Hashtable<String, ImageIcon>();


	/**
	 *
	 * @param gpsList
	 * @param uc
	 */
	public DbTableModel(List<GpsEntry> gpsEntries, UnitConverter uc) {
		this.uc = uc;
		this.gpsEntries = gpsEntries;

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

		return getGpsEntries().size();
	}

	@Override
	public String getColumnName(int col) {
	    return colNames[col];
	}

	@Override
	public Object getValueAt(int row, int col) {

		GpsEntry gpsEntry = gpsEntries.get(row);
		switch (col) {
			case 0:
				return new PlainColorIcon(gpsEntry.getColor());
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
	 * @return the gpsEntries
	 */
	public List<GpsEntry> getGpsEntries() {
		return gpsEntries;
	}

	/**
	 * @param gpsEntries the gpsEntries to set
	 */
	public void setGpsEntries(List<GpsEntry> gpsEntries) {
		this.gpsEntries = gpsEntries;
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
}
