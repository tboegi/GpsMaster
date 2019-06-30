package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.cleaning.CleaningAlgorithm;
import org.gpsmaster.cleaning.Duplicates;
import org.gpsmaster.cleaning.MinDistance;
import org.gpsmaster.cleaning.Singleton;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.markers.Marker;

import eu.fuegenstein.messagecenter.MessageCenter;

public class CleaningDialog extends GenericDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7970617363233860922L;

	private ActionListener actionListener = null;
	private JPanel algoPanel = null;
	private List<CleaningAlgorithm> algorithms = new ArrayList<CleaningAlgorithm>();
	private List<Marker> markerList = null;
	private CleaningAlgorithm selected = null;
	
	/**
	 * default constructor
	 * @param frame
	 * @param msg
	 */
	public CleaningDialog(JFrame frame, MessageCenter msg) {
		super(frame, msg);
		
		// TODO populate this list automatically with all subclasses of CleaningAlgorithm
		algorithms.add(new Duplicates());
		algorithms.add(new MinDistance());
		algorithms.add(new Singleton());
	}

	@Override
	public String getTitle() {		
		return "Apply cleaning algorithm";
	}

	/**
	 * 
	 * @param grp
	 */
	public void setWaypointGroup(WaypointGroup grp) {
		for (CleaningAlgorithm algo : algorithms) {
			algo.setWaypointGroup(grp);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Marker> getMarkerList() {
		return markerList;
	}

	/**
	 * 
	 * @param markerList
	 */
	public void setMarkerList(List<Marker> markerList) {
		this.markerList = markerList;
		for (CleaningAlgorithm algo : algorithms) {
			algo.setMarkerList(markerList);
		}
	}
	
	@Override
	public void begin() {

		if (actionListener == null) {
			actionListener = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					for (CleaningAlgorithm algo : algorithms) {
						if (algo.getName().equals(e.getActionCommand())) {
							remove(algoPanel);
							algoPanel = algo.getPanel();
							algoPanel.setBorder(new EmptyBorder(2, 4, 2, 2));
							add(algoPanel, BorderLayout.CENTER);
							revalidate();
							repaint();
							selected = algo;

							return;
						}
					}				
				}
			};
		}		
		
		setLocationRelativeTo(parentFrame);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		// setPreferredSize(new Dimension(300, 200));
		
		ButtonGroup buttonGroup = new ButtonGroup();
		JPanel radioPanel = new JPanel();
		radioPanel.setBackground(Color.WHITE);
		radioPanel.setLayout(new GridLayout(0, 1));
		radioPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		for (CleaningAlgorithm algo : algorithms) {
			JRadioButton radioButton = new JRadioButton(algo.getName());
			radioButton.setActionCommand(algo.getName());
			radioButton.addActionListener(actionListener);
			radioButton.setBackground(Color.WHITE);
			if (algorithms.indexOf(algo) == 0) {
				radioButton.setSelected(true);
			}
			buttonGroup.add(radioButton);
			radioPanel.add(radioButton);
		}
				
		// default selection: first algorithm
		algoPanel = algorithms.get(0).getPanel(); // list may not be empty
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton previewButton = new JButton("Preview");
		previewButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	preview();
	        }
	    });
		buttonPanel.add(previewButton);
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	apply();	        
	        }
	    });
		buttonPanel.add(applyButton);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	for (CleaningAlgorithm algo : algorithms) {
	        		algo.clear();
	        	}
	        	dispose();
	        }
	    });
		buttonPanel.add(closeButton);
		
		add(radioPanel, BorderLayout.WEST);
		add(buttonPanel, BorderLayout.SOUTH);
		add(algoPanel, BorderLayout.CENTER);
		
		pack();

		Point location = new Point();
		location.x = parentFrame.getLocation().x + parentFrame.getWidth() / 2 - getWidth() / 2;
		location.y = parentFrame.getLocation().y + parentFrame.getHeight() / 2 - getHeight() / 2;
		setLocation(location);			
		setVisible(true);

	}

	/**
	 * 
	 */
	private void apply() {
		if (selected != null) {
			selected.doClean();
			selected.clear();
			firePropertyChange("repaintMapPanel", null,  null);
		}
	}

	/**
	 * 
	 */
	private void preview() {
		if (selected != null) {
			System.out.println(selected.getName());
			selected.preview();
			System.out.println(selected.getAffected());
			firePropertyChange("repaintMapPanel", null,  null);
		}
	}

}
