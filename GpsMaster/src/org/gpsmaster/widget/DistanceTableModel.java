package org.gpsmaster.widget;

import javax.swing.table.DefaultTableModel;

public class DistanceTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6500837718518411378L;
	private String[] colNames = new String[] {"from", "to", "distance", "direct", "duration", "speed", "average"};
	/**
	 * 
	 */
	public DistanceTableModel() {
		
		setColumnCount(6);
	}

	/**
	 * 
	 * @param m1
	 * @param m2
	 * @param dist
	 * @param direct
	 * @param duration
	 * @param speed
	 */
	public void addValues(String m1, String m2, String dist, String direct, String duration, String speed) {
		addRow(new Object[]{m1, m2, dist, direct, duration, speed});
	}
	
	/**
	 * 
	 */
	public void clear() {
		setRowCount(0);		
	}
	
	@Override
	public String getColumnName(int col) {
	    return colNames[col];
	}
	/*
    @Override
    public Class<?> getColumnClass(int column) {
        switch(column) {
            case 0:                       
            case 1: return ImageIcon.class;
            default: return Object.class;
        }
    }
    */
}
