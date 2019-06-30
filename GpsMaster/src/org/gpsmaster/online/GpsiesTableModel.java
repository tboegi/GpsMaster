package org.gpsmaster.online;

import java.util.Date;

import javax.swing.Icon;

import org.gpsmaster.dialogs.TransferableItemTableModel;
import eu.fuegenstein.unit.UnitConverter;

/**
 * Model for list of tracks from online services
 * 
 * @author rfu
 * 
 */
public class GpsiesTableModel extends TransferableItemTableModel // extends AbstractTableModel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 453877575896893489L;

	private final String [] colNames = {"","Name", "Length", "Date" };
	/**
	 * 
	 * @param unitConverter
	 */
	public GpsiesTableModel(UnitConverter unitConverter) {
		super(unitConverter);				
	}
	
	/**
	 * @return column count
	 */
	public int getColumnCount()
	{		
		return colNames.length;
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
            case 3:
            	return Date.class;
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
				return (long) uc.dist(track.getLength() * 1000);
			case 3:
				return track.getDate();
		}
		return "--";
	}

}
