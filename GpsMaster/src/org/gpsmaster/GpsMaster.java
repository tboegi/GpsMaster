package org.gpsmaster;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.activation.UnknownObjectException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationException;

import org.gpsmaster.UnitConverter;
import org.gpsmaster.PathFinder.PathFindType;
import org.gpsmaster.UnitConverter.UNIT;
import org.gpsmaster.device.MoveBikeCompMPT;
import org.gpsmaster.device.TrackEntry;
import org.gpsmaster.fileloader.FileLoader;
import org.gpsmaster.fileloader.FileLoaderFactory;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.GPXPanel;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;
import org.gpsmaster.tree.GPXTree;
import org.gpsmaster.tree.GPXTreeRenderer;
import org.gpsmaster.dialogs.EditPropsDialog;
import org.gpsmaster.dialogs.ElevationDialog;
import org.gpsmaster.dialogs.InfoDialog;
import org.gpsmaster.dialogs.MergeDialog;
import org.gpsmaster.dialogs.TimeshiftDialog;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import fuegenstein.eu.utils.Hypsometric;

/**
 * 
 * The main application class for GPS Master, a GUI for manipulating files containing GPS data.<br />
 * Based on GPX Creator by Matt Hoover, extended by Rainer Fügenstein
 * More info at www.gpxcreator.com.
 * 
 * @author hooverm
 * @author rfuegen
 * 
 */
@SuppressWarnings("serial")
public class GpsMaster extends JComponent {
    
	private String me = "GpsMaster 0.5x";
	
    // indents show layout hierarchy
    private JFrame frame;
    private JPanel glassPane;
    private static String lookAndFeel;
        private JToolBar toolBarMain;       // NORTH
            private boolean fileIOHappening;
            private JButton btnFileNew;    
            private JButton btnFileOpen;
            private JButton btnDeviceOpen;
            private JFileChooser chooserFileOpen;
            // private File[] filesOpened;
            private GPXFile gpxFileOpened;
            private JButton btnFileSave;
            private JButton btnPrint;
            private JFileChooser chooserFileSave;
            private File fileSave;
            private JButton btnObjectDelete;
            private JButton btnEditProperties;
            private JButton btnMerge;
            private JToggleButton tglPathFinder;
            private SwingWorker<Void, Void> pathFindWorker;
            private JToggleButton tglAddPoints;
            private JToggleButton tglDelPoints;
            private JToggleButton tglSplitTrackseg;
            private JToggleButton tglMeasure;
            private JToggleButton tglProgress;
            private JButton btnTimeShift;
            private JButton btnInfo;
            private JButton btnCorrectEle;
            private JButton btnEleChart;
            private JButton btnSpeedChart;
            private JComboBox<String> comboBoxTileSource;            
            private JLabel lblLat;
            private JTextField textFieldLat;
            private JLabel lblLon;
            private JTextField textFieldLon;
            private JToggleButton tglLatLonFocus;
        private JSplitPane splitPaneMain;   // CENTER
            private JSplitPane splitPaneSidebar;    // LEFT
                private JPanel containerLeftSidebarTop;        // TOP
                    private JPanel containerExplorerHeading;
                        private JLabel labelExplorerHeading;
                    private JScrollPane scrollPaneExplorer;
                        private DefaultMutableTreeNode root;
                        private DefaultTreeModel treeModel;
                        private JTree tree;
                        private DefaultMutableTreeNode currSelection;
                        // private DefaultMutableTreeNode prevSelection;
                private JPanel containerLeftSidebarBottom;    // BOTTOM
                    private JPanel containerPropertiesHeading;
                        private JLabel labelPropertiesHeading;
                    private JScrollPane scrollPaneProperties;
                        private DefaultTableModel tableModelProperties;
                        private JTable tableProperties;
                        private SimpleDateFormat sdf;
            private GPXPanel mapPanel;              // RIGHT
                private JPanel panelRoutingOptions;
                private JLabel lblMapQuestFoot;
                private JLabel lblMapQuestBike;
                private JLabel lblYOURSFoot;
                private JLabel lblYOURSBike;
                private List<JLabel> lblsRoutingOptions;
                private PathFinder pathFinder;
                private PathFinder pathFinderMapquest;
                private PathFinder pathFinderYOURS;
                private PathFinder.PathFindType pathFindType;
                private JPanel panelRoutingCancel;
                private JLabel lblRoutingCancel;
    private GPXObject activeGPXObject;
    private Cursor mapCursor;
    private boolean mouseOverLink;
    private WaypointGroup activeWptGrp;
    private DefaultMutableTreeNode activeTracksegNode;
    private Waypoint activeWpt;
    private Color transparentYellow;
    private Color transparentGrey;
    private Color transparentRed;
    
    /**
     * @author rfuegen
     */
    private MessageCenter msg = null; 
    private Config conf;
    private UnitConverter uc = new UnitConverter();
    private String configFilename = "GpsMaster.config";
    private ProgressType progressType = ProgressType.NONE;
    private Timer timer;
    private long lastPropDisplay = 0; // for waypoint properties display timer
    protected List<Integer> extensionIdx = new ArrayList<Integer>();

    FileLoaderFactory loaderFactory = new FileLoaderFactory();
    private MessagePanel msgOpen = null;
    private MessagePanel msgMeasure = null;
    private MessagePanel msgRouting = null;
    private MessagePanel msgSave = null;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GpsMaster window = new GpsMaster();
                    window.frame.setVisible(true);
                    window.frame.requestFocusInWindow();
                 } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public GpsMaster() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
       	   	 
        /* MAIN FRAME
         * --------------------------------------------------------------------------------------------------------- */
        frame = new JFrame("GPS Master");

        // ---     
        transparentGrey = new Color(160, 160, 160, 192);
        transparentYellow = new Color(177, 177, 25, 192);
        transparentRed = new Color(177, 25, 25, 208);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        int frameWidth = (screenWidth * 2) / 3;
        int frameHeight = (screenHeight * 2) / 3;
        frame.setBounds(((screenWidth - frameWidth) / 2), ((screenHeight - frameHeight) / 2), frameWidth, frameHeight);
        frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/gpx-creator.png")).getImage());
        
        
        /* MAIN SPLIT PANE
         * --------------------------------------------------------------------------------------------------------- */
        splitPaneMain = new JSplitPane();
        splitPaneMain.setContinuousLayout(true);
        frame.getContentPane().add(splitPaneMain, BorderLayout.CENTER);
        
        splitPaneMain.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // must be scheduled with invokeLater, or if user moves divider fast enough, the update won't happen 
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updatePropTableWidths();
                    }
                });
            }
        });

        /* TIMER ACTION LISTENER
         * -------------------------------------------------------------------------------------------------------- */        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
            	if (actionEvent.getSource() == timer) {         
            		// un-display waypoint properties after a few seconds
            		if (lastPropDisplay != 0) {
            			if ((System.currentTimeMillis() - lastPropDisplay) > 4000) {
            				updatePropsTable();
            				lastPropDisplay = 0;
            			}
            		}
            	}
            }
        };

        timer = new Timer(5000, actionListener);
        timer.setInitialDelay(5000);
        timer.start();


        /* LOAD & APPLY CONFIG
    	 * --------------------------------------------------------------------------------------------------------- */

        msg = new MessageCenter(frame);
		loadConfig();
    	uc.setOutputSystem(conf.getUnitSystem());
    	msg.setScreenTime(conf.getScreenTime());
    	
