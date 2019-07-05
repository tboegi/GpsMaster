package org.gpsmaster;

import java.awt.Color;
import java.awt.RenderingHints;

import javax.swing.JFrame;

import org.gpsmaster.UnitConverter.UNIT;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 *
 * A chart for displaying a GPX element's elevation profile.
 *
 * @author Matt Hoover
 *
 */
@SuppressWarnings("serial")
public class ElevationChart extends JFrame {

	private UnitConverter uc = null;
	String distUnit = "?";

    /**
     * Constructs the {@link ElevationChart} window.
     *
     * @param label             The chart window label.
     * @param headingPrefix     The heading for the graphics on the chart.
     * @param wptGrp            The GPX element being plotted.
     * @param som				The System of Measurement to display the values in
     */
    public ElevationChart(String title, String headingPrefix, WaypointGroup wptGrp, UnitConverter converter) {
        super(title);
        uc = converter;
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
        double lengthMeters = 0;
        Waypoint curr = wptGrp.getStart();
        Waypoint prev;
        for (Waypoint wpt : wptGrp.getWaypoints()) {
            prev = curr;
            curr = wpt;
            double increment = curr.getDistance(prev);
            //double increment = OsmMercator.getDistance(curr.getLat(), curr.getLon(), prev.getLat(), prev.getLon());
            if (!Double.isNaN(increment)) {
                lengthMeters += increment;
            }
            xyseries.add(uc.dist(lengthMeters, UNIT.KM), uc.dist(curr.getEle(), UNIT.M));
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

    	double eleMin = 0; double eleMax = 0;
    	JFreeChart jfreechart = null;

        eleMin = uc.dist(wptGrp.getEleMinMeters(), UNIT.M);
        eleMax = uc.dist(wptGrp.getEleMaxMeters(), UNIT.M);
        String yAxis = "Elevation ("+uc.getUnit(UNIT.M)+")";
        String xAxis = "Distance ("+uc.getUnit(UNIT.KM)+")";

        if (wptGrp.getWptGrpType() == WptGrpType.WAYPOINTS) {
            jfreechart = ChartFactory.createScatterPlot(
                    headingPrefix + " - " + wptGrp.getName(), xAxis, yAxis,
                    xydataset, PlotOrientation.VERTICAL, false, false, false);
        } else {
            jfreechart = ChartFactory.createXYAreaChart(
                headingPrefix + " - " + wptGrp.getName(), xAxis, yAxis,
                xydataset, PlotOrientation.VERTICAL, false, false, false);
        }

        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.getRenderer().setSeriesPaint(0, new Color(38, 128, 224));
        xyplot.setForegroundAlpha(0.65F);

        ValueAxis domainAxis = xyplot.getDomainAxis();
    	domainAxis.setRange(0, uc.dist(wptGrp.getLengthMeters(), UNIT.KM));

        double eleChange = eleMax - eleMin;
        double padding = eleChange / 10D;
        double rangeMin = eleMin - padding;
        if (eleMin >= 0 & rangeMin < 0) {
            rangeMin = 0;
        }
        double rangeMax = eleMax + padding;
        ValueAxis rangeAxis = xyplot.getRangeAxis();
        rangeAxis.setRange(rangeMin, rangeMax);

        domainAxis.setTickMarkPaint(Color.black);
        domainAxis.setLowerMargin(0.0D);
        domainAxis.setUpperMargin(0.0D);
        rangeAxis.setTickMarkPaint(Color.black);
        return jfreechart;
    }
}
