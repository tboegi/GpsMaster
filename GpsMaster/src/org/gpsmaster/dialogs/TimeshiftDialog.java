package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;


@SuppressWarnings("serial")
public class TimeshiftDialog extends JDialog {


	private GPXObject gpxObject = null;

	private final JPanel contentPane = new JPanel();
	private JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());

	/**
	 * Create the dialog.
	 */
	public TimeshiftDialog(Frame frame, String title, GPXObject gpx) {
        super(frame, title, true);
        setForeground(Color.BLACK);
        getContentPane().setForeground(Color.BLACK);
        gpxObject = gpx;

        // set icon image
        // TODO use central method
        InputStream in = GpsMaster.class.getResourceAsStream("/org/gpsmaster/icons/toolbar/timeshift.png");
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setIconImage(img);
        // set bounds
        int width = 400;
        int x_offset = (frame.getWidth() - width) / 2;
        int height = 100;
        int y_offset = (frame.getHeight() - height) / 2;
        setBounds(frame.getX() + x_offset, frame.getY() + y_offset, width, height);
        setMinimumSize(new Dimension(width, height));

		getContentPane().setLayout(new BorderLayout());
		contentPane.setLayout(new FlowLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPane, BorderLayout.CENTER);

		JLabel label = new JLabel("New start time: ");
		contentPane.add(label);

		Date dateValue = new Date();
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "yyyy-MM-dd HH:mm:ss");
		timeSpinner.setEditor(timeEditor);
		timeSpinner.setValue(dateValue);
		contentPane.add(timeSpinner);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Date newdate = (Date) timeSpinner.getValue();
                timeshiftByDate(newdate);
                setVisible(false);
            }
        });
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
		buttonPane.add(cancelButton);
	}


    /**
     * timeshift a {@link WaypointGroup} by {@link Period}
     * @param wptGrp
     * @param delta
     */
    private void timeshiftWaypointGroup(WaypointGroup wptGrp, Period delta) {
    	if (wptGrp != null) {
    		for (Waypoint wpt : wptGrp.getWaypoints()) {
    			if (wpt.getTime() != null) {
    				DateTime oldDate = new DateTime(wpt.getTime());
    				wpt.setTime(oldDate.plus(delta).toDate());
    			}
    		}
    	}
    }

    /**
     * shift all times in the {@link WaypointGroup} to {@link Date}
     * in relation to the old startTime of the {@link WaypointGroup}
     * @param wptGrp
     * @param newDate
     */
    private void timeshiftWaypointGroup(WaypointGroup wptGrp, Date newDate) {
    	if (wptGrp.getWaypoints().size() > 0 ) {
    		Date oldDate = wptGrp.getWaypoints().get(0).getTime();
    		if (oldDate != null) {
    			Period delta = new Duration(new DateTime(oldDate), new DateTime(newDate)).toPeriod();
    			timeshiftWaypointGroup(wptGrp, delta);
    		}
    	}
    }

    /**
     * shift times of all segments in {@link Track} by {@link Period}
     * @param track
     * @param delta
     */
    private void timeshiftTrack(Track track, Period delta) {
    	for(WaypointGroup wptGrp : track.getTracksegs()) {
    		timeshiftWaypointGroup(wptGrp, delta);
    	}

    }

    /**
     * shift all timestamps of current {@link GPXObject} by {@link Period}
     * @param delta
     */
    private void timeShiftDelta(Period delta) {
    	if (gpxObject != null) {
    		if (gpxObject.isGPXFile()) {
    			GPXFile gpx = (GPXFile) gpxObject;
        		if (gpx.getMetadata().getTime() != null) {
        			// shift GPXFile date
        			DateTime oldDate = new DateTime(gpx.getMetadata().getTime());
        			gpx.getMetadata().setTime(oldDate.plus(delta).toDate());
        		}
        		for (Track track : gpx.getTracks()) {
        			timeshiftTrack(track, delta);
        		}
    		} else if (gpxObject.isTrack()) {
    			timeshiftTrack((Track) gpxObject, delta);
    		} else if (gpxObject.isTrackseg()) {
    			timeshiftWaypointGroup((WaypointGroup) gpxObject, delta);
    		} else {
    			throw new UnsupportedOperationException("Unsupported Object Type");
    		}
    	}
    }

    /**
     * Shift current {@link GPXfile} to a new date
     * @param newdate
     * TODO support timeshift of single tracks and segments
     */
    private void timeshiftByDate(Date newdate) {

    	if (gpxObject != null) {
    		if (gpxObject.isGPXFile()) { // just set GPX.Time to new date
    			((GPXFile) gpxObject).getMetadata().setTime(newdate);
    		} else if (gpxObject.isWaypointGroup()) {
    			timeshiftWaypointGroup((WaypointGroup) gpxObject, newdate);
    		}
    	}
    }


}
