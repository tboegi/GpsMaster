package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
// import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import eu.fuegenstein.messagecenter.MessageCenter;

/**
 * not really a dialog, just a progress bar for elevation correction
 * http://stackoverflow.com/questions/4637215/can-a-progress-bar-be-used-in-a-class-outside-main/4637725#4637725
 * @author rfu
 *
 */

@SuppressWarnings("serial")
public class ElevationDialog /* extends JDialog */ {

	private GPXObject gpxObject = null;

	JFrame frame = new JFrame();
	// JPanel panel = new JPanel();

	private JProgressBar itemBar = new JProgressBar(JProgressBar.HORIZONTAL);
    private JProgressBar trackpointBar = new JProgressBar(JProgressBar.HORIZONTAL);
    private CorrectionTask task = null;
    private MessageCenter msg = null;
    private PropertyChangeListener changeListener = null;

    private int itemCount = 0;
	private int totalWaypoints = 0; // total number of trackpoints to process
	private int totalItems = 0; // total number of items to process
    private int chunkSize = 200; // process {@link chunksize} waypoints per request

    private int cleanseFailed = 0;

	/**
	 * Constructor
	 * @param frame Component to position this dialog to
	 * @param gpx {@link GPXObject} to be elevation processed
	 * @param msg {@link MessageCenter} for showing errors, warnings etc.
	 */
	public ElevationDialog(Component parent, GPXObject gpxObject, MessageCenter msg)  {

		this.gpxObject = gpxObject;
		this.msg = msg;

        if (gpxObject.isGPXFile()) { // correct all tracks and segments
			GPXFile gpx = (GPXFile) gpxObject;
			totalWaypoints = (int) (gpx.getNumTrackPts() + gpx.getNumWayPts() + gpx.getNumRoutePts());
			for (Track track : gpx.getTracks()) {
				totalItems += track.getTracksegs().size();
			}
			totalItems += gpx.getRoutes().size();
			if (gpx.getWaypointGroup().getWaypoints().size() > 0) {
				totalItems++;
			}
		} else if (gpxObject.isTrack()) {
			Track track = (Track) gpxObject;
			totalItems = track.getTracksegs().size();
			totalWaypoints = (int) track.getNumPts();
		} else if (gpxObject.isTrackseg()) {
			totalItems = 1;
			totalWaypoints = ((WaypointGroup) gpxObject).getNumPts();
		} else if (gpxObject.isRoute()) {
			totalItems = 1;
			totalWaypoints = ((Route) gpxObject).getNumPts();
		}

        // set up frame
        // TODO move position of frame as mappanel/main frame moves
        frame.setLocation(parent.getLocationOnScreen().x+10, parent.getLocationOnScreen().y+10);
		frame.setUndecorated(true);
		frame.setBackground(Color.WHITE);
		frame.setOpacity(0.80f);
		frame.setIconImage(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/correct-elevation.png")).getImage());

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 7, 5));
		contentPane.setSize(frame.getSize());

		JPanel labelPane = new JPanel(new BorderLayout());
		labelPane.setBackground(Color.WHITE);
		JLabel label = new JLabel(String.format("Correcting elevation of %d trackpoints in %d segments", totalWaypoints, totalItems));
		labelPane.add(label, BorderLayout.LINE_START);