// Region GUI Setup
    	
        /* MAP PANEL
         * --------------------------------------------------------------------------------------------------------- */
        mapPanel = new GPXPanel(uc, msg);
        mapPanel.setLayout(new BoxLayout(mapPanel, BoxLayout.X_AXIS));
        mapPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        mapPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mapPanel.setDisplayPositionByLatLon(conf.getLat(), 
        		conf.getLon(), conf.getPositionZoom());
        mapPanel.setZoomContolsVisible(conf.getZoomControls());
        mapPanel.setProgressLabels(ProgressType.NONE);
        try {
            mapPanel.setTileLoader(new OsmFileCacheTileLoader(mapPanel));
        } catch (Exception e) {
            msg.Error("There was a problem constructing the tile cache on disk", e);
        }
        splitPaneMain.setRightComponent(mapPanel);
        
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mapPanel.getAttribution().handleAttribution(e.getPoint(), true);
                }
            }
        });
        
        mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        mapPanel.setCursor(mapCursor);
        mapPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean cursorHand = mapPanel.getAttribution().handleAttributionCursor(e.getPoint());
                if (cursorHand) {
                    mapPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    mouseOverLink = true;
                } else {
                    mapPanel.setCursor(mapCursor);
                    mouseOverLink = false;
                }
            }
        });

        MouseListener routingControlsHoverListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mapPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (e.getSource().equals(lblRoutingCancel)) {
                    glassPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                mapPanel.setCursor(mapCursor);
                if (e.getSource().equals(lblRoutingCancel)) {
                    glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                }
            }
        };
        
        MouseListener routingControlsClickListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JLabel clicked = (JLabel) e.getSource();
                if (clicked.equals(lblRoutingCancel)) {
                    try {
                        pathFindWorker.cancel(true);
                        updateButtonVisibility();
                        msg.InfoOff(msgRouting);
                        mapPanel.repaint();
                        updatePropsTable();
                    } catch (Exception ex) {
                    	msg.Error(ex);
                    }
                    panelRoutingCancel.repaint();
                    return;
                }
                
                for (JLabel lbl : lblsRoutingOptions) {
                    lbl.setBackground(transparentGrey);
                }
                clicked.setBackground(transparentYellow);
                panelRoutingOptions.repaint();
                if (clicked.equals(lblMapQuestFoot)) {
                    pathFinder = pathFinderMapquest;
                    pathFindType = PathFindType.FOOT;
                } else if (clicked.equals(lblMapQuestBike)) {
                    pathFinder = pathFinderMapquest;
                    pathFindType = PathFindType.BIKE;
                } else if (clicked.equals(lblYOURSFoot)) {
                    pathFinder = pathFinderYOURS;
                    pathFindType = PathFindType.FOOT;
                } else if (clicked.equals(lblYOURSBike)) {
                    pathFinder = pathFinderYOURS;
                    pathFindType = PathFindType.BIKE;
                }
            }
        };

        // pathfinding cancel panel (begin)
        panelRoutingCancel = new JPanel();
        panelRoutingCancel.setLayout(new BoxLayout(panelRoutingCancel, BoxLayout.Y_AXIS));
        panelRoutingCancel.setOpaque(false);
        panelRoutingCancel.setBorder(new CompoundBorder(
                new EmptyBorder(10, 10, 10, 10), new LineBorder(new Color(105, 105, 105))));
        panelRoutingCancel.setAlignmentY(Component.TOP_ALIGNMENT);
        mapPanel.add(panelRoutingCancel);
        
        lblRoutingCancel = new JLabel("Cancel Pathfinding Operation");
        lblRoutingCancel.setBorder(new CompoundBorder(
                new LineBorder(new Color(105, 105, 105)), new EmptyBorder(2, 4, 2, 4)));
        lblRoutingCancel.setAlignmentY(Component.TOP_ALIGNMENT);
        lblRoutingCancel.setOpaque(true);
        lblRoutingCancel.setBackground(transparentRed);
        panelRoutingCancel.add(lblRoutingCancel);

        Dimension dim = new Dimension(
                lblRoutingCancel.getPreferredSize().width, lblRoutingCancel.getPreferredSize().height);
        lblRoutingCancel.setMaximumSize(dim);
        lblRoutingCancel.setMinimumSize(dim);
        lblRoutingCancel.setPreferredSize(dim);
        lblRoutingCancel.addMouseListener(routingControlsHoverListener);
        lblRoutingCancel.addMouseListener(routingControlsClickListener);
        
        panelRoutingCancel.setVisible(false);
        // pathfinding cancel panel (end)
        
        // pathfinding options panel (begin)
        panelRoutingOptions = new JPanel();
        panelRoutingOptions.setLayout(new BoxLayout(panelRoutingOptions, BoxLayout.Y_AXIS));
        panelRoutingOptions.setOpaque(false);
        panelRoutingOptions.setBorder(new CompoundBorder(
                new EmptyBorder(10, 10, 10, 10), new LineBorder(new Color(105, 105, 105))));
        panelRoutingOptions.setAlignmentY(Component.TOP_ALIGNMENT);
        mapPanel.add(Box.createHorizontalGlue());
        mapPanel.add(panelRoutingOptions);
        
        lblMapQuestFoot = new JLabel("MapQuest (foot)");
        lblMapQuestBike = new JLabel("MapQuest (bike)");
        lblYOURSFoot = new JLabel("YOURS (foot)");
        lblYOURSBike = new JLabel("YOURS (bike)");
        
        lblsRoutingOptions = new ArrayList<JLabel>();
        lblsRoutingOptions.add(lblMapQuestFoot);
        lblsRoutingOptions.add(lblMapQuestBike);
        lblsRoutingOptions.add(lblYOURSFoot);
        lblsRoutingOptions.add(lblYOURSBike);
        
        for (JLabel lbl : lblsRoutingOptions) {
            lbl.setBorder(new CompoundBorder(
                    new LineBorder(new Color(105, 105, 105)), new EmptyBorder(2, 4, 2, 4)));
            lbl.setAlignmentY(Component.TOP_ALIGNMENT);
            lbl.setOpaque(true);
            lbl.setBackground(transparentGrey);
            panelRoutingOptions.add(lbl);
        }
        int maxWidth = 0;
        int maxHeight = 0;
        for (JLabel lbl : lblsRoutingOptions) {
            maxWidth = Math.max(maxWidth, lbl.getPreferredSize().width);
            maxHeight = Math.max(maxHeight, lbl.getPreferredSize().height);
        }
        dim = new Dimension(maxWidth, maxHeight);
        for (JLabel lbl : lblsRoutingOptions) {
            lbl.setMaximumSize(dim);
            lbl.setMinimumSize(dim);
            lbl.setPreferredSize(dim);
            lbl.addMouseListener(routingControlsHoverListener);
            lbl.addMouseListener(routingControlsClickListener);
        }
        
        lblMapQuestFoot.setBackground(transparentYellow);
        panelRoutingOptions.setVisible(false);
        
        pathFinderMapquest = new PathFinderMapQuest();
        pathFinderYOURS = new PathFinderYOURS();
        pathFinder = pathFinderMapquest;
        pathFindType = PathFindType.FOOT;
        // pathfinding options panel (end)
        
        // up/+ and down/- keys will also zoom the map in and out
        String zoomIn = "zoom in";
        mapPanel.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), zoomIn);
        // keyboard +
        mapPanel.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), zoomIn);
        // numpad +
        mapPanel.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), zoomIn);
        mapPanel.getActionMap().put(zoomIn, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.zoomIn();
            }
        });
        
        String zoomOut = "zoom out";
        mapPanel.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), zoomOut);
        // keyboard -
        mapPanel.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), zoomOut);
        // numpad -
        mapPanel.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), zoomOut);

        mapPanel.getActionMap().put(zoomOut, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.zoomOut();
            }
        });
        
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mapPanel.requestFocus();
            }
        });

        // https://today.java.net/pub/a/today/2006/03/23/multi-split-pane.html
        	
        /* SIDEBAR SPLIT PANE
        /* --------------------------------------------------------------------------------------------------------- */
        splitPaneSidebar = new JSplitPane();
        splitPaneSidebar.setMinimumSize(new Dimension(240, 25));
        splitPaneSidebar.setPreferredSize(new Dimension(240, 25));
        splitPaneSidebar.setContinuousLayout(true);
        splitPaneSidebar.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPaneMain.setLeftComponent(splitPaneSidebar);
        splitPaneSidebar.setDividerLocation(210);
        
        splitPaneSidebar.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // must be scheduled with invokeLater, or if user moves divider fast enough, the update won't happen 
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updatePropTableWidths();
                    }
                });
            }
        });
        
        /* LEFT SIDEBAR TOP CONTAINER
         * --------------------------------------------------------------------------------------------------------- */
        containerLeftSidebarTop = new JPanel();
        containerLeftSidebarTop.setPreferredSize(new Dimension(10, 100));
        containerLeftSidebarTop.setAlignmentY(Component.TOP_ALIGNMENT);
        containerLeftSidebarTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerLeftSidebarTop.setLayout(new BoxLayout(containerLeftSidebarTop, BoxLayout.Y_AXIS));
        splitPaneSidebar.setTopComponent(containerLeftSidebarTop);
        
        /* EXPLORER HEADING CONTAINER
         * --------------------------------------------------------------------------------------------------------- */
        containerExplorerHeading = new JPanel();
        containerExplorerHeading.setPreferredSize(new Dimension(10, 23));
        containerExplorerHeading.setMinimumSize(new Dimension(10, 23));
        containerExplorerHeading.setMaximumSize(new Dimension(32767, 23));
        containerExplorerHeading.setAlignmentY(Component.TOP_ALIGNMENT);
        containerExplorerHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerExplorerHeading.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        containerExplorerHeading.setLayout(new BoxLayout(containerExplorerHeading, BoxLayout.Y_AXIS));
        containerExplorerHeading.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 0, 1, (Color) new Color(0, 0, 0)), new EmptyBorder(2, 5, 5, 5)));
        containerLeftSidebarTop.add(containerExplorerHeading);
        
        /* EXPLORER HEADING
         * --------------------------------------------------------------------------------------------------------- */
        labelExplorerHeading = new JLabel("Explorer");
        labelExplorerHeading.setAlignmentY(Component.TOP_ALIGNMENT);
        labelExplorerHeading.setMaximumSize(new Dimension(32767, 14));
        labelExplorerHeading.setHorizontalTextPosition(SwingConstants.LEFT);
        labelExplorerHeading.setHorizontalAlignment(SwingConstants.LEFT);
        labelExplorerHeading.setFont(new Font("Segoe UI", Font.BOLD, 12));
        containerExplorerHeading.add(labelExplorerHeading);
        
        /* EXPLORER TREE/MODEL
         * --------------------------------------------------------------------------------------------------------- */
        root = new DefaultMutableTreeNode("GPX Files");
        treeModel = new DefaultTreeModel(root);
        tree = new GPXTree(treeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new GPXTreeRenderer());
        tree.putClientProperty("JTree.lineStyle", "None");
        tree.setBackground(Color.white);
        tree.setToggleClickCount(0);
        
        ImageIcon collapsed = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/tree-collapsed.png"));
        ImageIcon expanded = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/tree-expanded.png"));
        UIManager.put("Tree.collapsedIcon", collapsed);
        UIManager.put("Tree.expandedIcon", expanded);
        
        // give Java look and feel to tree only (to get rid of dotted line handles/connectors)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            SwingUtilities.updateComponentTreeUI(tree);
        } catch (Exception e) {
            msg.Error(e);
        }
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
        	msg.Error(e);
        }

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                deselectAllToggles(null);
                
                // set selected object as current selection and active in map panel 
                currSelection = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (currSelection != null) {
                    setActiveGPXObject((GPXObject) currSelection.getUserObject());
                }
                
                // necessary hack if bold selection style used in GPXTreeComponentFactory (keeps label sizes correct)                
                /*treeModel.nodeChanged(currSelection);
                if (prevSelection != null) {
                    treeModel.nodeChanged(prevSelection);
                }
                prevSelection = currSelection;*/
                
                updateButtonVisibility();
            }
        });
        
        treeModel.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeStructureChanged(TreeModelEvent e) {}
            @Override
            public void treeNodesRemoved(TreeModelEvent e) {}
            @Override
            public void treeNodesInserted(TreeModelEvent e) {}
            @Override
            public void treeNodesChanged(TreeModelEvent e) { // necessary for changed color, vis, waypoint vis
                mapPanel.repaint();
            }
        });
        
        /* EXPLORER TREE SCROLLPANE
         * --------------------------------------------------------------------------------------------------------- */
        UIManager.put("ScrollBar.minimumThumbSize", new Dimension(16, 16)); // prevent Windows L&F scroll thumb bug
        scrollPaneExplorer = new JScrollPane(tree);
        scrollPaneExplorer.setAlignmentY(Component.TOP_ALIGNMENT);
        scrollPaneExplorer.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPaneExplorer.setBorder(new LineBorder(new Color(0, 0, 0)));
        containerLeftSidebarTop.add(scrollPaneExplorer);
        
        /* LEFT SIDEBAR BOTTOM CONTAINER
         * --------------------------------------------------------------------------------------------------------- */
        containerLeftSidebarBottom = new JPanel();
        containerLeftSidebarBottom.setAlignmentY(Component.TOP_ALIGNMENT);
        containerLeftSidebarBottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerLeftSidebarBottom.setLayout(new BoxLayout(containerLeftSidebarBottom, BoxLayout.Y_AXIS));
        splitPaneSidebar.setBottomComponent(containerLeftSidebarBottom);
        
        /* PROPERTIES CONTAINER
         * --------------------------------------------------------------------------------------------------------- */
        containerPropertiesHeading = new JPanel();
        containerPropertiesHeading.setMaximumSize(new Dimension(32767, 23));
        containerPropertiesHeading.setMinimumSize(new Dimension(10, 23));
        containerPropertiesHeading.setPreferredSize(new Dimension(10, 23));
        containerPropertiesHeading.setAlignmentY(Component.TOP_ALIGNMENT);
        containerPropertiesHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerPropertiesHeading.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        containerPropertiesHeading.setLayout(new BoxLayout(containerPropertiesHeading, BoxLayout.Y_AXIS));
        containerPropertiesHeading.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 0, 1, (Color) new Color(0, 0, 0)), new EmptyBorder(2, 5, 5, 5)));
        containerLeftSidebarBottom.add(containerPropertiesHeading);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        
        /* PROPERTIES HEADING
         * --------------------------------------------------------------------------------------------------------- */
        labelPropertiesHeading = new JLabel("Properties");
        labelPropertiesHeading.setMaximumSize(new Dimension(32767, 14));
        labelPropertiesHeading.setHorizontalTextPosition(SwingConstants.LEFT);
        labelPropertiesHeading.setHorizontalAlignment(SwingConstants.LEFT);
        labelPropertiesHeading.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelPropertiesHeading.setAlignmentY(0.0f);
        containerPropertiesHeading.add(labelPropertiesHeading);
        
        /* PROPERTIES TABLE/MODEL
         * --------------------------------------------------------------------------------------------------------- */
        tableModelProperties = new DefaultTableModel(new Object[]{"Name", "Value"},0);
        tableProperties = new JTable(tableModelProperties);
        tableProperties.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableProperties.setAlignmentY(Component.TOP_ALIGNMENT);
        tableProperties.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableProperties.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableProperties.setFillsViewportHeight(true);
        tableProperties.setTableHeader(null);
        tableProperties.setEnabled(false);
        tableProperties.getColumnModel().setColumnMargin(0);
        
        // custom cell renderer. renders extension properties in BLUE.  
        class propTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

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

        Enumeration<TableColumn> enumeration = tableProperties.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
        	TableColumn column = enumeration.nextElement();
        	column.setCellRenderer(new propTableCellRenderer());
        }
         
        /* PROPERTIES TABLE SCROLLPANE
         * --------------------------------------------------------------------------------------------------------- */
        scrollPaneProperties = new JScrollPane(tableProperties);
        scrollPaneProperties.setAlignmentY(Component.TOP_ALIGNMENT);
        scrollPaneProperties.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPaneProperties.setBorder(new LineBorder(new Color(0, 0, 0)));
        containerLeftSidebarBottom.add(scrollPaneProperties);
        
        /* MAIN TOOLBAR
         * --------------------------------------------------------------------------------------------------------- */
        toolBarMain = new JToolBar();
        toolBarMain.setLayout(new BoxLayout(toolBarMain, BoxLayout.X_AXIS));
        toolBarMain.setFloatable(false);
        toolBarMain.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        frame.getContentPane().add(toolBarMain, BorderLayout.NORTH);
        
        /* NEW FILE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnFileNew = new JButton("");
        btnFileNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileNew();
            }
        });
        
        btnFileNew.setToolTipText("<html>Create new GPX file<br>[CTRL+N]</html>");
        btnFileNew.setFocusable(false);
        btnFileNew.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-new.png")));
        btnFileNew.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-new-disabled.png")));
        String ctrlNew = "CTRL+N";
        mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), ctrlNew);
        mapPanel.getActionMap().put(ctrlNew, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileNew();
            }
        });
        toolBarMain.add(btnFileNew);
        
        /* OPEN FILE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        int chooserWidth = (frame.getWidth() * 8) / 10;
        int chooserHeight = (frame.getHeight() * 8) / 10;
        chooserWidth = Math.min(864, chooserWidth);
        chooserHeight = Math.min(539, chooserHeight);
        
        btnFileOpen = new JButton("");
        chooserFileOpen = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                dialog.setIconImage(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-open.png")).getImage());
                return dialog;
            }
        };
        chooserFileOpen.setPreferredSize(new Dimension(chooserWidth, chooserHeight));

        btnFileOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileOpen();
            }
        });
        btnFileOpen.setToolTipText("<html>Open GPX file<br>[CTRL+O]</html>");
        btnFileOpen.setFocusable(false);
        btnFileOpen.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-open.png")));
        btnFileOpen.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-open-disabled.png")));
        String ctrlOpen = "CTRL+O";
        mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), ctrlOpen);
        mapPanel.getActionMap().put(ctrlOpen, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileOpen();
            }
        });
        toolBarMain.add(btnFileOpen);
        

        /* OPEN FROM DEVICE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnDeviceOpen = new JButton("");
        btnDeviceOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	getFromDevice();
            }
        });
        
        btnDeviceOpen.setToolTipText("<html>Get Data from Device<br>[CTRL+G]</html>");
        btnDeviceOpen.setFocusable(false);
        btnDeviceOpen.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-device.png")));
        btnDeviceOpen.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-device-disabled.png")));
        String ctrlDev = "CTRL+G";
        mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK), ctrlDev);
        mapPanel.getActionMap().put(ctrlDev, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	getFromDevice(); 
            }
        });
        toolBarMain.add(btnDeviceOpen);
        btnDeviceOpen.setEnabled(false); // remove after implementation

        /* SAVE FILE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnFileSave = new JButton("");
        chooserFileSave = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                dialog.setIconImage(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-save.png")).getImage());
                return dialog;
            }
        };
        chooserFileSave.setPreferredSize(new Dimension(chooserWidth, chooserHeight));
        btnFileSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileSave();
            }
        });
        
        btnFileSave.setToolTipText("<html>Save selected GPX file<br>[CTRL+S]</html>");
        btnFileSave.setFocusable(false);
        btnFileSave.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-save.png")));
        btnFileSave.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/file-save-disabled.png")));
        String ctrlSave = "CTRL+S";
        mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), ctrlSave);
        mapPanel.getActionMap().put(ctrlSave, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileSave();
            }
        });
        toolBarMain.add(btnFileSave);
        btnFileSave.setEnabled(false);
        
        /* PRINT BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnPrint = new JButton("");
        btnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printVisibleMap();
            }
        });
        
        btnPrint.setToolTipText("<html>Print current map view[CTRL+P]</html>");
        btnPrint.setFocusable(false);
        btnPrint.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/print.png")));
        btnPrint.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/print-disabled.png")));
        String ctrlPrint = "CTRL+P";
        mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK), ctrlPrint);
        mapPanel.getActionMap().put(ctrlSave, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printVisibleMap();
            }
        });
        toolBarMain.add(btnPrint);
        btnPrint.setEnabled(false);
        btnPrint.setVisible(false); // for now

        /* OBJECT DELETE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnObjectDelete = new JButton("");
        btnObjectDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteActiveGPXObject();
            }
        });
        btnObjectDelete.setToolTipText("<html>Delete selected object<br>[CTRL+D]</html>");
        btnObjectDelete.setFocusable(false);
        btnObjectDelete.setIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/object-delete.png")));
        btnObjectDelete.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/object-delete-disabled.png")));
        String ctrlDelete = "CTRL+D";
        mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), ctrlDelete);
        mapPanel.getActionMap().put(ctrlDelete, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteActiveGPXObject();
            }
        });
        toolBarMain.add(btnObjectDelete);
        toolBarMain.addSeparator();
        btnObjectDelete.setEnabled(false);

        /* MERGE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnMerge = new JButton("");
        btnMerge.setToolTipText("Merge visible tracks into a new GPX");
        btnMerge.setFocusable(false);
        btnMerge.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/merge.png")));
        btnMerge.setEnabled(false);
        btnMerge.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/merge-disabled.png")));
        btnMerge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doMerge();
            }
        });
        toolBarMain.add(btnMerge);
        
        
        /* EDIT PROPERTIES BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnEditProperties = new JButton("");
        btnEditProperties.setToolTipText("Edit properties");
        btnEditProperties.setFocusable(false);
        btnEditProperties.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/edit-properties.png")));
        btnEditProperties.setEnabled(false);
        btnEditProperties.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/edit-properties-disabled.png")));
        btnEditProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editProperties();
            }
        });
        toolBarMain.add(btnEditProperties);
        
        /* PATHFINDER BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglPathFinder = new JToggleButton("");
        tglPathFinder.setToolTipText("Find path");
        tglPathFinder.setFocusable(false);
        tglPathFinder.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/path-find.png")));
        tglPathFinder.setEnabled(false);
        tglPathFinder.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/path-find-disabled.png")));
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	// TODO move following code to separate method
                if (tglPathFinder.isSelected() && activeGPXObject != null && !mouseOverLink) {
                    int zoom = mapPanel.getZoom();
                    int x = e.getX();
                    int y = e.getY();
                    Point mapCenter = mapPanel.getCenter();
                    int xStart = mapCenter.x - mapPanel.getWidth() / 2;
                    int yStart = mapCenter.y - mapPanel.getHeight() / 2;
                    final double lat = OsmMercator.YToLat(yStart + y, zoom);
                    final double lon = OsmMercator.XToLon(xStart + x, zoom);
                    
                    Route route = null;
                    DefaultMutableTreeNode gpxFileNode = null;
                    if (activeGPXObject.isGPXFileWithOneRoute()) {
                        route = ((GPXFile) activeGPXObject).getRoutes().get(0);
                        gpxFileNode = currSelection;
                    } else if (activeGPXObject.isRoute()) {
                        route = (Route) activeGPXObject;
                        gpxFileNode = (DefaultMutableTreeNode) currSelection.getParent();
                    }
                    final Route finalRoute = route;
                    final DefaultMutableTreeNode finalGPXFileNode = gpxFileNode;
                    
                    if (route.getPath().getNumPts() == 0) { // route is empty, so add first point
                        Waypoint wpt = new Waypoint(lat, lon);
                        route.getPath().addWaypoint(wpt, false);
                    } else { // route is not empty, so find path from current end to the point that was clicked
                        pathFindWorker = new SwingWorker<Void, Void>() {
                            @Override
                            public Void doInBackground() {
                                msgRouting = msg.InfoOn("finding path ...", new Cursor(Cursor.WAIT_CURSOR));
                                panelRoutingCancel.setVisible(true);
                                tglPathFinder.setEnabled(false);
                                btnCorrectEle.setEnabled(false);
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        frame.repaint();
                                    }
                                });
                                Waypoint pathfindStart = finalRoute.getPath().getEnd();
                                double startLat = pathfindStart.getLat();
                                double startLon = pathfindStart.getLon();
                                
                                String xml = pathFinder.getXMLResponse(pathFindType, startLat, startLon, lat, lon);
                                if (isCancelled()) {
                                    return null;
                                }
                                panelRoutingCancel.setVisible(false);
                                List<Waypoint> newPathFound = pathFinder.parseXML(xml);
   
                                for (Waypoint wpt : newPathFound) {
                                    finalRoute.getPath().addWaypoint(wpt, false);                            
                                }
                                
                                finalRoute.getPath().updateLength();
                                if (finalRoute.getPath().getLengthMeters() < 400000) {
                                    finalRoute.getPath().correctElevation(true);
                                }
                                return null;
                            }
                            @Override
                            protected void done() {
                                panelRoutingCancel.setVisible(false);
                                if (isCancelled()) {
                                    return;
                                }
                                
                                Object gpxFileObject = finalGPXFileNode.getUserObject();
                                GPXFile gpxFile = (GPXFile) gpxFileObject;
                                gpxFile.updateAllProperties();
                                
                                updateButtonVisibility();
                                msg.InfoOff(msgRouting);
                                mapPanel.repaint();
                                updatePropsTable();
                            }
                        };
                        pathFindWorker.execute();
                    }
                    mapPanel.repaint();
                    updatePropsTable();
                }
            }
        });
        tglPathFinder.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deselectAllToggles(tglPathFinder);
                    mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                    
                    if (activeGPXObject.isGPXFileWithNoRoutes()) {
                        Route route = ((GPXFile) activeGPXObject).addRoute();
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(route);
                        treeModel.insertNodeInto(newNode, currSelection, 0);
                        updateButtonVisibility();
                    }
                    panelRoutingOptions.setVisible(true);
                } else {
                    mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    panelRoutingOptions.setVisible(false);
                }
                mapPanel.repaint();
            }
        });
        toolBarMain.add(tglPathFinder);
        
        /* ADD POINTS BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglAddPoints = new JToggleButton("");
        tglAddPoints.setToolTipText("Add points");
        tglAddPoints.setFocusable(false);
        tglAddPoints.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/add-points.png")));
        tglAddPoints.setEnabled(false);
        tglAddPoints.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/add-points-disabled.png")));
        toolBarMain.add(tglAddPoints);
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tglAddPoints.isSelected() && activeGPXObject != null && !mouseOverLink) {
                    int zoom = mapPanel.getZoom();
                    int x = e.getX();
                    int y = e.getY();
                    Point mapCenter = mapPanel.getCenter();
                    int xStart = mapCenter.x - mapPanel.getWidth() / 2;
                    int yStart = mapCenter.y - mapPanel.getHeight() / 2;
                    double lat = OsmMercator.YToLat(yStart + y, zoom);
                    double lon = OsmMercator.XToLon(xStart + x, zoom);
                    Waypoint wpt = new Waypoint(lat, lon);
                    
                    if (activeGPXObject.isGPXFileWithOneRoute()) {
                        Route route = ((GPXFile) activeGPXObject).getRoutes().get(0);
                        route.getPath().addWaypoint(wpt, false);
                    } else if (activeGPXObject.isRoute()) {
                        Route route = (Route) activeGPXObject;
                        route.getPath().addWaypoint(wpt, false);
                    } else if (activeGPXObject.isWaypointGroup()
                            && ((WaypointGroup) activeGPXObject).getWptGrpType() == WptGrpType.WAYPOINTS) {
                        WaypointGroup wptGrp = (WaypointGroup) activeGPXObject;
                        wptGrp.addWaypoint(wpt, false);
                    }
                    DefaultMutableTreeNode gpxFileNode = currSelection;
                    while (!((GPXObject) gpxFileNode.getUserObject()).isGPXFile()) {
                        gpxFileNode = (DefaultMutableTreeNode) gpxFileNode.getParent();
                    }
                    Object gpxFileObject = gpxFileNode.getUserObject();
                    GPXFile gpxFile = (GPXFile) gpxFileObject;
                    gpxFile.updateAllProperties();
                    
                    mapPanel.repaint();
                    updatePropsTable();
                }
            }
        });
        tglAddPoints.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deselectAllToggles(tglAddPoints);
                    mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                    
                    if (activeGPXObject.isGPXFileWithNoRoutes()) {
                        Route route = ((GPXFile) activeGPXObject).addRoute();
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(route);
                        treeModel.insertNodeInto(newNode, currSelection, 0);
                        updateButtonVisibility();
                    }
                } else {
                    mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                }
            }
        });
        
        /* DELETE POINTS BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglDelPoints = new JToggleButton("");
        tglDelPoints.setToolTipText("Delete points");
        tglDelPoints.setFocusable(false);
        tglDelPoints.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/delete-points.png")));
        tglDelPoints.setEnabled(false);
        tglDelPoints.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/delete-points-disabled.png")));
        toolBarMain.add(tglDelPoints);
        tglDelPoints.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deselectAllToggles(tglDelPoints);
                    mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                } else {
                    mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                }
            }
        });
        
        
        /* SPLIT TRACKSEG BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglSplitTrackseg = new JToggleButton("");
        tglSplitTrackseg.setToolTipText("Split track segment");
        tglSplitTrackseg.setFocusable(false);
        tglSplitTrackseg.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/trackseg-split.png")));
        tglSplitTrackseg.setEnabled(false);
        tglSplitTrackseg.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/trackseg-split-disabled.png")));
        toolBarMain.add(tglSplitTrackseg);
        
        tglSplitTrackseg.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deselectAllToggles(tglSplitTrackseg);
                    mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                } else {
                    mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                }
            }
        });
        
        /* CORRECT ELEVATION BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnCorrectEle = new JButton("");
        btnCorrectEle.setToolTipText("Correct elevation");
        btnCorrectEle.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/correct-elevation.png")));
        btnCorrectEle.setEnabled(false);
        btnCorrectEle.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/correct-elevation-disabled.png")));
        btnCorrectEle.setFocusable(false);
        btnCorrectEle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               	correctElevation();
            }
        });
        toolBarMain.add(btnCorrectEle);
        
        /* ELEVATION CHART BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnEleChart = new JButton("");
        btnEleChart.setToolTipText("View elevation profile");
        btnEleChart.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/elevation-chart.png")));
        btnEleChart.setEnabled(false);
        btnEleChart.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/elevation-chart-disabled.png")));
        btnEleChart.setFocusable(false);
        btnEleChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildChart("Elevation profile", "/org/gpsmaster/icons/elevation-chart.png");
            }
        });
        toolBarMain.add(btnEleChart);
        
        /* SPEED CHART BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnSpeedChart = new JButton("");
        btnSpeedChart.setToolTipText("View speed profile");
        btnSpeedChart.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/speed-chart.png")));
        btnSpeedChart.setEnabled(false);
        btnSpeedChart.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/speed-chart-disabled.png")));
        btnSpeedChart.setFocusable(false);
        btnSpeedChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildChart("Speed profile", "/org/gpsmaster/icons/speed-chart.png");
            }
        });
        toolBarMain.add(btnSpeedChart);

        /* MEASURE DISTANCE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglMeasure = new JToggleButton("");
        tglMeasure.setToolTipText("Measure distance between two waypoints");
        tglMeasure.setFocusable(false);
        tglMeasure.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/measure.png")));
        tglMeasure.setEnabled(false);
        tglMeasure.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/measure-disabled.png")));
        toolBarMain.add(tglMeasure);
        tglMeasure.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deselectAllToggles(tglMeasure);
                    mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                    msgMeasure = msg.InfoOn(" ... ");
                } else {
                    mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    msg.InfoOff(msgMeasure);
                    mapPanel.getMarkerPoints().clear(); // remove measuremarkers only
                    
                }
            }
        });

        /* SHOW PROGRESS LABELS BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglProgress = new JToggleButton("");
        tglProgress.setToolTipText("Show progress labels");
        tglProgress.setFocusable(false);
        tglProgress.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/progress-disabled.png")));
        tglProgress.setEnabled(false);
        tglProgress.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/progress-disabled.png")));
        toolBarMain.add(tglProgress);
        tglProgress.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if ((e.getStateChange() == ItemEvent.SELECTED) || (e.getStateChange() == ItemEvent.DESELECTED)) {
                	switch(progressType) {
                	case NONE:
                		progressType = ProgressType.RELATIVE;
                		tglProgress.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/progress-rel.png")));
                		tglProgress.setSelected(true);
                		break;
                	case RELATIVE:
                		progressType = ProgressType.ABSOLUTE;
                		tglProgress.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/progress-abs.png")));
                		tglProgress.setSelected(true);
                		break;
                	case ABSOLUTE:
                		progressType = ProgressType.NONE;
                		tglProgress.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/progress-none.png")));
                		tglProgress.setSelected(false);
                		break;                		
                	}
                }
                mapPanel.setProgressLabels(progressType);
        		mapPanel.repaint();
            }

        });

        /* TIMESHIFT BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnTimeShift = new JButton("");
        btnTimeShift.setToolTipText("Timeshift GPX File [CTRL-T]");
        btnTimeShift.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/timeshift.png")));
        btnTimeShift.setEnabled(false);
        btnTimeShift.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/timeshift-disabled.png")));
        btnTimeShift.setFocusable(false);
        btnTimeShift.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doTimeShift();
            }
        });
        String ctrlT = "CTRL+T";
        mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK), ctrlT);
        mapPanel.getActionMap().put(ctrlT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doTimeShift();
            }
        });

        toolBarMain.add(btnTimeShift);


        //
        // the 3 listeners below are shared by multiple functionalities (delete points, split trackseg)
        // --------------------------------------------------------------------------------------------
        
        mapPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateActiveWpt(e);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                activeWptGrp = null;
                activeWpt = null;
                mapPanel.setShownPoint(null);
                mapPanel.repaint();
            }
        });
        
        mapPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                updateActiveWpt(e);
            }
        });
        
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                activeWptGrp = null;
                activeWpt = null;
                mapPanel.setShownPoint(null);
                mapPanel.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {

                if (activeWpt != null && activeWptGrp != null && !mouseOverLink) {
                    DefaultMutableTreeNode findFile = currSelection;
                    while (!((GPXObject) findFile.getUserObject()).isGPXFile()) {
                        findFile = (DefaultMutableTreeNode) findFile.getParent();
                    }
                    GPXFile gpxFile = (GPXFile) findFile.getUserObject();
                    
                    if (tglDelPoints.isSelected()) {
                        activeWptGrp.removeWaypoint(activeWpt);
                        gpxFile.updateAllProperties();
                    } else if (tglMeasure.isSelected()) {
                    	doMeasure(activeWpt);
                    } else if (tglSplitTrackseg.isSelected()) {               	                   
                        splitTrackSeg(gpxFile);
                    } 
                    
                    activeWptGrp = null;
                    activeWpt = null;
                    mapPanel.setShownPoint(null);
                    mapPanel.repaint();
                    // updatePropsTable();
                    updateActiveWpt(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                updateActiveWpt(e);
            }
        });
                
        /* TILE SOURCE SELECTOR
         * --------------------------------------------------------------------------------------------------------- */
        toolBarMain.add(Box.createHorizontalGlue());
        
        /* EDIT PROPERTIES BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnInfo = new JButton("");
        btnInfo.setToolTipText("Show Info");
        btnInfo.setFocusable(false);
        btnInfo.setIcon(new ImageIcon(
                GpsMaster.class.getResource("/org/gpsmaster/icons/about.png")));
        btnInfo.setEnabled(true);
        btnInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInfo();
            }
        });
        toolBarMain.add(btnInfo);
        
        final TileSource openStreetMap = new OsmTileSource.Mapnik();
        final TileSource openCycleMap = new OsmTileSource.CycleMap(); 
        final TileSource bingAerial = new BingAerialTileSource();
        final TileSource mapQuestOsm = new MapQuestOsmTileSource();
        final TileSource mapQuestOpenAerial = new MapQuestOpenAerialTileSource();
        
        comboBoxTileSource = new JComboBox<String>();
        comboBoxTileSource.setMaximumRowCount(18);
        
        comboBoxTileSource.addItem("OpenStreetMap");
        comboBoxTileSource.addItem("OpenCycleMap");
        comboBoxTileSource.addItem("Bing Aerial");
        comboBoxTileSource.addItem("MapQuest-OSM");
        comboBoxTileSource.addItem("MapQuest Open Aerial");
        
        comboBoxTileSource.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboBoxTileSource.getSelectedItem();
                if (selected.equals("OpenStreetMap")) {
                    mapPanel.setTileSource(openStreetMap);
                } else if (selected.equals("OpenCycleMap")) {
                    mapPanel.setTileSource(openCycleMap);
                } else if (selected.equals("Bing Aerial")) {
                    mapPanel.setTileSource(bingAerial);
                } else if (selected.equals("MapQuest-OSM")) {
                    mapPanel.setTileSource(mapQuestOsm);
                } else if (selected.equals("MapQuest Open Aerial")) {
                    mapPanel.setTileSource(mapQuestOpenAerial);
                }
            }
        });
        
        comboBoxTileSource.setFocusable(false);
        toolBarMain.add(comboBoxTileSource);
        
        // the tile sources below are not licensed for public usage
        
        /*final TileSource googleMaps = new TemplatedTMSTileSource(
                "Google Maps",
                "http://mt{switch:0,1,2,3}.google.com/vt/lyrs=m&x={x}&y={y}&z={zoom}", 22);
        final TileSource googleSat = new TemplatedTMSTileSource(
                "Google Satellite",
                "http://mt{switch:0,1,2,3}.google.com/vt/lyrs=s&x={x}&y={y}&z={zoom}", 21);
        final TileSource googleSatMap = new TemplatedTMSTileSource(
                "Google Satellite + Labels",
                "http://mt{switch:0,1,2,3}.google.com/vt/lyrs=y&x={x}&y={y}&z={zoom}", 21);
        final TileSource googleTerrain = new TemplatedTMSTileSource(
                "Google Terrain",
                "http://mt{switch:0,1,2,3}.google.com/vt/lyrs=p&x={x}&y={y}&z={zoom}", 15);
        final TileSource esriTopoUSA = new TemplatedTMSTileSource(
                "Esri Topo USA",
                "http://server.arcgisonline.com/ArcGIS/rest/services/" +
                "USA_Topo_Maps/MapServer/tile/{zoom}/{y}/{x}.jpg", 15);
        final TileSource esriTopoWorld = new TemplatedTMSTileSource(
                "Esri Topo World",
                "http://server.arcgisonline.com/ArcGIS/rest/services/" +
                "World_Topo_Map/MapServer/tile/{zoom}/{y}/{x}.jpg", 19);

        comboBoxTileSource.addItem("Google Maps");
        comboBoxTileSource.addItem("Google Satellite");
        comboBoxTileSource.addItem("Google Satellite + Labels");
        comboBoxTileSource.addItem("Google Terrain");
        comboBoxTileSource.addItem("Esri Topo USA");
        comboBoxTileSource.addItem("Esri Topo World");
        
        comboBoxTileSource.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboBoxTileSource.getSelectedItem();
                if (selected.equals("Google Maps")) {
                    mapPanel.setTileSource(googleMaps);
                } else if (selected.equals("Google Satellite")) {
                    mapPanel.setTileSource(googleSat);
                } else if (selected.equals("Google Satellite + Labels")) {
                    mapPanel.setTileSource(googleSatMap);
                } else if (selected.equals("Google Terrain")) {
                    mapPanel.setTileSource(googleTerrain);
                } else if (selected.equals("Esri Topo USA")) {
                    mapPanel.setTileSource(esriTopoUSA);
                } else if (selected.equals("Esri Topo World")) {
                    mapPanel.setTileSource(esriTopoWorld);
                }
            }
        });*/
        
        comboBoxTileSource.setMaximumSize(comboBoxTileSource.getPreferredSize());
        
        /* LAT/LON INPUT/SEEKER
         * --------------------------------------------------------------------------------------------------------- */
        toolBarMain.addSeparator();
        
        lblLat = new JLabel(" Lat ");
        lblLat.setFont(new Font("Tahoma", Font.PLAIN, 11));
        toolBarMain.add(lblLat);
        
        textFieldLat = new JTextField();
        textFieldLat.setPreferredSize(new Dimension(80, 24));
        textFieldLat.setMinimumSize(new Dimension(25, 24));
        textFieldLat.setMaximumSize(new Dimension(80, 24));
        textFieldLat.setColumns(9);
        textFieldLat.setFocusable(false);
        textFieldLat.setFocusTraversalKeysEnabled(false);
        textFieldLat.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    textFieldLat.setFocusable(false);
                    textFieldLon.setFocusable(true);
                    textFieldLon.requestFocusInWindow();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tglLatLonFocus.setSelected(false);
                    tglLatLonFocus.setSelected(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    tglLatLonFocus.setSelected(false);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (tglLatLonFocus.isSelected()) {
                    tglLatLonFocus.setSelected(false);
                    tglLatLonFocus.setSelected(true);
                }
            }
        });
        toolBarMain.add(textFieldLat);
        
        lblLon = new JLabel(" Lon ");
        lblLon.setFont(new Font("Tahoma", Font.PLAIN, 11));
        toolBarMain.add(lblLon);
        
        textFieldLon = new JTextField();
        textFieldLon.setPreferredSize(new Dimension(80, 24));
        textFieldLon.setMinimumSize(new Dimension(25, 24));
        textFieldLon.setMaximumSize(new Dimension(80, 24));
        textFieldLon.setColumns(9);
        textFieldLon.setFocusable(false);
        textFieldLon.setFocusTraversalKeysEnabled(false);
        textFieldLon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    textFieldLat.setFocusable(true);
                    textFieldLon.setFocusable(false);
                    textFieldLat.requestFocusInWindow();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tglLatLonFocus.setSelected(false);
                    tglLatLonFocus.setSelected(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    tglLatLonFocus.setSelected(false);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (tglLatLonFocus.isSelected()) {
                    tglLatLonFocus.setSelected(false);
                    tglLatLonFocus.setSelected(true);
                }
            }
        });
        toolBarMain.add(textFieldLon);
        
        long eventMask = AWTEvent.MOUSE_EVENT_MASK;  
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {  
            public void eventDispatched(AWTEvent e) {
                if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                    if (e.getSource() == (Object) textFieldLat) {
                        textFieldLat.setFocusable(true);
                    } else {
                        textFieldLat.setFocusable(false);
                    }
                    if (e.getSource() == (Object) textFieldLon) {
                        textFieldLon.setFocusable(true);
                    } else {
                        textFieldLon.setFocusable(false);
                    }
                }
            }
        }, eventMask);
        
        tglLatLonFocus = new JToggleButton("");
        tglLatLonFocus.setToolTipText("Focus on latitude/longitude");
        tglLatLonFocus.setIcon(new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/crosshair.png")));
        tglLatLonFocus.setFocusable(false);
        tglLatLonFocus.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deselectAllToggles(tglLatLonFocus);
                    mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                    String latString = textFieldLat.getText();
                    String lonString = textFieldLon.getText();
                    try {
                        double latDouble = Double.parseDouble(latString);
                        double lonDouble = Double.parseDouble(lonString);
                        mapPanel.setShowCrosshair(true);
                        mapPanel.setCrosshairLat(latDouble);
                        mapPanel.setCrosshairLon(lonDouble);
                        Point p = new Point(mapPanel.getWidth() / 2, mapPanel.getHeight() / 2); 
                        mapPanel.setDisplayPositionByLatLon(p, latDouble, lonDouble, mapPanel.getZoom());
                    } catch (Exception e1) {
                        // nothing
                    }
                    mapPanel.repaint();
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    mapPanel.setShowCrosshair(false);
                    mapPanel.repaint();
                }
            }
        });
