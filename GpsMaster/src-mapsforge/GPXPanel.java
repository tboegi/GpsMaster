package org.gpsmaster.gpxpanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.marker.Marker;
import org.gpsmaster.painter.PaintCoordinator;
import org.gpsmaster.painter.Painter;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.debug.TileCoordinatesLayer;
import org.mapsforge.map.layer.debug.TileGridLayer;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.download.tilesource.TileSource;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.swing.controller.MapViewComponentListener;
import org.mapsforge.map.swing.controller.MouseEventListener;
import org.mapsforge.map.swing.util.JavaUtilPreferences;
import org.mapsforge.map.swing.view.MainFrame;
import org.mapsforge.map.swing.view.MapView;

import eu.fuegenstein.gis.GeoBounds;
import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.unit.UnitConverter;

/**
 * 
 * An extension of {@link JMapViewer} to include the display of GPX elements and related functionality.
 * 
 * @author Matt Hoover
 * @author rfu
 * 
 * http://mapsforge.org/docs/master/index.html
 * 
 */
@SuppressWarnings("serial")
public class GPXPanel extends MapView {
    
    private List<GPXFile> gpxFiles;
    
    private Image imgCrosshair;
    private double crosshairLat;
    private double crosshairLon;

    private boolean showCrosshair = false;
    private boolean autoCenter = true; // TODO getter/setter
    private Point shownPoint;
    private Color activeColor = Color.WHITE; // TODO quick fix, better fix activeWpt&Grp handling 
    
    private MouseAdapter mouseAdapter = null;
    private MessageCenter msg = null;
    private ReentrantLock gpxFilesLock = new ReentrantLock(); // lock for central List<GPXFile>
    private List<Marker> markerList;
    private List<Painter> painterList;
    private PaintCoordinator coordinator = new PaintCoordinator();

    private int tileSize = 0;
    
    
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

        imgCrosshair = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/crosshair-map.png")).getImage();
        
        markerList = new ArrayList<Marker>();
        painterList = new ArrayList<Painter>();
        
        mouseAdapter = new MouseAdapter() {
			@Override 
			public void mouseClicked(MouseEvent e) {				
				checkMarkerClick(e);
			}			
		};
		addMouseListener(mouseAdapter);
		
