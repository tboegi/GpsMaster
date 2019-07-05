package org.gpsmaster.gpxpanel;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.UnitConverter;
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
    private float trackLineWidth = 3;
    private boolean showCrosshair;
    private boolean paintBorder = true;
    private Point shownPoint;
    private Color activeColor;

    private MessageCenter msg = null;
    private LabelPainter labelPainter = null;
    private ReentrantLock gpxFilesLock = new ReentrantLock(); // lock for central List<GPXFile>
    private List<Waypoint> markerPoints;


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
        labelPainter = new LabelPainter(this,  converter);

        imgPathStart = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/path-start.png")).getImage();
        imgPathEnd = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/path-end.png")).getImage();
        imgMarkerPt = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/markerpoint.png")).getImage();
        imgWayPt = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/waypoint.png")).getImage();
        imgCrosshair = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/crosshair-map.png")).getImage();

        // markers = new Hashtable<Waypoint, ClickableMarker>();
        markerPoints = new ArrayList<Waypoint>();
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


    public float getTrackLineWidth() {
		return trackLineWidth;
	}

	public void setLineWidth(float trackLineWidth) {
		this.trackLineWidth = trackLineWidth;
	}

	public Color getActiveColor() {
        return activeColor;
    }

    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
    }

    // pass-through properties for label painter

    public void setProgressType(ProgressType show) {
        labelPainter.setProgressType(show);
    }

    public ProgressType getProgressType() {
    	return labelPainter.getProgressType();
    }

    public void setArrowType(ArrowType type) {
    	labelPainter.setArrowType(type);
    }

    public ArrowType getArrowType() {
    	return labelPainter.getArrowType();
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
			msg.error("addGPXFile: Unable to acquire lock:", e);
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
			msg.error("removeGPXFile: Unable to acquire lock:", e);
		} finally {
			gpxFilesLock.unlock();
			repaint();
		}
    }


    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        try {
			if (gpxFilesLock.tryLock(lockTimeout, TimeUnit.SECONDS)) {
		        paintFiles(g2d, gpxFiles);
			}
		} catch (InterruptedException e) {
			msg.volatileError("Paint", e);
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
    private void paintFiles(Graphics2D g2d, List<GPXFile> files) {
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
                                labelPainter.paint(g2d, path);
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
     * paints a path with colored segments
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

            Stroke saveStroke = g2d.getStroke();
            Color saveColor = g2d.getColor();

            path = new GeneralPath();
            rtept = waypointPath.getStart();
            point = getMapPosition(rtept.getLat(), rtept.getLon(), false);
            path.moveTo(point.x, point.y);
            Point prev = point;
            for (int i = 1; i < waypoints.size(); i++) {
                rtept = waypoints.get(i);
                point = getMapPosition(rtept.getLat(), rtept.getLon(), false);
                if (point.equals(prev) == false) { // performance improvement?
                	path.lineTo(point.x, point.y);
                }
                prev = point;
            }

            // hack (end)
            if (paintBorder) {
                // draw black border
                g2d.setStroke(new BasicStroke(trackLineWidth + 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(Color.BLACK);
            	g2d.draw(path);
            }

            // draw colored route
            g2d.setStroke(new BasicStroke(trackLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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
                if (getParent().getBounds().contains(point)) { // TODO offset?
                	g2d.drawOval(point.x-2, point.y-2, 4, 4);
                }
            }
        }
        // System.out.println(String.format("%d %d %d %d", getBounds().x, getBounds().y, getBounds().width, getBounds().y));
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
