package org.gpsmaster.dialogs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.gpsmaster.filehub.TransferLogEntry;
import org.gpsmaster.filehub.TransferableItem;

import eu.fuegenstein.unit.UnitConverter;

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
	protected UnitConverter uc = null;

	protected ArrayList<TransferableItem> itemList = new ArrayList<TransferableItem>(); // rename to itemList

	/**
	 * Constructor
	 * @param unitConverter
	 */
	public TransferableItemTableModel(UnitConverter unitConverter) {
		super();
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
	 * get the {@link Color} of a row depending on its state.
	 * @param inRowNum 0-based number of the row
	 * @return
	 */
	public Color getRowColor(int inRowNum) {
		TransferableItem item = itemList.get(inRowNum);
		switch(item.getTransferState()) {
			case TransferableItem.STATE_QUEUED:
				return Color.DARK_GRAY;
			case TransferableItem.STATE_FINISHED:  // TODO black & bold
				switch(item.getFailureState()) {
					case TransferLogEntry.ERROR:
						return Color.RED;
					case TransferLogEntry.WARNING:
						return Color.ORANGE;
				}
				return Color.BLUE;
			case TransferableItem.STATE_PROCESSING: // TODO black & italic or some kind of animation
				return Color.DARK_GRAY;

		}
		return Color.BLACK;
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
