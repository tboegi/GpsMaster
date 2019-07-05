package org.gpsmaster;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;

import javax.swing.JFrame;

import org.gpsmaster.UnitConverter.UNIT;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openstreetmap.gui.jmapviewer.OsmMercator;


/**
 *
 * A chart for displaying a GPX element's speed profile.
 *
 * @author Matt Hoover
 *
 */
@SuppressWarnings("serial")
public class SpeedChart extends JFrame {

	private UnitConverter uc = null;
    private double maxRawSpeed;

    /**
     * Constructs the {@link SpeedChart} window.
     *
     * @param title             The chart window title.
     * @param headingPrefix     The heading for the graphics on the chart.
     * @param wptGrp            The GPX element being plotted.
     * @param som				The System of Measurement to display the values in
     */
    public SpeedChart(String title, String headingPrefix, WaypointGroup wptGrp, UnitConverter converter) {
        super(title);
        uc = converter;
        maxRawSpeed = 0;
        XYDataset xydataset = createDataset(wptGrp);
        JFreeChart jfreechart = createChart(xydataset, wptGrp, headingPrefix);
        jfreechart.setRenderingHints(
                new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setMaximumDrawHeight(99999);
        chartpanel.setMaximumDrawWidth(99999);
        chartpanel.setMinimumDrawHeight(1);
        chartpanel.setMinimumDrawWidth(1);
        setContentPane(chartpanel);
    }

    /**
     * Creates the dataset to be used on the chart.
     */
    private XYDataset createDataset(WaypointGroup wptGrp) {
        XYSeries xyseries = new XYSeries(wptGrp.getName());
        double lengthMeters = 0; // in meters
        double length = 0;
        double speed = 0;

        Waypoint curr = wptGrp.getStart();
        Waypoint prev;
        for (Waypoint wpt : wptGrp.getWaypoints()) {
            prev = curr;
            curr = wpt;
            double incrementMeters = OsmMercator.getDistance(curr.getLat(), curr.getLon(), prev.getLat(), prev.getLon());
            double incrementMillis = curr.getTime().getTime() - prev.getTime().getTime();
            double incrementHours = incrementMillis / 3600000D;
            // TODO mph bug here (xAxis)
            if (!Double.isNaN(incrementMeters) && !Double.isNaN(incrementMillis) && incrementHours > 0) {
            	lengthMeters += incrementMeters;
            	length = uc.dist(lengthMeters, UNIT.KM);
            	double incrementKm = uc.dist(incrementMeters, UNIT.KM);
            	speed = uc.speed(incrementKm / incrementHours, UNIT.KMPH);
            	xyseries.add(length, speed);
                maxRawSpeed = Math.max(speed, maxRawSpeed);
            }
        }
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        xyseriescollection.addSeries(xyseries);
        xyseriescollection.setIntervalWidth(0.0D);
        return xyseriescollection;
    }

    /**
     * Creates the chart to be used in the window parentFrame.
     */
    private JFreeChart createChart(XYDataset xydataset, WaypointGroup wptGrp, String headingPrefix) {

        String xAxis = "Distance ("+uc.getUnit(UNIT.KM)+")";
        String yAxis = "Speed ("+uc.getUnit(UNIT.KMPH)+")";
        double length = uc.dist(wptGrp.getLengthMeters(), UNIT.KM);

    	JFreeChart jfreechart = null;
        jfreechart = ChartFactory.createXYLineChart(
            headingPrefix + " - " + wptGrp.getName(), xAxis, yAxis,
            xydataset, PlotOrientation.VERTICAL, false, false, false);

        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.getRenderer().setSeriesPaint(0, new Color(255, 0, 0));
        xyplot.setForegroundAlpha(0.65F);
        xyplot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));

        ValueAxis domainAxis = xyplot.getDomainAxis();
        domainAxis.setRange(0, length);

        double padding = maxRawSpeed / 10D;
        double rangeMax = maxRawSpeed + padding;
        ValueAxis rangeAxis = xyplot.getRangeAxis();
        rangeAxis.setRange(0, rangeMax);

        domainAxis.setTickMarkPaint(Color.black);
        domainAxis.setLowerMargin(0.0D);
        domainAxis.setUpperMargin(0.0D);
        rangeAxis.setTickMarkPaint(Color.black);
        return jfreechart;
    }
}
