package org.gpsmaster.gpxpanel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import org.gpsmaster.UnitConverter;
import org.gpsmaster.UnitConverter.UNIT;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Class providing functionality to paint extras along a Track Segment
 * (progress labels, directional arrows etc.)
 *
 * @author rfu
 *
 */
public class LabelPainter {

	private JMapViewer mapViewer = null;
	private UnitConverter uc = null;
    private Hashtable<Integer, Double> labelDistance = new Hashtable<Integer, Double>();
    private Polygon arrowHead = new Polygon();

    private boolean paintArrows = false;
	private ProgressType progressType = ProgressType.NONE;


	/*
	 * Default Constructor
	 */
	public LabelPainter(JMapViewer viewer, UnitConverter converter) {

		mapViewer = viewer;
		uc = converter;

		// TODO replace hashtable with function   labelDistance = f(zoom)
        labelDistance.put(1, new Double(100000));
        labelDistance.put(2, new Double(100000));
        labelDistance.put(3, new Double(100000));
        labelDistance.put(4, new Double(100000));
        labelDistance.put(5, new Double(100000));
        labelDistance.put(6, new Double(80000));
        labelDistance.put(7, new Double(40000));
        labelDistance.put(8, new Double(22000));
        labelDistance.put(9, new Double(12000));
        labelDistance.put(10, new Double(6000));	// OK
        labelDistance.put(11, new Double(3000));	// OK
        labelDistance.put(12, new Double(1500));	// OK
        labelDistance.put(13, new Double(900));		// OK
        labelDistance.put(14, new Double(500));		// OK
        labelDistance.put(15, new Double(350));		// OK
        labelDistance.put(16, new Double(200));		// OK
        labelDistance.put(17, new Double(150));		// OK
        labelDistance.put(18, new Double(100));		// OK
        labelDistance.put(19, new Double(100));		//
        labelDistance.put(20, new Double(50));		// OK
        labelDistance.put(21, new Double(50));		//
        labelDistance.put(22, new Double(20));		// OK
        labelDistance.put(23, new Double(20));		//
        labelDistance.put(24, new Double(10));		// OK
        labelDistance.put(25, new Double(10));		//

        arrowHead.addPoint(0, 8);
        arrowHead.addPoint(-5,  -5);
        arrowHead.addPoint(5, -5);

	}

	/*
	 * Properties
	 */
	public boolean getPaintArrows() {
		return paintArrows;
	}

	public void setPaintArrows(boolean paintArrows) {
		this.paintArrows = paintArrows;
	}

	public ProgressType getProgressType() {
		return progressType;
	}

	public void setProgressType(ProgressType type) {
		progressType = type;
	}

	public Polygon getArrowHead()
	{
		return arrowHead;
	}

	public void setArrowHead(Polygon arrow) {
		arrowHead = arrow;
	}

	/*
	 * Methods
	 */

    /**
     * paints a directed arrow parallel to the line
     * specified by (from, to) at an offset of {@link offset} pixels
     */

	private void paintParallelArrow() {
		/*
    	int offset = 10;

    	BasicStroke stroke = new BasicStroke(1); // width = 1
    	double length = from.distance(to);
    	// double length = 40;

    	Point newFrom = new Point();
    	Point newTo = new Point();

    	newFrom.x = (int) (from.x + offset * (to.y - from.y) / length);
    	newTo.x = (int) (to.x + offset * (to.y - from.y) / length);
    	newFrom.y = (int) (from.y + offset * (from.x - to.x) / length);
    	newTo.y = (int) (to.y + offset  * (from.x - to.x) / length);

    	GeneralPath path = new GeneralPath();
    	g2d.setStroke(stroke);
    	path.moveTo(newFrom.x, newFrom.y);
    	path.lineTo(newTo.x, newTo.y);
    	g2d.draw(path);
    	g2d.drawOval(newTo.x - 2, newTo.y - 2, 4, 4); // TODO draw arrow
        	*/

	}
	/**
	 * Paint a directional arrow directly on track
	 * @param g2d
	 * @param wptFrom
	 * @param wptTo
	 */
    // http://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
    // post <11>
	private void paintTrackArrow(Graphics2D g2d, Color color, Waypoint wptFrom, Waypoint wptTo) {

		Point from = mapViewer.getMapPosition(wptFrom.getLat(), wptFrom.getLon(), false);
		Point to = mapViewer.getMapPosition(wptTo.getLat(), wptTo.getLon(), false);

			AffineTransform saveTransform = g2d.getTransform();
	    	AffineTransform transform = new AffineTransform();
	    	transform.setToIdentity();
	    	double angle = Math.atan2(to.y - from.y, to.x - from.x);
	    	transform.translate(to.x, to.y);
	    	transform.rotate((angle-Math.PI/2d));
	    	g2d.setColor(color);
	    	g2d.transform(transform);
	    	g2d.fill(arrowHead);
	    	g2d.setTransform(saveTransform);
    }

