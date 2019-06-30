package org.gpsmaster.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

/**
 * 
 * @author rfu
 *
 */
public class InteractiveChart extends JFreeChart {

	private boolean isInteractive = true;
	private ChartDataset dataset = null;
	private XYPlot plot = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5060394003487166823L;

	/**
	 * 
	 * @param plot
	 */
	public InteractiveChart(XYPlot plot) {
		super(plot);		
		this.plot = plot;
		setDefaults();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isInteractive() {
		return isInteractive;
	}

	/**
	 * 
	 * @param isInteractive
	 */
	public void setInteractive(boolean isInteractive) {
		this.isInteractive = isInteractive;
	}


	/**
	 * 
	 * @return
	 */
	public ChartDataset getChartDataset() {
		return dataset;
	}

	/**
	 * 
	 * @param dataset
	 */
	public void setChartDataset(ChartDataset dataset) {
		this.dataset = dataset;
		makeChart();
	}
	
	/**
	 * 
	 */
	private void makeChart() {		
		plot.setRangeAxis(dataset.getRangeAxis());
		plot.setDomainAxis(dataset.getDomainAxis());
		plot.setDataset(dataset.getCollection());
	}
	
	private void setDefaults() {
		removeLegend();
	}
	
}