		PropertyChangeListener changeListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				handleEvent(evt);				
			}
		};
		GpsMaster.active.addPropertyChangeListener(changeListener);
		
		tileSize = getModel().displayModel.getTileSize();
    }
    
    public List<GPXFile> getGPXFiles() {
        return gpxFiles;
    }

    /**
     * 
     * @return
     */
    public byte getZoom() {
    	return this.getModel().mapViewPosition.getZoomLevel();
    }
    
    /**
     * 
     * @param painter
     */
    public void addPainter(Painter painter) {
    	painter.setMapViewer(this);
    	painter.setCoordinator(coordinator);
    	painterList.add(painter);
    	Collections.sort(painterList);
    }
    
    /**
     * 
     * @param painter
     */
    public void removePainter(Painter painter) {
    	painterList.remove(painter);
    }
    
    // TODO --- redesign the following methods to be more consistent
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

    /**
     * Highlight the given point on the map. does not pan to point.
     * @param shownPoint
     */
    public void setShownPoint(Point shownPoint) {
        this.shownPoint = shownPoint;
    }

    /**
     * Highlight {@link Waypoint} on the map and pan to center if required/requested
     * @param wpt Waypoint to highlight
     * @param center {@link true} show in center if originally outside the visible area
     * the map is not automatically repainted. call repaint() if required.
     * TODO implement smoother scrolling/panning
     */
    private void setShownWaypoint(Waypoint wpt, boolean center) {
    	Point point = null;
    	if (wpt != null) {
    		// MapViewPosition pos = getModel().mapViewPosition;
    		
	    	point = getMapPosition(wpt.getLat(), wpt.getLon(), false);
	        if ((this.contains(point) == false) && center) {        	
	        	setDisplayPosition(new Coordinate(wpt.getLat(), wpt.getLon()), getZoom());
	        	// TODO bug: point not highlighted after panning to center
	        }
    	}
        shownPoint = point;
    }

    // --- REDESIGN end ---
    
	public Color getActiveColor() {
        return activeColor;
    }

    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
    }
    
    
    /**
     * Get the list of arbitrary markers which are displayed on the
     * map at their respective locations, but are not intended to
     * be kept in a {@link GPXFile}
     *  
     * @return List of current Markers
     */
    public List<Marker> getMarkerList() {
    	return markerList;
    }

    /**
     * 
     * @param m
     * @param autoCenter if {@link true} pan map to show marker in the center.
     */
    public void addMarker(Marker m) {
    	if (markerList.contains(m) == false) {
    		markerList.add(m);
    	}
    }
    
    /**
     * 
     * @param m
     */
    public void removeMarker(Marker m) {
    	if (markerList.contains(m)) {
    		markerList.remove(m);
    	}
    }
    
	/**
     * Get semaphore to avoid concurrent access to GPXFiles list
     * @return
     */
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
	public synchronized void paint(Graphics g) {    	
        super.paint(g);
                
        if (activeColor == null) { // quick hack
        	activeColor = Color.GRAY; 
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        try {
			if (gpxFilesLock.tryLock(lockTimeout, TimeUnit.SECONDS)) {
				coordinator.clear();
				// invoke all registered painters
		    	for (Painter painter : painterList) {
		    		for (GPXFile gpx : gpxFiles) {
		    			painter.paint(g2d, gpx);
		    		}
		    		painter.paint(g2d, markerList);
		    	}		    	
			}
		} catch (InterruptedException e) {
			msg.volatileError("Paint", e);
		} finally {
			gpxFilesLock.unlock();
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

            g2d.setStroke(saveStroke);
            g2d.setColor(saveColor);
        }        
    }
                    
    /**
     * check if a marker was clicked
     * and fire PropertyChangeEvent if applicable.
     * only the first matching marker will be considered.
     * 
     */
    private void checkMarkerClick(MouseEvent e) {
    	for (Marker marker : markerList) {
			if (marker.contains(e.getPoint())) { // redundant code, consolidate
				firePropertyChange(e.getClickCount() + "click", null, marker);
				return;
			}    		
    	}
    	for (GPXFile gpx : gpxFiles) {
    		for (Waypoint wpt : gpx.getWaypointGroup().getWaypoints()) {
    			Marker marker = (Marker) wpt;
    			if (marker.contains(e.getPoint())) {  // redundant code, consolidate
    				firePropertyChange(e.getClickCount() + "click", null, marker);
    				return;
    			}
    		}
    	}
    }
    
    /**
     * TEST!!
     * @return
     */
    public Point getMapPosition(double lat, double lon, boolean dummy) {
    	        	    	
    	long mapSize = MercatorProjection.getMapSize(getZoom(), tileSize);
    	int y = (int) MercatorProjection.latitudeToPixelY(lat, mapSize);
    	int x = (int) MercatorProjection.longitudeToPixelX(lon, mapSize);
    	
    	return new Point(x, y);
    	
    }
    
    /**
     * TEST!!
     * @return
     */
    public Point getMapPosition(LatLong latLon) {
    	        	    	
    	return getMapPosition(latLon.latitude, latLon.longitude, false);
    	
    }
    
    /**
     * 
     * @param evt
     */
    private void handleEvent(PropertyChangeEvent evt) {
    	String command = evt.getPropertyName();
    	if (command.equals(Const.PCE_REPAINTMAP)) {
    		repaint();
    	} else if (command.equals(Const.PCE_ACTIVEWPT)) {
    		setShownWaypoint(GpsMaster.active.getTrackpoint(), autoCenter);
    		repaint();
    	} else if (command.equals(Const.PCE_ADDMARKER)) {
    		Marker m = (Marker) evt.getNewValue();
    		addMarker(m);
    	} else if (command.equals(Const.PCE_REMOVEMARKER)) {
    		Marker m = (Marker) evt.getNewValue();
    		removeMarker(m);    		
    	} else if (command.equals(Const.PCE_CENTERMAP)) {
    		Waypoint wpt = (Waypoint) evt.getNewValue();
    		setDisplayPosition(new Coordinate(wpt.getLat(), wpt.getLon()), getZoom());
    	}
    }
    
    /**
     * Centers the {@link GPXObject} and sets zoom for best fit to panel.
     */
    public void fitGPXObjectToPanel(GPXObject gpxObject) {
    	if (gpxObject != null) {
	        int maxZoom = getModel().mapViewPosition.getZoomLevelMax();
	        int xMin = (int) OsmMercator.LonToX(gpxObject.getMinLon(), maxZoom);
	        int xMax = (int) OsmMercator.LonToX(gpxObject.getMaxLon(), maxZoom);
	        int yMin = (int) OsmMercator.LatToY(gpxObject.getMaxLat(), maxZoom); // screen y-axis positive is down
	        int yMax = (int) OsmMercator.LatToY(gpxObject.getMinLat(), maxZoom); // screen y-axis positive is down
	        
	        if (xMin > xMax || yMin > yMax) {
	            //
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
    
    /**
     * 
     * @return
     */
    public GeoBounds getVisibleBounds() {
    	
    	GeoBounds bounds = new GeoBounds();    	
    	Point center = getCenter();
    	
		bounds.setW(OsmMercator.XToLon(center.x - getWidth() / 2, getZoom()));
		bounds.setN(OsmMercator.YToLat(center.y - getHeight() / 2, getZoom()));
		bounds.setE(OsmMercator.XToLon(center.x + getWidth() / 2, getZoom()));
		bounds.setS(OsmMercator.YToLat(center.y + getHeight() / 2, getZoom()));

    	return bounds;
    }
    
}
