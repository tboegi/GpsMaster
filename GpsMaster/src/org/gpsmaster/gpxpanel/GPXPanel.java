package org.gpsmaster.gpxpanel;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.ProgressType;
import org.gpsmaster.UnitConverter;
import org.gpsmaster.UnitConverter.UNIT;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import eu.fuegenstein.messagecenter.MessageCenter;


// import org.gpsmaster.ClickableMarker;

/**
 *
 * An extension of {@link JMapViewer} to include the display of GPX elements and related functionality.
 *
 * @author Matt Hoover
 *
 */
@SuppressWarnings("serial")
public class GPXPanel extends JMapViewer {

    private List<GPXFile> gpxFiles;

    private Image imgPathStart;
    private Image imgWayPt;
    private Image imgMarkerPt;
    private Image imgPathEnd;
    private Image imgCrosshair;
    private double crosshairLat;
    private double crosshairLon;
    private boolean showCrosshair;
    private boolean paintBorder = false;
    private Point shownPoint;
    private Color activeColor;

    private UnitConverter uc = null;
    private MessageCenter msg = null;
    private ReentrantLock gpxFilesLock = new ReentrantLock(); // lock for central List<GPXFile>
    private ProgressType progressType = ProgressType.NONE;
    private Hashtable<Integer, Double> labelDistance = new Hashtable<Integer, Double>();
    private List<Waypoint> markerPoints;

    private Polygon arrowHead = new Polygon();

    private final long lockTimeout = 5;
    /**
     * Constructs a new {@link GPXPanel} instance.
     */
    public GPXPanel(UnitConverter converter, MessageCenter msg) {
        super(new MemoryTileCache(), 8);
        this.setTileSource(new OsmTileSource.Mapnik());
        DefaultMapController mapController = new DefaultMapController(this);
        mapController.setDoubleClickZoomEnabled(false);
        mapController.setMovementEnabled(true);
        mapController.setWheelZoomEnabled(true);
        mapController.setMovementMouseButton(MouseEvent.BUTTON1);
        this.setScrollWrapEnabled(false); // TODO make everything work with wrapping?
        this.setZoomButtonStyle(ZOOM_BUTTON_STYLE.VERTICAL);
        this.msg = msg;
        gpxFiles = new ArrayList<GPXFile>();

        uc = converter;

        imgPathStart = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/path-start.png")).getImage();
        imgPathEnd = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/path-end.png")).getImage();
        imgMarkerPt = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/markerpoint.png")).getImage();
        imgWayPt = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/waypoint.png")).getImage();
        imgCrosshair = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/crosshair-map.png")).getImage();

        // markers = new Hashtable<Waypoint, ClickableMarker>();
        markerPoints = new ArrayList<Waypoint>();

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

        arrowHead.addPoint(0, 5);
        arrowHead.addPoint(-5,  -5);
        arrowHead.addPoint(5, -5);

    }

    public List<GPXFile> getGPXFiles() {
        return gpxFiles;
    }

    public void setCrosshairLat(double crosshairLat) {
        this.crosshairLat = crosshairLat;
    }

    public void setCrosshairLon(double crosshairLon) {
        this.crosshairLon = crosshairLon;
    }

    public void setShowCrosshair(boolean showCrosshair) {
        this.showCrosshair = showCrosshair;
    }

    public Point getShownPoint() {
        return shownPoint;
    }

    public void setShownPoint(Point shownPoint) {
        this.shownPoint = shownPoint;
    }

    public void setProgressLabels(ProgressType show) {
        progressType = show;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
    }

    public List<Waypoint> getMarkerPoints() {
    	return markerPoints;
    }

	public ReentrantLock getGpxFilesLock() {
		return gpxFilesLock;
	}

	public void setGpxFilesLock(ReentrantLock lock) {
		this.gpxFilesLock = lock;
	}


    /**
     * Adds the chosen {@link GPXFile} to the panel.
     * (thread safe)
     */
    public void addGPXFile(GPXFile gpxFile) {
    	try {
			if (gpxFilesLock.tryLock(lockTimeout, TimeUnit.SECONDS)) {
				gpxFiles.add(gpxFile);
			}
		} catch (InterruptedException e) {
			msg.Error("addGPXFile: Unable to acquire lock:", e);
		} finally {
			gpxFilesLock.unlock();
			repaint();
		}
    }

