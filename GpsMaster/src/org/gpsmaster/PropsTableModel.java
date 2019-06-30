package org.gpsmaster;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.gpsmaster.gpxpanel.GPXExtension;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import com.topografix.gpx._1._1.LinkType;

import eu.fuegenstein.unit.UnitConverter;
import eu.fuegenstein.util.XTime;

/**
 * Table model containing properties of the specified {@link GPXObject} 
 * @author rfu
 * TODO rewrite this into a self contained {@link JPanel} (or similar) class
 *  
 */
@Deprecated
public class PropsTableModel extends DefaultTableModel {

	/*
	 * This TableModel holds three values for each row:  
	 * 	1	Property Name as displayed in table (string)
	 * 	2	Property Value (object)
	 * 	3	is editable (boolean)
	 * 3rd column isn't displayed, for future (internal) use only
	 */

	private static final long serialVersionUID = -2702982954383747924L;
	private Timer timer;
	private long lastPropDisplay = 0;
	private int displayTime = 4; // default time on display for Trackpoints in seconds
	private GPXObject gpxObject = null;
	protected List<Integer> extensionIdx = new ArrayList<Integer>();
	private DateFormat sdf = null;
	private UnitConverter uc = null;
	private JTable myTable = null; // JTable using this model		
	
    /**
     * custom cell renderer. renders sourceFmt properties in BLUE.
     */
    class propTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6876231432766928405L;