	/**
     * paint progress label
     * @param wpt location of the label
     * @param distance
     */
    private void paintLabel(Graphics2D g2d, Waypoint wpt, DateTime startTime, double distance, String distFormat) {

			String timeString = "";
			Point point = mapViewer.getMapPosition(wpt.getLat(), wpt.getLon(), false);
			switch(progressType) {
			case ABSOLUTE:
					timeString = String.format("%tT", wpt.getTime());
				break;
			case RELATIVE:
				DateTime currTime = new DateTime(wpt.getTime());
				Period period = new Duration(startTime,currTime).toPeriod();
				timeString = String.format("%02d:%02d:%02d",
						period.getHours(), period.getMinutes(), period.getSeconds());
				if (period.getDays() > 0) {
					 timeString = String.format("%dd ", period.getDays()).concat(timeString);
				}
				break;
			default:
				break;
			}

			String distString = String.format(distFormat, uc.dist(distance, UNIT.KM));
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D box = null;
			if (timeString.length() > distString.length()) {
				box = metrics.getStringBounds(timeString, g2d);
			} else {
				box = metrics.getStringBounds(distString, g2d);
			}

			g2d.setColor(new Color(255, 255, 255, 155)); // R,G,B,Opacity
			g2d.fillRoundRect(
					point.x - 3,
					point.y - (int) box.getHeight() - 3,
					(int) box.getWidth()+6,
					(int) (box.getHeight() + 4) * 2 - 1,
					5, 5);

			g2d.setColor(Color.BLACK);
			g2d.drawString(timeString, point.x, point.y - 1);
			g2d.drawString(distString, point.x, point.y + (int) box.getHeight()); // TODO apply SoM
    }


    /**
     * paints distance & elapsed time along path
     * TODO consider start of new day at midnight
     * TODO paint labels for active track/segment only
     * @author rfuegen
     */
    private void doPaint(Graphics2D g2d, WaypointGroup wptGrp)
    {
    	double distance = 0;
    	double labelDist = 0;
    	double arrowDist = 0;
    	double offset = 0;

   	    String distFormat = "%.2f "+uc.getUnit(UNIT.KM);

    	// Date startTime = wptGrp.getStart().getTime();
    	DateTime startTime = new DateTime(wptGrp.getStart().getTime());

    	g2d.setColor(Color.BLACK);
    	Waypoint prev = wptGrp.getStart();

    	double minLabelDist = 500; // do not paint labels within ... meters

    	int zoom = mapViewer.getZoom();
    	if (labelDistance.containsKey(zoom)) {
    		minLabelDist = labelDistance.get(zoom);
    	}
    	double minArrowDist = minLabelDist / 2;

    	offset = minLabelDist / 2; // paint arrows halfway between labels

    	if (progressType != ProgressType.NONE) {
    		// always paint first label
    		paintLabel(g2d, wptGrp.getStart(), startTime, distance, distFormat);
    	}

    	for (Waypoint curr: wptGrp.getWaypoints() ) {

   			// do not paint a label if distance to last label is less than (x)
   			if ((labelDist >= minLabelDist) && (progressType != ProgressType.NONE)) {
   			    paintLabel(g2d, curr, startTime, distance, distFormat);
   			    labelDist = 0;
    		}
   			if ((arrowDist >= minArrowDist) && paintArrows) {
   				// paintTrackArrow(g2d, wptGrp.getColor(), prev, curr);
   				paintTrackArrow(g2d, Color.BLACK, prev, curr);
   				arrowDist = 0;
   				minArrowDist = minLabelDist * 2;
   			}

    		double increment = curr.getDistance(prev);
    		if (!Double.isNaN(increment)) {
    		    distance += increment;
    		    labelDist += increment;
    		    arrowDist += increment;
    		}
   			prev = curr;
    	}
    	if (progressType != ProgressType.NONE) {
    		// paint label on endpoint
    		// TODO: don't paint second-to-last waypoint if to close
    		paintLabel(g2d, wptGrp.getEnd(), startTime, distance, distFormat);
    	}

    	// TODO label orientation based on track direction
    	// TODO prevent overlapping labels
     }

    /**
     * Main entry method
     * @param g2d
     * @param waypointGroup
     */
    public void paint(Graphics2D g2d, WaypointGroup waypointGroup) {
    	if ((progressType != ProgressType.NONE) || paintArrows) {
    		doPaint(g2d, waypointGroup);
    	}
    }
}
