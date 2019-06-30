package org.gpsmaster.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXExtension;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Waypoint;

import eu.fuegenstein.swing.ExtendedTable;
import eu.fuegenstein.unit.UnitConverter;

public class GpxPropertiesPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7442048764647078316L;
	private GPXObject currentGpx = null;
	private JTree extensionsTree = null;
	private GpxExtensionTreeRenderer treeRenderer = null;
	private GpxPropertiesTableModel propsTableModel = null;
	private JTable propsTable = null;
	private UnitConverter uc = null;
	
	private Timer timer;
	private long lastPropDisplay = 0;
	private int displayTime = 4; // default time on display for Trackpoints in seconds
	
	/**
	 * invoked when active gpx object or trackpoint changes.
	 */
	private PropertyChangeListener changeListener = new PropertyChangeListener() {			
		@Override
		public void propertyChange(PropertyChangeEvent e) {
			String command = e.getPropertyName();
			if (command.equals(Const.PCE_ACTIVEGPX) || command.equals(Const.PCE_REFRESHGPX)) {
				setActiveGpxObject(GpsMaster.active.getGpxObject());
			} else if (command.equals(Const.PCE_ACTIVE_TRKPT)) {
				Waypoint wpt = GpsMaster.active.getTrackpoint(); 
				setTrackpoint(wpt, GpsMaster.active.getIndexOf(wpt));
			}  else if (command.equals(Const.PCE_ACTIVE_WPT)) {
				Waypoint waypoint = GpsMaster.active.getWaypoint();
				setWaypoint(waypoint);
			}
		}
	};
	
    /* TIMER ACTION LISTENER
     * -------------------------------------------------------------------------------------------------------- */        
    private ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
        	if (actionEvent.getSource() == timer) {         
        		// un-display waypoint properties after a few seconds
        		if (lastPropDisplay != 0) {
        			if ((System.currentTimeMillis() - lastPropDisplay) > (displayTime * 1000)) {
        				propsTableModel.setGpxObject(currentGpx);
        				updateExtensionTree(currentGpx.getExtension());
        				updateWidth();
        				lastPropDisplay = 0;
        				timer.stop();
        			}
        		}
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
	 * Constructor
	 * @param uc
	 */
	public GpxPropertiesPanel(UnitConverter uc) {
		super();
		this.uc = uc;
		setup();
		
	    timer = new Timer(1000, actionListener);
	    timer.setInitialDelay(1000);
	}

	/**
	 * sets for how long the Trackpoint properties are displayed
	 * @param seconds time on display in seconds
	 */
	public void setDisplayDuration(int seconds) {
		displayTime = seconds;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDisplayDuration() {
		return displayTime;
	}
	
	/**
	 * 
	 * @param gpx
	 */
	private void setActiveGpxObject(GPXObject gpx) {
		
		currentGpx = gpx;
		propsTableModel.setGpxObject(currentGpx);		
		updateWidth();
		
		if (currentGpx != null) {			
			updateExtensionTree(currentGpx.getExtension());
		}
		
		if (timer.isRunning()) {
			timer.stop();
		}		
	}
	
	/**
	 * 
	 * @param trackpoint
	 * @param indexOf
	 */
	private void setTrackpoint(Waypoint trackpoint, int indexOf) {
		if (trackpoint != null) {
			propsTableModel.setTrackpoint(trackpoint, indexOf);
			updateExtensionTree(null != trackpoint ? trackpoint.getExtension() : null);
			updateWidth();
			lastPropDisplay = System.currentTimeMillis();
			timer.start();
		}
	}

	/**
	 * 
	 * @param waypoint
	 * fuse with setTrackpoint(), bool useTimer as parameter
	 */
	private void setWaypoint(Waypoint waypoint) {
		if (waypoint != null) {
			propsTableModel.setTrackpoint(waypoint, -1);
			updateExtensionTree(null != waypoint ? waypoint.getExtension() : null);
			updateWidth();
		}
	}
	
	/**
	 * update the extensions tree in the lower half of the panel 
	 * @param ext
	 */
	private void updateExtensionTree(GPXExtension ext) {
		if (extensionsTree != null) {
			remove(extensionsTree);
			extensionsTree = null;
		}
		// since the root TreeNode can only be specified in the tree's constructor,
		// a new instance of the tree needs to be added
		if ((ext != null) && (ext.getChildCount() > 0)) {
			extensionsTree = new JTree(ext);
			extensionsTree.setRootVisible(false);
			extensionsTree.setEditable(false);
			extensionsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			extensionsTree.setShowsRootHandles(true);
	        extensionsTree.setCellRenderer(treeRenderer);
			extensionsTree.putClientProperty("JTree.lineStyle", "None");
			extensionsTree.setBackground(Color.white);
			extensionsTree.setToggleClickCount(0);
			extensionsTree.setAlignmentX(Component.LEFT_ALIGNMENT);
			extensionsTree.setAlignmentY(Component.TOP_ALIGNMENT);			
			
			add(extensionsTree);
			
			for (int i = 0; i < extensionsTree.getRowCount(); i++) {
				extensionsTree.expandRow(i);
			}			
		}
		revalidate();
		repaint();
	}
	
	/**
	 * set up panel:
	 *  - property table on top
	 *  - extensions tree below
	 */
	private void setup() {
		
		GpsMaster.active.addPropertyChangeListener(changeListener);
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(Color.white);
		propsTableModel = new GpxPropertiesTableModel(uc);
		propsTable = new ExtendedTable(propsTableModel);
		propsTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		propsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		add(propsTable);
		
		treeRenderer = new GpxExtensionTreeRenderer();
	}

    /**
     * Dynamically adjusts the widths of the columns in the properties table for optimal display.
     * TODO fix ExtendedTable.minimizeColumnWidth() and replace this code
     */

    private void updateWidth() {
        int nameWidth = 0;
        for (int row = 0; row < propsTable.getRowCount(); row++) {
            TableCellRenderer renderer = propsTable.getCellRenderer(row, 0);
            Component comp = propsTable.prepareRenderer(renderer, row, 0);
            nameWidth = Math.max (comp.getPreferredSize().width, nameWidth);
        }
        nameWidth += propsTable.getIntercellSpacing().width;
        nameWidth += 10;
        propsTable.getColumn("Name").setMaxWidth(nameWidth);
        propsTable.getColumn("Name").setMinWidth(nameWidth);
        propsTable.getColumn("Name").setPreferredWidth(nameWidth);
        
        int valueWidth = 0;
        for (int row = 0; row < propsTable.getRowCount(); row++) {
            TableCellRenderer renderer = propsTable.getCellRenderer(row, 1);
            Component comp = propsTable.prepareRenderer(renderer, row, 1);
            valueWidth = Math.max (comp.getPreferredSize().width, valueWidth);
        }
        valueWidth += propsTable.getIntercellSpacing().width;
        int tableWidth = valueWidth + nameWidth;       
        if (propsTable.getParent() instanceof JScrollPane) { // does not work. parent = JViewport
        	JScrollPane scrollPaneProperties = (JScrollPane) propsTable.getParent(); 
	        if (scrollPaneProperties.getVerticalScrollBar().isVisible()) {
	            tableWidth += scrollPaneProperties.getVerticalScrollBar().getWidth();
	        }
	        if (tableWidth > scrollPaneProperties.getWidth()) {
	        	propsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	            valueWidth += 10;
	        } else {
	        	propsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	            valueWidth = scrollPaneProperties.getWidth() + nameWidth;
	        }
	        propsTable.getColumn("Value").setMinWidth(valueWidth);
	        propsTable.getColumn("Value").setPreferredWidth(valueWidth);
        }
    }
}
