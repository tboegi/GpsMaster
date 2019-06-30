package org.gpsmaster.dialogs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.filehub.TransferableItem;

import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.util.LogEntry;

/**
 * Table Model for {@link TransferableItem}s
 * containing methods & fields shared by derived TableModels
 * 
 * @author rfu
 */
public abstract class TransferableItemTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9141345826973751141L;
	private final String ICONPATH = Const.ICONPATH_DIALOGS; // 
	
	protected UnitConverter uc = null;
	
	protected Icon queuedIcon = null;
	protected Icon processingIcon = null;
	protected Icon okIcon = null;
	protected Icon warningIcon = null;
	protected Icon errorIcon = null;
	
	protected ArrayList<TransferableItem> itemList = new ArrayList<TransferableItem>();

	/**
	 * 
	 */
	public TransferableItemTableModel() {
		super();
		
		queuedIcon = new ImageIcon(GpsMaster.class.getResource(ICONPATH.concat("state-queued.png")));
		processingIcon = new ImageIcon(GpsMaster.class.getResource(ICONPATH.concat("state-processing.png")));
		okIcon = new ImageIcon(GpsMaster.class.getResource(ICONPATH.concat("state-ok.png")));
		warningIcon = new ImageIcon(GpsMaster.class.getResource(ICONPATH.concat("state-warning.png")));
		errorIcon = new ImageIcon(GpsMaster.class.getResource(ICONPATH.concat("state-error.png")));
	}
	
	/**
	 * Constructor
	 * @param unitConverter
	 */
	public TransferableItemTableModel(UnitConverter unitConverter) {
		this();
		this.uc = unitConverter;
		
	
	}

	/**
	 * @return the uc
	 */
	public UnitConverter getUnitConverter() {
		return uc;
	}
	
	/**
	 * Get number of columns
	 */
	public abstract int getColumnCount();

	/**
	 * 
	 */
	public abstract String getColumnName(int colNum);

	/**
	 * 
	 */
	public abstract Object getValueAt(int arg0, int arg1);

	/**
	 * Get number of rows
	 */
	public int getRowCount() {
		return itemList.size();
	}

	/**
	 * Get the icon representing the state of an {@link TransferableItem}
	 * @param rowNum
	 * @return
	 */
	protected Icon getStatusIcon(TransferableItem item) {
		// TransferableItem item = itemList.get(rowNum); 
		switch(item.getTransferState()) {
		case TransferableItem.STATE_QUEUED:
			return queuedIcon;
		case TransferableItem.STATE_FINISHED:
			switch(item.getLog().getFailureState()) {
				case LogEntry.ERROR:
					return errorIcon;
				case LogEntry.WARNING:
					return warningIcon;
			}
			return okIcon;
		case TransferableItem.STATE_PROCESSING:
			return processingIcon;
		default:
			return null;
		}
	}
	
	/**
	 * 
	 * @param track
	 */
	public void addItem(TransferableItem item) {
		itemList.add(item);
		fireTableDataChanged(); // TODO refresh only last row:
		// fireTableRowsInserted(itemList.size() - 1, itemList.size());
	}
	
	/**
	 * Add a list of tracks to this model
	 * @param inList list of tracks to add
	 */
	public void addItems(ArrayList<TransferableItem> inList)
	{
		final int prevCount = itemList.size();
		if (inList != null && inList.size() > 0) {
			itemList.addAll(inList);
		}
		final int updatedCount = itemList.size();
		if (prevCount <= 0)
			fireTableDataChanged();
		else
			fireTableRowsInserted(prevCount, updatedCount-1);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return (itemList.size() == 0);
	}
	/**
	 * 
	 * @param inRowNum
	 * @return
	 */
	public TransferableItem getItem(int inRowNum)
	{
		return itemList.get(inRowNum);
	}
	
	/**
	 * Get list of all items available for download
	 * @return
	 */
	public List<TransferableItem> getItemList() {
		return itemList;
	}
	
	/**
	 * Let the table know that the content of a {@link TransferableItem} has changed
	 * @param item track with changed data
	 */
	public void refreshTrack(TransferableItem item) {
		int row = itemList.indexOf(item);
		if (row != -1) {
			fireTableRowsUpdated(row, row); // convert view to / from model
		}
	}
		
	/**
	 * Clear the list of tracks
	 */
	public void clear()
	{
		itemList.clear();
	}
}
