package org.gpsmaster.online;

import javax.swing.Icon;

import org.gpsmaster.dialogs.TransferableItemTableModel;

import eu.fuegenstein.unit.UnitConverter;

/**
 * 
 * @author rfu
 *
 */
public class OsmTableModel extends TransferableItemTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7707200773529970756L;
	private final String [] colNames = {"", "Name", "OID", "Type" };
	/**
	 * 
	 * @param unitConverter
	 */
	public OsmTableModel(UnitConverter unitConverter) {
		super(unitConverter);				
	}
	
	/**
	 * @return column count
	 */
	public int getColumnCount()
	{		
		return 3; // 4 with type
	}

	/**
	 * 
	 */
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
	        case 0:
	        	return Icon.class;
            case 2:            	
            	return Long.class;
            default: return String.class;
        }
    }
    
	/**
	 * @param colNum column number
	 * @return column label for given column
	 */
	public String getColumnName(int colNum)
	{
		return colNames[colNum];
	}

	/**
	 * @param inRowNum row number
	 * @param inColNum column number
	 * @return cell entry at given row and column
	 */
	public Object getValueAt(int inRowNum, int inColNum)
	{
		OnlineTrack track = (OnlineTrack) itemList.get(inRowNum);
		switch(inColNum) {
		case 0:
			return getStatusIcon(track);
		case 1:
			return track.getName();
		case 2:					
			return track.getId();
		case 3:
			return track.getType();
		}
		return "--";
	}

	
}
