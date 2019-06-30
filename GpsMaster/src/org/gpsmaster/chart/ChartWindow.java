package org.gpsmaster.chart;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.gpsmaster.GpsMaster;

public class ChartWindow extends JFrame {

	private String iconPath = "/org/gpsmaster/icons/dialogs/";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8464782608480799141L;

	/**
	 * Default constructor
	 */
	public ChartWindow(JFrame parentFrame) {			
		setLocation(100, 0);
		setSize((int) (parentFrame.getWidth() * 0.75f), parentFrame.getHeight()); // TODO improve
		setLocationRelativeTo(parentFrame);	
		setIconImage(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("chart.png"))).getImage());	
	}
	
	/**
	 * 
	 * @param chartPanel
	 */
	public void setChartPanel(FloatableChartPanel chartPanel) {
		getContentPane().add(chartPanel);
		setTitle(chartPanel.getChartTitle());
	}
		
}
