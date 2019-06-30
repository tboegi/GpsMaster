package org.gpsmaster.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.Waypoint;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

import eu.fuegenstein.swing.JButtonlessComboBox;

/**
 * 
 * @author rfu
 *
 */
public class FloatableChartPanel extends ChartPanel {

	private SpringLayout springLayout = new SpringLayout();
	private JLabel floatLabel = new JLabel();
	private JLabel titleLabel = new JLabel("Interactive Chart");
	private String iconPath = "/org/gpsmaster/icons/chart/";
	private ChartMouseListener mouseListener = null;
	private PropertyChangeListener changeListener = null;

	private JButtonlessComboBox<ChartAxis> xCombo = null; // combo box for X-Axis datatype selection
	private JButtonlessComboBox<ChartAxis> yCombo = null; // combo box for Y-Axis datatype selection
	
	private ValueMarker marker = null;
	private boolean interactive = false;
	
	public final static int MODE_NONE = 0;		// don't show any tear action icon
	public final static int MODE_FLOAT = 1;		// show "tear off" icon
	public final static int MODE_ATTACH = 2;	// show "glue back" icon

	private XYPlot plot = null; // shortcut
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4115882625036746218L;

	/**
	 * Standard constructor
	 * @param chart
	 */
	public FloatableChartPanel(JFreeChart chart) {
		super(chart);
		setLayout(springLayout);
		init();
		setDefaults();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getChartTitle() {
		return titleLabel.getText();
	}

	public boolean isInteractive() {
		return interactive;
	}

	/**
	 * 
	 * @param interactive
	 */
	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
		if (interactive) {
			interactiveOn();
		} else {
			interactiveOff();
		}
	}

	/**
	 * 
	 * @param chartTitle
	 */
	public void setChartTitle(String chartTitle) {
		if (chartTitle == null) {
			titleLabel.setVisible(false);
		} else {
			titleLabel.setText(chartTitle);
			titleLabel.setVisible(true);
		}		
	}

	/**
	 * Get the GUI Component used to set the float mode 
	 * @return
	 */
	public JLabel getFloatComponent() {
		return floatLabel;
	}

	/**
	 * 
	 * @param mode
	 */
	public void setFloatMode(int mode) {
	
		switch(mode) {
		case MODE_NONE:
			floatLabel.setVisible(false);
			break;
		case MODE_FLOAT:
			floatLabel.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("tearoff.png"))));
			floatLabel.setToolTipText("Open chart in new window");
			floatLabel.setVisible(true);
			break;
		case MODE_ATTACH:
			floatLabel.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("glueback.png"))));
			floatLabel.setToolTipText("Attach chart to map msgPanel");
			floatLabel.setVisible(true);
			break;
		default:
			throw new IllegalArgumentException("unsupported tear action");
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public JButtonlessComboBox<ChartAxis> getxCombo() {
		return xCombo;
	}

	/**
	 * 
	 * @return
	 */
	public JButtonlessComboBox<ChartAxis> getyCombo() {
		return yCombo;
	}

	/**
	 * Set defaults suitable for GpsMaster charts
	 */
	private void setDefaults() {
		setMaximumDrawHeight(99999);
		setMaximumDrawWidth(99999);
		setMinimumDrawHeight(1);
		setMinimumDrawWidth(1);
	}
	
	/**
	 * 
	 */
	private void init() {
		
		// Set up UI components
		springLayout.putConstraint(SpringLayout.NORTH, floatLabel, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, floatLabel, 0, SpringLayout.EAST, this);
		floatLabel.setVisible(false);
		add(floatLabel);
		
		// title
		springLayout.putConstraint(SpringLayout.NORTH, titleLabel, 5, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, titleLabel, 0, SpringLayout.HORIZONTAL_CENTER, this);
		
		Font titleFont = new Font(getFont().getFontName(), Font.BOLD, getFont().getSize() + 2);
		titleLabel.setFont(titleFont);
		titleLabel.setVisible(false);		
		titleLabel.setOpaque(true);
		titleLabel.setBackground(Color.WHITE);		
		add(titleLabel);
		
		// X-Axis datatype selector
		xCombo = new JButtonlessComboBox<ChartAxis>();
		springLayout.putConstraint(SpringLayout.SOUTH, xCombo, -50, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, xCombo, 10, SpringLayout.EAST, this);
		add(xCombo);

		// Y-Axis datatype selector
		yCombo = new JButtonlessComboBox<ChartAxis>();
		springLayout.putConstraint(SpringLayout.NORTH, yCombo, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, yCombo, 50, SpringLayout.WEST, this);
		add(yCombo);

		plot = (XYPlot) getChart().getPlot(); // shortcut
		
	}
	
	/**
	 * initialise all objects required for interactiveness
	 * 
	 */
	private void interactiveOn() {
		if (marker == null) {
			marker = new ValueMarker(0);
			marker.setPaint(Color.BLACK);			
			plot.addDomainMarker(marker);
		}

		if (mouseListener == null) {
			mouseListener = new ChartMouseListener() {
				
				@Override
				public void chartMouseMoved(ChartMouseEvent e) {
					if (interactive) {
						highlightWaypoint(e);
					}
				}
				
				@Override
				public void chartMouseClicked(ChartMouseEvent event) {
					// ignored					
				}
			};				
			addChartMouseListener(mouseListener);
		}
		
		/**
		 * this handler is called from the outside when a waypoint is
		 * highlighted on the map msgPanel. used to highlight the corresponding
		 * value on the chart.
		 */
		if (changeListener == null) {
			changeListener = new PropertyChangeListener() {				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(Const.PCE_ACTIVE_TRKPT)) {
						highlightDomain(evt);
					}					
				}
			};
			GpsMaster.active.addPropertyChangeListener(changeListener);
		}
	}
	
	/**
	 * 
	 */
	private void interactiveOff() {
		if (mouseListener != null) {
			removeChartMouseListener(mouseListener);
			mouseListener = null;
		}
		if (marker != null) {
			plot.removeDomainMarker(marker);
			marker = null;
		}
		if (changeListener != null) {
			GpsMaster.active.removePropertyChangeListener(changeListener);
		}
	}
	
	/**
	 * Find the waypoint that is related to the X-Axis value under the mouse
	 * and send a notification to highlight it on the map msgPanel
	 * TODO this should actually be in the {@link InteractiveChart}, not here in the msgPanel
	 * @param xValue
	 */
	private void highlightWaypoint(ChartMouseEvent e) {
		InteractiveChart chart = (InteractiveChart) getChart();
		ValueAxis xAxis = plot.getDomainAxis();
		Rectangle2D dataArea = getScreenDataArea();
		double xValue = xAxis.java2DToValue(e.getTrigger().getX(), dataArea, 
                RectangleEdge.BOTTOM);
		marker.setValue(xValue);
		Waypoint wpt = chart.getChartDataset().getWaypointForX(xValue);
        if (wpt != null) {
        	GpsMaster.active.setTrackpoint(wpt, false); 
        	// do not autoSetGroup for performance reasons (also, it's not required yet)
        }
	}
	
	/**
	 * Set a marker on the domain axis that corresponds to a waypoint
	 * @param e event containing {@link Waypoint}
	 */
	private void highlightDomain(PropertyChangeEvent e) {
		Waypoint wpt = GpsMaster.active.getTrackpoint();
		if (wpt != null) {
			InteractiveChart chart = (InteractiveChart) getChart();
			double xValue =  chart.getChartDataset().lookupXValue(wpt);
			if (xValue != Double.NaN) {
				marker.setValue(xValue);
			}		
		} else {
			// TODO hide marker
		}
	}
}
