package org.gpsmaster.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.gpsmaster.Const;

import eu.fuegenstein.swing.Widget;
import eu.fuegenstein.swing.WidgetLayout;
import eu.fuegenstein.util.ProgressItem;
import eu.fuegenstein.util.IProgressReporter;

/**
 * Class representing a generic progress bar widget
 * 
 * @author rfu
 *
 */
public class ProgressWidget extends Widget implements IProgressReporter {

	private JPanel titlePanel = new JPanel(new BorderLayout());
	private JLabel title = new JLabel();
	
	private JPanel barPanel = new JPanel();
	
	private JLabel footer = new JLabel();
	private JLabel cancelLabel = new JLabel();
	
	private final Dimension barDimension = new Dimension(360, 20);
	
	private Map<ProgressItem, JProgressBar> progressItems = new HashMap<ProgressItem, JProgressBar>();			

	private boolean isCancelled = false;
	
	private static final long serialVersionUID = 5518506687851519071L;
	
	/**
	 * Default Constructor
	 */	
	public ProgressWidget() {
		super();
		corner = WidgetLayout.TOP_LEFT;
		setupPanels();
	}
		
	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title.getText();
	}

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String text) {
		title.setText(text);
	}

	/**
	 * 
	 */
	public void setFooter(String text) {
		footer.setText(text);
		footer.setVisible(true);
	}
	
	/**
	 * 
	 */
	private void setupPanels() {
		setOpaque(false);
		setLayout(new BorderLayout());
		
		// setup title bar with cancel icon
		titlePanel.add(title, BorderLayout.LINE_START);
		JLabel cancel = new JLabel();
		cancel.setIcon(new ImageIcon(this.getClass().getResource(Const.ICONPATH + "cancel.png")));
		cancel.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	isCancelled = true;
            	cancelLabel.setVisible(true);
            }
		});
		titlePanel.add(cancel, BorderLayout.LINE_END);
		titlePanel.setBackground(BACKGROUNDCOLOR);
		titlePanel.setBorder(new EmptyBorder(6,6,2,6));
		add(titlePanel, BorderLayout.NORTH);
		
		// panel for progress bars		
		barPanel.setLayout(new BoxLayout(barPanel, BoxLayout.Y_AXIS));
		barPanel.setBackground(BACKGROUNDCOLOR);
		add(barPanel, BorderLayout.CENTER);
				
		cancelLabel.setBackground(BACKGROUNDCOLOR);
		cancelLabel.setText("Cancelling ...");
		cancelLabel.setForeground(Color.RED);
		cancelLabel.setVisible(false);
		
		JPanel footerPanel = new JPanel();
		footerPanel.setBackground(BACKGROUNDCOLOR);
		footerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		footerPanel.setBorder(new EmptyBorder(2,6,2,6));
		footerPanel.add(cancelLabel);
		footerPanel.add(footer);
		footer.setVisible(false);
		add(footerPanel, BorderLayout.SOUTH);
				
		// setPreferredSize(new Dimension(400, 400));
	}
	
	@Override
	public void addProgressItem(ProgressItem item) {
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setMaximum(item.getMaxValue());
		progressBar.setMinimum(item.getMinValue());
		progressBar.setValue(item.getValue());
		if (item.getName() != null) {
			progressBar.setToolTipText(item.getName());
		}
		progressBar.setPreferredSize(barDimension);
		progressBar.setStringPainted(false);
		progressBar.setBackground(BACKGROUNDCOLOR);
		progressBar.setForeground(Color.BLUE);
		progressBar.setBorder(new EmptyBorder(2, 6, 2, 6));
		progressBar.setVisible(true);
		progressItems.put(item, progressBar);
		barPanel.add(progressBar);

		revalidate();
		repaint();
	}
	
	@Override
	public void removeProgressItem(ProgressItem item) {
		
		if (progressItems.containsKey(item)) {
			JProgressBar bar = progressItems.get(item);
			barPanel.remove(bar);
			progressItems.remove(item);
		}
		
		revalidate();
		repaint();
	}

	@Override
	public boolean isCancelled() {		
		return isCancelled;
	}

	
	/**
	 * TODO define some callback mechanism instead
	 */
	@Override
	public void update() {
		for(Map.Entry<ProgressItem, JProgressBar> entry : progressItems.entrySet()) {
			ProgressItem item = entry.getKey();
			JProgressBar bar = entry.getValue();
			
			bar.setValue(item.getValue());
			bar.setMaximum(item.getMaxValue());
		}
		
	}

	@Override
	public void clear() {
		isCancelled = false;
		cancelLabel.setVisible(false);
		progressItems.clear();		
	}

	
	// TBI
}
