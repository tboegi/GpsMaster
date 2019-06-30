package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.markers.PhotoMarker;


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
public class ImageViewer extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7056561782303771912L;
	
	private JFrame parentFrame = null;
	private JLabel filenameLabel = new JLabel();
	private ImageDisplay imageDisplay = null;
	private JTable exifTable = null;
	private ExifModel exifModel = new ExifModel();
	
	/**
	 * 
	 * @param frame
	 */
	public ImageViewer(JFrame frame) {
		
		parentFrame = frame;
		setup();
		
	}
	/**
	 * 
	 */
	private void setup() {
		
		
		JPanel exifPane = new JPanel();
		JPanel imagePanel = new JPanel();

		Container contentPane = getContentPane();
		Dimension dimension = new Dimension(640, 480);
		setMinimumSize(dimension);
		
		setLocationRelativeTo(parentFrame);
		Point location = new Point();
		location.x = parentFrame.getLocation().x + parentFrame.getWidth() / 2 - getWidth() / 2;
		location.y = parentFrame.getLocation().y + parentFrame.getHeight() / 2 - getHeight() / 2;
		setLocation(location);			
		
		setTitle("View Image");
		setIconImage(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/markers/photo.png")).getImage());
		contentPane.setLayout(new BorderLayout());
		
		// setup filename label
		filenameLabel = new JLabel();
		contentPane.add(filenameLabel, BorderLayout.NORTH);
		
		// setup image pane
		imagePanel.setLayout(new BorderLayout());
		imageDisplay = new ImageDisplay();
		imagePanel.add(imageDisplay, BorderLayout.CENTER);
		
		JButton zoomButton = new JButton();
		zoomButton.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/dialogs/zoom-best-fit.png")));
		zoomButton.setToolTipText("Zoom Best Fit");
		zoomButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (imageDisplay != null) {
					imageDisplay.zoomBestFitOrOne();
				}				
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(zoomButton);
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
	 * @param marker
	 * @throws IOException
	 */
	public synchronized void showMarker(PhotoMarker marker) {
		String filename = marker.getDirectory();
		filenameLabel.setText(filename);
		imageDisplay.setImage(new File(filename), marker.getOrientation());
		exifModel.setTags(marker.getExifTags());
		exifTable.tableChanged(new TableModelEvent(exifModel));
	}

	
}
