package org.gpsmaster.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.marker.Marker;

/**
 * 
 * An sourceFmt of {@link JTree} customized for the display of GPX elements.
 * 
 * This tree manages {@link TreeNode}s only. 
 * Changes to {@link GPXObject}s have to be handled externally.
 * 
 * @author Matt Hoover
 * @author Rainer Fügenstein
 * 
 * REWRITE this mess !!
 * 
 */
@SuppressWarnings("serial")
public class GPXTree extends JTree {
    
    private GPXObject gpxObj;
    private JColorChooser colorChooser;
    private DefaultTreeModel treeModel;
    private JDialog dialog;
    private ActionListener colorListener = null; // ActionListener for CustomColorChooser
    
    private BufferedImage paletteIcon = null;
    private Icon removeIcon = null;
    private Icon addIcon = null;
    
    private JMenuItem removeItem = null;
    private JMenuItem toRouteItem = null;
    private JMenuItem toTrackItem = null;
    private JMenuItem addRouteItem = null;

    private ArrayList<TreePath> expandedPaths = new ArrayList<TreePath>();
    private boolean saveExpansionState = false;
    
    /**
     * Default constructor.
     */
    public GPXTree(DefaultTreeModel treeModel) {
        super(treeModel);
        final DefaultTreeModel finalTreeModel = treeModel;
        this.treeModel = treeModel;
        colorChooser = new JColorChooser();

        // ActionLister called when user selects a Color in CustomColorChooser
        colorListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gpxObj.setColor(colorChooser.getColor());
                GpsMaster.active.refresh();
                
                finalTreeModel.nodeChanged((TreeNode) finalTreeModel.getRoot());
            }
        };
               
        addTreeExpansionListener(new TreeExpansionListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				System.out.println("treeExpanded");
				
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				System.out.println("treeCollapsed");
				
			}
		});
        try {
            paletteIcon = ImageIO.read(GpsMaster.class.getResourceAsStream(Const.ICONPATH + "color-palette.png"));
            removeIcon = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_TREE + "tree-remove.png"));
            addIcon = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_TREE + "tree-add.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }   
       
        // set up popup menus        
        removeItem = new JMenuItem("Remove", removeIcon);
        removeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChange(Const.PCE_REMOVEGPX, null, null);				
			}
		});
        
        toTrackItem = new JMenuItem("Convert to Track");
        toTrackItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChange(Const.PCE_TOTRACK, null, null);				
			}
		});
        
        toRouteItem = new JMenuItem("Convert to Route");
        toRouteItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChange(Const.PCE_TOROUTE, null, null);				
			}
		});
        
        addRouteItem = new JMenuItem("Add Route", addIcon);
        addRouteItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChange(Const.PCE_ADDROUTE, null, null);			
			}
		});
        
        // set some tree defaults
        setScrollsOnExpand(true);
        setExpandsSelectedPaths(true);
    }
    
    /**
     * 
     * @param chooser
     */
    public void setColorChooser(JColorChooser chooser) {
    	colorChooser = chooser;    	    	
    }
    
    /***
     * 
     * @param gpxObject to select 
     */
    public void setSelectedGpxObject(GPXObject gpxObject) {
    	    	
    	setSelectionPath(new TreePath(treeModel.getPathToRoot(gpxObject)));
    }
    
    /**
     * 
     * @param node
     */
    public void refresh(TreeNode node) {
    	
    	//  DefaultTreeModel#insertNodeInto(DefaultMutableNode, DefaultMutableNode, int)
    	
    	// http://tech.chitgoks.com/2009/11/18/save-jtree-tree-node-state/ 
    	// saveExpansionState = true;
    	
    	// remember expanded nodes
		expandedPaths.clear();
		// saveExpansionState((TreeNode) treeModel.getRoot());
		
    	if (node.getParent() != null) {
    		treeModel.nodeStructureChanged(node.getParent());    	
    	} else {
    		treeModel.nodeStructureChanged(node);
    	}
    	        
    	// restoreExpansionState();
    }
        
    /**
     * remember all nodes currently expanded below (and including) the given node
     * (make sure expandedPaths is cleared before first call
     * @param node
     */
    private void saveExpansionState(TreeNode node) {
    	
    	TreePath path = new TreePath(treeModel.getPathToRoot(node));
    	if (isExpanded(path)) {
    		expandedPaths.add(path);
    	}
		for (int i = 0; i < node.getChildCount(); i++) {    			
			saveExpansionState(node.getChildAt(i));
		}
    }

    private void restoreExpansionState() {
    	for(TreePath path : expandedPaths) {
    		expandPath(path);
    	}
    	expandedPaths.clear();
    }
    
    // for tree node panel
    // https://stackoverflow.com/questions/23923669/attaching-a-listener-to-jtree
    
    /**
     * Extends mouse interactivity with the tree. 
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {

    	int type = e.getID();
        int x = e.getX();
        int y = e.getY();

    	int row = getRowForLocation(e.getX(), e.getY());
        if (row == -1) {
            super.processMouseEvent(e);
            return;
        }
    	
        TreePath tp = this.getPathForLocation(x, y);
        if (tp.getLastPathComponent() instanceof Marker) {
        	super.processMouseEvent(e);
        	return; // quick hack
        }        
        
        gpxObj = (GPXObject) tp.getLastPathComponent();
    
    	if (e.isPopupTrigger()) {
    		System.out.println("popuptrigger " + e.getSource().toString());
    		// http://stackoverflow.com/questions/517704/right-click-context-menu-for-java-jtree/2452238
    		setSelectionRow(row);
    		
    		if (gpxObj instanceof Route) {
    			JPopupMenu popupMenu = new JPopupMenu();
    			popupMenu.add(removeItem);
    			popupMenu.add(toTrackItem);
    			popupMenu.show(e.getComponent(), e.getX(), e.getY());	
    		} else if (gpxObj instanceof Track) {
    			JPopupMenu popupMenu = new JPopupMenu();
    			popupMenu.add(removeItem);
    			popupMenu.add(toRouteItem);
    			popupMenu.show(e.getComponent(), e.getX(), e.getY());
    		} else if (gpxObj instanceof GPXFile) {
    			JPopupMenu popupMenu = new JPopupMenu();
    			popupMenu.add(addRouteItem);
    			popupMenu.add(removeItem);    			
    			popupMenu.show(e.getComponent(), e.getX(), e.getY());
    			
    		} else {
    			JPopupMenu popupMenu = new JPopupMenu();
    			popupMenu.add(removeItem);
    			popupMenu.show(e.getComponent(), e.getX(), e.getY());
    		}
            
    	} else if (type == MouseEvent.MOUSE_CLICKED || type == MouseEvent.MOUSE_RELEASED) {
            // do nothing (for now)
        } else if (type == MouseEvent.MOUSE_PRESSED) {
        	// http://stackoverflow.com/questions/31375773/how-do-you-make-components-of-jpanel-as-a-node-in-jtree-usable
            int xOffset, yOffset;
            int rowTemp;
                        
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
                        
            x = x - xOffset;
            y = y - yOffset;
            if (x >= 0 && x <= 9) {
                if (y >= 4 && y <= 12) {
                    gpxObj.setVisible(!gpxObj.isVisible());                    
                }
            } else if (x >= 13 && x <= 19) {
                if (y >= 4 && y <= 12) {
                    gpxObj.setTrackPtsVisible(!gpxObj.isTrackPtsVisible());                    
                }
            } else if (x >= 23 && x <= 32) {
                if (y >= 4 && y <= 12) {
                	dialog = JColorChooser.createDialog(null, "Choose a Color", true, colorChooser, colorListener, null);
                	dialog.setIconImage(paletteIcon);
                    colorChooser.setColor(gpxObj.getColor());
                    dialog.setVisible(true);
                }
            } else if (x > 36) {
                this.clearSelection();
                super.processMouseEvent(e);
            }
            // something most likely has changed.
            treeModel.nodeChanged(gpxObj);
            repaint();
            GpsMaster.active.repaintMap();
        } else {
            super.processMouseEvent(e);
        }
    }   
        
}
 