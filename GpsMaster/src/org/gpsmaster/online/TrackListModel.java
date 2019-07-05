package org.gpsmaster.online;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.gpsmaster.Const;

import eu.fuegenstein.unit.UnitConverter;



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
	private UnitConverter uc = null;

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
	}

	/**
	 * @return the uc
	 */
	public UnitConverter getUnitConverter() {
		return uc;
	}

	/**
	 * @param uc the uc to set
	 */
	public void setUnitConverter(UnitConverter uc) {
		this.uc = uc;
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
		switch(inColNum) {
		case 0:
			return track.getTrackName();
		case 1:
			if (uc != null) {
				return uc.dist(track.getLength(), Const.FMT_DIST);
			}
			return String.format(Const.FMT_DIST + " km", track.getLength() / 1000);

		}
		return "--";
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
