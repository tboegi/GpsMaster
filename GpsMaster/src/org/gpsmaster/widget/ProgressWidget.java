package org.gpsmaster.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import eu.fuegenstein.swing.Widget;
import eu.fuegenstein.swing.WidgetLayout;



/**
 * Class representing a generic progress bar widget
 * 
 * @author rfu
 *
 */
public class ProgressWidget extends Widget {

	JPanel titlePane = new JPanel(new BorderLayout());
	JLabel label = new JLabel();
	Dimension barDimension = new Dimension(360, 20);
	
	private List<JProgressBar> progressBars = new ArrayList<JProgressBar>();		
	private int barCount = 1;
	
	private static final long serialVersionUID = 5518506687851519071L;
	
	/**
	 * Default Constructor
	 */
	
	public ProgressWidget() {
		super();
		corner = WidgetLayout.TOP_LEFT;
		setup();
		setupBars();
		setupLayout();		
	}
	
	/**
	 * Constructor
	 * @param numBars number of progress bars to show
	 */
	public ProgressWidget(int numBars) {		
		super();
		barCount = numBars;
		corner = WidgetLayout.TOP_LEFT;
		setup();
		setupBars();
		setupLayout();		
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return label.getText();
	}

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		label.setText(title);
	}

	/**
	 * Sets the minimum value of a progress bar
	 * @param bar number of bar to set minimum value. 0-based index.
	 * @param minimum minimum value to set
	 * 
	 */
	public void setMinimum(int bar, int minimum) {
		checkNumber(bar);
		progressBars.get(bar).setMinimum(minimum);
	}
	
	/**
	 * 
	 * @param bar 
	 * @return minimum value of the specified bar
	 */
	public int getMinimum(int bar) {
		checkNumber(bar);
		return progressBars.get(bar).getMinimum();
	}
	
	/**
	 * Sets the maximum value of a progress bar
	 * @param bar number of bar to set maximum value. 0-based index.
	 * @param maximum maximum value to set
	 * 
	 */
	public void setMaximum(int bar, int maximum) {
		checkNumber(bar);
		progressBars.get(bar).setMinimum(maximum);
	}
	
	/**
	 * 
	 * @param bar 
	 * @return maximum value of the specified bar
	 */
	public int getMaximum(int bar) {
		checkNumber(bar);
		return progressBars.get(bar).getMaximum();
	}

	
	/**
	 * Sets the current value of a progress bar
	 * @param bar number of bar to set value. 0-based index.
	 * @param value value to set
	 * 
	 */
	public void setValue(int bar, int value) {
		checkNumber(bar);
		progressBars.get(bar).setValue(value);
	}
	
	/**
	 * 
	 * @param bar 
	 * @return value of the specified bar
	 */
	public int getValue(int bar) {
		checkNumber(bar);
		return progressBars.get(bar).getValue();
	}

	/**
	 * Shortcut if there is only one bar
	 * @param minimum
	 */
	public void setMinimum(int minimum) {
		progressBars.get(0).setMinimum(minimum);
	}
	
	/**
	 * 
	 * @return minimum value
	 */
	public int getMinimum() {
		return progressBars.get(0).getMinimum();
	}


	/**
	 * Shortcut if there is only one bar
	 * @param minimum
	 */
	public void setMaximum(int maximum) {
		progressBars.get(0).setMaximum(maximum);
	}
	
	/**
	 * 
	 * @return minimum value
	 */
	public int getMaximum() {
		return progressBars.get(0).getMaximum();
	}


	public void setValue(int value) {
		progressBars.get(0).setValue(value);
	}
	
	/**
	 * 
	 * @param bar 
	 * @return value of the specified bar
	 */
	public int getValue() {
		return progressBars.get(0).getValue();
	}

	/**
	 * Set the number of progress bars to display
	 * @param count
	 */
	public void setBarCount(int count) {
		if (count < 1) {
			throw new IllegalArgumentException();
		}
		barCount = count;
		clearBars();
		setupBars();
		setupLayout();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getBarCount() {
		return barCount;
	}
	
	/**
	 * 
	 */
	private void setupBars() {
		progressBars.clear();
		for (int i = 0; i < barCount; i++) {
			JProgressBar progressBar = new JProgressBar();
			progressBar.setMinimum(0);
			progressBar.setValue(0);
			// progressBar.setMinimumSize(barDimension);
			progressBar.setPreferredSize(barDimension);
			progressBar.setStringPainted(false);
			progressBar.setBackground(transparentWhite);
			progressBar.setForeground(Color.BLUE);
			progressBar.setBorder(new EmptyBorder(2, 6, 2, 6));
			progressBar.setVisible(true);
			progressBars.add(progressBar);
			
			add(progressBar);
		}
		add(Box.createRigidArea(new Dimension(0,5))); // does not work
	}

	/**
	 * 
	 */
	private void clearBars() {
		for (JProgressBar bar : progressBars) {
			remove(bar);
		}
	}

	/**
	 * 
	 */
	private void setup() {
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
		titlePane.add(label, BorderLayout.LINE_START);
		JLabel cancel = new JLabel();
		cancel.setIcon(new ImageIcon(this.getClass().getResource("/org/gpsmaster/icons/cancel.png")));
		cancel.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	// TODO durchdenken. wie cancel nach aussen schicken?
            }
		});
		titlePane.add(cancel, BorderLayout.LINE_END);
		titlePane.setBackground(transparentWhite);
		titlePane.setBorder(new EmptyBorder(6,6,2,6));
		add(titlePane);
		
	}
	/**
	 * 
	 */
	private void setupLayout() {
		setMaximumSize(getPreferredSize());
	}
	
	private void checkNumber(int bar) {
		if ((bar + 1) > progressBars.size()) {
			throw new IndexOutOfBoundsException();
		}
	}
	
	// TBI
}
