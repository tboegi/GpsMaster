package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.TableModelEvent;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.marker.PhotoMarker;

import eu.fuegenstein.messagecenter.MessageCenter;


/**
 * Dialog displaying an image that is referenced by a PhotoMarker
 * (or any other image as set via setImage() )
 * 
 * @author rfu
 * 
 * TODO for next version: setWaypointGroup(), left/right button,
 * 		PhotoMarker on map for mark current photo 
 * 
 */
public class ImageViewer extends GenericDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7056561782303771912L;

	private List<GPXFile> gpxFiles = null;
	private PropertyChangeListener changeListener = null;
	private JLabel filenameLabel = new JLabel();
	private ImageDisplay imageDisplay = null;
	private JTable exifTable = null;
	private ExifTableModel exifModel = new ExifTableModel();
	private List<PhotoMarker> markers = new ArrayList<PhotoMarker>();
	private PhotoMarker current = null;
	
	private JButton prevButton = new JButton();
	private JToggleButton centerButton = new JToggleButton();
	private JButton nextButton = new JButton();
	private JButton zoomButton = new JButton();
	
	/**
	 * 
	 * @param frame
	 */
	public ImageViewer(JFrame frame, MessageCenter msg) {
		super(frame, msg);					
	}
	
	/**
	 * 
	 */
	 @Override
	public void begin() {		
		
		JPanel exifPane = new JPanel();
		JPanel imagePanel = new JPanel();

		Container contentPane = getContentPane();
		Dimension dimension = new Dimension(640, 480);
		setMinimumSize(dimension);
				
		setTitle("View Image");
		setIconImage(new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_MARKER+"photo.png")).getImage());
		contentPane.setLayout(new BorderLayout());
		
		// setup filename label
		filenameLabel = new JLabel();
		contentPane.add(filenameLabel, BorderLayout.NORTH);
		
		// setup image pane
		imagePanel.setLayout(new BorderLayout());
		imageDisplay = new ImageDisplay();
		imagePanel.add(imageDisplay, BorderLayout.CENTER);

		prevButton.setIcon(new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_DIALOGS + "prev.png")));
		// prevButton.setText("prev");
		prevButton.setEnabled(false);
		prevButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				int pos = markers.indexOf(current);
				if (pos > 0) {
					showMarker(markers.get(pos - 1));
					setPrevNext();
					centerMap();
				}
			}
		});
		
		nextButton.setIcon(new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_DIALOGS + "next.png")));
		// nextButton.setText("next");
		nextButton.setEnabled(false);
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int pos = markers.indexOf(current);
				if (pos < markers.size() - 1) {
					showMarker(markers.get(pos + 1));
					setPrevNext();
					centerMap();
				}				
			}
		});
		zoomButton.setIcon(new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_DIALOGS + "zoom-best-fit.png")));
		zoomButton.setToolTipText("Zoom Best Fit");
		zoomButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (imageDisplay != null) {
					imageDisplay.zoomBestFitOrOne();
				}				
			}
		});
		
		centerButton.setIcon(new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_DIALOGS + "centreview.png")));		
		centerButton.setToolTipText("Center Map");
		centerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				centerMap();				
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(prevButton);
		buttonPanel.add(centerButton);
		buttonPanel.add(zoomButton);
		buttonPanel.add(nextButton);
		imagePanel.add(buttonPanel, BorderLayout.SOUTH);
		
		// setup exif pane
		exifPane.setLayout(new BorderLayout());
		exifTable = new JTable(exifModel);		
		exifTable.getColumnModel().getColumn(0).setPreferredWidth(25);
		exifTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		exifTable.getColumnModel().getColumn(2).setPreferredWidth(285);
		JScrollPane exifScroll = new JScrollPane(exifTable);
		exifPane.add(exifScroll, BorderLayout.CENTER);
				
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Image", imagePanel);		
		tabbedPane.addTab("EXIF", exifPane);
		
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		pack();
		
		changeListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propName = evt.getPropertyName(); 
				if (propName.equals(Const.PCE_REFRESHGPX) || propName.equals(Const.PCE_NEWGPX)) {
					refresh();
					setPrevNext();
				}
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);
		setCenterLocation();
		setVisible(true);
	}
	
	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void showImage(File file) throws IOException {
		// TODO set orientation
		filenameLabel.setText(file.getName());		
		imageDisplay.setImage(file, -1);		
	}
	
	/**
	 * 
	 * @param filename
	 * @throws IOException 
	 */
	public void showImage(String filename) throws IOException {		
		showImage(new File(filename));
	}

	/**
	 * 
	 * @param gpxFiles
	 */
	public void setGpxFiles(List<GPXFile> gpxFiles) {
		this.gpxFiles = gpxFiles;
		refresh();
		setPrevNext();
	}
	
	/**
	 * 
	 * @param marker
	 * @throws IOException
	 */
	public synchronized void showMarker(PhotoMarker marker) {
		if (current != null) {
			current.setSelected(false);
		}
		String filename = marker.getDirectory();
		filenameLabel.setText(filename);
		imageDisplay.setImage(new File(filename), marker.getOrientation());
		exifModel.setTags(marker.getExifTags());
		exifTable.tableChanged(new TableModelEvent(exifModel));
		current = marker;
		current.setSelected(true);
		setPrevNext();
		GpsMaster.active.repaintMap();
	}

	@Override
	public String getTitle() {
		return "ImageViewer";
	}

	public void clear() {
		imageDisplay.setImage(null, 0);
		prevButton.setEnabled(false);
		nextButton.setEnabled(false);
		current = null;
		gpxFiles = null;
		markers.clear();
	}
	
	/**
	 * 
	 */
	public void dispose() {
		GpsMaster.active.removePropertyChangeListener(changeListener);
		if (current != null) {
			current.setSelected(false);
		}
		super.dispose();
	}
	
	/**
	 * 
	 */
	private void refresh() {
		if (gpxFiles != null) {
			markers.clear();
			for (GPXFile gpx : gpxFiles) {
				for (Waypoint wpt : gpx.getWaypointGroup().getWaypoints()) {
					if (wpt instanceof PhotoMarker) {
						markers.add((PhotoMarker) wpt);
					}					
				}
			}
		}
		if ((current != null) && (markers.size() > 0)) {
			if (markers.contains(current) == false) {
				showMarker(markers.get(0));
			}
		}
	}
	
	/**
	 * Enable/disable prev/next buttons
	 */
	private void setPrevNext() {
		prevButton.setEnabled(false);
		nextButton.setEnabled(false);

		if (current != null) {
			int pos = markers.indexOf(current);
			if (pos > 0) {
				prevButton.setEnabled(true);
			}
			if (pos < markers.size() - 1) {
				nextButton.setEnabled(true);
			}			
		}
	}
	
	/**
	 * 
	 */
	private void centerMap() {
		if ((current != null) && centerButton.isSelected()) {
			GpsMaster.active.centerMap(current);
		}

	}
	
}
