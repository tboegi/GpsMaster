package org.gpsmaster.online;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.dialogs.DistanceRenderer;
import org.gpsmaster.dialogs.GenericDownloadDialog;
import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.TransferableItem;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.filehub.DataType;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.swing.ExtendedTable;
import eu.fuegenstein.unit.UnitConverter;

/**
 * Function to load track information from Gpsies.com
 * bounded by the currently viewed area
 * 
 * @author rfu
 * @author tim.prune
 * Based on code from GpsPrune
 * http://activityworkshop.net/
 * 
 */
public class DownloadGpsies extends GenericDownloadDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7062740464390862897L;
	
	private static final int RESULTS_PER_PAGE = 20; // Number of results per page
	/** Maximum number of results to get */
	private static final int INITIAL_PAGES = 3; // number of pages to load on startup
	
	private int currPage = 1; // # of last page loaded 
	private TransferableItem currentItem = null;
	private InputStream inStream = null;
	
	private final List<TransferableItem> items = Collections.synchronizedList(new ArrayList<TransferableItem>());
	
	/**
	 * 
	 * @param frame
	 * @param msg
	 * @param fileHub
	 * @param unitConverter
	 */
	public DownloadGpsies(JFrame frame, MessageCenter msg, FileHub fileHub, UnitConverter unitConverter) {
		super(frame, msg, fileHub, unitConverter);
		setIcon(Const.ICONPATH_DLBAR, "gpsies-down.png");
		
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
		return "Distance";
	}

	/**
	 * 
	 * @param page
	 */
	public void downloadTrackList(int page) {
		URL url = null;
		InputStream inStream = null;
		
		String urlString = "http://ws.gpsies.com/api.do?BBOX=" +
				bounds.getW() + "," + bounds.getS() + "," + bounds.getE() + "," + bounds.getN() +
				"&limit=" + RESULTS_PER_PAGE + "&resultPage=" + page +
				"&key=" + Const.GPSIES_API_KEY + "&filetype=gpxTrk"; // TODO support KMZ download ( to save bandwidth)
			// Parse returned XML with a special handler
			GpsiesXmlHandler xmlHandler = new GpsiesXmlHandler((GpsiesTableModel) trackListModel);
			try
			{
				url = new URL(urlString);
				SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
				URLConnection conn = url.openConnection();							
				conn.setRequestProperty("User-Agent", GpsMaster.ME);
				inStream = conn.getInputStream();
				saxParser.parse(inStream, xmlHandler);
			}
			catch (Exception e) {
				msg.error(e);
				e.printStackTrace();
				// descMessage = e.getClass().getName() + " - " + e.getMessage();
			}
			// Close stream and ignore errors
			try {
				inStream.close();
			} catch (Exception e) {}
	}
	
	/**
	 * 
	 */
	public void begin()
	{
		descPanel.setVisible(true); // we need the description panel
		
		// Download initial tracklist in background
		SwingWorker<Void, Void> trackListWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				busyOn();
				msgPanel = msg.infoOn("Retrieving list of tracks for current map view ...");
				// Loop for each page of the results
				do
				{
					downloadTrackList(currPage);
					currPage++;
				}
				while ((currPage <= INITIAL_PAGES) && (trackListModel.getRowCount() % RESULTS_PER_PAGE == 0));
				
				return null;
			}
		
			@Override
			protected void done() {
				
				// TODO implement/enable "get more tracks" button

				trackTable.minimizeColumnWidth(0, ExtendedTable.WIDTH_MAX); // status icon
				trackTable.minimizeColumnWidth(2, ExtendedTable.WIDTH_MAX); // distance
				trackTable.minimizeColumnWidth(3, ExtendedTable.WIDTH_MAX); // date
				if (trackListModel.getRowCount() == 0) {
					msg.volatileWarning("No tracks found."); 
				} else {
					msg.volatileInfo(trackListModel.getRowCount() + " Tracks found.");
				}
				msg.infoOff(msgPanel);
				busyOff();
	
			}
		};
		trackListWorker.execute();
	}

	/**
	 * 
	 */
	protected void loadSelected() {
		
		// Find the row(s) selected in the table and get the corresponding track
		int numSelected = trackTable.getSelectedRowCount();
		if (numSelected > 0) {
			
    		int[] rowNums = trackTable.getSelectedRows();
    		for (int i=0; i<numSelected; i++)
    		{
    			int rowNum = trackTable.convertRowIndexToModel(rowNums[i]);
    			if (rowNum >= 0 && rowNum < trackListModel.getRowCount())
    			{
    				TransferableItem item = trackListModel.getItem(rowNum);   
    				item.setTransferState(TransferableItem.STATE_QUEUED);
    				item.setSourceFormat("gpx");
    				items.add(item);
    			}
    		}
    		trackTable.clearSelection();
    		fileHub.run();
		}
	}
	
	public String getName() {
		return "gpsies.com";
	}

	public DataType getDataType() {
		return DataType.STREAM; 
	}	
	
	public String getTitle() {		
		return "Download Tracks from Gpsies.com";
	}

	
	public boolean doShowProgressText() {
		return true;
	}

	@Override
	public List<TransferableItem> getItems() {
		return items;
	}

	@Override
	public GPXFile getGpxFile(TransferableItem item) throws UnsupportedOperationException {
		
		throw new UnsupportedOperationException();
	}

	public void open(TransferableItem transferableItem) {
		currentItem = transferableItem;
		
	}
	
	public InputStream getInputStream() throws Exception {
		if (currentItem != null) {
			OnlineTrack track = (OnlineTrack) currentItem;
			String url = track.getDownloadLink();
			URLConnection urlConnection = new URL(url).openConnection();
			inStream = urlConnection.getInputStream();			
		}
		return inStream;
	}

	@Override
	public void close() throws Exception {
		currentItem = null;
		if (inStream != null) {
			inStream.close();
		}
		// do not hammer the server
		Thread.sleep(2000);		
	}

	@Override
	protected void setupTableModel() {
		trackListModel = new GpsiesTableModel(uc);		
	}
	
	@Override
	protected void setupTable() {
		
		trackTable.getColumnModel().getColumn(0).setPreferredWidth(20); // transfer state
		trackTable.getColumnModel().getColumn(2).setPreferredWidth(80); // distance
		DistanceRenderer distRenderer = new DistanceRenderer(uc);
		trackTable.getColumnModel().getColumn(2).setCellRenderer(distRenderer);		
	}

}
