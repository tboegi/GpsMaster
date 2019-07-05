package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.cleaning.CleaningAlgorithm;
import org.gpsmaster.cleaning.CloudBuster;
import org.gpsmaster.cleaning.Duplicates;
import org.gpsmaster.cleaning.MinDistance;
import org.gpsmaster.cleaning.Singleton;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;

import eu.fuegenstein.messagecenter.MessageCenter;

public class CleaningDialog extends GenericDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 7970617363233860922L;

	private ActionListener actionListener = null;
	private PropertyChangeListener changeListener = null;
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
		algorithms.add(new CloudBuster());

		setGpxObject();

		changeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if (propertyName.equals(Const.PCE_ACTIVEGPX) || propertyName.equals(Const.PCE_REFRESHGPX)) {
					setGpxObject();
				}
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);
	}

	@Override
	public String getTitle() {
		return "Apply cleaning algorithm";
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
							algoPanel = algo.getPanel(backgroundColor);
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

		setCenterLocation();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setBackground(backgroundColor);
		// setPreferredSize(new Dimension(300, 200));

		ButtonGroup buttonGroup = new ButtonGroup();
		JPanel radioPanel = new JPanel();
		radioPanel.setBackground(backgroundColor);
		radioPanel.setLayout(new GridLayout(0, 1));
		radioPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		for (CleaningAlgorithm algo : algorithms) {
			JRadioButton radioButton = new JRadioButton(algo.getName());
			radioButton.setActionCommand(algo.getName());
			radioButton.addActionListener(actionListener);
			radioButton.setBackground(backgroundColor);
			if (algorithms.indexOf(algo) == 0) {
				radioButton.setSelected(true);
			}
			buttonGroup.add(radioButton);
			radioPanel.add(radioButton);
		}

		// default selection: first algorithm
		selected = algorithms.get(0); // list may not be empty
		algoPanel = selected.getPanel(backgroundColor);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(backgroundColor);
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
	        	dispose();
	        }
	    });
		buttonPanel.add(closeButton);

		add(radioPanel, BorderLayout.WEST);
		add(buttonPanel, BorderLayout.SOUTH);
		add(algoPanel, BorderLayout.CENTER);

		pack();
		setVisible(true);

	}

	/**
	 * to be called before destruction
	 */
	public void dispose() {
		GpsMaster.active.removePropertyChangeListener(changeListener);
    	for (CleaningAlgorithm algo : algorithms) {
    		algo.clear();
    	}
    	GpsMaster.active.repaintMap();
		super.dispose();
	}

	/**
	 * Set list of active {@link WaypointGroup}s
	 */
	private void setGpxObject() {
		for (CleaningAlgorithm algo : algorithms) {
			algo.clear();
			algo.setWaypointGroups(GpsMaster.active.getGroups());
		}
	}

	/**
	 *
	 */
	private void apply() {
		if (selected != null) {
			selected.doClean();
			selected.clear();
			GpsMaster.active.refresh();
			GpsMaster.active.repaintMap();
		}
	}

	/**
	 *
	 */
	private void preview() {
		if (selected != null) {
			System.out.println(selected.getName());
			selected.preview();
			GpsMaster.active.repaintMap();
		}
	}

}
