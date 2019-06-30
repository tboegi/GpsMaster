package org.gpsmaster.dialogs;

import eu.fuegenstein.osm.Nominatim;

public class NameSearch {

	private NameSearchPanel namePanel = null;
	private Nominatim nominatim = null;

	
	/**
	 * Default constructor
	 */
	public NameSearch() {
		nominatim = new Nominatim();
		
	}
	/**
	 * 
	 * @return
	 */
	public NameSearchPanel getNamePanel() {
		if (namePanel == null)
		{
			namePanel = new NameSearchPanel();
		}
		return namePanel;
	}


}
