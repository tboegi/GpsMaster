package org.gpsmaster.online;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.dialogs.GenericDownloadDialog;
import org.gpsmaster.gpsloader.GpsLoader;
import org.gpsmaster.gpsloader.GpsLoaderFactory;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;

/**
 * @author tim.prune
 * @author rfu
 *
 * Function to load track information from Gpsies.com
 * according to the currently viewed area
 *
 * TODO allow downloading by URL entered by user
 *
 */
public class DownloadGpsies extends GenericDownloadDialog
{
	/**
	 *
	 */
	private static final long serialVersionUID = 7062740464390862897L;
	/** Number of results per page */
	private static final int RESULTS_PER_PAGE = 20;
	/** Maximum number of results to get */
	private static final int MAX_RESULTS = 60;
	/** New API key (specific to this program) */
	private static final String GPSIES_API_KEY = "wbqmlgbborqsbtqx";


	/**
	 * Constructor
	 * @param inApp App object
	 */
	public DownloadGpsies(JFrame frame, MessageCenter msg) {
		super(frame, msg);
	}

	/**
	 * @return name key
	 */
	public String getNameKey() {
		return "function.getgpsies";
	}

	/**
	 * @param inColNum index of column, 0 or 1
	 * @return key for this column
	 */
	protected String getColumnKey(int inColNum)
	{
		if (inColNum == 0) return "Track Name";
		return "Length";
	}


	/**
	 * Run method to call gpsies.com in separate thread
	 */
	public void run()
	{
		// Act on callback to update list and send another request if necessary
		int currPage = 1;

		ArrayList<OnlineTrack> trackList = null;
		URL url = null;
		InputStream inStream = null;
		busyOn();
		MessagePanel panel = msg.infoOn("Retrieving list of tracks for current map view ...");
		// Loop for each page of the results
		do
		{
			// Example http://ws.gpsies.com/api.do?BBOX=10,51,12,53&limit=20&resultPage=1&key=oumgvvbckiwpvsnb
			String urlString = "http://ws.gpsies.com/api.do?BBOX=" +
				bounds.getW() + "," + bounds.getS() + "," + bounds.getE() + "," + bounds.getN() +
				"&limit=" + RESULTS_PER_PAGE + "&resultPage=" + currPage +
				"&key=" + GPSIES_API_KEY + "&filetype=gpxTrk"; // TODO support KMZ download
			// Parse the returned XML with a special handler
			GpsiesXmlHandler xmlHandler = new GpsiesXmlHandler();
			try
			{
				url = new URL(urlString);
				SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("User-Agent", GpsMaster.PROGRAM_NAME + " v" + GpsMaster.VERSION_NUMBER);
				inStream = conn.getInputStream();
				saxParser.parse(inStream, xmlHandler);
			}
			catch (Exception e) {
				msg.error(e);
				// descMessage = e.getClass().getName() + " - " + e.getMessage();
			}
			// Close stream and ignore errors
			try {
				inStream.close();
			} catch (Exception e) {}
			// Add track list to model
			trackList = xmlHandler.getTrackList();
			trackListModel.addTracks(trackList);

			// Compare number of results with results per page and call again if necessary
			currPage++;
		}
		while (trackList != null && trackList.size() == RESULTS_PER_PAGE
			&& trackListModel.getRowCount() < MAX_RESULTS && !cancelled);

		msg.infoOff(panel);
		msg.volatileInfo(trackListModel.getRowCount() + " Tracks found.");
		// Set status label according to error or "none found", leave blank if ok
		if ((trackList == null || trackList.size() == 0)) {
			msg.volatileWarning("No tracks found.");
		}
		busyOff();
	}

	/**
	 *
	 */
	protected void loadSelected() {

        SwingWorker<Void, Void> downloadWorker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
        		loadButton.setEnabled(false);
        		busyOn();
        		// Find the row(s) selected in the table and get the corresponding track
        		int numSelected = trackTable.getSelectedRowCount();
        		if (numSelected > 0) {
		    		int[] rowNums = trackTable.getSelectedRows();
		    		for (int i=0; i<numSelected; i++)
		    		{
		    			int rowNum = rowNums[i];
		    			if (rowNum >= 0 && rowNum < trackListModel.getRowCount() && !cancelled)
		    			{
	    					MessagePanel panel = msg.infoOn("Downloading \"" + trackListModel.getTrack(rowNum).getTrackName()+"\"");
		    				try {
		    					GpsLoader loader = GpsLoaderFactory.getLoader("gpx");
		    					String url = trackListModel.getTrack(rowNum).getDownloadLink();
		    					InputStream stream = new URL(url).openStream();
		    					GPXFile gpx = loader.load(stream);
		    					gpx.updateAllProperties();
		    					if (changeListener != null) {
		    						firePropertyChange("newGpx", null, gpx);
		    					}
		    					if (numSelected > 1) {
			    					// do not hammer the server
		    						Thread.sleep(2000);
		    					}
		    				}
		    				catch (Exception e) {
		    					msg.error(e);
		    				}
		    				finally {
		    					msg.infoOff(panel);
		    				}
		    			}
		    		}
        		}
                return null;
            }
            @Override
            protected void done() {
        		cancelled = true;
        		dispose();
        		busyOff();
            }
        };

        if (changeListener != null) {
        	downloadWorker.addPropertyChangeListener(changeListener);
        }
        downloadWorker.execute();
	}


	@Override
	public String getTitle() {
		return "Download Tracks from Gpsies.com";
	}


}
