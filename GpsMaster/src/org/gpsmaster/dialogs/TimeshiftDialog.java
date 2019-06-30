package org.gpsmaster.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.gpsmaster.Const;
import org.gpsmaster.GenericAlgorithm;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.timeshift.ClearTimestamps;
import org.gpsmaster.timeshift.LocalToUTC;
import org.gpsmaster.timeshift.Reverse;
import org.gpsmaster.timeshift.ShiftTime;
import org.gpsmaster.timeshift.TimeshiftAlgorithm;

import eu.fuegenstein.messagecenter.MessageCenter;

/**
 * 
 * @author rfu
 *
 * TODO generalize RadioPanel & ParamPanel functionality
 * 		into a superclass (see {@link CleaningDialog})
 */
@SuppressWarnings("serial")
public class TimeshiftDialog extends RadioButtonDialog {
	
	private PropertyChangeListener changeListener = null;
	private ActionListener selectionListener = null;
	
	private List<TimeshiftAlgorithm> algorithms = new ArrayList<TimeshiftAlgorithm>();
	private TimeshiftAlgorithm selected = null;

	private JButton applyButton = null;
	
	/**
	 * 
	 * @param frame
	 */
	public TimeshiftDialog(JFrame frame) {
        super(frame);
        setup();       		
	}

	/**
	 * 
	 * @param frame
	 * @param msg
	 */
	public TimeshiftDialog(JFrame frame, MessageCenter msg) {
        super(frame, msg);
        setup();       		
	}
	        
	@Override
	public void begin() {		
		setGpxObject();
		setVisible(true);
		
	}

	/**
	 * to be called before destruction
	 */
	public void dispose() {
		GpsMaster.active.removePropertyChangeListener(changeListener);
    	GpsMaster.active.repaintMap();
		super.dispose();
	}

	/**
	 * 
	 */
	private void setGpxObject() {
		System.out.println("setGpxObject " + GpsMaster.active.getGroups().size());
		for (GenericAlgorithm algo : algorithms) {
			algo.clear();
			algo.setWaypointGroups(GpsMaster.active.getGroups());			
		}	
	}

	/**
	 * 
	 */
	private void apply() {
		// TODO set busy cursor
		applyButton.setEnabled(false);
		selected.apply();
		GpsMaster.active.addUndoOperation(selected);		
		GpsMaster.active.refresh();
		GpsMaster.active.repaintMap();
		
		makeAlgoList(); // TODO re-instantiate selected only
		applyButton.setEnabled(true);
	}
	
	/**
	 * Each IUndoable class that has been put on the UndoStack
	 * needs to be re-initialized to prevent modification
	 * of members by successive calls of apply()
	 * 
	 */
	private void makeAlgoList() {

		algorithms.clear();
		algorithms.add(new ShiftTime());
		algorithms.add(new LocalToUTC());
		algorithms.add(new ClearTimestamps());
		algorithms.add(new Reverse());
		
	}
	
	/**
	 * setup radio buttons, action buttons etc.
	 */
	private void setup() {
		
        // set icon image
        setIcon(Const.ICONPATH_TOOLBAR, "timeshift.png");        
        makeAlgoList();
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

		selectionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (TimeshiftAlgorithm algo : algorithms) {
					if (algo.getName().equals(e.getActionCommand())) {						
						JPanel algoPanel = new GenericAlgorithmPanel(algo);
						algoPanel.setBackground(backgroundColor);
						setInfoPanel(algoPanel);
						revalidate();
						repaint();
						selected = algo;
						System.out.println("selected = " + algo.getName());
						return;
					}
				}				
			}
		};
				
		// set up radio buttons
		
		ButtonGroup group = new ButtonGroup();
		
		for (TimeshiftAlgorithm algo : algorithms) {
			JRadioButton rb = new JRadioButton(algo.getName());
			rb.setBackground(backgroundColor);
			rb.addActionListener(selectionListener);
			radioPanel.add(rb);
			group.add(rb);
			if (algorithms.indexOf(algo) == 0) {
				rb.setSelected(true);
			}
		}

		selected = algorithms.get(0); // list may not be empty
		setInfoPanel(new GenericAlgorithmPanel(selected));

		// button msgPanel
		
		applyButton = new JButton("Apply");
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
		
		pack();
		setCenterLocation();		

	}
	
	@Override
	public String getTitle() {
		return "Timeshift";
	}

}
