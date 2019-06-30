package org.gpsmaster.tree;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.WaypointGroup;

import com.sun.xml.internal.bind.v2.TODO;

/**
 * 
 * An extension of {@link JTree} customized for the display of GPX elements.
 * 
 * This tree manages {@link TreeNode}s only. 
 * Changes to {@link GPXObject}s have to be handled externally.
 * 
 * @author Matt Hoover
 * @author Rainer Fügenstein
 */
@SuppressWarnings("serial")
public class GPXTree extends JTree {
    
    private GPXObject gpxObj;
    private JColorChooser colorChooser;
    private DefaultTreeModel treeModel;
    private JDialog dialog;
    private ActionListener actionListener = null; // ActionListener for CustomColorChooser
    private BufferedImage img = null;
    private List<DefaultMutableTreeNode> toRemove = new ArrayList<DefaultMutableTreeNode>();
    
    /**
     * Default constructor.
     * 
     * @param treeModel     The data model.
     */
    public GPXTree(final DefaultTreeModel treeModel) {
        super(treeModel);
        this.treeModel = treeModel;
        
        colorChooser = new JColorChooser();
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gpxObj.setColor(colorChooser.getColor());
                GpsMaster.active.refresh();
                treeModel.nodeChanged((TreeNode) treeModel.getRoot());
            }
        };
                                
        try {
            img = ImageIO.read(GpsMaster.class.getResourceAsStream("/org/gpsmaster/icons/color-palette.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * 
     * @param chooser
     */
    public void setColorChooser(JColorChooser chooser) {
    	colorChooser = chooser;    	
    }

    /**
     * Add a {@link GPXObject} as child to an existing {@link GPXObject} 
     * @param newObject
     * @param parent
     */
    public void addGpxObject(GPXObject newObject, GPXObject parent) {
    	DefaultMutableTreeNode parentNode = findGpxObject(parent);
    	if (parentNode != null) {
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newObject);
			treeModel.insertNodeInto(newNode, parentNode, 0);
    	}
    }
    
    /**
     * Remove {@link GPXObject} with all child nodes from tree. 
     * @param gpxObject
     */
    public void removeGpxFile(GPXFile gpxFile) {
    	
    	// untested
    	DefaultMutableTreeNode node = findGpxObject(gpxFile);
    	if (node != null) {
            treeModel.removeNodeFromParent(node);
    	}
    }
    
    /**
     * Extends mouse interactivity with the tree. 
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        int type = e.getID();
        if (type == MouseEvent.MOUSE_CLICKED || type == MouseEvent.MOUSE_RELEASED) {
            // do nothing (for now)
        } else if (type == MouseEvent.MOUSE_PRESSED) {
            int x = e.getX();
            int y = e.getY();
            int xOffset, yOffset;
            int row, rowTemp;
            
            row = this.getRowForLocation(x, y);
            if (row == -1) {
                super.processMouseEvent(e);
                return;
            }
            
            xOffset = x;
            rowTemp = row;
            while (rowTemp == row) {
                xOffset--;
                rowTemp = this.getRowForLocation(xOffset, y);
            }
            xOffset++;
            
            yOffset = y;
            rowTemp = row;
            while (rowTemp == row) {
                yOffset--;
                rowTemp = this.getRowForLocation(x, yOffset);
            }
            yOffset++;
            
            TreePath tp = this.getPathForLocation(x, y);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent(); 
            Object userObject = node.getUserObject();
            gpxObj = (GPXObject) userObject;
            
            x = x - xOffset;
            y = y - yOffset;
            if (x >= 0 && x <= 9) {
                if (y >= 4 && y <= 12) {
                    gpxObj.setVisible(!gpxObj.isVisible());
                    treeModel.nodeChanged(node);
                }
            } else if (x >= 13 && x <= 19) {
                if (y >= 4 && y <= 12) {
                    gpxObj.setWptsVisible(!gpxObj.isWptsVisible());
                    treeModel.nodeChanged(node);
                }
            } else if (x >= 23 && x <= 32) {
                if (y >= 4 && y <= 12) {
                	dialog = JColorChooser.createDialog(null, "Choose a Color", true, colorChooser, actionListener, null);
                	dialog.setIconImage(img);
                    colorChooser.setColor(gpxObj.getColor());
                    dialog.setVisible(true);
                }
            } else if (x > 36) {
                this.clearSelection();
                super.processMouseEvent(e);
            }
        } else {
            super.processMouseEvent(e);
        }
    }
    
    /**
     * Add {@link GPXFile} at the end of the tree
     * @param gpxFile
     */
    public void addGpxFile(GPXFile gpxFile) {
        
    	DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
           	DefaultMutableTreeNode gpxFileNode = new DefaultMutableTreeNode(gpxFile);
        	treeModel.insertNodeInto(gpxFileNode, root, root.getChildCount());
        	/*
        	if (gpx.getWaypointGroup().getWaypoints().size() > 0) {
        		DefaultMutableTreeNode wptsNode = new DefaultMutableTreeNode(gpx.getWaypointGroup());
        		treeModel.insertNodeInto(wptsNode, gpxFileNode, gpxFileNode.getChildCount());
        	}
        	for (Route route : gpx.getRoutes()) {
        		DefaultMutableTreeNode rteNode = new DefaultMutableTreeNode(route);
        		treeModel.insertNodeInto(rteNode, gpxFileNode, gpxFileNode.getChildCount());
        	}
        	for (Track track : gpx.getTracks()) {    		
        		DefaultMutableTreeNode trkNode = new DefaultMutableTreeNode(track);
        		treeModel.insertNodeInto(trkNode, gpxFileNode, gpxFileNode.getChildCount());
        		for (WaypointGroup trackseg : track.getTracksegs()) {
        			DefaultMutableTreeNode trksegNode = new DefaultMutableTreeNode(trackseg);
        			treeModel.insertNodeInto(trksegNode, trkNode, trkNode.getChildCount());
        		}
        	}
            */
        	refreshGpxFile(gpxFileNode);
        	TreeNode[] nodes = treeModel.getPathToRoot(gpxFileNode);
        	setSelectionPath(new TreePath(nodes));
        	scrollRectToVisible(new Rectangle(0, 999999999, 1, 1));    	            	
    }
    
    /**
     * keep internal tree structure and GPX object hierarchy in sync
     * refresh all {@link GPXObject}s in tree  
     */
    public void refresh() {
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
    	if (rootNode != null) {
    		for (int i = 0; i < rootNode.getChildCount(); i++) {
        		DefaultMutableTreeNode gpxFileNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
        		refreshGpxFile(gpxFileNode);
    		}
    	}
    }
    
    /**
     * keep internal tree structure and GPX object hierarchy in sync
     * refresh the currently selected {@link GPXFile}  
     */
    public void refreshCurrent() {

    	
    }
    
    /**
     * compare the given {@link GPXFile} with the current tree
     * and add tree nodes for new {@link GPXObject}s if necessary 
     * @param gpx top-level treeNode 
     * 
     * TODO design flaw: what if GPXFile was removed?
     * 
     */
    private synchronized void refreshGpxFile(DefaultMutableTreeNode gpxFileNode) {
    	GPXFile gpx = (GPXFile) gpxFileNode.getUserObject();
    	System.out.println("tree.refresh: " + gpx.getName());
    	if (gpx.getWaypointGroup().getWaypoints().size() > 0) {
    		checkNew(gpxFileNode, gpx.getWaypointGroup());
    	}
    	for (Track track : gpx.getTracks()) {
    		DefaultMutableTreeNode trackNode = checkNew(gpxFileNode, track);
    		// segments
    		for (WaypointGroup trackSeg : track.getTracksegs()) {
    			checkNew(trackNode, trackSeg);
    		}
    	}
    	for (Route route : gpx.getRoutes()) {
    		checkNew(gpxFileNode, route);        		
    	}
    	    	
    	// check sub-objects of {@GPXFile} for removals
    	for (int i = 0; i < gpxFileNode.getChildCount(); i++) {
    		DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) gpxFileNode.getChildAt(i);
    		
    		if (childNode.getUserObject() instanceof WaypointGroup) {
    			WaypointGroup grp = (WaypointGroup) childNode.getUserObject();
    			if (grp.getWaypoints().size() == 0) {
    				toRemove.add(childNode);
    			}
    		} else if (childNode.getUserObject() instanceof Track) {
    			Track track = (Track) childNode.getUserObject();
    			if (gpx.getTracks().contains(track) == false) {
    				toRemove.add(childNode);
    			} else {
    				for (int j = 0; j < childNode.getChildCount(); j++) {
    					//  java.lang.ArrayIndexOutOfBoundsException on 1st file of multiload
    					DefaultMutableTreeNode trkSegNode = (DefaultMutableTreeNode) childNode.getChildAt(j);
    					WaypointGroup trkSeg = (WaypointGroup) trkSegNode.getUserObject();
    					if (track.getTracksegs().contains(trkSeg) == false) {
    						toRemove.add(trkSegNode);
    					}
    				}
    			}
    			
    		} else if (childNode.getUserObject() instanceof Route) {
    			Route route = (Route) childNode.getUserObject();
    			if (gpx.getRoutes().contains(route) == false) {
    				toRemove.add(childNode);
    			}
    		}

    	}
    	
    	for (DefaultMutableTreeNode node : toRemove) {
    		try {
    			treeModel.removeNodeFromParent(node);
    		} catch (IllegalArgumentException e) { 
    			// in case the parent node has already been removed
    		};
    	}
    	toRemove.clear();
    	
    	
    }
    
    /**
     * check if gpxObject is a child of parentNode. If not, add it as child node. 
     * @param parentNode
     * @param gpxObject
     * @return the found or newly added child node
     */
    private DefaultMutableTreeNode checkNew(DefaultMutableTreeNode parentNode, GPXObject gpxObject) {
    
    	// System.out.println(gpxObject.getName());
    	for (int i = 0; i < parentNode.getChildCount(); i++) {
    		DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i); 
    		if (childNode.getUserObject().equals(gpxObject)) {
    			return childNode;
    		}
    	}
    	// not found - add it 
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(gpxObject);
		treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
		return newNode;
    }

    /**
     * 
     * @param gpxObject
     * @param parent
     * @return
     */
    private DefaultMutableTreeNode findGpxObject(GPXObject gpxObject, DefaultMutableTreeNode node) {
    	if (node.getUserObject().equals(gpxObject)) {
    		return node;
    	}
    	    	
        MutableTreeNode child = null;
        Enumeration<DefaultMutableTreeNode> children = node.children();

        while (children.hasMoreElements()) {
            child = children.nextElement();
            DefaultMutableTreeNode found = findGpxObject(gpxObject, (DefaultMutableTreeNode) child);
            if (found != null) {
               	return found;
            }        	
    	}
    	return null;
    }
 
    /**
     * 
     * @param gpxObject
     * @return
     */
    private DefaultMutableTreeNode findGpxObject(GPXObject gpxObject) {
       	return findGpxObject(gpxObject, (DefaultMutableTreeNode) treeModel.getRoot());
    }

}
 