    /**
     * Removes the chosen {@link GPXFile} to the panel.
     * (thread safe)
     */
    public void removeGPXFile(GPXFile gpxFile) {
    	try {
			if (gpxFilesLock.tryLock(lockTimeout, TimeUnit.SECONDS)) {
				gpxFiles.remove(gpxFile);
			}
		} catch (InterruptedException e) {
			msg.Error("removeGPXFile: Unable to acquire lock:", e);
		} finally {
			gpxFilesLock.unlock();
			repaint();
		}
    }


    @Override
    protected  synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        try {
			if (gpxFilesLock.tryLock(lockTimeout, TimeUnit.SECONDS)) {
		        paintFiles(g2d, gpxFiles);
			}
		} catch (InterruptedException e) {
			msg.VolatileError("Paint", e);
		} finally {
			gpxFilesLock.unlock();
		}

        if (markerPoints.size() > 0) {
        	paintMarkerPoints(g2d);
        }
        if (showCrosshair) {
            Point p = null;
            if (crosshairLon > -180) { // hack fix for bug in JMapViewer.getMapPosition
                p = this.getMapPosition(new Coordinate(crosshairLat, crosshairLon), false);
            } else {
                p = this.getMapPosition(new Coordinate(crosshairLat, -180), false);
            }
            int offset = imgCrosshair.getWidth(null) / 2;
            g2d.drawImage(imgCrosshair, p.x - offset, p.y - offset, null);
        }
        if (shownPoint != null) {
            Stroke saveStroke = g2d.getStroke();
            Color saveColor = g2d.getColor();

            // square mark (with transparency)
            g2d.setColor(Color.black);
            g2d.drawRect(shownPoint.x - 9, shownPoint.y - 9, 17, 17);
            g2d.setColor(Color.white);
            g2d.drawRect(shownPoint.x - 8, shownPoint.y - 8, 15, 15);
            g2d.setColor(Color.black);
            g2d.drawRect(shownPoint.x - 7, shownPoint.y - 7, 13, 13);
            int red = activeColor.getRed();
            int green = activeColor.getGreen();
            int blue = activeColor.getBlue();
            AlphaComposite ac = AlphaComposite.SrcOver;
            g2d.setComposite(ac);
            g2d.setColor(new Color(255 - red, 255 - green, 255 - blue, 160));
            g2d.fill(new Rectangle(shownPoint.x - 6, shownPoint.y - 6, 11, 11));

            // X mark
            /*
            g2d.setStroke(new BasicStroke(5.5f));
            g2d.setColor(Color.black);
            g2d.drawLine(shownPoint.x - 8, shownPoint.y - 8, shownPoint.x + 8, shownPoint.y + 8);
            g2d.drawLine(shownPoint.x - 8, shownPoint.y + 8, shownPoint.x + 8, shownPoint.y - 8);
            g2d.setStroke(new BasicStroke(3));
            int red = activeColor.getRed();
            int green = activeColor.getGreen();
            int blue = activeColor.getBlue();
            g2d.setColor(new Color(255 - red, 255 - green, 255 - blue));
            g2d.drawLine(shownPoint.x - 8, shownPoint.y - 8, shownPoint.x + 8, shownPoint.y + 8);
            g2d.drawLine(shownPoint.x - 8, shownPoint.y + 8, shownPoint.x + 8, shownPoint.y - 8);
            */

            g2d.setStroke(saveStroke);
            g2d.setColor(saveColor);
        }
    }

    /**
     * Paints each file.
     */
    private  void paintFiles(Graphics2D g2d, List<GPXFile> files) {
        // TODO implement lock
    	for (GPXFile file: files) {
            if (file.isVisible()) {
                for (Route route : file.getRoutes()) {
                    if (route.isVisible()) {
                        paintPath(g2d, route.getPath());
                    }
                }
                for (Track track : file.getTracks()) {
                    if (track.isVisible()) {
                        for (WaypointGroup path : track.getTracksegs()) {
                            if (path.isVisible()) {
                                paintPath(g2d, path);
                                // paintColoredPath(g2d, path); // RFU
                            }
                            if (progressType != ProgressType.NONE) {
                            	paintProgress(g2d, path);
                            }
                        }
                    }
                }

            	if (file.getWaypointGroup().isVisible())
            	{
            		paintWaypointGroup(g2d, file.getWaypointGroup());
            	}
                if (file.isWptsVisible()) {
                    for (Route route : file.getRoutes()) {
                        if (route.isWptsVisible() && route.isVisible()) {
                            paintPathpointGroup(g2d, route.getPath());
                        }
                    }
                    for (Track track : file.getTracks()) {
                        if (track.isWptsVisible() && track.isVisible()) {
                            for (WaypointGroup wptGrp : track.getTracksegs()) {
                                paintPathpointGroup(g2d, wptGrp);
                            }
                        }
                    }
                }
                for (Route route : file.getRoutes()) {
                    if (route.isVisible()) {
                        paintStartAndEnd(g2d, route.getPath());
                    }
                }
                for (Track track : file.getTracks()) {
                    if (track.isVisible()) {
                        for (WaypointGroup path : track.getTracksegs()) {
                            if (path.isVisible()) {
                                paintStartAndEnd(g2d, path);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * paints a directed arrow parallel to to the line
     * specified by (from, to) at an offset of {@link offset} pixels
     */

    // http://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
    // post <11>
    private void paintArrow(Graphics2D g2d, Point from, Point to) {

    	AffineTransform transform = new AffineTransform();
    	AffineTransform saveTransform = g2d.getTransform();
    	// transform.setToIdentity();
    	double angle = Math.atan2(to.y - from.y, to.x - from.x);
    	transform.translate(to.x, to.y);
    	transform.rotate((angle-Math.PI/2d));

    	g2d.setTransform(transform);
    	g2d.fill(arrowHead);
    	g2d.setTransform(saveTransform);
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
     * paints a parh with colored segments
     * @param g2d
     * @param waypointPath
     */
    private void paintColoredPath(Graphics2D g2d, WaypointGroup waypointPath) {
        Point maxXY = getMapPosition(waypointPath.getMinLat(), waypointPath.getMaxLon(), false);
        Point minXY = getMapPosition(waypointPath.getMaxLat(), waypointPath.getMinLon(), false);
        if (maxXY.x < 0 || maxXY.y < 0 || minXY.x > getWidth() || minXY.y > getHeight()) {
            return; // don't paint paths that are completely off screen
        }

        if (waypointPath.getNumPts() >= 2) {
            g2d.setColor(waypointPath.getColor());
            List<Waypoint> waypoints = waypointPath.getWaypoints();
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GeneralPath path = new GeneralPath();
            Waypoint wpt = waypointPath.getStart();

            Point point = getMapPosition(wpt.getLat(), wpt.getLon(), false);
            path.moveTo(point.x, point.y);
            Point prev = point;
            for (int i = 1; i < waypoints.size(); i++) {
                wpt = waypoints.get(i);
                if (wpt.getSegmentColor() != null) {
                	g2d.draw(path);
                	path.reset();
                	path.moveTo(prev.x, prev.y);
                	g2d.setColor(wpt.getSegmentColor());
                }

                point = getMapPosition(wpt.getLat(), wpt.getLon(), false);
                path.lineTo(point.x, point.y);
                prev = point;
            }
            g2d.draw(path);
        }
    }

    /**
     * Paints a single path contained in a {@link WaypointGroup}.
     */
    private  void paintPath(Graphics2D g2d, WaypointGroup waypointPath) {
        Point maxXY = getMapPosition(waypointPath.getMinLat(), waypointPath.getMaxLon(), false);
        Point minXY = getMapPosition(waypointPath.getMaxLat(), waypointPath.getMinLon(), false);
        if (maxXY.x < 0 || maxXY.y < 0 || minXY.x > getWidth() || minXY.y > getHeight()) {
            return; // don't paint paths that are completely off screen
        }

        g2d.setColor(waypointPath.getColor());
        if (waypointPath.getNumPts() >= 2) {
            List<Waypoint> waypoints = waypointPath.getWaypoints();
            GeneralPath path;
            Waypoint rtept;
            Point point;
            int count = 0;

            Stroke saveStroke = g2d.getStroke();
            Color saveColor = g2d.getColor();

            // draw black border
            g2d.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(Color.BLACK);
            path = new GeneralPath();
            rtept = waypointPath.getStart();
            point = getMapPosition(rtept.getLat(), rtept.getLon(), false);
            path.moveTo(point.x, point.y);
            Point prev = point;
            for (int i = 1; i < waypoints.size(); i++) {
                rtept = waypoints.get(i);
                point = getMapPosition(rtept.getLat(), rtept.getLon(), false);
                path.lineTo(point.x, point.y);
/*
                 if (count > 50) {
                	paintArrow(g2d, prev, point);
                	count = 0;
                }
*/
                prev = point;
                count++;
            }

            // hack to fix zero degree angle join rounds (begin)
/*
            Waypoint w1, w2, w3;
            Point p1, p2, p3;
            double d1, d2;
            w1 = waypoints.get(0);
            w2 = waypoints.get(1);
            p1 = getMapPosition(w1.getLat(), w1.getLon(), false);
            p2 = getMapPosition(w2.getLat(), w2.getLon(), false);
            for (int i = 2; i < waypoints.size(); i++) {
                w3 = waypoints.get(i);
                p3 = getMapPosition(w3.getLat(), w3.getLon(), false);
                d1 = Math.sqrt(Math.pow((p2.x - p3.x), 2) + Math.pow((p2.y - p3.y), 2));
                d2 = Math.sqrt(Math.pow((p1.x - p3.x), 2) + Math.pow((p1.y - p3.y), 2));
                if ((d1 / d2) > 99) {
                    path.moveTo(p2.x, p2.y);
                    path.lineTo(p2.x, p2.y);
                }
                w1 = w2;
                w2 = w3;
                p1 = p2;
                p2 = p3;
            }
*/
            // hack (end)
            if (paintBorder) {
            	g2d.draw(path);
            }

            // draw colored route
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(saveColor);
            g2d.draw(path);
            g2d.setStroke(saveStroke);
        }
    }

    /**
     * Paints the waypoints for a path in {@link WaypointGroup}.
     */
    private  void paintPathpointGroup(Graphics2D g2d, WaypointGroup wptGrp) {
        if (wptGrp.isVisible() && wptGrp.isWptsVisible()) {
            List<Waypoint> wpts = wptGrp.getWaypoints();
            for (Waypoint wpt : wpts) {
                Point point = getMapPosition(wpt.getLat(), wpt.getLon(), false);
                // g2d.drawImage(imgPathPt, point.x - 3, point.y - 3, null);
                g2d.drawOval(point.x-2, point.y-2, 4, 4);

            }
        }
    }

    /**
     * Paints the waypoints in {@link WaypointGroup}.
     */
    private  void paintWaypointGroup(Graphics2D g2d, WaypointGroup wptGrp) {

        if (wptGrp.isVisible() && wptGrp.isWptsVisible()) {
        	g2d.setColor(Color.BLACK);
            for (Waypoint wpt : wptGrp.getWaypoints()) {
                Point point = getMapPosition(wpt.getLat(), wpt.getLon(), false);
                g2d.drawImage(imgWayPt, point.x - 9, point.y - 28, null);

                if (wpt.name.isEmpty() == false) {
                	// TODO draw name as tooltip instead
                	// http://stackoverflow.com/questions/11375250/set-tooltip-text-at-a-particular-location, answer 2
                	g2d.drawString(wpt.name, point.x + 6, point.y);
                }
            }
        }
    }

    /**
     * Paints the start/end markers of a {@link Route} or {@link Track}.
     */
    private  void paintStartAndEnd(Graphics2D g2d, WaypointGroup waypointPath) {
        if (waypointPath.getNumPts() >= 2) {
            Waypoint rteptEnd = waypointPath.getEnd();
            Point end = getMapPosition(rteptEnd.getLat(), rteptEnd.getLon(), false);
            g2d.setColor(Color.BLACK);
            g2d.drawImage(imgPathEnd, end.x - 9, end.y - 28, null);
        }

        if (waypointPath.getNumPts() >= 1) {
            Waypoint rteptStart = waypointPath.getStart();
            Point start = getMapPosition(rteptStart.getLat(), rteptStart.getLon(), false);
            g2d.setColor(Color.BLACK);
            g2d.drawImage(imgPathStart, start.x - 9, start.y - 28, null);
        }
    }

    /**
     * paints all {@link Waypoint} in
     *
     *  @author rfuegen
     */
    private  void paintMarkerPoints(Graphics2D g2d) {

    	  	for (Waypoint wpt : markerPoints) {
            Point point = getMapPosition(wpt.getLat(), wpt.getLon(), false);
            g2d.drawImage(imgMarkerPt, point.x - 9, point.y - 28, null);
    	}
    }

    /**
     * paint progress label
     * @param wpt location of the label
     * @param distance
     */
    private  void paintLabel(Graphics2D g2d, Waypoint wpt, DateTime startTime, double distance, String distFormat) {

			String timeString = "";
			Point point = getMapPosition(wpt.getLat(), wpt.getLon(), false);
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
    private  void paintProgress(Graphics2D g2d, WaypointGroup wptGrp)
    {
    	double distance = 0;
    	double labelDiff = 0;
   	    String distFormat = "%.2f "+uc.getUnit(UNIT.KM);

    	// Date startTime = wptGrp.getStart().getTime();
    	DateTime startTime = new DateTime(wptGrp.getStart().getTime());

    	g2d.setColor(Color.BLACK);
    	Waypoint prev = wptGrp.getStart();

    	double minLabelDist = 500; // do not paint labels within ... meters
    	int zoom = this.getZoom();
    	if (labelDistance.containsKey(zoom)) {
    		minLabelDist = labelDistance.get(zoom);
    	}

    	paintLabel(g2d, wptGrp.getStart(), startTime, distance, distFormat);
    	for (Waypoint curr: wptGrp.getWaypoints() ) {

   			// do not paint a label if distance to last label is less than (x)
   			if (labelDiff >= minLabelDist) {
   			    paintLabel(g2d, curr, startTime, distance, distFormat);
   			    labelDiff = 0;
    		}
    		double increment = curr.getDistance(prev);
    		if (!Double.isNaN(increment)) {
    		    distance += increment;
    		    labelDiff += increment;
    		}
   			prev = curr;
    	}
    	// paint label on endpoint
    	// TODO: don't paint second-to-last waypoint if to close
    	paintLabel(g2d, wptGrp.getEnd(), startTime, distance, distFormat);

    	// TODO label orientation based on track direction
    	// TODO prevent overlapping labels
     }


    /**
     * Centers the {@link GPXObject} and sets zoom for best fit to panel.
     */
    public void fitGPXObjectToPanel(GPXObject gpxObject) {
        int maxZoom = tileController.getTileSource().getMaxZoom();
        int xMin = OsmMercator.LonToX(gpxObject.getMinLon(), maxZoom);
        int xMax = OsmMercator.LonToX(gpxObject.getMaxLon(), maxZoom);
        int yMin = OsmMercator.LatToY(gpxObject.getMaxLat(), maxZoom); // screen y-axis positive is down
        int yMax = OsmMercator.LatToY(gpxObject.getMinLat(), maxZoom); // screen y-axis positive is down

        if (xMin > xMax || yMin > yMax) {
            //setDisplayPositionByLatLon(36, -98, 4); // U! S! A!
        } else {
            int width = Math.max(0, getWidth());
            int height = Math.max(0, getHeight());
            int zoom = maxZoom;
            int x = xMax - xMin;
            int y = yMax - yMin;
            while (x > width || y > height) {
                zoom--;
                x >>= 1;
                y >>= 1;
            }
            x = xMin + (xMax - xMin) / 2;
            y = yMin + (yMax - yMin) / 2;
            int z = 1 << (maxZoom - zoom);
            x /= z;
            y /= z;
            setDisplayPosition(x, y, zoom);
        }
    }


}
