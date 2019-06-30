package org.gpsmaster.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JSplitPane;

import org.gpsmaster.Core;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.UnitConverter;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import eu.fuegenstein.swing.JButtonlessComboBox;

/**
 * Main class handling charts & interactions
 * 
 * @author rfu
 *
 */
public class ChartHandler {

	private UnitConverter uc = null;
	private JSplitPane parentPane = null;
	private double dividerLocation = 0.85f;
	private ChartXAxis xAxis = null; // new DistanceAxis();
	private ChartYAxis yAxis = null; // new ElevationAxis();
	private JButtonlessComboBox<ChartAxis> xCombo = null;
	private JButtonlessComboBox<ChartAxis> yCombo = null;

	private FloatableChartPanel chartPanel = null;
	private InteractiveChart chart = null;
	private ChartDataset dataset = new ChartDataset();
	private XYPlot plot = null;

	private ChartWindow chartWindow = null;
	private PropertyChangeListener changeListener = null; // receive notifications about active GPX object
	private MouseAdapter tearListener = null; // tear off / glue back changeListener
	
	/**
	 * Default constructor
	 * @param uc
	 */
	public ChartHandler(UnitConverter uc) {
		super();
		this.uc = uc;				
		makeListeners();		
		init();
		refreshData();
	}
	

	/**
	 * 
	 * @return
	 */
	public ChartPanel getChartPanel() {
		return chartPanel;
	}
	
	/**
	 * set the {@link JSplitPane} the chartPanel resides in 
	 * @param splitPane
	 */
	public void setParentPane(JSplitPane splitPane, double dividerLocation) {
		
		if ((splitPane == null) && (parentPane != null)) {
			// remove chart from current parentPane
			parentPane.setBottomComponent(null);
		} else if (splitPane != null) {
			splitPane.setBottomComponent(chartPanel);
			splitPane.setDividerLocation(dividerLocation);
			this.dividerLocation = dividerLocation;
		}
		parentPane = splitPane;
	}
	
	/**
	 * 
	 * @return
	 */
	public ChartWindow getChartWindow() {
		return chartWindow;
	}

