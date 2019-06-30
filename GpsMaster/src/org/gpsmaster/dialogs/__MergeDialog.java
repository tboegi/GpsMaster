package org.gpsmaster.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;

import org.gpsmaster.Core;
import org.gpsmaster.gpxpanel.GPXFile;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import eu.fuegenstein.messagecenter.MessageCenter;

@SuppressWarnings("serial")
public class __MergeDialog extends JDialog {
	
	private enum Action {
		NONE,
		TRACK,
		SINGLE,
		MULTI,
		PARALLEL
	}
	
	private Container contentPane = null;

	private Core core = new Core();
	private MessageCenter msg = null;
	private List<GPXFile> gpxFiles = null;

	private SpringLayout springLayout = new SpringLayout();
	private JToggleButton trackButton = new JToggleButton();
	private JToggleButton multiButton = new JToggleButton();
	private JToggleButton singleButton = new JToggleButton();
	private JToggleButton paraButton = new JToggleButton();

	private JButton startButton = new JButton();
	private JButton closeButton = new JButton();

	private Action action = Action.NONE;
	
	 /* 
	 * @param parent
	 * @param gpxFiles
	 * @param msg
	 */
	public __MergeDialog(Frame frame, List<GPXFile> gpxfiles, MessageCenter msg)  {
		
		this.gpxFiles = gpxfiles;
		this.msg = msg;

        // set bounds
        int width = 400;
        int x_offset = (frame.getWidth() - width) / 2;
        int height = 250;
        int y_offset = (frame.getHeight() - height) / 2;
        setBounds(frame.getX() + x_offset, frame.getY() + y_offset, width, height);
        setPreferredSize(new Dimension(width, height));

        setTitle("Merge visible tracks / segments");
		setIconImage(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/merge.png")).getImage());
		setAlwaysOnTop(true);
		
		contentPane = getContentPane();
		contentPane.setLayout(springLayout);
		contentPane.setBackground(Color.WHITE);
		
		Font font = startButton.getFont(); // TODO get system font or whatever
		
		// for future extension: display statistics (# of tracks / segments / waypoints to merge)
		// JLabel topLabel = new JLabel();
		// topLabel.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
		// topLabel.setText("Merge visible tracks / segments");

		trackButton.setBackground(Color.WHITE);
		trackButton.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/dialogs/merge-tracks.png")));
		trackButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	singleButton.setSelected(false);
                	multiButton.setSelected(false);
                	paraButton.setSelected(false);
                	startButton.setEnabled(true);
                	action = Action.TRACK;
                }
            }
        });

		JTextArea trackText = new JTextArea();
		trackText.setText("Merge visible track and segments 1:1 into a single file.");
		trackText.setFont(font);
		trackText.setLineWrap(true);
		trackText.setWrapStyleWord(true);
		trackText.setEditable(false);
		trackText.setVisible(true);

		// 
		multiButton.setBackground(Color.WHITE);
		multiButton.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/dialogs/merge-multi.png")));
	    multiButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	trackButton.setSelected(false);
                	singleButton.setSelected(false);
                	paraButton.setSelected(false);
                	startButton.setEnabled(true);
                	action = Action.MULTI;
                }
            }
        });
		JTextArea multiText = new JTextArea();
		multiText.setText("Merge visible tracks and segments into a single track, keep segments separate");
		multiText.setFont(font);
		multiText.setLineWrap(true);
		multiText.setWrapStyleWord(true);
		multiText.setEditable(false);
		multiText.setVisible(true);
		
		// merge into single track
		singleButton.setBackground(Color.WHITE);
		singleButton.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/dialogs/merge-single.png")));
	    singleButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	trackButton.setSelected(false);
                	multiButton.setSelected(false);
                	paraButton.setSelected(false);
                	startButton.setEnabled(true);
                	action = Action.SINGLE;
                }
            }
        });
	    JTextArea singleText = new JTextArea();
		singleText.setText("Merge visible track segments into a new track with a single segment and sort trackpoints by time");
		singleText.setFont(font);
		singleText.setLineWrap(true);
		singleText.setWrapStyleWord(true);
		singleText.setEditable(false);
		singleText.setVisible(true);
		
		paraButton.setBackground(Color.WHITE);
		paraButton.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/dialogs/merge-parallel.png")));		
	    paraButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	trackButton.setSelected(false);
                	multiButton.setSelected(false);
                	singleButton.setSelected(false);
                	startButton.setEnabled(true);
                	action = Action.PARALLEL;
                }
            }
        });
	    JTextArea paraText = new JTextArea();
	    paraText.setText("Interpolate a new track between two track segments");
	    paraText.setFont(font);
	    paraText.setLineWrap(true);
	    paraText.setWrapStyleWord(true);
	    paraText.setEditable(false);
	    paraText.setVisible(true);

	    // ---
	    paraButton.setEnabled(false);
	    
		// contentPane.add(topLabel);
		contentPane.add(trackButton);
		contentPane.add(trackText);
		contentPane.add(multiButton);
		contentPane.add(multiText);
		contentPane.add(singleButton);
		contentPane.add(singleText);
		contentPane.add(paraButton);
		contentPane.add(paraText);
	
		startButton.setText("Start");
		startButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	doMerge();
            }				
		});
		
		closeButton.setText("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	firePropertyChange("dialogClosing", null, "merge");
                setVisible(false);
            }
        });
		
        // springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, topLabel, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);

		springLayout.putConstraint(SpringLayout.NORTH, trackButton, 5, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, trackButton, 10, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, trackText, 2, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, trackText, 5, SpringLayout.EAST, trackButton);
		springLayout.putConstraint(SpringLayout.EAST, trackText, -10, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, trackText, 0, SpringLayout.NORTH, trackButton);
		springLayout.putConstraint(SpringLayout.SOUTH, trackText, 0, SpringLayout.SOUTH, trackButton);
        
		springLayout.putConstraint(SpringLayout.NORTH, multiButton, 5, SpringLayout.SOUTH, trackButton);
		springLayout.putConstraint(SpringLayout.WEST, multiButton, 10, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, multiText, 2, SpringLayout.SOUTH, trackText);
		springLayout.putConstraint(SpringLayout.WEST, multiText, 5, SpringLayout.EAST, multiButton);
		springLayout.putConstraint(SpringLayout.EAST, multiText, -10, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, multiText, 0, SpringLayout.NORTH, multiButton);
		springLayout.putConstraint(SpringLayout.SOUTH, multiText, 0, SpringLayout.SOUTH, multiButton);
		
		springLayout.putConstraint(SpringLayout.NORTH, singleButton, 5, SpringLayout.SOUTH, multiButton);
		springLayout.putConstraint(SpringLayout.WEST, singleButton, 10, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, singleText, 5, SpringLayout.EAST, singleButton);
		springLayout.putConstraint(SpringLayout.EAST, singleText, -10, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, singleText, 0, SpringLayout.SOUTH, multiText);
		springLayout.putConstraint(SpringLayout.NORTH, singleText, 0, SpringLayout.NORTH, singleButton);
		springLayout.putConstraint(SpringLayout.SOUTH, singleText, 0, SpringLayout.SOUTH, singleButton);
		
		springLayout.putConstraint(SpringLayout.NORTH, paraButton, 5, SpringLayout.SOUTH, singleButton);
		springLayout.putConstraint(SpringLayout.WEST, paraButton, 10, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, paraText, 0, SpringLayout.SOUTH, singleText);
		springLayout.putConstraint(SpringLayout.WEST, paraText, 5, SpringLayout.EAST, paraButton);
		springLayout.putConstraint(SpringLayout.EAST, paraText, -10, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, paraText, 0, SpringLayout.NORTH, paraButton);
		springLayout.putConstraint(SpringLayout.SOUTH, paraText, 0, SpringLayout.SOUTH, paraButton);

		contentPane.add(startButton);
		contentPane.add(closeButton);
		
		springLayout.putConstraint(SpringLayout.EAST, startButton, 10, SpringLayout.WEST, closeButton);
		springLayout.putConstraint(SpringLayout.NORTH, startButton, 10, SpringLayout.SOUTH, paraButton);
		springLayout.putConstraint(SpringLayout.NORTH, closeButton, 10, SpringLayout.SOUTH, paraText);
		springLayout.putConstraint(SpringLayout.EAST, startButton, -5, SpringLayout.HORIZONTAL_CENTER, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, closeButton, 5, SpringLayout.HORIZONTAL_CENTER, contentPane);
		pack();

	}
	
	// TODO display # of objects to merge in header
	// TODO run checks before merging
	
	private void doMerge() {
		GPXFile newFile = null;
		
    	switch(action) {
    	case TRACK: 
    		newFile = core.mergeIntoTracks(gpxFiles);
    		break;
    	case MULTI:
    		newFile = core.mergeIntoMulti(gpxFiles);
    		break;
    	case SINGLE:
    		newFile = core.mergeIntoSingle(gpxFiles);
    		break;
    	case PARALLEL:
    		throw new NotImplementedException();
    	default:
    		throw new NotImplementedException();
    	}
		newFile.updateAllProperties();
		firePropertyChange("newGpx", null, newFile);
		msg.volatileInfo("Merging completed.");
	}

}
