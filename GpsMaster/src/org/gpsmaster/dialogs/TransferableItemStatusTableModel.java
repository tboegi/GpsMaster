package org.gpsmaster.dialogs;

import javax.swing.Icon;

import org.gpsmaster.filehub.TransferableItem;

/**
 * TableModel with two columns:
 *  1. status icon
 *  2. item name
 * @author rfu
 *
 */
public class TransferableItemStatusTableModel extends TransferableItemTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8654104902311780160L;

	@Override
	public int getColumnCount() {
		
		return 2; // status icon, name
	}

	@Override
	public String getColumnName(int colNum) {
		switch(colNum) {
		case 0: 
			return "";
		case 1:
			return "Name";
		}
		return null;
	}

	/**
	 * 
	 */
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        	case 0: 
        		return Icon.class;
            default: return String.class;
        }
    }
    
	@Override	
	public Object getValueAt(int inRowNum, int inColNum)
	{
		TransferableItem item = itemList.get(inRowNum);
		switch(inColNum) {
			case 0:
				return getStatusIcon(item);
			case 1:
				return item.getName();
		}
		return null;
	}

}