	/**
	 * Setting a {@link ChartWindow} allows the user to "tear out" the chart
	 * from below the map panel and display it in its own window.
	 * @param chartWindow Window to display chart. if {@link null}, "tearing out" is not available.
	 */
	public void setChartWindow(ChartWindow chartWindow) {
		this.chartWindow = chartWindow;
		if (chartWindow != null) {
			chartPanel.setFloatMode(FloatableChartPanel.MODE_FLOAT);
		} else {
			chartPanel.setFloatMode(FloatableChartPanel.MODE_NONE);
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isInteractive() {
		return chartPanel.isInteractive();
	}
	
	/**
	 * 
	 * @param interactive
	 */
	public void setInteractive(boolean interactive) {
		chartPanel.setInteractive(interactive);
	}
	
	/**
	 * 
	 * @param gpx
	 */
	public void setActiveGpxObject(GPXObject gpx) {
		dataset.clear();
		dataset.addWaypointGroups(GpsMaster.active.getGroups(Core.SEG_ROUTE_TRACK));
		refreshData();
	}
		
	public ChartXAxis getXAxis() {
		return xAxis;
	}
	
	public void setXAxis(ChartXAxis axis) {
		this.xAxis = axis;
	}

	public ChartYAxis getYAxis() {
		return yAxis;
	}

	public void setYAxis(ChartYAxis axis) {
		XYItemRenderer renderer = (XYItemRenderer) axis.getPreferredRenderer();
		if (renderer == null) {
			renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
	        plot.setRenderer(renderer);
		}
		this.yAxis = axis;
	}
	
	/**
	 * Handle mouse click to tear off / glue back the chart
	 * 
	 * @param e
	 */
	private void handleTear(MouseEvent e) {
		
		if (chartWindow != null) {
			if (chartWindow.isVisible() == false) {
				// move chart from map panel to chart window
				panelToFrame();
			} else {
				// move chart back from chart window to map panel
				frameToPanel();
			}
		}
	}

	/**
	 * Move the chart from map panel to a new frame (window)
	 */
	private void panelToFrame() {
		if (chartWindow != null) {
			chartPanel.setFloatMode(FloatableChartPanel.MODE_ATTACH);
			chartWindow.setSize(new Dimension(600, 400));
			chartWindow.setChartPanel(chartPanel);
			chartWindow.setVisible(true);
		}
	}

	/**
	 * 
	 */
	private void frameToPanel() {
		if (chartWindow != null) {
			chartPanel.setFloatMode(FloatableChartPanel.MODE_FLOAT);
			chartWindow.remove(chartPanel);
			chartWindow.setVisible(false);
			parentPane.setBottomComponent(chartPanel);
			parentPane.setDividerLocation(dividerLocation);
		}
	}

	/**
	 * instantiate required listeners
	 */
	private void makeListeners() {
        changeListener = new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				String command = e.getPropertyName();
				if (command.equals(GpsMaster.active.PCE_ACTIVEGPX)) {
					setActiveGpxObject(GpsMaster.active.getGpxObject());					
				} else if (command.equals(GpsMaster.active.PCE_REFRESHGPX)) {
					refreshData();
				}
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);
		
		tearListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    handleTear(e);
                }
            }
		};
	
	}

	
	/**
	 * Initialize chart & related objects
	 */
	private void init() {
		
		// initial axes
		xAxis = new DistanceAxis(uc);
		yAxis = new ElevationAxis(uc);

		plot = new XYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setForegroundAlpha(0.75f);
		
		XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
        plot.setRenderer(renderer);
		
		chart = new InteractiveChart(plot);
		GpsMasterChartTheme theme = new GpsMasterChartTheme("GpsMaster");
		theme.apply(chart);		
		
		chartPanel = new FloatableChartPanel(chart);
		chartPanel.setChart(chart);		
		chartPanel.getFloatComponent().addMouseListener(tearListener);
		chartPanel.setFloatMode(FloatableChartPanel.MODE_NONE);
		Color transparentYellow = new Color(255, 255, 2, 80);
		
		// setup X-Axis datatype selector
		xCombo = chartPanel.getxCombo();
		xCombo.setRenderer(new ComboAxisRenderer());
		xCombo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				handleComboSelection(xCombo, "X");				
			}
		});
		xCombo.setToolTipText("If anyone knows how to make this transparent, please let me know");
		// xCombo.setToolTipText("Select data for this axis");
		// xCombo.setOpaque(true);
		// xCombo.setBackground(transparentYellow);
		xCombo.setFocusable(false);
		xCombo.setBorder(null);
		xCombo.addItem(new DistanceAxis(uc));
		xCombo.addItem(new TimeAxis(uc));	
		xCombo.addItem(new DurationAxis(uc));

		// setup Y-Axis datatype selector
		yCombo = chartPanel.getyCombo();
		yCombo.setRenderer(new ComboAxisRenderer());
		yCombo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				handleComboSelection(yCombo, "Y");				
			}
		});
		yCombo.setToolTipText("Select data for this axis");
		// yCombo.setOpaque(false);
		yCombo.setFocusable(false);
		yCombo.setBorder(null);
		yCombo.addItem(new ElevationAxis(uc));
		yCombo.addItem(new SpeedAxis(uc));				
		
	}
	
	/**
	 * Handles the selection of a data type for an axis
	 * @param combo {@link JButtonlessComboBox} that triggered the event
	 * @param axis "X" for X-Axis, "Y" for Y-Axis
	 */
	protected void handleComboSelection(JComboBox<ChartAxis> combo, String axis) {
		if (axis.equals("X")) {
			xAxis = (ChartXAxis) combo.getSelectedItem();
			refreshData();
		}
		if (axis.equals("Y")) {
			yAxis = (ChartYAxis) combo.getSelectedItem();
			refreshData();
		}
	}


	/**
	 * 
	 */
	private void refreshData() {

		yAxis.setPadding(0.10f); // 10% padding
		xAxis.reset();
		dataset.setXAxis(xAxis);
		yAxis.reset();
		dataset.setYAxis(yAxis);
		
		chart.setChartDataset(dataset);
		String chartTitle = yAxis.getTitle().concat(" Chart");
		chartPanel.setChartTitle(chartTitle);
		// set colors
		// TODO receive notification when GPXObject.color changes
		// TODO move this to one of the classes
		for (WaypointGroup group : dataset.getWaypointGroups()) {
			int idx = dataset.getWaypointGroups().indexOf(group);
			plot.getRenderer().setSeriesPaint(idx, group.getColor()); 
		}
	}	
}