// EndRegion      
        
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tglLatLonFocus.isSelected() && !mouseOverLink) {
                    int zoom = mapPanel.getZoom();
                    int x = e.getX();
                    int y = e.getY();
                    Point mapCenter = mapPanel.getCenter();
                    int xStart = mapCenter.x - mapPanel.getWidth() / 2;
                    int yStart = mapCenter.y - mapPanel.getHeight() / 2;
                    double lat = OsmMercator.YToLat(yStart + y, zoom);
                    double lon = OsmMercator.XToLon(xStart + x, zoom);
                    textFieldLat.setText(String.format("%.6f", lat));
                    textFieldLon.setText(String.format("%.6f", lon));
                    mapPanel.setShowCrosshair(true);
                    mapPanel.setCrosshairLat(lat);
                    mapPanel.setCrosshairLon(lon);
                    mapPanel.repaint();
                }
            }
        });

        // set up file save/open dialogs
		for (String ext : loaderFactory.getExtensions()) {
			String desc = ext.toUpperCase() + " files (*." + ext+ ")";
			FileNameExtensionFilter extFilter = new FileNameExtensionFilter(desc, ext);
		    chooserFileOpen.addChoosableFileFilter(extFilter);
		    chooserFileSave.addChoosableFileFilter(extFilter);
		    // chooserFileOpen.setFileFilter(extFilter);        	
		}

        /*
         * save config on exit
         */
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent we) {
        		saveConfig();
        		System.exit(0);
        	}
        });
        
        Component horizontalGlue = Box.createHorizontalGlue();
        horizontalGlue.setMaximumSize(new Dimension(2, 0));
        horizontalGlue.setMinimumSize(new Dimension(2, 0));
        horizontalGlue.setPreferredSize(new Dimension(2, 0));
        toolBarMain.add(horizontalGlue);
        toolBarMain.add(tglLatLonFocus);
                  
        
        /* DEBUG / PROXY
         * --------------------------------------------------------------------------------------------------------- */
        
        // button for quick easy debugging
        JButton debug = new JButton("debug");
        debug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doDebug();
            }
        });
        debug.setFocusable(false);
        toolBarMain.addSeparator();
        toolBarMain.add(debug);
        
        /*java.util.Properties systemProperties = System.getProperties();
        systemProperties.setProperty("http.proxyHost", "proxy1.lmco.com");
        systemProperties.setProperty("http.proxyPort", "80");*/
    }

    /**
     * 
     * @param gpxFile
     */
	private void splitTrackSeg(GPXFile gpxFile) {
		WaypointGroup tracksegBeforeSplit = activeWptGrp;
		
		List<Waypoint> trackptsBeforeSplit = tracksegBeforeSplit.getWaypoints();
		int splitIndex = trackptsBeforeSplit.indexOf(activeWpt);
		
		List<Waypoint> trackptsAfterSplit1 = new ArrayList<Waypoint>(
		        trackptsBeforeSplit.subList(0, splitIndex + 1));
		List<Waypoint> trackptsAfterSplit2 = new ArrayList<Waypoint>(
		        trackptsBeforeSplit.subList(splitIndex, trackptsBeforeSplit.size()));
		WaypointGroup tracksegAfterSplit1 =
		        new WaypointGroup(tracksegBeforeSplit.getColor(), WptGrpType.TRACKSEG);
		WaypointGroup tracksegAfterSplit2 =
		        new WaypointGroup(tracksegBeforeSplit.getColor(), WptGrpType.TRACKSEG);
		tracksegAfterSplit1.setWaypoints(trackptsAfterSplit1);
		tracksegAfterSplit2.setWaypoints(trackptsAfterSplit2);
		
		DefaultMutableTreeNode oldTracksegNode = activeTracksegNode;
		DefaultMutableTreeNode trackNode = (DefaultMutableTreeNode) oldTracksegNode.getParent();
		
		Object trackObject = trackNode.getUserObject();
		
		Track track = (Track) trackObject;
		int insertIndex = track.getTracksegs().indexOf(tracksegBeforeSplit);
		track.getTracksegs().remove(tracksegBeforeSplit);
		track.getTracksegs().add(insertIndex, tracksegAfterSplit2);
		track.getTracksegs().add(insertIndex, tracksegAfterSplit1);
		
		treeModel.removeNodeFromParent(oldTracksegNode);
		DefaultMutableTreeNode newTracksegNode2 = new DefaultMutableTreeNode(tracksegAfterSplit2);
		DefaultMutableTreeNode newTracksegNode1 = new DefaultMutableTreeNode(tracksegAfterSplit1);
		treeModel.insertNodeInto(newTracksegNode2, trackNode, insertIndex);
		treeModel.insertNodeInto(newTracksegNode1, trackNode, insertIndex);
		
		TreeNode[] pathForNewSelection = treeModel.getPathToRoot(newTracksegNode2);
		tree.setSelectionPath(new TreePath(pathForNewSelection));
		gpxFile.updateAllProperties();
		tglSplitTrackseg.setSelected(true);
	}

    /**
     * Creates a new GPX file and loads it into the application.
     */
	private void fileNew() {
        if (fileIOHappening) {
            return;
        }
        
        String name = (String)JOptionPane.showInputDialog(frame, "Please type a name for the new route:",
                "New route", JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (name != null) {
            GPXFile gpxFile = new GPXFile(name);
            gpxFile.getMetadata().setTime(new Date());
            gpxFile.addRoute();

            mapPanel.addGPXFile(gpxFile);
            DefaultMutableTreeNode gpxFileNode = new DefaultMutableTreeNode(gpxFile);
            
            treeModel.insertNodeInto(gpxFileNode, root, root.getChildCount());
            treeModel.insertNodeInto(new DefaultMutableTreeNode(gpxFile.getRoutes().get(0)), gpxFileNode, 0);
            
            setActiveGPXObject((GPXObject) gpxFile);
            TreeNode[] pathToFileNode = treeModel.getPathToRoot(gpxFileNode);
            tree.setSelectionPath(new TreePath(pathToFileNode));
            tree.scrollRectToVisible(new Rectangle(0, 999999999, 1, 1));
        }
    }
    
	
	/**
	 * 
	 * @param files
	 */
	private void loadFiles(File[] files) {

		FileLoader loader = null;
		int validationFailed = 0; // number of files which failed validation
		int notSupported = 0;
		
		for (File file : files) {
	
	    	try {
				loader = loaderFactory.getLoader(getFilenameExt(file.getName()));
				loader.Open(file);
	    		loader.Validate();
	    	} catch (NotBoundException e) {
				msg.Error("Internal error", e);
			}  catch (ClassNotFoundException e) {
				if (files.length == 1) {
					msg.VolatileError("Unsupported format");
				} else {
					notSupported++;
				}
				loader = null;
			} catch (ValidationException e) {
				if (files.length == 1) {
					msg.VolatileWarning("Validation failed, file may not have loaded properly", e);
				} else {
					validationFailed++;
				}
			}
	    	
	    	if (loader != null) {
		        try {
					gpxFileOpened = loader.Load();
					loader.Close();
				} catch (Exception e) {
					gpxFileOpened = null;
					msg.Error(e);
				}
	    	}
	    	if (gpxFileOpened != null) {
	        	gpxFileOpened.updateAllProperties();
	            adjustColors(gpxFileOpened); // make this configurable?
	        	if (gpxFileOpened.getMetadata().getName().isEmpty()) {
	        		gpxFileOpened.getMetadata().setName(file.getName());
	        	}
	        	if (conf.useExtensions()) {
	        		postLoad(gpxFileOpened);
	        	}
	            treeAddGpxFile(gpxFileOpened);   
	    	}
		}
		if ((validationFailed > 0) && conf.getShowWarning()) {
			msg.VolatileWarning(String.format("%d files failed validation. files may not have loaded properly.", validationFailed));
		}
		if (notSupported > 0) {
			msg.Error(String.format("unsupported format for %d files.", notSupported));
		}
	}
	
    /**
     * Loads a GPS file into the application.
     */
    private void fileOpen() {
        if (fileIOHappening) {
            return;
        }

		chooserFileOpen.setCurrentDirectory(new File(conf.getLastOpenDirectory()));
		chooserFileOpen.setMultiSelectionEnabled(true);
        int returnVal = chooserFileOpen.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            conf.setLastOpenDirectory(chooserFileOpen.getCurrentDirectory().getPath());
        	            
		        SwingWorker<Void, Void> fileOpenWorker = new SwingWorker<Void, Void>() {
	                @Override
	                public Void doInBackground() {
	                	loadFiles(chooserFileOpen.getSelectedFiles());
	                    return null;
	                }
	                @Override
	                protected void done() {
	                	msg.InfoOff(msgOpen);
	                	setFileIOHappening(false);
	                }
	            };
	            
	            msgOpen = msg.InfoOn("opening file(s) ...", new Cursor(Cursor.WAIT_CURSOR));
	            setFileIOHappening(true);
	            // frame.repaint();
	            fileOpenWorker.execute();
        }
    }

    /**
	 * Saves the active {@link GPXFile} to disk.
	 */
	private void fileSave() {
		
		// MessagePanel msgSave = null;
	    if (fileIOHappening) {
	        return;
	    }
	    
	    if (currSelection == null) {
	        return;
	    }
	
	    while (!((GPXObject) currSelection.getUserObject()).isGPXFile()) {
	        currSelection = (DefaultMutableTreeNode) currSelection.getParent();
	    }
	    TreeNode[] nodes = treeModel.getPathToRoot(currSelection);
	    tree.setSelectionPath(new TreePath(nodes));
	    GPXFile gpx = (GPXFile) currSelection.getUserObject();
	    
	    chooserFileSave.setCurrentDirectory(
                new File(conf.getLastSaveDirectory()));
        // TODO default filename = GPXFile.GetName();
	    boolean hasExt = false;
	    String filename = gpx.getMetadata().getName();
	    for (String ext : loaderFactory.getExtensions()) {
	    	if (filename.endsWith(ext)) {
	    		hasExt = true;
	    	}
	    }
	    if (!hasExt) { filename = filename.concat(".gpx"); }
        chooserFileSave.setSelectedFile(new File(filename));

	    boolean ok = false;
	    while(!ok) {
	    	ok = true;
		    int returnVal = chooserFileSave.showSaveDialog(frame);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		        fileSave = chooserFileSave.getSelectedFile();
		        String ext = getFilenameExt(fileSave.getName());
		        if (ok && ext.isEmpty()) {
		        	JOptionPane.showConfirmDialog(frame, "please specify an extension.",
		                    "no extension", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
		        	ok = false;
		        }  
		        if (ok && !loaderFactory.getExtensions().contains(ext)) {
		        	JOptionPane.showConfirmDialog(frame, "unsupported file format.",
		                    "error", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
		        	ok = false;
		        }
		       
		        if (ok && fileSave.exists()) {
		            int response = JOptionPane.showConfirmDialog(frame, "<html>" + fileSave.getName() +
		                    " already exists.<br>Do you want to replace it?</html>",
		                    "Confirm file overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		            if (response == JOptionPane.CANCEL_OPTION || response == JOptionPane.CLOSED_OPTION) {
		                return; // cancel the save operation
		            }
		        }
		    }
	    }
	    	
	    conf.setLastSaveDirectory(chooserFileSave.getCurrentDirectory().getPath());

        SwingWorker<Void, Void> fileSaveWorker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
				FileLoader loader;
				GPXFile gpx = (GPXFile) activeGPXObject;
				try {
					fileSave = chooserFileSave.getSelectedFile();
					preSave(gpx);
					loader = loaderFactory.getLoader(getFilenameExt(fileSave.getName()));
					loader.Save(gpx, fileSave);
				} catch (ClassNotFoundException e) {
					msg.Error("No writer for this file format available.");
				} catch (FileNotFoundException e) {
					msg.Error(e);
				}
                
                return null;
            }
            @Override
            protected void done() {
            	msg.InfoOff(msgSave);
                setFileIOHappening(false);
            }
        };
        setFileIOHappening(true);
        msgSave = msg.InfoOn("Saving file ...", new Cursor(Cursor.WAIT_CURSOR));
        fileSaveWorker.execute();
	}

	/**
     * 
     * @param filename
     * @return
     */
    private String getFilenameExt(String fileName) {
    	String extension = "";

    	int i = fileName.lastIndexOf('.');
    	int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

    	if (i > p) {
    	    extension = fileName.substring(i+1);
    	}
    	return extension;
    }
    
    /**
     * 
     * @param gpxObject
     * @param parent
     * @return
     */
    private DefaultMutableTreeNode treeFindGpxObject(GPXObject gpxObject, DefaultMutableTreeNode node) {
    	if (node.getUserObject().equals(gpxObject)) {
    		return node;
    	}
    	
        MutableTreeNode child = null;
        Enumeration<DefaultMutableTreeNode> children = node.children();
        while (children.hasMoreElements()) {
            child = children.nextElement();
            DefaultMutableTreeNode found = treeFindGpxObject(gpxObject, (DefaultMutableTreeNode) child);
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
    private DefaultMutableTreeNode treeFindGpxObject(GPXObject gpxObject) {
       	return treeFindGpxObject(gpxObject, (DefaultMutableTreeNode) treeModel.getRoot());
    }

    /**
     * find the top level {@link GPXFile} that contains the specified node
     * @param node
     * @return
     */
    private GPXFile treeFindGPXFile(DefaultMutableTreeNode node) {
        while (!((GPXObject) node.getUserObject()).isGPXFile()) {
            node = (DefaultMutableTreeNode) node.getParent();
        }
    	return (GPXFile) node.getUserObject();
    }
    
    /**
     *  find the {@link GPXFile} which contains the given {@link GPXObject}
     * @param gpxObject
     * @return top level {@link GPXFile} containing the {@link GPXObject} 
     */
    private GPXFile treeFindGPXFile(GPXObject gpxObject) {
    	if (gpxObject.isGPXFile()) {
    		return (GPXFile) gpxObject;
    	}    		
    	DefaultMutableTreeNode node = treeFindGpxObject(gpxObject);    	
    	return treeFindGPXFile(node);
    }
    
    /**
     * add a newly created {@link WaypointGroup} to an existing {@link Track}
     * @param track
     * @param segment
     * @throws UnknownObjectException 
     */
    private void treeAddTrackSegment(Track track, WaypointGroup trackseg) throws UnknownObjectException {
    	
       DefaultMutableTreeNode trkNode = treeFindGpxObject(track);
       if (trkNode != null) {
			DefaultMutableTreeNode trksegNode = new DefaultMutableTreeNode(trackseg);
			treeModel.insertNodeInto(trksegNode, trkNode, trkNode.getChildCount());    	   
       } else {
    	   throw new UnknownObjectException("Treenode for Track "+track.getName()+ "not found"); 
       }
    }
    
    /*
     * adds a newly created {@link GPXFile} to the {@link MapPanel} and the tree
     */
    private void treeAddGpxFile(GPXFile newFile) {

    	mapPanel.addGPXFile(newFile); // mapPanel.addGPXFile is thread safe

    	DefaultMutableTreeNode gpxFileNode = new DefaultMutableTreeNode(newFile);
    	treeModel.insertNodeInto(gpxFileNode, root, root.getChildCount());
    	if (newFile.getWaypointGroup().getWaypoints().size() > 0) {
    		DefaultMutableTreeNode wptsNode = new DefaultMutableTreeNode(newFile.getWaypointGroup());
    		treeModel.insertNodeInto(wptsNode, gpxFileNode, gpxFileNode.getChildCount());
    	}
    	for (Route route : newFile.getRoutes()) {
    		DefaultMutableTreeNode rteNode = new DefaultMutableTreeNode(route);
    		treeModel.insertNodeInto(rteNode, gpxFileNode, gpxFileNode.getChildCount());
    	}
    	for (Track track : newFile.getTracks()) {    		
    		DefaultMutableTreeNode trkNode = new DefaultMutableTreeNode(track);
    		treeModel.insertNodeInto(trkNode, gpxFileNode, gpxFileNode.getChildCount());
    		for (WaypointGroup trackseg : track.getTracksegs()) {
    			DefaultMutableTreeNode trksegNode = new DefaultMutableTreeNode(trackseg);
    			treeModel.insertNodeInto(trksegNode, trkNode, trkNode.getChildCount());
    		}
    	}
    
    	setActiveGPXObject((GPXObject) newFile);
    	TreeNode[] nodes = treeModel.getPathToRoot(gpxFileNode);
    	tree.setSelectionPath(new TreePath(nodes));
    	tree.scrollRectToVisible(new Rectangle(0, 999999999, 1, 1));
    	
    }
    
    /**
     * Removes the active {@link GPXObject} from its parent container.
     */
    private void deleteActiveGPXObject() {
        if (fileIOHappening) {
            return;
        }
        
        if (activeGPXObject != null) {
            DefaultMutableTreeNode currentNode = currSelection;
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) currentNode.getParent();
            TreeNode[] parentPath = treeModel.getPathToRoot(parentNode);
            Object parentObject = parentNode.getUserObject();
            
            /* 
            DefaultMutableTreeNode gpxFileNode = currSelection;
            while (!((GPXObject) gpxFileNode.getUserObject()).isGPXFile()) {
                gpxFileNode = (DefaultMutableTreeNode) gpxFileNode.getParent();
            }
            GPXFile gpxFile = (GPXFile) gpxFileNode.getUserObject();
            */
            GPXFile gpxFile = treeFindGPXFile(currSelection);
            // RFU
            
            treeModel.removeNodeFromParent(currentNode);
            
            if (activeGPXObject.isGPXFile()) { // this is a GPX file
                clearPropsTable();
            	mapPanel.removeGPXFile((GPXFile) activeGPXObject);
                activeGPXObject = null;
            } else {
                if (activeGPXObject.isRoute()) { // this is a route
                    ((GPXFile) parentObject).getRoutes().remove((Route) activeGPXObject);
                } else if (activeGPXObject.isTrack()) { // this is a track
                    ((GPXFile) parentObject).getTracks().remove((Track) activeGPXObject);
                } else if (activeGPXObject.isWaypointGroup()) {
                    WaypointGroup wptGrp = (WaypointGroup) currentNode.getUserObject(); 
                    if (wptGrp.getWptGrpType() == WptGrpType.TRACKSEG) { // track seg
                        ((Track) parentObject).getTracksegs().remove((WaypointGroup) currentNode.getUserObject());
                    } else { // this is a top-level waypoint group
                        ((GPXFile) parentObject).getWaypointGroup().getWaypoints().clear();
                    }
                }
                gpxFile.updateAllProperties();
                tree.setSelectionPath(new TreePath(parentPath));
            } 
            mapPanel.repaint();
        }
    }

    /*
     * update the top level {@link GPXFile} to which the
     * specified {link GPXObject} belongs
     * 
     */
    private void updateGpxFile(GPXObject gpxObject) {
    	// lock!
    	if (gpxObject.isGPXFile()) {
    		gpxObject.updateAllProperties();
    	}
    	// unlock!
    }

    /**
     * thread-safe method to modify GPXObjects in the GPXFiles list
     * and synchronize with tree model.
     * called via PropertyChangeHandlers by user dialogs
     */
    private void handlePropertyChangeEvent(PropertyChangeEvent event) {
    	
    	if (event.getNewValue() != null) {
    		String command = event.getPropertyName();
    		if (command.equals("newGpx")) {
    			// add a new GPXFile
    			GPXFile gpxFile = (GPXFile) event.getNewValue();
    			treeAddGpxFile(gpxFile);    		
    		} else if (command.equals("updateGpx")) {
    			// update all properties for GPXFile
    			GPXFile gpxFile = treeFindGPXFile((GPXObject) event.getNewValue());
    			if (gpxFile == null) {
    				msg.Error("Internal error: modifyGpxFilesTree: object not found");
    			} else {
    				// thread safe?
    				gpxFile.updateAllProperties();
    				updatePropsTable();
    			}
    			
    		} else if (command.equals("deleteGpx")) {
    			msg.VolatileError("handlePropertyChangeEvent: gpxDelete not implemented");
    		}
    	}
    	
    }
    
    /**
     * 
     * @param gpxObject
     */
    private void setActiveGPXObject(GPXObject gpxObject) {
        activeGPXObject = gpxObject;
        gpxObject.setVisible(true);
        mapPanel.fitGPXObjectToPanel(gpxObject);
        updatePropsTable();
    }

    // Region common methods
    // used by both GpsMaster and MapPanel
    
    /**
     * String containing a duration in human readable form
     * "2hr 10min 26sec"
     * @param duration in milliseconds
     * @return
     */
    public String getTimeString(long duration) {
    	// TODO better move to separate (util)class (also used in GPXPanel)
        Period period = new Duration(duration).toPeriod();	// getTimeString(long duration)
		String timeString = String.format("%dhr %dmin %dsec", 
						period.getHours(), period.getMinutes(), period.getSeconds());   
		if (period.getDays() > 0) {
			 timeString = String.format("%dd ", period.getDays()).concat(timeString);  								
		}
		return timeString;
    }
    
    // EndRegion

    // Region Properties Display
    /**
     * 
     * @param extensions
     */
    private void propsDisplayExtensions(Hashtable<String, String> extensions) {
    	
    	if (extensions.size() > 0) {
    		Iterator<String> i = extensions.keySet().iterator();
    		while (i.hasNext()) {
    			String key = i.next();
    			tableModelProperties.addRow(new Object[]{key, extensions.get(key)});
        		extensionIdx.add(tableModelProperties.getRowCount()-1);
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
            tableModelProperties.addRow(new Object[]{"start time", startTimeString});
            tableModelProperties.addRow(new Object[]{"end time", endTimeString});
        }
        
        if (o.getDuration() != 0) {
        	tableModelProperties.addRow(new Object[]{"duration", getTimeString(o.getDuration())});
        }        
        if (o.getDurationExStop() != 0) {
        	tableModelProperties.addRow(new Object[]{"duration ex stop", getTimeString(o.getDurationExStop())});
        }        
        
        if (o.getLengthMeters() > 0) {
        	String distFormat = "%.2f ".concat(uc.getUnit(UNIT.KM));
        	double dist = uc.dist(o.getLengthMeters(), UNIT.KM);
            tableModelProperties.addRow(new Object[]{"distance", String.format(distFormat, dist)});
            
            String speedFormat = "%.1f ".concat(uc.getUnit(UNIT.KMPH));
            double speed = uc.speed(o.getMaxSpeedKmph(), UNIT.KMPH);
            tableModelProperties.addRow(new Object[]{"max speed", String.format(speedFormat, speed)});
            
            if (o.getDuration() > 0) {
            	double avgSpeed = (dist / o.getDuration() * 3600000);	
            	tableModelProperties.addRow(new Object[]{"avg speed", String.format(speedFormat, avgSpeed)});
            }
            if (o.getDurationExStop() > 0) {
            	double avgSpeedEx = (dist / o.getDurationExStop() * 3600000);	
            	tableModelProperties.addRow(new Object[]{"avg speed ex stop", String.format(speedFormat, avgSpeedEx)});
            }        	

        }
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayRiseFall(GPXObject o) {
    	       
    	String formatDist = "%.0f "+uc.getUnit(UNIT.M);
        double grossRise = uc.dist(o.getGrossRiseMeters(), UNIT.M);
        tableModelProperties.addRow(new Object[]{"gross rise", String.format(formatDist, grossRise)});
        double grossFall = uc.dist(o.getGrossFallMeters(), UNIT.M);
        tableModelProperties.addRow(new Object[]{"gross fall", String.format(formatDist, grossFall)});

		long riseTime = o.getRiseTime();
		if (riseTime > 0) {
			tableModelProperties.addRow(new Object[]{"rise time", getTimeString(riseTime)});
		}
		long fallTime = o.getFallTime();
		if (fallTime > 0) {
			tableModelProperties.addRow(new Object[]{"fall time", getTimeString(fallTime)});
		}
                
		String formatSpeed = "%.0f "+uc.getUnit(UNIT.MHR);
        double avgRiseSpeed = uc.speed((grossRise / riseTime) * 3600000, UNIT.MHR);
        if (Double.isNaN(avgRiseSpeed) || Double.isInfinite(avgRiseSpeed)) {
            avgRiseSpeed = 0;
        }
        if (avgRiseSpeed != 0) {
            tableModelProperties.addRow(new Object[]{"avg rise speed", String.format(formatSpeed, avgRiseSpeed)});
        }        
        double avgFallSpeed = uc.speed((grossFall / fallTime) * 3600000, UNIT.MHR);
        if (Double.isNaN(avgFallSpeed) || Double.isInfinite(avgFallSpeed)) {
            avgFallSpeed = 0;
        }
        if (avgFallSpeed != 0) {
            tableModelProperties.addRow(new Object[]{"avg fall speed", String.format(formatSpeed, avgFallSpeed)});
        }
    }
    

    /**
     * 
     * @param o
     */
    private void propsDisplayElevation(GPXObject o) {

    	double eleStart = uc.dist(o.getEleStartMeters(), UNIT.M);
    	String format = "%.0f "+uc.getUnit(UNIT.M);
    	if (eleStart > 0) {
    		tableModelProperties.addRow(new Object[]{"elevation (start)", String.format(format, eleStart)});    		
    	}
    	double eleEnd = uc.dist(o.getEleEndMeters(), UNIT.M);
    	if (eleEnd > 0) {
    		tableModelProperties.addRow(new Object[]{"elevation (end)", String.format(format, eleEnd)});    		
    	}
    	double eleMin = uc.dist(o.getEleMinMeters(), UNIT.M); 
    	tableModelProperties.addRow(new Object[]{"min elevation", String.format(format, eleMin)});
    	double eleMax = uc.dist(o.getEleMaxMeters(), UNIT.M);
    	tableModelProperties.addRow(new Object[]{"max elevation", String.format(format, eleMax)});
    }

    /**
     * 
     * @param o
     */
    private void propsDisplayWaypointGrp(GPXObject o) {
    	WaypointGroup wptGrp = (WaypointGroup) o;
    	tableModelProperties.addRow(new Object[]{"name", wptGrp.getName()});
        tableModelProperties.addRow(new Object[]{"# of pts", wptGrp.getWaypoints().size()});
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayRoute(GPXObject o) {
    	Route route = (Route) o;
    	tableModelProperties.addRow(new Object[]{"name", route.getName()});
        tableModelProperties.addRow(new Object[]{"# of pts", route.getNumPts()});
    }
    /**
     * 
     * @param o
     */
    private void propsDisplayTrack(GPXObject o) {
    	
    	Track track = (Track) o;
    	if (track.getName().isEmpty() == false) {
    		tableModelProperties.addRow(new Object[]{"track name", track.getName()});
    	}
    	if (track.getDesc().isEmpty() == false) {
    		tableModelProperties.addRow(new Object[]{"desc", track.getDesc()});
    	}
    	if (track.getType().isEmpty() == false) {
    		tableModelProperties.addRow(new Object[]{"type", track.getType()});
    	}

    	if (track.getTracksegs().size() > 0) {
    		tableModelProperties.addRow(new Object[]{"segments", track.getTracksegs().size()});
    	}
    	if (track.getNumPts() > 0) {
    		tableModelProperties.addRow(new Object[]{"# of pts", track.getNumPts()});
    	}
        if (track.getNumber() != 0) {
            tableModelProperties.addRow(new Object[]{"track number", track.getNumber()});
        }    	
    }
    
    /**
     * 
     * @param o
     */
    private void propsDisplayGpxFile(GPXObject o) {
    	
    	GPXFile gpxFile = (GPXFile) o;
        tableModelProperties.addRow(new Object[]{"GPX name", gpxFile.getMetadata().getName()});
        if (!gpxFile.getMetadata().getDesc().isEmpty()) {
            tableModelProperties.addRow(new Object[]{"GPX desc", gpxFile.getMetadata().getDesc()});
        }
        if (!gpxFile.getCreator().isEmpty()) {
        	tableModelProperties.addRow(new Object[]{"creator", gpxFile.getCreator()});
        }
        
        // if (!gpxFile.getMetadata().getLink().isEmpty()) {
        // tableModelProperties.addRow(new Object[]{"link", gpxFile.getLink()});
        // }
        String timeString = "";
        if (gpxFile.getMetadata().getTime() != null) {
            Date time = gpxFile.getMetadata().getTime();
            timeString = sdf.format(time);
        }
        tableModelProperties.addRow(new Object[]{"GPX time", timeString}); // show even if empty
        if (gpxFile.getRoutes().size() > 0) {
        	tableModelProperties.addRow(new Object[]{"# of routes", gpxFile.getRoutes().size()});
        }
        if (gpxFile.getTracks().size() > 0) {
        	tableModelProperties.addRow(new Object[]{"# of tracks", gpxFile.getTracks().size()});
        }
        if (gpxFile.getNumWayPts() > 0) {
        	tableModelProperties.addRow(new Object[]{"# of waypoints", gpxFile.getNumWayPts()});
        }
        if (gpxFile.getNumTrackPts() > 0) {
        	tableModelProperties.addRow(new Object[]{"# of trackpoints", gpxFile.getNumTrackPts()});
        }        
        
    }
    
    /**
     * show properties of current GPX object in properties table   
     */
    private void updatePropsTable() {
   
    	if (activeGPXObject != null) {
        	clearPropsTable();

    		if (activeGPXObject.isGPXFile()) {
	    		propsDisplayGpxFile(activeGPXObject);
	            propsDisplayEssentials(activeGPXObject);
	            propsDisplayElevation(activeGPXObject);
	            propsDisplayRiseFall(activeGPXObject);
	            propsDisplayExtensions(activeGPXObject.getExtensions());
	    	} else if (activeGPXObject.isTrack()) {
	    		propsDisplayTrack(activeGPXObject);
	            propsDisplayEssentials(activeGPXObject);
	            propsDisplayElevation(activeGPXObject);            
	            propsDisplayRiseFall(activeGPXObject);
	            propsDisplayExtensions(activeGPXObject.getExtensions());
	    	} else if (activeGPXObject.isRoute()) {
	    		propsDisplayRoute(activeGPXObject);
	    		// ...
	    		propsDisplayElevation(activeGPXObject);
	    	} else if (activeGPXObject.isTrackseg()) {
	    		propsDisplayWaypointGrp(activeGPXObject);
	    		propsDisplayEssentials(activeGPXObject);
	    		propsDisplayElevation(activeGPXObject);
	    		propsDisplayRiseFall(activeGPXObject);
	    		propsDisplayExtensions(activeGPXObject.getExtensions()); // ??
	    	} else if (activeGPXObject.isWaypointGroup()) {
	    		propsDisplayWaypointGrp(activeGPXObject);
	    		propsDisplayElevation(activeGPXObject);
	    		propsDisplayExtensions(activeGPXObject.getExtensions());
	    	}
	    	updatePropTableWidths();
    	}
    	
    }
            
    /**
     * displays the properties of a trackpoint 
     * 
     * @param wpt
     */
    private void propsDisplayTrackpoint(Waypoint wpt) {
    	clearPropsTable();

    	// mandatory
    	tableModelProperties.addRow(new Object[]{"trackpoint #", activeWptGrp.getWaypoints().indexOf(wpt)});
    	tableModelProperties.addRow(new Object[]{"latitude", wpt.getLat()});
    	tableModelProperties.addRow(new Object[]{"longitude", wpt.getLon()});
    	tableModelProperties.addRow(new Object[]{"elevation", wpt.getEle()}); // TODO: meters, unit conversion
    	Date time = wpt.getTime();
    	if (time != null) {
    		tableModelProperties.addRow(new Object[]{"time", sdf.format(time)});
    	}
    	// optional
    	if (wpt.getSat() > 0) { tableModelProperties.addRow(new Object[]{"sat", wpt.getSat()}); }
    	if (wpt.getHdop() > 0) { tableModelProperties.addRow(new Object[]{"hdop", wpt.getHdop()}); }
    	if (wpt.getVdop() > 0) { tableModelProperties.addRow(new Object[]{"vdop", wpt.getVdop()}); }
    	if (wpt.getPdop() > 0) { tableModelProperties.addRow(new Object[]{"pdop", wpt.getPdop()}); }
    	// TODO: also support the remaining ones
    	if (wpt.getName().isEmpty() == false) {
    		tableModelProperties.addRow(new Object[]{"name", wpt.getName()});
    	}
    	if (wpt.getDesc().isEmpty() == false) {
    		tableModelProperties.addRow(new Object[]{"desc", wpt.getDesc()});
    	}
    	propsDisplayExtensions(wpt.getExtensions());
    	lastPropDisplay = System.currentTimeMillis();
    }
    
    /**
     * Dynamically adjusts the widths of the columns in the properties table for optimal display.
     */
    private void updatePropTableWidths() {
        int nameWidth = 0;
        for (int row = 0; row < tableProperties.getRowCount(); row++) {
            TableCellRenderer renderer = tableProperties.getCellRenderer(row, 0);
            Component comp = tableProperties.prepareRenderer(renderer, row, 0);
            nameWidth = Math.max (comp.getPreferredSize().width, nameWidth);
        }
        nameWidth += tableProperties.getIntercellSpacing().width;
        nameWidth += 10;
        tableProperties.getColumn("Name").setMaxWidth(nameWidth);
        tableProperties.getColumn("Name").setMinWidth(nameWidth);
        tableProperties.getColumn("Name").setPreferredWidth(nameWidth);
        
        int valueWidth = 0;
        for (int row = 0; row < tableProperties.getRowCount(); row++) {
            TableCellRenderer renderer = tableProperties.getCellRenderer(row, 1);
            Component comp = tableProperties.prepareRenderer(renderer, row, 1);
            valueWidth = Math.max (comp.getPreferredSize().width, valueWidth);
        }
        valueWidth += tableProperties.getIntercellSpacing().width;
        int tableWidth = valueWidth + nameWidth;
        if (scrollPaneProperties.getVerticalScrollBar().isVisible()) {
            tableWidth += scrollPaneProperties.getVerticalScrollBar().getWidth();
        }
        if (tableWidth > scrollPaneProperties.getWidth()) {
            tableProperties.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            valueWidth += 10;
        } else {
            tableProperties.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            valueWidth = scrollPaneProperties.getWidth() + nameWidth;
        }
        tableProperties.getColumn("Value").setMinWidth(valueWidth);
        tableProperties.getColumn("Value").setPreferredWidth(valueWidth);
    }
    
    /**
     * Clears the properties table.
     */
    private void clearPropsTable() {
        tableModelProperties.setRowCount(0);
    	extensionIdx.clear();
    }
    // EndRegion
    
    /**
     * Common function used by multiple mouse listeners.  An "active waypoint" is one that is moused over
     * as a candidate for an action (for example, deletion or usage as a splitting point). 
     */
    private void updateActiveWpt(MouseEvent e) {
// RFU
    	// if (tglDelPoints.isSelected() || tglSplitTrackseg.isSelected() || tglMeasure.isSelected()) {
    	if (activeGPXObject != null) {  // always highlight waypoint under mouse cursor
            updateActiveWptGrp();
            activeWpt = null;
            if (activeWptGrp != null) {
                mapPanel.setActiveColor(activeWptGrp.getColor());
                Point p = e.getPoint();
                boolean found = false;
                double minDistance = Double.MAX_VALUE;
                // RFU TODO if (measureMode) 
                int start, end;
                if (tglSplitTrackseg.isSelected()) { // don't allow splitting at endpoints
                    start = 1;
                    end = activeWptGrp.getNumPts() - 1;
                } else {
                    start = 0;
                    end = activeWptGrp.getNumPts();
                }
                for (int i = start; i < end; i++) {
                    Waypoint wpt = activeWptGrp.getWaypoints().get(i);
                    Point w = mapPanel.getMapPosition(wpt.getLat(), wpt.getLon(), false);
                    int dx = w.x - p.x;
                    int dy = w.y - p.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < 10 && distance < minDistance) {
                        minDistance = distance;
                        activeWpt = wpt;
                        mapPanel.setShownPoint(w);
                        found = true;
                    }
                }

                if (found) {
                    clearPropsTable();
                    propsDisplayTrackpoint(activeWpt);
                } else {
                    activeWptGrp = null;
                    activeWpt = null;
                    mapPanel.setShownPoint(null);
                }
                mapPanel.repaint();
            } else {
                tglDelPoints.setSelected(false);
                tglSplitTrackseg.setSelected(false);
                tglMeasure.setSelected(false);
            }
        }        
    }
    
    /**
     * Determines which {@link GPXObject}s are active and sets the appropriate variables.
     */
    private void updateActiveWptGrp() {
        activeWptGrp = null;
        activeTracksegNode = null;
        // TODO nullpointer exception here after deleting TRACK from gpxfile
        if (activeGPXObject.isWaypointGroup()) {
            activeWptGrp = (WaypointGroup) activeGPXObject;
            if (activeWptGrp.getWptGrpType() == WptGrpType.TRACKSEG) {
                activeTracksegNode = (DefaultMutableTreeNode) currSelection;
            }
        } else if (activeGPXObject.isRoute()) {
            activeWptGrp = ((Route) activeGPXObject).getPath(); 
        } else if (activeGPXObject.isTrackWithOneSeg()) {
            Track trk = (Track) activeGPXObject;
            activeWptGrp = trk.getTracksegs().get(0);
            activeTracksegNode = (DefaultMutableTreeNode) currSelection.getFirstChild();
        } else if (activeGPXObject.isGPXFile()) {
            GPXFile gpxFile = (GPXFile) activeGPXObject;
            if (gpxFile.isGPXFileWithOneRouteOnly()) { // one route only
                activeWptGrp = gpxFile.getRoutes().get(0).getPath(); 
            } else if (gpxFile.isGPXFileWithOneTracksegOnly()) { // one trackseg only
                Track trk = gpxFile.getTracks().get(0);
                activeWptGrp = trk.getTracksegs().get(0);

                @SuppressWarnings("unchecked")
                // TODO BUG nullpointer exception here
                Enumeration<DefaultMutableTreeNode> children = currSelection.children();
                if (children != null) {
	                DefaultMutableTreeNode trackNode = null;
	                while (children.hasMoreElements()) {
	                    trackNode = children.nextElement();
	                    if (((GPXObject) trackNode.getUserObject()).isTrackseg()) {
	                    	break;
	                	}
	            	}
            	activeTracksegNode = (DefaultMutableTreeNode) trackNode.getFirstChild();
                }
        	}
    	}
        
    }

    /**
     * Registers a list of {@link JToggleButton}s and deselects them all, optionally leaving one selected.
     * Used to prevent multiple toggles from being selected simultaneously.
     */
    private void deselectAllToggles(JToggleButton exceptThisOne) {
        List<JToggleButton> toggles = new ArrayList<JToggleButton>();
        toggles.add(tglAddPoints);
        toggles.add(tglDelPoints);
        toggles.add(tglSplitTrackseg);
        toggles.add(tglLatLonFocus);
        toggles.add(tglPathFinder);
        toggles.add(tglMeasure);
        // toggles.add(tglProgress);
        
        for (JToggleButton toggle : toggles) {
            if (toggle != exceptThisOne && toggle.isSelected()) {
                toggle.setSelected(false);
            }
        }
        
    }
    
    /**
     * Dynamically enables/disables certain toolbar buttons dependent on which type of {@link GPXObject} is active
     * and what operations are allowed on that type of element.
     */
    private void updateButtonVisibility() {
        btnFileNew.setEnabled(true);
        btnFileOpen.setEnabled(true);            
        btnFileSave.setEnabled(false);
        btnObjectDelete.setEnabled(false);
        btnMerge.setEnabled(false);
        tglAddPoints.setEnabled(false);
        tglDelPoints.setEnabled(false);
        tglSplitTrackseg.setEnabled(false);
        btnCorrectEle.setEnabled(false);
        btnEleChart.setEnabled(false);
        btnSpeedChart.setEnabled(false);
        btnEditProperties.setEnabled(false);
        tglPathFinder.setEnabled(false);
        tglMeasure.setEnabled(false);
        tglProgress.setEnabled(false);
        btnTimeShift.setEnabled(false);
        btnPrint.setEnabled(false);
        
        if (currSelection != null) {
            btnFileSave.setEnabled(true);
            btnObjectDelete.setEnabled(true);
            btnMerge.setEnabled(true);
            btnPrint.setEnabled(true);
            
            GPXObject o = activeGPXObject;
            
            if (o.isRoute() || o.isWaypoints() || o.isGPXFileWithOneRoute() || o.isGPXFileWithNoRoutes()) {
                tglAddPoints.setEnabled(true);
            }
            if (o.isTrackseg() || o.isTrackWithOneSeg() || o.isRoute() || o.isWaypoints()
                    || o.isGPXFileWithOneTracksegOnly() || o.isGPXFileWithOneRouteOnly()) {
                tglDelPoints.setEnabled(true);

                btnEleChart.setEnabled(true);
            }
            if (o.isTrackseg() || o.isTrackWithOneSeg()  || o.isGPXFileWithOneTracksegOnly()){
                tglSplitTrackseg.setEnabled(true);
            }
            if (o.isTrackseg() || o.isTrackWithOneSeg() || o.isGPXFileWithOneTracksegOnly()) {
                btnSpeedChart.setEnabled(true);
            }
            if (o.isGPXFile() || o.isRoute() || o.isTrack()) {
                btnEditProperties.setEnabled(true);
            }
            if (o.isRoute() || o.isGPXFileWithOneRoute() || o.isGPXFileWithNoRoutes()) {
                tglPathFinder.setEnabled(true);
            }
            if (o.isTrackseg() || o.isTrack() || o.isGPXFile()) {
                tglMeasure.setEnabled(true);
                tglProgress.setEnabled(true);
                btnCorrectEle.setEnabled(true);
                
                // btnTimeShift.setEnabled(true); // as soon as "shift by delta" is implemented
            }
            if (o.isTrackseg()) {
            	btnTimeShift.setEnabled(true);
            }
        }
        
        if (fileIOHappening) {
            btnFileNew.setEnabled(false);
            btnFileOpen.setEnabled(false);            
            btnFileSave.setEnabled(false);
            btnObjectDelete.setEnabled(false);
        }
    }
    
    /**
     * Builds the selected chart type and displays the new window frame.
     */
    private void buildChart(String chartName, String iconPath) {
        if (activeGPXObject != null) {
            updateActiveWptGrp();
            if (activeWptGrp != null) {
                DefaultMutableTreeNode gpxFileNode = currSelection;
                while (!((GPXObject) gpxFileNode.getUserObject()).isGPXFile()) {
                    gpxFileNode = (DefaultMutableTreeNode) gpxFileNode.getParent();
                }
                GPXFile gpxFile = (GPXFile) gpxFileNode.getUserObject();
                JFrame f = null;
                if (chartName.equals("Elevation profile")) {
                    f = new ElevationChart(chartName, gpxFile.getMetadata().getName(), activeWptGrp, uc);
                } else if (chartName.equals("Speed profile")) {
                    f = new SpeedChart(chartName, gpxFile.getMetadata().getName(), activeWptGrp, uc);
                } else {
                    return; // invalid chart name given
                }
                
                ImageIcon icon = new ImageIcon(GpsMaster.class.getResource(iconPath));
                f.setIconImage(icon.getImage());
                f.setSize(frame.getWidth() - 150, frame.getHeight() - 100);
                f.setLocationRelativeTo(frame);
                f.setVisible(true);
            }
        }
    }
    
    /**
     * Displays the edit properties dialog and saves the user-selected values to the active {@link GPXObject}. 
     */
    private void editProperties() {
        EditPropsDialog dlg = new EditPropsDialog(frame, "Edit properties", activeGPXObject);
        dlg.setVisible(true);
         if (activeGPXObject.isGPXFile()) {
        	GPXFile gpx = (GPXFile) activeGPXObject;
        	if (dlg.getActivity() != null) {
	        	if (gpx.getExtensions().containsKey("activity")) {
	        		gpx.getExtensions().remove("activity");        		
	        	}
	        	gpx.getExtensions().put("activity", dlg.getActivity());
	        }
	        if (dlg.getName() != null) {
	            gpx.getMetadata().setName(dlg.getName());
	        }
	        if (dlg.getDesc() != null) {
	            gpx.getMetadata().setDesc(dlg.getDesc());
	        }
        } else {
            if (dlg.getName() != null) {
                activeGPXObject.setName(dlg.getName());
            }
            if (dlg.getDesc() != null) {
                activeGPXObject.setDesc(dlg.getDesc());
            }
        }
        
        if (activeGPXObject.isRoute()) {
            if (dlg.getGPXType() != null) {
                ((Route) activeGPXObject).setType(dlg.getGPXType());
            }
            if (dlg.getNumber() != null) {
                ((Route) activeGPXObject).setNumber(dlg.getNumber());
            }
        }
        if (activeGPXObject.isTrack()) {
            if (dlg.getGPXType() != null) {
                ((Track) activeGPXObject).setType(dlg.getGPXType());
            }
            if (dlg.getNumber() != null) {
                ((Track) activeGPXObject).setNumber(dlg.getNumber());
            }
        }
        treeModel.nodeChanged(currSelection);
        updatePropsTable();
    }


    /**
     * apply color modifications to a {@link GPXFile}
     * use a different color for each track, if there
     * is more than one track.
     */
    private void adjustColors(GPXFile gpxFile) {
    	
    	for (Track track: gpxFile.getTracks()) {
    		// TODO: how to color tracks
    		
	    	// if a track contains multiple segments, 
	    	// color them in different shades           
    		if (track.getTracksegs().size() > 1) {
    			int delta = 25;
    			int red = track.getColor().getRed();
    			int green = track.getColor().getGreen();
    			int blue = track.getColor().getBlue();
    		    // increase every "Black" channel by delta 
    			// decrease every "White" channel by delta
    			for (WaypointGroup seg: track.getTracksegs()) {    				
    			    if (track.getColor().getRed() == 0)
    			    {
    			    	red = Math.min(red + delta, 255);	
    			    } else {
    			    	red = Math.max(red - delta, 0);
    			    }
    			    if (track.getColor().getGreen() == 0)
    			    {
    			    	green = Math.min(green + delta, 255);	
    			    } else {
    			    	green = Math.max(green - delta, 0);
    			    }
    			    if (track.getColor().getBlue() == 0)
    			    {
    			    	blue = Math.min(blue + delta, 255);	
    			    } else {
    			    	blue = Math.max(blue - delta, 0);
    			    }
    				 
    			    Color newColor = new Color(red, green, blue);  		
    				seg.setColor(newColor);
    			}
    		}
    	}
    }

    /**
     * retrieve GPS data from a connected device.
     */
    private void getFromDevice() {
    	        	
    	try {
			MoveBikeCompMPT move = new MoveBikeCompMPT();
			move.getConnectionParams().put("DBFILE", "D:\\Projekte\\Touring\\Temp\\bikecomp.db");
			move.connect();
			for (TrackEntry entry : move.getTracklist()) {
				System.out.println(entry.GetId());
				if ((entry.GetId() >= 166) && (entry.GetId() <= 170)) {
					GPXFile gpx = move.load(entry);
					treeAddGpxFile(gpx);							
				}
			}
			move.disconnect();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    /**
     * 
     */
    private void showInfo() {
    	
    	InfoDialog dlg = new InfoDialog(frame);
    	dlg.setVisible(true);
    	
    }
    
    /**
     * Merges all visible Files/Tracks/Track Segments into a new GPXFile
     * TODO (optionally) merge into a single TrackSeg 
     */
    private void doMerge() {
    
    	Track newTrack = null;
    	int trackNumber = 0;
    	
    	MessagePanel msgMerge = msg.InfoOn("Merging ...", new Cursor(Cursor.WAIT_CURSOR));    	
        GPXFile newFile = new GPXFile();
        newFile.getMetadata().setName("merged GPX");

        for (GPXFile file: mapPanel.getGPXFiles()) {
    	   if (file.isVisible()){
     		   
    		   for (Track track: file.getTracks()) {
    			   if (track.isVisible())
    			   {
    				   trackNumber++;
    				   newTrack = new Track(track.getColor()); // better: construct new color based on old one(s)
    				   newTrack.setName(track.getName());
    				   newTrack.setType(track.getType());
    				   newTrack.setDesc(track.getDesc());
    				   newTrack.setNumber(trackNumber);
    				   
    				   for (WaypointGroup trackSeg: track.getTracksegs()) {
    					   if (trackSeg.isVisible()) { 					
    						   newTrack.getTracksegs().add(new WaypointGroup(trackSeg));
    					   }
    				   }
    				   if (newTrack.getTracksegs().isEmpty() == false) {
    					   newFile.getTracks().add(newTrack);
    				   }
    			   }  			   
    		   }
    		   
    		   // TODO same for routes
    		   
    		   // waypoints
    		   if (file.getWaypointGroup().isVisible()) {
    			   newFile.getWaypointGroup().setName(file.getWaypointGroup().getName()); // only last one is set!
    			   newFile.getWaypointGroup().setDesc(file.getWaypointGroup().getDesc()); // only last one is set!
    			   for (Waypoint wp: file.getWaypointGroup().getWaypoints()) {
    				   newFile.getWaypointGroup().addWaypoint(new Waypoint(wp));
    			   }
    		   }    		   
    	   }
        }

        if (newFile.getTracks().isEmpty() == false) { // check also for routes & waypoints
        	newFile.updateAllProperties();
        	treeAddGpxFile(newFile);
        }
        msg.InfoOff(msgMerge);
    }

    
    /*
     * handles the selection of a {@link Waypoint} 
     * when the {@link tglMeasure} button is enabled 
     */
    private void doMeasure(Waypoint waypoint) {
      	// TODO calculate distance & duration across track segments

    	Waypoint wpt1;
    	Waypoint wpt2;
    	double distance = 0;
    	
    	String unit = uc.getUnit(UNIT.KM);
    	String format = "distance %1$.2f "+unit+", direct %2$.2f "+unit+", duration ";
    	
    	List<Waypoint> list = mapPanel.getMarkerPoints();
    	if (list.contains(waypoint)) { // deselect waypoint
    		list.remove(waypoint);
    		mapPanel.repaint();
    	} else {
    		if (list.size() < 2) {
    			list.add(waypoint);   // waypoint selected
    			mapPanel.repaint();
    		}
    	}

    	// update measurements display
    	if (mapPanel.getMarkerPoints().size() == 2) {
    		// order: wpt1 is earliest
    		if (list.get(0).getTime().compareTo(list.get(1).getTime()) < 0) {
    			wpt1 = list.get(0);
    			wpt2 = list.get(1);
    		} else {
    			wpt1 = list.get(1);
    			wpt2 = list.get(0);
    		}
    		
    		int start = activeWptGrp.getWaypoints().indexOf(wpt1);
    		int end = activeWptGrp.getWaypoints().indexOf(wpt2);
    		if (start == -1 || end == -1)
    		{
    			msg.Error("internal error: waypoint not in WaypointGroup");
    		} else {
    			// distance
    			
    			Waypoint prev = activeWptGrp.getWaypoints().get(start);
    			for (int i = start; i <= end; i++) {
    				Waypoint curr = activeWptGrp.getWaypoints().get(i);
    	    		distance += curr.getDistance(prev);
    	    		prev = curr;
    			}
    			// as the crow flies
    			double direct = wpt1.getDistance(wpt2);
    			
				DateTime startTime = new DateTime(wpt1.getTime());
				DateTime endTime = new DateTime(wpt2.getTime());
				Period period = new Duration(startTime, endTime).toPeriod(); 					
				String timeString = String.format("%02d:%02d:%02d", 
						period.getHours(), period.getMinutes(), period.getSeconds());   
				if (period.getDays() > 0) {
					 timeString = String.format("%dd ", period.getDays()).concat(timeString);  								
				}
				System.out.println(distance);
    			String text = String.format(format, uc.dist(distance, UNIT.KM), uc.dist(direct, UNIT.KM)).concat(timeString);
    			msgMeasure.setText(text);
    		}   		
    	}
    	
    }

    /**
     * open timeshift dialog
     * TODO receive events for
     */
    private void doTimeShift() {
    	try {
	    	TimeshiftDialog dlg = new TimeshiftDialog(this.frame, "Timeshift", activeGPXObject);
	    	dlg.setVisible(true);
	    	activeGPXObject.updateAllProperties();
	    	updatePropsTable();
	    	if (progressType != ProgressType.NONE) {
	    		frame.repaint();
	    	}
    	} catch (Exception e) {
    		msg.Error(e);
    	}
    }
    

    /**
     * helper method for color pre-processing
     * save current color as extension
     * @param o {@link GPXObject} to process
     */
    private void preColor(GPXObject o) {
    	if (o.getExtensions().contains("color")) {
    		o.getExtensions().remove("color");
    	}
    	Color c = o.getColor();
    	String colorString = String.format("%d,%d,%d,%d", c.getRed(),c.getGreen(), c.getBlue(), c.getAlpha());
    	o.getExtensions().put("color", colorString);    	
    }
    
    /**
     * process {@link GPXFile} before saving
     * (save colors as extended attributes)
     */
    private void preSave(GPXFile gpx) {
    	// if file was originally created by another device,
    	// save this info as extension
    	if (gpx.getCreator().isEmpty()) {
    		gpx.setCreator(me);
    	} else if (gpx.getCreator().equals(me) == false) {
    		gpx.getExtensions().put("originator", gpx.getCreator());
    		gpx.setCreator(me);
    	}
    	if (conf.useExtensions()) {
	    	// save colors as extensions
	    	preColor(gpx);
	    	for (Track track : gpx.getTracks()) {
	    		preColor(track);
	    		for (WaypointGroup seg : track.getTracksegs()) {
	    			preColor(seg);
	    		}    		
	    	}
	    	for (Route route : gpx.getRoutes()) {
	    		preColor(route);
	    	}
    	}
    }

    /**
     * retrieve color from extended attributes and set it
     * (if applicable)
     * @param o
     */
    private void postColor(GPXObject o) {
    	Color color = null;
    	if (o.getExtensions().containsKey("color")) {
    		String colorString = o.getExtensions().get("color");
    		String[] rgba = colorString.split(",");
    		if (rgba.length == 4) {
    			try {
    				int r = Integer.parseInt(rgba[0]);
    				int g = Integer.parseInt(rgba[1]);
    				int b = Integer.parseInt(rgba[2]);
    				int a = Integer.parseInt(rgba[3]);
    				color = new Color(r, g, b, a);
    			} catch (NumberFormatException e) {
    				
    			}    			    			
    		} else {
    			color = Color.getColor(colorString);
    		}
    	}
    	if (color != null) {
    		o.setColor(color);
    	} // else show warning?
    }
    
    /**
     * post-process {@link GPXFile} after loading
     * (set GpsMaster specific values according to extensions)
     */
    private void postLoad(GPXFile gpx) {
    	if (gpx.getMetadata().getTime() == null) {
			// try to set date to date of first waypoint
			if (gpx.getTracks().size() > 0) {
				Track track = gpx.getTracks().get(0);
				if (track.getTracksegs().size() > 0) {
					gpx.getMetadata().setTime(track.getTracksegs().get(0).getStart().getTime());
				}   					   					
			}
    	}
    	postColor(gpx);
    	for (Track track : gpx.getTracks()) {
    		postColor(track);
    		for (WaypointGroup seg : track.getTracksegs()) {
    			postColor(seg);
    		}    		
    	}
    	for (Route route : gpx.getRoutes()) {
    		postColor(route);
    	}
    }

    
    private void cleanTrackSegment(Track track, WaypointGroup wptGrp) {
    	    	
    	double distance = 0;
    	
    	if (wptGrp.getWaypoints().size() > 2) {

    		WaypointGroup newSegment = track.addTrackseg();
        	newSegment.setName(wptGrp.getName()+" (cleaned)");
        	List<Waypoint> waypoints = wptGrp.getWaypoints(); // shortcut

		    Waypoint prev = waypoints.get(0);
	    	for (int i = 1; i < waypoints.size(); i++) {
	    		Waypoint wpt = waypoints.get(i);
	    		distance += wpt.getDistance(prev);
	    		if (distance > conf.getCleaningDistance()) {
//		    		System.out.println(String.format("%d %f", i, distance));
	    			newSegment.addWaypoint(wpt);
	    			distance = 0;	    			
	    		}

	    		prev = wpt;
	    	}	
    		track.updateAllProperties();
    		try {
				treeAddTrackSegment(track, newSegment);
			} catch (UnknownObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    }
    
    /**
     * print the visible contents of the map panel 
     */
    private void printVisibleMap() {
    	
    	PrinterJob job = PrinterJob.getPrinterJob();
    	job.setJobName("GpsMaster");
    	    	
    	if (job.printDialog()) {
    		MapPrinter mapPrinter = new MapPrinter();
    		mapPrinter.setMapPanel(mapPanel);
    		job.setPrintable(mapPrinter);
    		try {
    			job.print();
    		} catch (PrinterException e) {
    			msg.Error(e);
    		}
    	}
    		
    	
    }
    
    /**
     * 
     */
    private void saveMapToImage() {
    	
    	int width = mapPanel.getWidth();
    	int height = mapPanel.getHeight();
    	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	mapPanel.paint(img.getGraphics());
    	try {
			ImageIO.write(img, "png", new File("mapPanel.png"));
		} catch (IOException e) {
			msg.Error(e);
		}
    }
    
    /*
     * Load configuration from file
     */
    private void loadConfig() {
       	try {
       		FileInputStream inputStream = new FileInputStream(configFilename);
    		JAXBContext context = JAXBContext.newInstance(Config.class);
    		Unmarshaller u = context.createUnmarshaller();
    		conf = (Config) u.unmarshal(inputStream);
    		inputStream.close();
    	} catch (FileNotFoundException e) {            
            msg.VolatileWarning("Configuration file not found, using defaults.");
    	} catch (JAXBException e) {
    		// JAXBException.getMessage() == null !!
    		msg.VolatileWarning("Unable to parse configuration file, using defaults. existing file will be overwritten on exit.");
		} catch (IOException e) {
			msg.Error(e);
		} finally {
			if (conf == null) {
				conf = new Config();
			}
		}
    }
    
    /*
     * saves the current configuration to file
     */
    private void saveConfig() {
    	
        Point mapCenter = mapPanel.getCenter();
        double lat = OsmMercator.YToLat(mapCenter.y, mapPanel.getZoom());
        double lon = OsmMercator.XToLon(mapCenter.x, mapPanel.getZoom());     
        conf.setLon(lon);
        conf.setLat(lat);
        conf.setPositionZoom(mapPanel.getZoom());
        
    	// TODO bugfix: why is zoom level / deviceconfig not saved?
        
        // temp
        // conf.setShowWarning(true);
        // conf.setUseExtensions(false);
/*
        DeviceConfig deviceConfig = new DeviceConfig();
        deviceConfig.setName("MoveBikeComputer@Galaxy S3");
        deviceConfig.setDescription("get Tracks recorded with Move!BikeComputer on GT-I9300 via MTP");
        deviceConfig.setLoaderClass("org.gpsmaster.device.MoveBikeCompMPT");
        deviceConfig.getConnectionParams().put("DBFILE", "Internal storage/bikeComputer/database/bikecomp.db");
        deviceConfig.getConnectionParams().put("DEVICE", "GT-I9300");
        conf.getDeviceLoaders().add(deviceConfig);
*/
        
    	try {    		
			FileOutputStream outStream = new FileOutputStream(configFilename);
			JAXBContext context = JAXBContext.newInstance(Config.class);
			Marshaller m = context.createMarshaller();
			
			m.marshal(conf, outStream);    	
			} catch (Exception e) {   		
				msg.VolatileWarning("Error saving configuration", e);           
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ie) {
            }
    	}
    }

    /**
     * 
     */
    private void correctElevation() {

		PropertyChangeListener listener = new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				System.out.println(event.getPropertyName());
				handlePropertyChangeEvent(event);				
			}
		};
		
		btnCorrectEle.setEnabled(false);
    	ElevationDialog dlg = new ElevationDialog(mapPanel, activeGPXObject, msg);		
    	dlg.setChangeListener(listener);
		dlg.setVisible(true);
		dlg.runCorrection();
    }

    /**
     * Sets a flag to synchronize I/O operations with {@link GPXFile}s.  Must be called before and after each I/O.
     */
    private void setFileIOHappening(boolean happening) {
        fileIOHappening = happening;
        updateButtonVisibility();
    }

    /**
     * 
     * @param wptGrp
     */
    private void colorByElevation(WaypointGroup wptGrp) {
    	
    	Hypsometric hypso = new Hypsometric();
    	Color prev = Color.BLACK;
    	for (Waypoint wpt : wptGrp.getWaypoints()) {
    		Color color = hypso.getColor((int) wpt.getEle());
    		if (color != prev ) {
    			System.out.println(color);
    			wpt.setSegmentColor(color);
    		}
    		prev = color;
    	}    	
    }
    
	private void doDebug() {

		/*
		if (activeGPXObject.isTrackseg()) {
			colorByElevation((WaypointGroup) activeGPXObject);
		}
		
		*/
		PropertyChangeListener testListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO Auto-generated method stub
				System.out.println(evt.getPropertyName());
				
			}
		};
		

		MergeDialog dlg = new MergeDialog(frame, mapPanel.getGPXFiles(), msg);
		dlg.addPropertyChangeListener(testListener);
		dlg.setVisible(true);
		
		
		/*
		if (activeGPXObject.isTrack()) {
			Track track = (Track) activeGPXObject;
			// cleanTrackSegment(track, track.getTracksegs().get(0));
			List<Waypoint> waypoints = track.getTracksegs().get(0).getWaypoints();
			int i = 0;
			while (i < waypoints.size()) {
				waypoints.get(i).setSegmentColor(
						new Color((int) (Math.random() * 255), 
								  (int) (Math.random() * 255), 
								  (int) (Math.random() * 255)));
				i = i + 100;
			}

		}
		mapPanel.repaint();
		*/
				
		/*		
		ImageIcon icon = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/waypoint.png"));
		GPXFile gpx = ((GPXFile) activeGPXObject);
		Track track = gpx.getTracks().get(0);
		WaypointGroup seg = track.getTracksegs().get(0);
		for (int i = 0; i < seg.getWaypoints().size(); i = i + 20) {
			Waypoint wpt = seg.getWaypoints().get(i);
			ClickableMarker marker = new ClickableMarker(wpt);
			marker.setImage(icon);
 			marker.addMouseListener(listener);
			mapPanel.getMarkers().put(wpt, marker);
		}
		
*/
		
    	/*
    	StringWriter writer = new StringWriter();
    	GPXFile gpx = (GPXFile) activeGPXObject;
    	try {
			JAXBContext context = JAXBContext.newInstance(GPXFile.class);
			Marshaller m = context.createMarshaller();
			m.marshal(gpx, writer);
			System.out.println(writer);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

    }

}
