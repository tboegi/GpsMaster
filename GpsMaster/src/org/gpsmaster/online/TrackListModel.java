package org.gpsmaster.online;

import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;



/**
 * Model for list of tracks from gpsies.com
 * 
 * @author rfu
 * @author tim.prune
 * Code taken from GpsPrune
 * http://activityworkshop.net/
 * 
 */
public class TrackListModel extends AbstractTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 453877575896893489L;
	/** List of tracks */
	private ArrayList<OnlineTrack> trackList = null;
	/** Column heading for track name */
	private String nameColLabel = null;
	/** Column heading for length */
	private String lengthColLabel = null;
	/** Number of columns */
	private int numColumns = 2;
	/** Formatter for distances */
	private NumberFormat distanceFormatter = NumberFormat.getInstance();

	/**
	 * Constructor
	 * @param inColumn1Key key for first column
	 * @param inColumn2Key key for second column
	 */
	public TrackListModel(String col1Label, String col2Label)
	{
		nameColLabel = col1Label;
		lengthColLabel = col2Label;
		numColumns = (lengthColLabel != null?2:1);
		distanceFormatter.setMaximumFractionDigits(1);
	}

	/**
	 * @return column count
	 */
	public int getColumnCount()
	{		
		return numColumns;
	}

	/**
	 * @return number of rows
	 */
	public int getRowCount()
	{
		if (trackList == null) return 0;
		return trackList.size();
	}

	/** @return true if there are no rows */
	public boolean isEmpty()
	{
		return getRowCount() == 0;
	}

	/**
	 * @param inColNum column number
	 * @return column label for given column
	 */
	public String getColumnName(int inColNum)
	{
		if (inColNum == 0) {return nameColLabel;}
		return lengthColLabel;
	}

	/**
	 * @param inRowNum row number
	 * @param inColNum column number
	 * @return cell entry at given row and column
	 */
	public Object getValueAt(int inRowNum, int inColNum)
	{
		// TODO apply unit converter
		OnlineTrack track = trackList.get(inRowNum);
		if (inColNum == 0) {return track.getTrackName();}
		double lengthM = track.getLength();
		// convert to current distance units
		// Unit distUnit = Config.getUnitSet().getDistanceUnit();
		double length = lengthM / 1000; // * distUnit.getMultFactorFromStd();
		// Make text
		return distanceFormatter.format(length) + " km"; // + I18nManager.getText(distUnit.getShortnameKey());
	}

	/**
	 * Add a list of tracks to this model
	 * @param inList list of tracks to add
	 */
	public void addTracks(ArrayList<OnlineTrack> inList)
	{
		if (trackList == null) {trackList = new ArrayList<OnlineTrack>();}
		final int prevCount = trackList.size();
		if (inList != null && inList.size() > 0) {
			trackList.addAll(inList);
		}
		final int updatedCount = trackList.size();
		if (prevCount <= 0)
			fireTableDataChanged();
		else
			fireTableRowsInserted(prevCount, updatedCount-1);
	}

	/**
	 * @param inRowNum row number from 0
	 * @return track object for this row
	 */
	public OnlineTrack getTrack(int inRowNum)
	{
		return trackList.get(inRowNum);
	}

	/**
	 * Clear the list of tracks
	 */
	public void clear()
	{
		trackList = null;
	}
}