		@Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(null);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setText(String.valueOf(value));
            if (extensionIdx.contains(row)) {
            	setForeground(Color.BLUE);
            } else {
            	setForeground(Color.BLACK);
            }
            return this;
        }
    }
	
    /* TIMER ACTION LISTENER
     * -------------------------------------------------------------------------------------------------------- */        
    private ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
        	if (actionEvent.getSource() == timer) {         
        		// un-display waypoint properties after a few seconds
        		if (lastPropDisplay != 0) {
        			if ((System.currentTimeMillis() - lastPropDisplay) > (displayTime * 1000)) {
        				updatePropsTable();
        				updateWidth();
        				lastPropDisplay = 0;
        				timer.stop();
        			}
        		}
        	}
        }
    };

    /*
     * Listener called when the active {@link GPXObject} changes
     */
    private PropertyChangeListener changeListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent e) {
			String propertyName = e.getPropertyName();
			if (propertyName.equals(Const.PCE_ACTIVEGPX)) {
				setGpxObject(GpsMaster.active.getGpxObject());
			} else if (propertyName.equals(Const.PCE_REFRESHGPX)) {
				updatePropsTable();
				updateWidth();
			} else if (propertyName.equals(Const.PCE_ACTIVE_TRKPT)) {
				Waypoint wpt = GpsMaster.active.getTrackpoint(); 
				setTrackpoint(wpt, GpsMaster.active.getIndexOf(wpt));
			}			
		}
	};
	
     /* Single click on table when {@link Waypoint} properties are displayed
      * stops the timer until another {@link Waypoint} or {@link GPXObject} is set.
      * TODO show some kind of icon (pin) when propsdisplay is locked
      */
    private MouseAdapter mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
    		if (timer.isRunning()) {
    			timer.stop();
    		}
        }
    };
    
     
	/**
	 * Default Constructor
	 */
	public PropsTableModel(UnitConverter converter) {
		super(new Object[]{"Name", "Value"}, 0);
		setColumnCount(2);
		uc = converter;
		sdf = new SimpleDateFormat(Const.SDF_STANDARD);
				
	    timer = new Timer(1000, actionListener);
	    timer.setInitialDelay(1000);
	    
	    GpsMaster.active.addPropertyChangeListener(changeListener);
	}

	/**
	 * Set the {@link JTable} using this model
	 * @param table {@link JTable} using this model
	 */
	public void setTable(JTable table) {
		myTable = table;

        Enumeration<TableColumn> enumeration = myTable.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
        	TableColumn column = enumeration.nextElement();
        	column.setCellRenderer(new propTableCellRenderer());
        }
        
        myTable.addMouseListener(mouseListener);
	}
	
	/**
	 * sets for how long the Trackpoint properties are displayed
	 * @param seconds time on display in seconds
	 */
	public void setDisplayDuration(int seconds) {
		displayTime = seconds;
	}
	
	public int getDisplayDuration() {
		return displayTime;
	}
	
	/**
	 * 
	 * @param gpx
	 */
	public synchronized void setGpxObject(GPXObject gpx) {
		if (timer.isRunning()) {
			timer.stop();
		}
		gpxObject = gpx;
		updatePropsTable();
		updateWidth();		
	}
	
	/**
	 * 
	 * @param trackpoint
	 */
	public void setTrackpoint(Waypoint trackpoint, int indexOf) {
		propsDisplayTrackpoint(trackpoint, indexOf);
		lastPropDisplay = System.currentTimeMillis();
		timer.start();
	}
		
	// TODO edit properties: stop timer on editing waypoint properties

    /**
     * 
     * @param links
     */
    private void propsDisplayLink(List<LinkType> links) {
    	for (LinkType link : links) {
    		// URL url = null;
    		String text = "link";
			if (link.getText() != null) {
				text = link.getText();
			}
			addRow(new Object[]{text, link.getHref()});
			/*
			try {
				url = new URL(link.getHref());
				addRow(new Object[]{text, url});
			} catch (MalformedURLException e) {
				addRow(new Object[]{text, "<malformed URL>"});
			}
			*/
    	}
    }
        
    /**
     * recursively add sourceFmt tree to property table
     * one key/value pair per row
     * @param sourceFmt top-level {@link GPXExtension} object
     * TODO display as {@link JTree}
     */
    private void propsDisplayExtension(GPXExtension extension) {
    	if (extension != null) {
    		for (GPXExtension sub : extension.getExtensions()) {
    			if (sub.getValue() != null) {
    				addRow(new Object[]{sub.getKey(), sub.getValue(), true});
    				extensionIdx.add(getRowCount()-1);
    			}
    			propsDisplayExtension(sub);
    		}
    	}
    }
    
    /**
     * Display properties which are common to all GPX objects 
     * @param o
     */
    private void propsDisplayEssentials(GPXObject o) {
    	
    	Date startTime = o.getStartTime();
    	Date endTime = o.getEndTime();
    	
        if (startTime != null && endTime != null) {
            String startTimeString = "";
            String endTimeString = "";
            startTimeString = sdf.format(startTime);
            endTimeString = sdf.format(endTime);
            addRow(new Object[]{"start time", startTimeString, false});
            addRow(new Object[]{"end time", endTimeString, false});
        }
        
        if (o.getDuration() != 0) {
        	addRow(new Object[]{"duration", XTime.getDurationText(o.getDuration()), false});
        }        
        /* don't display while still buggy
        if (o.getDurationExStop() != 0) {
        	addRow(new Object[]{"duration ex stop", getTimeString(o.getDurationExStop())});
        }        
        */
        double distance = o.getLengthMeters();
        if (distance > 0) {
            addRow(new Object[]{"distance", uc.dist(distance, Const.FMT_DIST), false});
            
            addRow(new Object[]{"max speed", uc.speed(o.getMaxSpeedMps(), Const.FMT_SPEED), false});
            
            if (o.getDuration() > 0) {
            	double avgSpeed = distance / o.getDuration(); // meters per second	
            	addRow(new Object[]{"avg speed", uc.speed(avgSpeed, Const.FMT_SPEED), false});
            }
            /* don't display while still buggy
            if (o.getDurationExStop() > 0) {
            	double avgSpeedEx = (dist / o.getDurationExStop() * 3600000);	
            	addRow(new Object[]{"avg speed ex stop", String.format(speedFormat, avgSpeedEx)});
            }        	
			*/
        }
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayRiseFall(GPXObject o) {
    	       
    	double grossRise = o.getGrossRiseMeters();
    	double grossFall = o.getGrossFallMeters();
    	
        addRow(new Object[]{"gross rise", uc.ele(grossRise, Const.FMT_ELE), false});
        addRow(new Object[]{"gross fall", uc.ele(grossFall, Const.FMT_ELE), false});
        
		long riseTime = o.getRiseTime();
		if (riseTime > 0) {
			addRow(new Object[]{"rise time", XTime.getDurationText(riseTime), false});
		}
		long fallTime = o.getFallTime();
		if (fallTime > 0) {
			addRow(new Object[]{"fall time", XTime.getDurationText(fallTime), false});
		}
                		
        double avgRiseSpeed = grossRise / riseTime;
        if (Double.isNaN(avgRiseSpeed) || Double.isInfinite(avgRiseSpeed)) {
            avgRiseSpeed = 0;
        }
        if (avgRiseSpeed != 0) {
            addRow(new Object[]{"avg rise speed", uc.vertSpeed(avgRiseSpeed, Const.FMT_ELESPEED), false});
        }        
        double avgFallSpeed = grossFall / riseTime;
        if (Double.isNaN(avgFallSpeed) || Double.isInfinite(avgFallSpeed)) {
            avgFallSpeed = 0;
        }
        if (avgFallSpeed != 0) {
            addRow(new Object[]{"avg fall speed", uc.vertSpeed(avgFallSpeed, Const.FMT_ELESPEED), false});
        }
    }
    

    /**
     * 
     * @param o
     */
    private void propsDisplayElevation(GPXObject o) {

    	double eleStart = o.getEleStartMeters();
    	if (eleStart > 0) {
    		addRow(new Object[]{"elevation (start)", uc.ele(eleStart, Const.FMT_ELE), false});    		
    	}
    	double eleEnd = o.getEleEndMeters();
    	if (eleEnd > 0) {
    		addRow(new Object[]{"elevation (end)", uc.ele(eleEnd, Const.FMT_ELE), false});    		
    	}
    	
    	double eleMin = o.getEleMinMeters();
    	if (eleMin != Integer.MAX_VALUE) {
    		addRow(new Object[]{"min elevation", uc.ele(eleMin, Const.FMT_ELE), false});
    	}
    	double eleMax = o.getEleMaxMeters();
    	if (eleMax != Integer.MIN_VALUE) {    	
    		addRow(new Object[]{"max elevation", uc.ele(eleMax, Const.FMT_ELE), false});
    	}
    }

    /**
	 * displays the properties of a trackpoint 
	 * 
	 * @param wpt
	 */
	private void propsDisplayTrackpoint(Waypoint wpt, int indexOf) {
				
		if (wpt != null) {
			clear();
			// mandatory
			if (indexOf > -1) {
				addRow(new Object[]{"trackpoint #", indexOf, false});
			}
			addRow(new Object[]{"latitude", wpt.getLat(), false});
			addRow(new Object[]{"longitude", wpt.getLon(), false});
			addRow(new Object[]{"elevation", uc.ele(wpt.getEle(), Const.FMT_ELE), false});
			Date time = wpt.getTime();
			
			// optional
			if (time != null) {
				addRow(new Object[]{"time", sdf.format(time), false});
			}			
			if (wpt.getSat() > 0) { addRow(new Object[]{"sat", wpt.getSat(), false}); }
			if (wpt.getHdop() > 0) { addRow(new Object[]{"hdop", wpt.getHdop(), false}); }
			if (wpt.getVdop() > 0) { addRow(new Object[]{"vdop", wpt.getVdop(), false}); }
			if (wpt.getPdop() > 0) { addRow(new Object[]{"pdop", wpt.getPdop(), false}); }
			if (wpt.getName().isEmpty() == false) {
				addRow(new Object[]{"name", wpt.getName(), true});
			}
			if (wpt.getDesc().isEmpty() == false) {
				addRow(new Object[]{"desc", wpt.getDesc(), true});
			}
			if (wpt.getType().isEmpty() == false) {
				addRow(new Object[]{"type", wpt.getType(), true});
			}
			if (wpt.getCmt().isEmpty() == false) {
				addRow(new Object[]{"cmt", wpt.getCmt(), true});
			}
			if (wpt.getSrc().isEmpty() == false) {
				addRow(new Object[]{"src", wpt.getSrc(), true});
			}
			if (wpt.getSym().isEmpty() == false) {
				addRow(new Object[]{"sym", wpt.getSym(), true});
			}
			if (wpt.getFix().isEmpty() == false) {
				addRow(new Object[]{"fix", wpt.getFix(), true});
			}
			propsDisplayLink(wpt.getLink());
			if (wpt.getMagvar() > 0) { addRow(new Object[]{"magvar", wpt.getMagvar(), false}); }
			if (wpt.getGeoidheight() > 0) { addRow(new Object[]{"geoidheight", wpt.getGeoidheight(), false}); }
			if (wpt.getAgeofdgpsdata() > 0) { addRow(new Object[]{"ageofdgpsdata", wpt.getAgeofdgpsdata(), false}); }
			if (wpt.getDgpsid() > 0) { addRow(new Object[]{"dgpsid", wpt.getDgpsid(), false}); }
			propsDisplayExtension(wpt.getExtension());
			lastPropDisplay = System.currentTimeMillis();
		}
	}

	/**
     * 
     * @param o
     */
    private void propsDisplayWaypointGrp(GPXObject o) {
    	WaypointGroup wptGrp = (WaypointGroup) o;
    	addRow(new Object[]{"name", wptGrp.getName(), true});
        addRow(new Object[]{"# of pts", wptGrp.getWaypoints().size(), false});
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayRoute(GPXObject o) {
    	Route route = (Route) o;
    	addRow(new Object[]{"name", route.getName(), true});
        addRow(new Object[]{"# of pts", route.getNumPts(), false});
    }
    /**
     * 
     * @param o
     */
    private void propsDisplayTrack(GPXObject o) {
    	
    	Track track = (Track) o;
    	if (track.getName() != null) {
    		addRow(new Object[]{"track name", track.getName(), true});
    	}
    	if (track.getDesc() != null) {
    		addRow(new Object[]{"desc", track.getDesc(), true});
    	}
    	if (track.getType() != null) {
    		addRow(new Object[]{"type", track.getType(), true});
    	}

    	if (track.getTracksegs().size() > 0) {
    		addRow(new Object[]{"segments", track.getTracksegs().size(), false});
    	}
    	if (track.getNumPts() > 0) {
    		addRow(new Object[]{"# of pts", track.getNumPts(), false});
    	}
        if (track.getNumber() != 0) {
            addRow(new Object[]{"track number", track.getNumber(), true}); // editable?
        }    	
        propsDisplayLink(track.getLink());
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayGpxFile(GPXObject o) {
    	
    	GPXFile gpxFile = (GPXFile) o;
        addRow(new Object[]{"GPX name", gpxFile.getMetadata().getName(), true});
        if (gpxFile.getMetadata().getDesc() != null) {
            addRow(new Object[]{"GPX desc", gpxFile.getMetadata().getDesc(), true});
        }
        if (!gpxFile.getCreator().isEmpty()) {
        	addRow(new Object[]{"creator", gpxFile.getCreator()});
        }
        
        // if (!gpxFile.getMetadata().getLink().isEmpty()) {
        // addRow(new Object[]{"link", gpxFile.getLink()});
        // }
        String timeString = "";
        if (gpxFile.getMetadata().getTime() != null) {
            Date time = gpxFile.getMetadata().getTime();
            timeString = sdf.format(time);
        }
        addRow(new Object[]{"GPX time", timeString, false}); // show even if empty
        if (gpxFile.getRoutes().size() > 0) {
        	addRow(new Object[]{"# of routes", gpxFile.getRoutes().size(), false});
        }
        if (gpxFile.getTracks().size() > 0) {
        	addRow(new Object[]{"# of tracks", gpxFile.getTracks().size(), false});
        }
        if (gpxFile.getNumWayPts() > 0) {
        	addRow(new Object[]{"# of waypoints", gpxFile.getNumWayPts(), false});
        }
        if (gpxFile.getNumTrackPts() > 0) {
        	addRow(new Object[]{"# of trackpoints", gpxFile.getNumTrackPts(), false});
        }        
        
    }
    
    /**
     * show properties of current GPX object in properties table   
     */
    private void updatePropsTable() {
    	clear();
    	if (gpxObject != null) {
    		if (gpxObject.isGPXFile()) {
	    		propsDisplayGpxFile(gpxObject);
	            propsDisplayEssentials(gpxObject);
	            propsDisplayElevation(gpxObject);
	            propsDisplayRiseFall(gpxObject);
	            propsDisplayExtension(gpxObject.getExtension());
	    	} else if (gpxObject.isTrack()) {
	    		propsDisplayTrack(gpxObject);
	            propsDisplayEssentials(gpxObject);
	            propsDisplayElevation(gpxObject);            
	            propsDisplayRiseFall(gpxObject);
	            propsDisplayExtension(gpxObject.getExtension());
	    	} else if (gpxObject.isRoute()) {
	    		propsDisplayRoute(gpxObject);
	    		propsDisplayEssentials(gpxObject);
	    		propsDisplayElevation(gpxObject);
	    	} else if (gpxObject.isTrackseg()) {
	    		propsDisplayWaypointGrp(gpxObject);
	    		propsDisplayEssentials(gpxObject);
	    		propsDisplayElevation(gpxObject);
	    		propsDisplayRiseFall(gpxObject);
	    		propsDisplayExtension(gpxObject.getExtension());
	    	} else if (gpxObject.isWaypointGroup()) {
	    		propsDisplayWaypointGrp(gpxObject);
	    		propsDisplayElevation(gpxObject);
	    		propsDisplayExtension(gpxObject.getExtension());
	    	}
    	}    	
    }
    
    /**
     * Dynamically adjusts the widths of the columns in the properties table for optimal display.
     * TODO move this into propsTableModel class
     */

    private void updateWidth() {
        int nameWidth = 0;
        for (int row = 0; row < myTable.getRowCount(); row++) {
            TableCellRenderer renderer = myTable.getCellRenderer(row, 0);
            Component comp = myTable.prepareRenderer(renderer, row, 0);
            nameWidth = Math.max (comp.getPreferredSize().width, nameWidth);
        }
        nameWidth += myTable.getIntercellSpacing().width;
        nameWidth += 10;
        myTable.getColumn("Name").setMaxWidth(nameWidth);
        myTable.getColumn("Name").setMinWidth(nameWidth);
        myTable.getColumn("Name").setPreferredWidth(nameWidth);
        
        int valueWidth = 0;
        for (int row = 0; row < myTable.getRowCount(); row++) {
            TableCellRenderer renderer = myTable.getCellRenderer(row, 1);
            Component comp = myTable.prepareRenderer(renderer, row, 1);
            valueWidth = Math.max (comp.getPreferredSize().width, valueWidth);
        }
        valueWidth += myTable.getIntercellSpacing().width;
        int tableWidth = valueWidth + nameWidth;       
        if (myTable.getParent() instanceof JScrollPane) { // does not work. parent = JViewport
        	JScrollPane scrollPaneProperties = (JScrollPane) myTable.getParent(); 
	        if (scrollPaneProperties.getVerticalScrollBar().isVisible()) {
	            tableWidth += scrollPaneProperties.getVerticalScrollBar().getWidth();
	        }
	        if (tableWidth > scrollPaneProperties.getWidth()) {
	        	myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	            valueWidth += 10;
	        } else {
	        	myTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	            valueWidth = scrollPaneProperties.getWidth() + nameWidth;
	        }
	        myTable.getColumn("Value").setMinWidth(valueWidth);
	        myTable.getColumn("Value").setPreferredWidth(valueWidth);
        }
    }
    
    /**
     * 
     */
    public void clear() {
    	setRowCount(0);
    	extensionIdx.clear();
    }
}
