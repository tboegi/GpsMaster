package org.gpsmaster.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;

public class MergeDialog extends JDialog {
	
	private Container contentPane = null;
	
	private MessageCenter msg = null;
	private List<GPXFile> gpxfiles = null;
	private GPXFile newGpxFile = null;
	
	private JToggleButton multiButton = new JToggleButton();
	private JToggleButton singleButton = new JToggleButton();
	private JToggleButton paraButton = new JToggleButton();

	/**
	 * 
	 * @param parent
	 * @param gpxfiles
	 * @param msg
	 */
	public MergeDialog(Frame frame, List<GPXFile> gpxfiles, MessageCenter msg)  {
		
		this.gpxfiles = gpxfiles;
		this.msg = msg;

        // set bounds
        int width = 250;
        int x_offset = (frame.getWidth() - width) / 2;
        int height = 400;
        int y_offset = (frame.getHeight() - height) / 2;
        setBounds(frame.getX() + x_offset, frame.getY() + y_offset, width, height);
        setMinimumSize(new Dimension(width, height));

        setTitle("Merge");
		setIconImage(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/merge.png")).getImage());
		setAlwaysOnTop(true);
		
		contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setBackground(Color.WHITE);
		
		JLabel topLabel = new JLabel();
		topLabel.setText("Merge visible tracks / segments");
		
		multiButton.setBackground(Color.WHITE);
		multiButton.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/merge-multi.png")));
		multiButton.setText("Merge selected tracks and segments into a single track, keep segments separate");
		// JTextArea multiText = new JTextArea();
		// multiText.setText("Merge selected tracks and segments into a single track, keep segments separate");
		// multiText.setLineWrap(true);
		// multiText.setWrapStyleWord(true);
		// multiText.setEditable(false);
		// multiText.setVisible(true);
	    multiButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	singleButton.setSelected(false);
                	paraButton.setSelected(false);
            		firePropertyChange("newGpxFile", newGpxFile, null);
                }
            }
        });

		singleButton.setBackground(Color.WHITE);
		singleButton.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/merge-single.png")));
		singleButton.setText("Merge visibile track segments into a new track with a single segment and sort trackpoints by time");
	    singleButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	multiButton.setSelected(false);
                	paraButton.setSelected(false);
                }
            }
        });
		
		paraButton.setBackground(Color.WHITE);
		paraButton.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/merge-parallel.png")));
		paraButton.setText("Interpolate a new track between selected track segments");
		
	    paraButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	multiButton.setSelected(false);
                	singleButton.setSelected(false);
                }
            }
        });
		
		contentPane.add(topLabel);
		contentPane.add(multiButton);
		contentPane.add(singleButton);
		contentPane.add(paraButton);
		//	contentPane.setVisible(true);
				

	}
	
	
}
