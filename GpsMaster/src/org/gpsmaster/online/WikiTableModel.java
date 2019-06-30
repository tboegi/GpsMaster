package org.gpsmaster.online;

import org.gpsmaster.dialogs.TransferableItemTableModel;

import eu.fuegenstein.unit.UnitConverter;

public class WikiTableModel extends TransferableItemTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7005489159315438665L;
	private final String [] colNames = {"Article", "Distance"};
	
	public WikiTableModel(UnitConverter unitConverter) {
		super(unitConverter);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getColumnCount() {

		return 2;
	}

	@Override
	public String getColumnName(int colNum) {
		
		return colNames[colNum];
	}

	@Override
	public Object getValueAt(int rowNum, int colNum) {
		OnlineTrack track = (OnlineTrack) itemList.get(rowNum);
		switch(colNum) {
		case 0:
			return track.getName();
		case 1:					
			return (long) uc.dist(track.getLength() * 1000);
		}
		return "--";

	}
	
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 1:
            	return Long.class;
            default: return String.class;
        }
    }

}
