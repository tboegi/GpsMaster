package org.gpsmaster.online;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.dialogs.DistanceRenderer;
import org.gpsmaster.dialogs.GenericDownloadDialog;
import org.gpsmaster.filehub.TransferableItem;
import org.gpsmaster.filehub.DataType;
import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.IItemTarget;
import org.gpsmaster.filehub.MapTarget;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.marker.WikiMarker;

import com.topografix.gpx._1._1.LinkType;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.unit.UnitConverter;


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
	private final List<TransferableItem> items = Collections.synchronizedList(new ArrayList<TransferableItem>());
	

	/**
	 * 
	 * @param frame
	 * @param msg
	 * @param fileHub
	 * @param unitConverter
	 */
	public GetWikipedia(JFrame frame, MessageCenter msg, FileHub fileHub, UnitConverter unitConverter) {
		super(frame, msg, fileHub, unitConverter);
		setIcon(Const.ICONPATH_DLBAR, "wiki-down.png");
		
		items.add(new OnlineTrack()); // a single dummy entry
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
		WikiXmlHandler xmlHandler = new WikiXmlHandler((WikiTableModel) trackListModel);
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
	 * Only a single {@link GPXFile} is created and processed via fileHub at the first call
	 * of this procedure; successive calls just add the newly selected articles as {@link Waypoint}s 
	 * to the existing {@link GPXFile}. this doesn't work well with other {@link IItemTarget}s than 
	 * the {@link MapTarget}, but is more convenient to the user. 
	 */
	protected void loadSelected()
	{
		// no need to run in background, since it is pretty fast.
		// Find the rows selected in the table and get the corresponding coords
		int numSelected = trackTable.getSelectedRowCount();
		if (numSelected > 0) { 
			int[] rowNums = trackTable.getSelectedRows();
			for (int i=0; i<numSelected; i++)
			{
				int rowNum = rowNums[i];
				if (rowNum >= 0 && rowNum < trackListModel.getRowCount())
				{
					OnlineTrack track = (OnlineTrack) trackListModel.getItem(rowNum);
					String coords = track.getDownloadLink();
					String[] latlon = coords.split(",");
					if (latlon.length == 2)
					{					
						WikiMarker marker = new WikiMarker(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));
						marker.setName(track.getName());
						LinkType link = new LinkType();
						link.setType("Wikipedia");
						link.setText(track.getName());
						link.setHref(track.getWebUrl());
						marker.setDesc(track.getDescription());
						marker.getLink().add(link);
						gpx.getWaypointGroup().addWaypoint(marker);					
					}
				}
			}
		}
		trackTable.clearSelection();
        fileHub.run();
        GpsMaster.active.refresh();
	}

	/**
	 * Download list of articles for the current map view (in background)
	 */
	protected void downloadArticleList() {
				
		SwingWorker<Void, Void> downloadWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				msgPanel = msg.infoOn("Retrieving list of nearby Wikipedia articles ...");
				busyOn();
								
				double lat = (bounds.getN() + bounds.getS()) / 2.0;
				double lon = (bounds.getE() + bounds.getW()) / 2.0;

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
				return null;
			}
			
			@Override
			protected void done() {
				msg.infoOff(msgPanel);
				busyOff();
			}
			
		};
		downloadWorker.execute();
	}
	
	@Override
	public boolean doShowProgressText() {
		// This is fast; no need to show progress
		return false;
	}

	public DataType getDataType() {
		return DataType.GPXFILE; 
	}
	
	@Override
	public List<TransferableItem> getItems() {
		return items;
	}

	public GPXFile getGpxFile(TransferableItem item)  {
		items.clear(); // prevent this from being called again
		return gpx;
	}
	
	public void open(TransferableItem transferableItem) {
		throw new UnsupportedOperationException();
		
	}
	
	public InputStream getInputStream() throws Exception {
		throw new UnsupportedOperationException();		
	}
	
	public void close() throws Exception {
		throw new UnsupportedOperationException();		
	}

	@Override
	protected void setupTableModel() {
		trackListModel = new WikiTableModel(uc);
		
	}

	/**
	 * Set distance renderer for 2nd column
	 */
	@Override
	protected void setupTable() {
		
		trackTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		trackTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		DistanceRenderer distRenderer = new DistanceRenderer(uc);
		trackTable.getColumnModel().getColumn(1).setCellRenderer(distRenderer);
		
	}

	@Override
	public void begin() {
		descPanel.setVisible(true);
		downloadArticleList();		
	}

	@Override
	protected String getColumnKey(int inColNum) {
		// TODO Auto-generated method stub
		return "---";
	}

}