		JLabel cancel = new JLabel();
		cancel.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/cancel.png")));
		cancel.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	if (task != null) {
            	 task.cancel(true);
            	}
            }
		});
		labelPane.add(cancel, BorderLayout.LINE_END);
		labelPane.setBorder(new EmptyBorder(2,2,2,2));
		contentPane.add(labelPane);
		contentPane.add(Box.createRigidArea(new Dimension(0,4)));

		Dimension barDimension = new Dimension(360, 20);
		itemBar.setBackground(Color.WHITE);
		itemBar.setMinimum(0);
		itemBar.setMaximum(totalItems);
		itemBar.setValue(0);
		itemBar.setMaximumSize(barDimension);
		itemBar.setPreferredSize(barDimension);
		itemBar.setStringPainted(false);
		itemBar.setForeground(Color.BLUE);
		itemBar.setVisible(true);
		contentPane.add(itemBar);
		contentPane.add(Box.createRigidArea(new Dimension(0,2)));

		trackpointBar.setBackground(Color.WHITE);
		trackpointBar.setMinimum(0);
		trackpointBar.setMaximum(totalWaypoints);
		trackpointBar.setValue(0);
		trackpointBar.setMaximumSize(barDimension);
		trackpointBar.setPreferredSize(barDimension);
		trackpointBar.setVisible(true);
		trackpointBar.setStringPainted(false);
		trackpointBar.setForeground(Color.BLUE);
		contentPane.add(trackpointBar);

		frame.add(contentPane);
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 *
	 * @param listener
	 */
	public void setChangeListener(PropertyChangeListener listener) {
		changeListener = listener;
	}

	/**
	 * class representing interim results sent to the progress bars
	 * @author rfu
	 *
	 */
	private class Progress {
		private int waypoints = 0;
		private int items = 0;

		Progress(int items, int waypoins) {
			this.setItems(items);
			this.setWaypoints(waypoins);
		}

		public int getWaypoints() {
			return waypoints;
		}

		public void setWaypoints(int waypoints) {
			this.waypoints = waypoints;
		}

		public int getItems() {
			return items;
		}

		public void setItems(int items) {
			this.items = items;
		}
	}

	/**
	 * run elevation correction as background task
	 */
	public void runCorrection() {
		// trackpointBar.setIndeterminate(true);
		task = new CorrectionTask();
		if (changeListener != null) {
			task.addPropertyChangeListener(changeListener);
		}
		task.execute();
	}

	/**
	 *
	 * @author rfu
	 *
	 */
	private class CorrectionTask extends SwingWorker<Void, Progress> {

		@Override
		protected Void doInBackground() throws Exception {
			// TODO replace with Core.getWaypointGroups();
			if (gpxObject.isGPXFile()) { // correct and cleanse all tracks and segments
				GPXFile gpx = (GPXFile) gpxObject;
				for (Track track : gpx.getTracks()) {
					for (WaypointGroup trackSeg : track.getTracksegs()) {
						correctElevation(trackSeg);
						cleanseElevation(trackSeg);
					}
				}
				if (gpx.getWaypointGroup().getWaypoints().size() > 0) {
					correctElevation(gpx.getWaypointGroup());
					cleanseElevation(gpx.getWaypointGroup());
				}
			} else if (gpxObject.isTrack()) {
				Track track = (Track) gpxObject;
				for (WaypointGroup trackSeg : track.getTracksegs()) {
					correctElevation(trackSeg);
					cleanseElevation(trackSeg);
				}
			} else if (gpxObject.isWaypointGroup() || gpxObject.isRoute()) {
				correctElevation((WaypointGroup) gpxObject);
				cleanseElevation((WaypointGroup) gpxObject);
			}
			return null;
		}

		@Override
		protected void process(List<Progress> progressList) {
			Progress progress = progressList.get(progressList.size() - 1);
			itemBar.setValue(progress.getItems());
			trackpointBar.setValue(progress.getWaypoints());
		}

	    /**
	     * Corrects the elevation of each {@link Waypoint} in the group and updates the aggregate group properties.<br />
	     * (Optionally can do a "cleanse," attempting to fill missing data (SRTM voids) in the response.<br />)
	     * Note: The MapQuest Open Elevation API has a bug with POST XML, and the useFilter parameter.
	     *       Because of this, the request must be a POST KVP (key/value pair).  The useFilter parameter returns
	     *       data of much higher quality.
	     *
	     * @return  The status of the response.
	     */
		private void correctElevation(WaypointGroup waypointGroup) throws Exception {

			int totals = 0;
			int grpCtr = 0; // waypoint group counter

			List<Waypoint> waypoints = waypointGroup.getWaypoints();
			itemCount++;
			trackpointBar.setMaximum(waypoints.size());
			while(grpCtr < waypoints.size() && !task.isCancelled()) {

				int blockCtr = 0;
				int firstInBlock = grpCtr;

				// build a chunk
		        Locale prevLocale = Locale.getDefault();
		        Locale.setDefault(new Locale("en", "US"));

				String latLngCollection = "";
				while((grpCtr < waypoints.size()) && (blockCtr < chunkSize)) {
					Waypoint wpt = waypoints.get(grpCtr);
			        latLngCollection += String.format("%.6f,%.6f,", wpt.getLat(), wpt.getLon());

					grpCtr++;
					blockCtr++;
					totals++;
				}
				latLngCollection = latLngCollection.substring(0, latLngCollection.length()-1);
				Locale.setDefault(prevLocale);

				// make request
		        String url = "http://open.mapquestapi.com/elevation/v1/profile";
		        String charset = "UTF-8";
		        String param1 = "kvp"; // inFormat
		        String param2 = latLngCollection;
		        String param3 = "xml"; // outFormat
		        String param4 = "true"; // useFilter
		        String query = null;
		        URLConnection connection = null;
		        OutputStream output = null;
		        InputStream response = null;
		        BufferedReader br = null;
		        StringBuilder builder = new StringBuilder();
		        try {
		            query = "key=Fmjtd%7Cluub2lu12u%2Ca2%3Do5-96y5qz" +
		                    String.format("&inFormat=%s" + "&latLngCollection=%s" + "&outFormat=%s" + "&useFilter=%s",
		                    URLEncoder.encode(param1, charset),
		                    URLEncoder.encode(param2, charset),
		                    URLEncoder.encode(param3, charset),
		                    URLEncoder.encode(param4, charset));
		            connection = new URL(url).openConnection();
		            connection.setDoOutput(true);
		            connection.setRequestProperty("Accept-Charset", charset);
		            connection.setRequestProperty(
		                    "Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		            output = connection.getOutputStream();
		            output.write(query.getBytes(charset));
		            output.close();
		            response = connection.getInputStream();
		            br = new BufferedReader((Reader) new InputStreamReader(response, "UTF-8"));
		            for(String line=br.readLine(); line!=null; line=br.readLine()) {
		                builder.append(line);
		                builder.append('\n');
		            }
		        } catch (IOException e) {
		            msg.Error("http request failed", e);
		            task.cancel(true);
		            break;
		        }

				// process response

		        String responseStr = builder.toString();
		        if (responseStr.contains("Given Route exceeds the maximum allowed distance")) {
		        	// should not happen since we process in chunks
		        	msg.Error("Given Route exceeds the maximum allowed distance");
		            task.cancel(true);
		            break;
		        }
				// TODO check for error in response
	            List<Double> eleList = getEleArrayFromXMLResponse(responseStr);
	            if (eleList.size() != blockCtr) {
	            	msg.Error("Result size mismatch");
	            	task.cancel(true);
	            	break;
	            }
	            for (int i = 0; i < eleList.size(); i++) {
	            	waypoints.get(firstInBlock+i).setEle(eleList.get(i));
	            }
	            // update progress bar
		        publish(new Progress(itemCount, totals));

			}
	        // TODO also update parent
			waypointGroup.updateEleProps();
		}

		/**
		 *
		 */
		@Override
		protected void done() {
			if (task != null && task.isCancelled()) {
				msg.volatileWarning("Elevation correction cancelled.");
			} else {
				msg.volatileInfo("Elevation correction finished.");
				if (cleanseFailed == 1) {
					msg.volatileWarning("Cleansing not possible for one track segment/waypoint group");
				} else if (cleanseFailed > 1) {
					msg.volatileWarning(String.format("Cleansing not possible for %d track segments/waypoint groups." , cleanseFailed));
				}
			}
			firePropertyChange("updateGpx", null, gpxObject);
			firePropertyChange("dialogClosing", null, "elevation");
			frame.setVisible(false);
		}
	}


    /**
     * Cleanse the elevation data.  Any {@link Waypoint} with an elevation of -32768 needs to be interpolated.
     *
     * @return  The status of the cleanse.
     */
    private void cleanseElevation(WaypointGroup wptGrp) {

    	List<Waypoint> waypoints = wptGrp.getWaypoints();
    	double eleStart = wptGrp.getStart().getEle();
        double eleEnd = wptGrp.getEnd().getEle();

        if (eleStart == -32768) {
            for (int i = 0; i < waypoints.size(); i++) {
                if (waypoints.get(i).getEle() != -32768) {
                    eleStart = waypoints.get(i).getEle();
                    break;
                }
            }
        }

        if (eleEnd == -32768) {
            for (int i = waypoints.size() - 1; i >= 0; i--) {
                if (waypoints.get(i).getEle() != -32768) {
                    eleEnd = waypoints.get(i).getEle();
                    break;
                }
            }
        }

        if (eleStart == -32768 && eleEnd == -32768) {
        	// hopeless! (impossible to correct)
        	cleanseFailed++;
            return;
        }

        waypoints.get(0).setEle(eleStart);
        waypoints.get(waypoints.size() - 1).setEle(eleEnd);

        for (int i = 0; i < waypoints.size(); i++) {
            if (waypoints.get(i).getEle() == -32768) {
                Waypoint neighborBefore = null;
                Waypoint neighborAfter = null;
                double distBefore = 0;
                double distAfter = 0;

                Waypoint curr = waypoints.get(i);
                Waypoint prev = waypoints.get(i);
                for (int j = i - 1; j >= 0; j--) {
                    prev = curr;
                    curr = waypoints.get(j);
                    distBefore += curr.getDistance(prev);
                    if (waypoints.get(j).getEle() != -32768) {
                        neighborBefore = waypoints.get(j);
                        break;
                    }
                }

                curr = waypoints.get(i);
                prev = waypoints.get(i);
                for (int j = i + 1; j < waypoints.size(); j++) {
                    prev = curr;
                    curr = waypoints.get(j);
                    distAfter += curr.getDistance(prev);
                    if (waypoints.get(j).getEle() != -32768) {
                        neighborAfter = waypoints.get(j);
                        break;
                    }
                }

                if ((neighborBefore != null) && (neighborAfter != null)) {
	                double distDiff = distBefore + distAfter;
	                double eleDiff = neighborAfter.getEle() - neighborBefore.getEle();
	                double eleCleansed = ((distBefore / distDiff) * eleDiff) + neighborBefore.getEle();
	                waypoints.get(i).setEle(eleCleansed);
                }
            }
        }
    }

    /**
     * Parses an XML response string.
     *
     * @return  A list of numerical elevation values.
     */
    private /* static */ List<Double> getEleArrayFromXMLResponse(String xmlResponse) {
        List<Double> ret = new ArrayList<Double>();
        InputStream is = new ByteArrayInputStream(xmlResponse.getBytes());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        try {
            XMLStreamReader xsr = xif.createXMLStreamReader(is, "ISO-8859-1");
            while (xsr.hasNext()) {
                xsr.next();
                if (xsr.getEventType() == XMLStreamReader.START_ELEMENT) {
                    if (xsr.getLocalName().equals("height")) {
                        xsr.next();
                        if (xsr.isCharacters()) {
                            ret.add(Double.parseDouble(xsr.getText()));
                        }
                    }
                }
            }
            xsr.close();
        }  catch (Exception e) {
            msg.Error("There was a problem parsing the XML response", e);
        }
        return ret;
    }

}
