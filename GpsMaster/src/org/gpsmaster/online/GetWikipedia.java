package org.gpsmaster.online;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gpsmaster.dialogs.GenericDownloadDialog;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.marker.WikiMarker;

import com.topografix.gpx._1._1.LinkType;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;


/**
 * Function to load nearby point information from Wikipedia
 * according to the currently viewed area
 *
 * @author tim.prune
 * Code taken from GpsPrune
 * http://activityworkshop.net/
 *
 */
public class GetWikipedia extends GenericDownloadDialog
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3863016323284963978L;
	/** Maximum number of results to get */
	private static final int MAX_RESULTS = 40;
	/** Maximum distance from point in km */
	private static final int MAX_DISTANCE = 15;
	/** Username to use for geonames queries */
	private static final String GEONAMES_USERNAME = "rfuegen";

	private GPXFile gpx = new GPXFile("Wikipedia");

	/**
	 * Constructor
	 * @param inApp App object
	 */
	public GetWikipedia(JFrame frame, MessageCenter msg) {
		super(frame, msg);
	}

	/**
	 * @return name key
	 */
	public String getNameKey() {
		return "function.getwikipedia";
	}

	@Override
	public String getTitle() {
		return "Get nearby Wikipedia articles";
	}

	/**
	 * @param inColNum index of column, 0 or 1
	 * @return key for this column
	 */
	protected String getColumnKey(int inColNum)
	{
		if (inColNum == 0) return "Wikipedia Article";
		return "Distance";
	}


	/**
	 * Run method to call geonames in separate thread
	 */
	public void run()
	{
		busyOn();
		MessagePanel panel = msg.infoOn("Retrieving list of nearby Wikipedia articles ...");
		trackListModel.setUnitConverter(uc);
		// TODO Get coordinates from current trackpoint (if any)
		double lat = 16.0, lon = 45.0;

		if (bounds != null)
		{
			lat = (bounds.getN() + bounds.getS()) / 2.0;
			lon = (bounds.getE() + bounds.getW()) / 2.0;
		}

		// Firstly try the local language
		String lang = Locale.getDefault().getLanguage();
		submitSearch(lat, lon, lang);
		// If we didn't get anything, try a secondary language
		if (trackListModel.isEmpty() && errorMessage == null && lang.equals("als")) {
			submitSearch(lat, lon, "de");
		}
		// If still nothing then try english
		if (trackListModel.isEmpty() && errorMessage == null && !lang.equals("en")) {
			submitSearch(lat, lon, "en");
		}

		// Set status label according to error or "none found", leave blank if ok
		if (errorMessage == null && trackListModel.isEmpty()) {
			msg.warning("No Wikipedia articles found in perimeter.");
		}
		if (errorMessage != null) {
			msg.volatileError(errorMessage);
		}
		msg.infoOff(panel);
		busyOff();
	}

	/**
	 * Submit the search for the given params
	 * @param inLat latitude
	 * @param inLon longitude
	 * @param inLang language code to use, such as en or de
	 */
	private void submitSearch(double inLat, double inLon, String inLang)
	{
		// Example http://api.geonames.org/findNearbyWikipedia?lat=47&lng=9
		String urlString = "http://api.geonames.org/findNearbyWikipedia?lat=" +
			inLat + "&lng=" + inLon + "&maxRows=" + MAX_RESULTS
			+ "&radius=" + MAX_DISTANCE + "&lang=" + inLang
			+ "&username=" + GEONAMES_USERNAME;
		// Parse the returned XML with a special handler
		GetWikipediaXmlHandler xmlHandler = new GetWikipediaXmlHandler();
		InputStream inStream = null;

		try
		{
			URL url = new URL(urlString);
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			inStream = url.openStream();
			saxParser.parse(inStream, xmlHandler);
		}
		catch (Exception e) {
			msg.error(e);
		}
		// Close stream and ignore errors
		try {
			inStream.close();
		} catch (Exception e) {}
		// Add track list to model
		ArrayList<OnlineTrack> trackList = xmlHandler.getTrackList();
		trackListModel.addTracks(trackList);

		// Show error message if any
		if (trackListModel.isEmpty())
		{
			String error = xmlHandler.getErrorMessage();
			if (error != null && !error.equals(""))
			{
				msg.volatileError(error);
			}
		}
	}

	/**
	 * Load the selected point(s)
	 */
	protected void loadSelected()
	{
        SwingWorker<Void, Void> downloadWorker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
        		loadButton.setEnabled(false);
			// Find the rows selected in the table and get the corresponding coords
				int numSelected = trackTable.getSelectedRowCount();
				if (numSelected > 0) {
					int[] rowNums = trackTable.getSelectedRows();
					for (int i=0; i<numSelected; i++)
					{
						int rowNum = rowNums[i];
						if (rowNum >= 0 && rowNum < trackListModel.getRowCount())
						{
							OnlineTrack track = trackListModel.getTrack(rowNum);
							String coords = track.getDownloadLink();
							String[] latlon = coords.split(",");
							if (latlon.length == 2)
							{
								WikiMarker marker = new WikiMarker(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));
								marker.setName(track.getTrackName());
								LinkType link = new LinkType();
								link.setType("Wikipedia");
								link.setText(track.getTrackName());
								link.setHref(track.getWebUrl());
								marker.setDesc(track.getDescription());
								marker.getLink().add(link);
								gpx.getWaypointGroup().addWaypoint(marker);
							}
						}
					}
					firePropertyChange("newGpx", null, gpx);
				}
                return null;
            }
            @Override
            protected void done() {
            	loadButton.setEnabled(true);
        		cancelled = true;
        		dispose();
            }
        };

        if (changeListener != null) {
        	downloadWorker.addPropertyChangeListener(changeListener);
        }
        downloadWorker.execute();
	}

}
