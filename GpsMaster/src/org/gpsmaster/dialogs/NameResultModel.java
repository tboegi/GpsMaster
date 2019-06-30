package org.gpsmaster.dialogs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import eu.fuegenstein.osm.NominatimPlace;


public class NameResultModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2497389856633140568L;

	private final int NUM_COLUMNS = 1;
	private List<NominatimPlace> places = new ArrayList<NominatimPlace>();
	
	/**
	 * 
	 * @param place
	 */
	public void add(NominatimPlace place) {
		places.add(place);
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public NominatimPlace getPlace(int index) {
		return places.get(index);
	}
	
	@Override
	public int getColumnCount() {		
		return NUM_COLUMNS;
	}
	@Override
	public int getRowCount() {
		return places.size();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {		
		return places.get(rowIndex).getDisplayName();
	}
	
	public void clear() {
		places.clear();		
	}
}
