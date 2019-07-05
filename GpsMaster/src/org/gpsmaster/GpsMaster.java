package org.gpsmaster;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
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
import java.io.StringWriter;
import java.rmi.activation.UnknownObjectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
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
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gpsmaster.UnitConverter;
import org.gpsmaster.PathFinder.PathFindType;
import org.gpsmaster.cleaning.Gaussian;
import org.gpsmaster.device.MoveBikeCompMPT;
import org.gpsmaster.gpsloader.FileDropHandler;
import org.gpsmaster.gpsloader.GpsLoader;
import org.gpsmaster.gpsloader.GpsLoaderFactory;
import org.gpsmaster.gpsloader.GpxLoader;
import org.gpsmaster.gpxpanel.ArrowType;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.GPXPanel;
import org.gpsmaster.gpxpanel.ProgressType;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;
import org.gpsmaster.markers.Marker;
import org.gpsmaster.markers.PhotoMarker;
import org.gpsmaster.markers.WaypointMarker;
import org.gpsmaster.markers.WikiMarker;
import org.gpsmaster.online.DownloadGpsies;
import org.gpsmaster.online.DownloadOsm;
import org.gpsmaster.online.GetWikipedia;
import org.gpsmaster.online.OnlineTrack;
import org.gpsmaster.online.UploadGpsies;
import org.gpsmaster.tree.GPXTree;
import org.gpsmaster.tree.GPXTreeRenderer;
import org.gpsmaster.dialogs.BrowserLauncher;
import org.gpsmaster.dialogs.CleaningDialog;
import org.gpsmaster.dialogs.EditPropsDialog;
import org.gpsmaster.dialogs.ElevationDialog;
import org.gpsmaster.dialogs.GenericDownloadDialog;
import org.gpsmaster.dialogs.ImageViewer;
import org.gpsmaster.dialogs.InfoDialog;
import org.gpsmaster.dialogs.TimeshiftDialog;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.LinkType;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.util.Hypsometric;

/**
 *
 * The main application class for GPS Master, a GUI for manipulating files containing GPS data.<br />
 * Based on GPX Creator by Matt Hoover, extended by Rainer Fügenstein
 * More info at
 * 		www.gpsmaster.org
 * 		www.gpxcreator.com
 *
 * @author hooverm
 * @author rfuegen
 *
 */
@SuppressWarnings("serial")
public class GpsMaster extends JComponent {

	public static final String PROGRAM_NAME = "GpsMaster";
	public static final String VERSION_NUMBER = "0.60.15";
	public static final String ORIGINATOR = "GpsMaster 0.5x";

    // indents show layout hierarchy
    private JFrame frame;
    private JPanel glassPane;  // TODO remove after routing code consolidation
    private static String lookAndFeel;
    	private JPanel menuBarsPanel;

        private JToolBar toolBarMain;       // NORTH
            private JButton btnFileNew;
            private JButton btnFileOpen;
            private JToggleButton tglDownload;
            private JFileChooser chooserFileOpen;
            private JButton btnFileSave;
            private JButton btnPrint;
            private JFileChooser chooserFileSave;
            private File fileSave;
            private JButton btnObjectDelete;
            private JButton btnEditProperties;
            private JToggleButton tglPathFinder;
            private SwingWorker<Void, Void> pathFindWorker;
            private JToggleButton tglAddPoint;
            private JToggleButton tglDelPoints;
            private JToggleButton tglSplitTrackseg;
            private JToggleButton tglMeasure;
            private JToggleButton tglProgress;
            private JToggleButton tglArrows;
            private JButton btnTimeShift;
            private JButton btnCorrectEle;
            private JButton btnEleChart;
            private JButton btnSpeedChart;
            private JToggleButton tglToolbar;
            private JButton btnInfo;
            private JComboBox<String> comboBoxTileSource;
            private JLabel lblLat;
            private JTextField textFieldLat;
            private JLabel lblLon;
            private JTextField textFieldLon;
            private JToggleButton tglLatLonFocus;
            private JToggleButton tglAutoFit;
        private JToolBar toolBarSide;
    		private JButton btnCleaning;
        	private JButton btnRemoveTime;
        	private JButton btnMergeOneToOne;
        	private JButton btnMergeToMulti;
        	private JButton btnMergeToSingle;
        	private JButton btnMergeParallel;
            private JToggleButton tglAddWaypoint;
        private JToolBar toolBarDownload;
        	private JButton btnDeviceOpen;
        	private JButton btnDownloadOsm;
        	private JButton btnDownloadGpsies;
        	private JButton btnUploadGpsies;
        	private JButton btndownloadWiki;
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
                private JPanel containerLeftSidebarBottom;    // BOTTOM
                    private JPanel containerPropertiesHeading;
                        private JLabel labelPropertiesHeading;
                    private JScrollPane scrollPaneProperties;
                        private PropsTableModel propsTableModel;
                        private JTable tableProperties;
            private JSplitPane splitPaneMap;

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
	            private JPanel chartPanel;

	private Container contentPane;
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
    private Core core = new Core();
    private MessageCenter msg = null;
    private Config conf;
    public UnitConverter uc = new UnitConverter();
    private String configFilename = "./GpsMaster.config";
    private ProgressType progressType = ProgressType.NONE;
    private ArrowType arrowType = ArrowType.NONE;

    private GpsLoaderFactory loaderFactory = new GpsLoaderFactory();
    private ActivityHandler activityHandler = null;
    private ImageViewer imageViewer = null;
    private MeasureThings measure = null;
    private PropertyChangeListener propertyListener = null;
    private WindowAdapter windowListener = null;
    private FileDropHandler dropHandler = null;
    // List containing all JToggleButtons which are mutually exclusive:
    private List<JToggleButton> toggles = new ArrayList<JToggleButton>();
    private Timer timer = null;
    private boolean autoFitToPanel = true;
    // semaphores for SwingWorker() jobs
    private boolean fileIOHappening = false;
    private boolean downloadHappening = false;
    // globally defined members to be passed as parameters to SwingWorker() jobs
    private MessagePanel msgRouting = null;

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
     * Initialize the contents of the parentFrame.
     */
    private void initialize() {

    	final String iconPath = "/org/gpsmaster/icons/";

        /* MAIN FRAME
         * --------------------------------------------------------------------------------------------------------- */
        frame = new JFrame("GPS Master");
        contentPane = frame.getContentPane();

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
        frame.setIconImage(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("gpsmaster.png"))).getImage());

        /* TIMER ACTION LISTENER (for future use)
         * -------------------------------------------------------------------------------------------------------- */
        ActionListener timerListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
            	if (actionEvent.getSource() == timer) {
            		// ...
            	}
            }
        };

        timer = new Timer(5000, timerListener);
        timer.setInitialDelay(5000);
        // timer.start();


        /* CENTRAL PROPERTY CHANGE LISTENER
         * -------------------------------------------------------------------------------------------------------- */
        propertyListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				handlePropertyChangeEvent(e);
			}
		};

        /* CENTRAL WINDOW/COMPONENT CHANGE LISTENER
         * -------------------------------------------------------------------------------------------------------- */
		windowListener = new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
				handleWindowEvent(e, WindowEvent.WINDOW_CLOSING);
			}
        	@Override
        	public void windowClosed(WindowEvent e) {
				handleWindowEvent(e, WindowEvent.WINDOW_CLOSED);
			}
		};


        /* LOAD & APPLY CONFIG
    	 * --------------------------------------------------------------------------------------------------------- */
        msg = new MessageCenter(frame);
		loadConfig();
    	uc.setOutputSystem(conf.getUnitSystem());
    	msg.setScreenTime(conf.getScreenTime());

        setupPanels();
        setupMenuBar();
        setupDownloadBar();
        setupToolbar();

        if (conf.getActivitySupport()) {
        	activityHandler = new ActivityHandler(mapPanel, contentPane, msg);
    		activityHandler.getWidget().setAlignmentY(TOP_ALIGNMENT);
        }

        dropHandler = new FileDropHandler(msg);
        dropHandler.addPropertyChangeListener(propertyListener);
        mapPanel.addPropertyChangeListener(propertyListener);
        mapPanel.setTransferHandler(dropHandler);
    }

	private void setupPanels() {

		final String iconPath = "/org/gpsmaster/icons/";

        /* MENUBARS PANEL
         * --------------------------------------------------------------------------------------------------------- */

		menuBarsPanel = new JPanel();
		menuBarsPanel.setLayout(new BorderLayout());
		contentPane.add(menuBarsPanel, BorderLayout.NORTH);

        /* MAIN SPLIT PANE
         * --------------------------------------------------------------------------------------------------------- */
        splitPaneMain = new JSplitPane();
        splitPaneMain.setContinuousLayout(true);
        contentPane.add(splitPaneMain, BorderLayout.CENTER);

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

		/* MAP PANEL
         * --------------------------------------------------------------------------------------------------------- */
        mapPanel = new GPXPanel(uc, msg);
        mapPanel.setLayout(new BoxLayout(mapPanel, BoxLayout.X_AXIS));
        mapPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        mapPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mapPanel.setDisplayPositionByLatLon(conf.getLat(),
        		conf.getLon(), conf.getPositionZoom());
        mapPanel.setZoomContolsVisible(conf.getZoomControls());
        mapPanel.setProgressType(ProgressType.NONE);
        mapPanel.setArrowType(ArrowType.NONE);
        mapPanel.setLineWidth(conf.getTrackLineWidth());

        try {
            mapPanel.setTileLoader(new OsmFileCacheTileLoader(mapPanel));
        } catch (Exception e) {
            msg.error("There was a problem constructing the tile cache on disk", e);
        }
        // splitPaneMain.setRightComponent(mapPanel);

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
                        msg.infoOff(msgRouting);
                        mapPanel.repaint();
                        updatePropsTable();
                    } catch (Exception ex) {
                    	msg.error(ex);
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

        ImageIcon collapsed = new ImageIcon(GpsMaster.class.getResource(iconPath.concat("tree-collapsed.png")));
        ImageIcon expanded = new ImageIcon(GpsMaster.class.getResource(iconPath.concat("tree-expanded.png")));
        UIManager.put("Tree.collapsedIcon", collapsed);
        UIManager.put("Tree.expandedIcon", expanded);

        // give Java look and feel to tree only (to get rid of dotted line handles/connectors)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            SwingUtilities.updateComponentTreeUI(tree);
        } catch (Exception e) {
            msg.error(e);
        }
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
        	msg.error(e);
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
        propsTableModel = new PropsTableModel(uc);
        tableProperties = new JTable(propsTableModel);
        tableProperties.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableProperties.setAlignmentY(Component.TOP_ALIGNMENT);
        tableProperties.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableProperties.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableProperties.setFillsViewportHeight(true);
        tableProperties.setTableHeader(null);
        tableProperties.setEnabled(false);
        tableProperties.getColumnModel().setColumnMargin(0);
        propsTableModel.setTable(tableProperties);

        /* PROPERTIES TABLE SCROLLPANE
         * --------------------------------------------------------------------------------------------------------- */
        scrollPaneProperties = new JScrollPane(tableProperties);
        scrollPaneProperties.setAlignmentY(Component.TOP_ALIGNMENT);
        scrollPaneProperties.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPaneProperties.setBorder(new LineBorder(new Color(0, 0, 0)));
        containerLeftSidebarBottom.add(scrollPaneProperties);

        /* CHARTPANEL
        /* --------------------------------------------------------------------------------------------------------- */
        chartPanel = new JPanel(); // JFreeChart ....
        chartPanel.setMinimumSize(new Dimension(mapPanel.getWidth(), 0));
        chartPanel.setSize(mapPanel.getWidth(), 0);
        chartPanel.setPreferredSize(new Dimension(mapPanel.getWidth(), 0));

        /* MAP & CHART PANEL SPLITPANE
         * --------------------------------------------------------------------------------------------------------- */
        splitPaneMap = new JSplitPane();
        splitPaneMap.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPaneMap.setTopComponent(mapPanel);
        // splitPaneMap.setBottomComponent(chartPanel);
        splitPaneMap.setBottomComponent(null);
        splitPaneMap.resetToPreferredSizes();
        splitPaneMap.setDividerLocation(0.80);  // ???

        splitPaneMain.setRightComponent(splitPaneMap);
	}

	/**
	 *
	 */
	private void setupDownloadBar() {

		final String iconPath = "/org/gpsmaster/icons/downloadbar/";

		toolBarDownload = new JToolBar();
		toolBarDownload.setVisible(false);

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
	    btnDeviceOpen.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-device.png"))));
	    btnDeviceOpen.setDisabledIcon(
	            new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-device-disabled.png"))));
	    String ctrlDev = "CTRL+G";
	    mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	            KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK), ctrlDev);
	    mapPanel.getActionMap().put(ctrlDev, new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	getFromDevice();
	        }
	    });
	    toolBarDownload.add(btnDeviceOpen);
	    btnDeviceOpen.setEnabled(false); // remove after implementation

	    /* DOWNLOAD FROM OSM BUTTON
	     * --------------------------------------------------------------------------------------------------------- */
	    btnDownloadOsm = new JButton("");
	    btnDownloadOsm.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	downloadOsm();
	        }
	    });

	    btnDownloadOsm.setToolTipText("<html>Download Relation from OSM<br>[CTRL+R]</html>");
	    btnDownloadOsm.setFocusable(false);
	    btnDownloadOsm.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("download-osm.png"))));
	    btnDownloadOsm.setDisabledIcon(
	            new ImageIcon(GpsMaster.class.getResource(iconPath.concat("download-osm-disabled.png"))));
	    String ctrlOsm = "CTRL+R";
	    mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	            KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), ctrlOsm);
	    mapPanel.getActionMap().put(ctrlOsm, new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	downloadOsm();
	        }
	    });
	    toolBarDownload.add(btnDownloadOsm);
	    btnDownloadOsm.setEnabled(true);

	    /* DOWNLOAD FROM GPSIES BUTTON
	     * --------------------------------------------------------------------------------------------------------- */
	    btnDownloadGpsies = new JButton("");
	    btnDownloadGpsies.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	downloadGpsies();
	        }
	    });

	    btnDownloadGpsies.setToolTipText("Download Tracks from www.gpsies.com");
	    btnDownloadGpsies.setFocusable(false);
	    btnDownloadGpsies.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("gpsies-down.png"))));
	    btnDownloadGpsies.setDisabledIcon(
	            new ImageIcon(GpsMaster.class.getResource(iconPath.concat("gpsies-down-disabled.png"))));
	    // String ctrlOsm = "CTRL+R";
	    /*
	    mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	            KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), ctrlOsm);
	    mapPanel.getActionMap().put(ctrlOsm, new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	downloadFromOsm();
	        }
	    });
	    */
	    toolBarDownload.add(btnDownloadGpsies);
	    btnDownloadGpsies.setEnabled(true);

	    /* UPLOAD TO GPSIES BUTTON
	     * --------------------------------------------------------------------------------------------------------- */
	    btnUploadGpsies = new JButton("");
	    btnUploadGpsies.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	uploadGpsies();
	        }
	    });

	    btnUploadGpsies.setToolTipText("Upload Track to www.gpsies.com");
	    btnUploadGpsies.setFocusable(false);
	    btnUploadGpsies.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("gpsies-up.png"))));
	    btnUploadGpsies.setDisabledIcon(
	            new ImageIcon(GpsMaster.class.getResource(iconPath.concat("gpsies-up-disabled.png"))));
	    // String ctrlOsm = "CTRL+R";
	    /*
	    mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	            KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), ctrlOsm);
	    mapPanel.getActionMap().put(ctrlOsm, new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	downloadFromOsm();
	        }
	    });
	    */
	    toolBarDownload.add(btnUploadGpsies);
	    btnUploadGpsies.setEnabled(false);

	    /* GET FROM WIKIPEDIA BUTTON
	     * --------------------------------------------------------------------------------------------------------- */
	    btndownloadWiki = new JButton("");
	    btndownloadWiki.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	downloadWiki();
	        }
	    });

	    btndownloadWiki.setToolTipText("<html>Get nearby articles from Wikipedia<br>[CTRL+W]</html>");
	    btndownloadWiki.setFocusable(false);
	    btndownloadWiki.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("wiki-down.png"))));
	    btndownloadWiki.setDisabledIcon(
	            new ImageIcon(GpsMaster.class.getResource(iconPath.concat("wiki-down-disabled.png"))));
	    String ctrlWiki = "CTRL+W";

	    mapPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	            KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK), ctrlWiki);
	    mapPanel.getActionMap().put(ctrlOsm, new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	downloadWiki();
	        }
	    });

	    toolBarDownload.add(btndownloadWiki);
	    btndownloadWiki.setEnabled(true);

		menuBarsPanel.add(toolBarDownload, BorderLayout.SOUTH);

	}

	// TODO
	// encapsulate toolbars (menu, download, side) in their own classes
	// with enable/disable methods, possibly consolidate Listeners/Handlers
	// handle "setButtonVisibility" internally, if possible
	// goal: move as many globally defined members/variables into specific classes
	// as possible

    /**
     *
     */
	private void setupMenuBar() {

		final String iconPath = "/org/gpsmaster/icons/menubar/";

		/* MAIN TOOLBAR
         * --------------------------------------------------------------------------------------------------------- */
        toolBarMain = new JToolBar();
        toolBarMain.setLayout(new BoxLayout(toolBarMain, BoxLayout.X_AXIS));
        toolBarMain.setFloatable(false);
        toolBarMain.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

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
        btnFileNew.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-new.png"))));
        btnFileNew.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-new-disabled.png"))));
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
                dialog.setIconImage(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-open.png"))).getImage());
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
        btnFileOpen.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-open.png"))));
        btnFileOpen.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-open-disabled.png"))));
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

        /* MORE TRANSFER OPTIONS BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglDownload = new JToggleButton("");
        tglDownload.setToolTipText("More Transfer Options");
        tglDownload.setFocusable(false);
        tglDownload.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-open-more.png"))));
        tglDownload.setEnabled(true);
        // tglDownload.setDisabledIcon(
        //         new ImageIcon(GpsMaster.class.getResource(iconPath.concat("delete-points-disabled.png")));
        toolBarMain.add(tglDownload);
        tglDownload.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    toolBarDownload.setVisible(true);
                    tglDownload.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-open-less.png"))));
                } else {
                	toolBarDownload.setVisible(false);
                	tglDownload.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-open-more.png"))));
                }
            }
        });

        /* SAVE FILE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnFileSave = new JButton("");
        chooserFileSave = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                dialog.setIconImage(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-save.png"))).getImage());
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
        btnFileSave.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-save.png"))));
        btnFileSave.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("file-save-disabled.png"))));
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
        btnPrint.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("print.png"))));
        btnPrint.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("print-disabled.png"))));
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
        btnPrint.setVisible(true);

        /* OBJECT REMOVE BUTTON
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
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("object-delete.png"))));
        btnObjectDelete.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("object-delete-disabled.png"))));
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

        /* EDIT PROPERTIES BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnEditProperties = new JButton("");
        btnEditProperties.setToolTipText("Edit properties");
        btnEditProperties.setFocusable(false);
        btnEditProperties.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("edit-properties.png"))));
        btnEditProperties.setEnabled(false);
        btnEditProperties.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("edit-properties-disabled.png"))));
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
                GpsMaster.class.getResource(iconPath.concat("path-find.png"))));
        tglPathFinder.setEnabled(false);
        tglPathFinder.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("path-find-disabled.png"))));
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	// TODO move following code to separate method
                if (tglPathFinder.isSelected() && activeGPXObject != null && !mouseOverLink) {
                	pathFinder(e);
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
        toggles.add(tglPathFinder);

        /* ADD POINTS BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglAddPoint = new JToggleButton("");
        tglAddPoint.setToolTipText("Add points");
        tglAddPoint.setFocusable(false);
        tglAddPoint.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("add-points.png"))));
        tglAddPoint.setEnabled(false);
        tglAddPoint.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("add-points-disabled.png"))));
        toolBarMain.add(tglAddPoint);
        toggles.add(tglAddPoint);

        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	Waypoint wpt = null;

                if (activeGPXObject != null && !mouseOverLink) {
                    int zoom = mapPanel.getZoom();
                    int x = e.getX();
                    int y = e.getY();
                    Point mapCenter = mapPanel.getCenter();
                    int xStart = mapCenter.x - mapPanel.getWidth() / 2;
                    int yStart = mapCenter.y - mapPanel.getHeight() / 2;
                    double lat = OsmMercator.YToLat(yStart + y, zoom);
                    double lon = OsmMercator.XToLon(xStart + x, zoom);

                    GPXFile gpxFile = treeFindGPXFile(currSelection);
                	if (tglAddPoint.isSelected()) {
	                    wpt = new Waypoint(lat, lon);

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
	                    /*
	                    DefaultMutableTreeNode gpxFileNode = currSelection;
	                    while (!((GPXObject) gpxFileNode.getUserObject()).isGPXFile()) {
	                        gpxFileNode = (DefaultMutableTreeNode) gpxFileNode.getParent();
	                    }
	                    Object gpxFileObject = gpxFileNode.getUserObject();
	                    GPXFile gpxFile = (GPXFile) gpxFileObject;
	                    */
	                }
                	if (tglAddWaypoint.isSelected()) {
                		wpt = new WaypointMarker(lat, lon);
                		WaypointGroup grp = gpxFile.getWaypointGroup();
                		if (grp.getWaypoints().size() == 0) {
                			DefaultMutableTreeNode gpxNode = treeFindGpxObject(gpxFile);
                			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(grp);
                			treeModel.insertNodeInto(newNode, gpxNode, 0);
                		}
                		grp.addWaypoint(wpt);

                	}

                	if (wpt != null) {
	                    gpxFile.updateAllProperties();
	                    mapPanel.repaint();
	                    updatePropsTable();
                	}
                }
            }
        });
        tglAddPoint.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deselectAllToggles(tglAddPoint);
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
        tglDelPoints.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("delete-points.png"))));
        tglDelPoints.setEnabled(false);
        tglDelPoints.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("delete-points-disabled.png"))));
        toolBarMain.add(tglDelPoints);
        toggles.add(tglDelPoints);

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

        /* ELEVATION CHART BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnEleChart = new JButton("");
        btnEleChart.setToolTipText("View elevation profile");
        btnEleChart.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("elevation-chart.png"))));
        btnEleChart.setEnabled(false);
        btnEleChart.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("elevation-chart-disabled.png"))));
        btnEleChart.setFocusable(false);
        btnEleChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildChart("Elevation profile", iconPath.concat("elevation-chart.png"));
            }
        });
        toolBarMain.add(btnEleChart);

        /* SPEED CHART BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnSpeedChart = new JButton("");
        btnSpeedChart.setToolTipText("View speed profile");
        btnSpeedChart.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("speed-chart.png"))));
        btnSpeedChart.setEnabled(false);
        btnSpeedChart.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("speed-chart-disabled.png"))));
        btnSpeedChart.setFocusable(false);
        btnSpeedChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildChart("Speed profile", iconPath.concat("speed-chart.png"));
            }
        });
        toolBarMain.add(btnSpeedChart);

        /* MEASURE DISTANCE BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglMeasure = new JToggleButton("");
        tglMeasure.setToolTipText("Measure distance between two waypoints");
        tglMeasure.setFocusable(false);
        tglMeasure.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("measure.png"))));
        tglMeasure.setEnabled(false);
        tglMeasure.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("measure-disabled.png"))));
        toolBarMain.add(tglMeasure);
        tglMeasure.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // deselectAllToggles(tglMeasure); // MeasureThings handles object changes internally, no need to deselect
                    mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                    measure = new MeasureThings(msg, mapPanel.getMarkerList());
                    mapPanel.addPropertyChangeListener(measure.getPropertyChangeListener());
                    addPropertyChangeListener(measure.getPropertyChangeListener());
                    measure.setActiveGpxObject(activeGPXObject);
                } else {
                    mapPanel.removePropertyChangeListener(measure.getPropertyChangeListener());
                    removePropertyChangeListener(measure.getPropertyChangeListener());
                    mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    measure.clear();
                    measure = null;
                }
            }
        });

        /* SHOW PROGRESS LABELS BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglProgress = new JToggleButton("");
        tglProgress.setToolTipText("Show progress labels");
        tglProgress.setFocusable(false);
        tglProgress.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("progress-none.png"))));
        tglProgress.setEnabled(false);
        tglProgress.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("progress-disabled.png"))));
        toolBarMain.add(tglProgress);
        toggles.add(tglMeasure);

        tglProgress.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if ((e.getStateChange() == ItemEvent.SELECTED) || (e.getStateChange() == ItemEvent.DESELECTED)) {
                	switch(progressType) {
                	case NONE:
                		progressType = ProgressType.RELATIVE;
                		tglProgress.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("progress-rel.png"))));
                		tglProgress.setSelected(true);
                		break;
                	case RELATIVE:
                		progressType = ProgressType.ABSOLUTE;
                		tglProgress.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("progress-abs.png"))));
                		tglProgress.setSelected(true);
                		break;
                	case ABSOLUTE:
                		progressType = ProgressType.NONE;
                		tglProgress.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("progress-none.png"))));
                		tglProgress.setSelected(false);
                		break;
                	}
                }
                mapPanel.setProgressType(progressType);
        		mapPanel.repaint();
            }
        });

        /* SHOW DIRECTIONAL ARROWS BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglArrows = new JToggleButton("");
        tglArrows.setToolTipText("Show directional arrows [CTRL-A]");
        tglArrows.setFocusable(false);
        tglArrows.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("arrows-enabled.png"))));
        tglArrows.setEnabled(false);
        tglArrows.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("arrows-disabled.png"))));
        toolBarMain.add(tglArrows);
        tglArrows.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if ((e.getStateChange() == ItemEvent.SELECTED) || (e.getStateChange() == ItemEvent.DESELECTED)) {
                	switch(arrowType) {
                	case NONE:
                		arrowType = ArrowType.ONTRACK;
                		tglArrows.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("arrow-ontrack.png"))));
                		tglArrows.setSelected(true);
                		break;
                	case ONTRACK:
                		arrowType = ArrowType.PARALLEL;
                		tglArrows.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("arrow-parallel.png"))));
                		tglArrows.setSelected(true);
                		break;
                	case PARALLEL:
                		arrowType = ArrowType.NONE;
                		tglArrows.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("arrows-enabled.png"))));
                		tglArrows.setSelected(false);
                		break;
                	}
                }
                mapPanel.setArrowType(arrowType);
        		mapPanel.repaint();
            }
        });


        toolBarMain.add(Box.createHorizontalGlue());

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

                    // notify listeners of the newly selected Waypoint
                    firePropertyChange("1click", null, activeWpt);

                    // legacy: handle selected waypoint the old way
                    if (tglDelPoints.isSelected()) {
                        activeWptGrp.removeWaypoint(activeWpt);
                        gpxFile.updateAllProperties();
                    } else if (tglSplitTrackseg.isSelected()) {
                        splitTrackSeg(gpxFile);
                    }

                    activeWptGrp = null;
                    activeWpt = null;
                    mapPanel.setShownPoint(null);
                    mapPanel.repaint();
                    updatePropsTable();
                    updateActiveWpt(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                updateActiveWpt(e);
            }
        });


        /* TOGGLE TOOLBAR BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglToolbar = new JToggleButton("");
        tglToolbar.setToolTipText("Display Toolbar");
        tglToolbar.setFocusable(false);
        tglToolbar.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("toolbar-enabled.png"))));
        tglToolbar.setEnabled(true);
        tglToolbar.setDisabledIcon(
                new ImageIcon(GpsMaster.class.getResource(iconPath.concat("arrows-disabled.png")))); // TODO
        toolBarMain.add(tglToolbar);
        tglToolbar.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
            	switch(e.getStateChange()) {
            	case ItemEvent.SELECTED:
            		contentPane.add(toolBarSide, BorderLayout.WEST);
            		break;
            	case ItemEvent.DESELECTED:
            		contentPane.remove(toolBarSide);
            		break;
            	}
            	frame.validate();
            }
        });


        /* TILE SOURCE SELECTOR
         * --------------------------------------------------------------------------------------------------------- */

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
        tglLatLonFocus.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath.concat("crosshair.png"))));
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

        Component horizontalGlue = Box.createHorizontalGlue();
        horizontalGlue.setMaximumSize(new Dimension(2, 0));
        horizontalGlue.setMinimumSize(new Dimension(2, 0));
        horizontalGlue.setPreferredSize(new Dimension(2, 0));
        toolBarMain.add(horizontalGlue);
        toolBarMain.add(tglLatLonFocus);
        toggles.add(tglLatLonFocus);

        /* AUTOFIT BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglAutoFit = new JToggleButton();
        tglAutoFit.setToolTipText("Resize map panel to fit selected track or segment");
        tglAutoFit.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("autofit.png"))));
        tglAutoFit.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("autofit-disabled.png"))));
        tglAutoFit.setEnabled(autoFitToPanel);
        tglAutoFit.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				autoFitToPanel = ((e.getStateChange() == ItemEvent.SELECTED));
			}
		});
        tglAutoFit.setSelected(autoFitToPanel);
        tglAutoFit.setEnabled(true);
        toolBarMain.add(tglAutoFit);

        /* INFO BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnInfo = new JButton("");
        btnInfo.setToolTipText("Show Info");
        btnInfo.setFocusable(false);
        btnInfo.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("about.png"))));
        btnInfo.setEnabled(true);
        btnInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInfo();
            }
        });
        toolBarMain.add(btnInfo);

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
		    if (ext.equals(conf.getDefaultExt())) {
		    	chooserFileOpen.setFileFilter(extFilter);
		    }
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
        // toolBarMain.addSeparator();
        // toolBarMain.add(debug);

		menuBarsPanel.add(toolBarMain, BorderLayout.NORTH);

        /*java.util.Properties systemProperties = System.getProperties();
        systemProperties.setProperty("http.proxyHost", "proxy1.lmco.com");
        systemProperties.setProperty("http.proxyPort", "80");*/
	}

	/**
	 *
	 */
	private void setupToolbar() {

		final String iconPath = "/org/gpsmaster/icons/toolbar/";

		/* FLOATABLE TOOLBAR
	     * --------------------------------------------------------------------------------------------------------- */
	    toolBarSide = new JToolBar(JToolBar.VERTICAL);
	    toolBarSide.setFloatable(true);
	    toolBarSide.setName("Toolbar");

	    // --- EDITING -----------------------------

        /* SPLIT TRACKSEG BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglSplitTrackseg = new JToggleButton("");
        tglSplitTrackseg.setToolTipText("Split track segment");
        tglSplitTrackseg.setFocusable(false);
        tglSplitTrackseg.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("trackseg-split.png"))));
        tglSplitTrackseg.setEnabled(false);
        tglSplitTrackseg.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("trackseg-split-disabled.png"))));
        toolBarSide.add(tglSplitTrackseg);
        toggles.add(tglSplitTrackseg);

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

        /* ADD WAYPOINT BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        tglAddWaypoint = new JToggleButton("");
        tglAddWaypoint.setToolTipText("Add new Waypoint");
        tglAddWaypoint.setFocusable(false);
        tglAddWaypoint.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("add-waypoint.png"))));
        tglAddWaypoint.setEnabled(false);
        tglAddWaypoint.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("add-waypoint-disabled.png"))));
        toolBarSide.add(tglAddWaypoint);
        toggles.add(tglAddWaypoint);

        tglAddWaypoint.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deselectAllToggles(tglAddWaypoint);
                    mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                } else {
                    mapCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                }
            }
        });

        toolBarSide.addSeparator();

        // --- CLEANING & CORRECTION -----------------------------


        /* CORRECT ELEVATION BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnCorrectEle = new JButton("");
        btnCorrectEle.setToolTipText("Correct elevation");
        btnCorrectEle.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("correct-elevation.png"))));
        btnCorrectEle.setEnabled(false);
        btnCorrectEle.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("correct-elevation-disabled.png"))));
        btnCorrectEle.setFocusable(false);
        btnCorrectEle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               	correctElevation();
            }
        });
        toolBarSide.add(btnCorrectEle);


        /* REMOVE TIMESTAMPS BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnRemoveTime = new JButton("");
        btnRemoveTime.setToolTipText("Remove timestamps from selected objects");
        btnRemoveTime.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("remove-time.png"))));
        btnRemoveTime.setEnabled(false);
        btnRemoveTime.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("remove-time-disabled.png"))));
        btnRemoveTime.setFocusable(false);
        btnRemoveTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// TODO ask for confirmation
               	core.removeTimestamps(activeGPXObject);
               	activeGPXObject.updateAllProperties();
               	msg.volatileInfo("Timestamps removed.");
            }
        });
        toolBarSide.add(btnRemoveTime);

        /* CLEAN NARROW WAYPOINTS
         * --------------------------------------------------------------------------------------------------------- */
        btnCleaning = new JButton("");
        btnCleaning.setToolTipText(String.format("Remove waypoints"));
        btnCleaning.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("clean-distance.png"))));
        btnCleaning.setEnabled(false);
        btnCleaning.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("clean-distance-disabled.png"))));
        btnCleaning.setFocusable(false);
        btnCleaning.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	doCleaning();
            }
        });
        toolBarSide.add(btnCleaning);

        toolBarSide.addSeparator();

        /* MERGE 1:1 INTO NEW GPX
         * --------------------------------------------------------------------------------------------------------- */
        btnMergeOneToOne = new JButton("");
        btnMergeOneToOne.setToolTipText("Merge visible tracks and segments 1:1 into a new GPX file.");
        btnMergeOneToOne.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("merge-tracks.png"))));
        btnMergeOneToOne.setEnabled(false);
        btnMergeOneToOne.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("merge-tracks-disabled.png"))));
        btnMergeOneToOne.setFocusable(false);
        btnMergeOneToOne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// TODO ask for confirmation
            	GPXFile newFile = core.mergeIntoTracks(mapPanel.getGPXFiles());
            	if (newFile != null) {
            		newFile.updateAllProperties();
            		treeAddGpxFile(newFile);
            	}
               	msg.volatileInfo("Merging completed.");
            }
        });
        toolBarSide.add(btnMergeOneToOne);

        /* MERGE INTO SINGLE TRACK, MULTIPLE SEGMENTS
         * --------------------------------------------------------------------------------------------------------- */
        btnMergeToMulti = new JButton("");
        btnMergeToMulti.setToolTipText("Merge visible tracks and segments into a single track, keep segments separate.");
        btnMergeToMulti.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("merge-multi.png"))));
        btnMergeToMulti.setEnabled(false);
        btnMergeToMulti.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("merge-multi-disabled.png"))));
        btnMergeToMulti.setFocusable(false);
        btnMergeToMulti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// TODO ask for confirmation
            	GPXFile newFile = core.mergeIntoMulti(mapPanel.getGPXFiles());
            	if (newFile != null) {
            		newFile.updateAllProperties();
            		treeAddGpxFile(newFile);
            	}
               	msg.volatileInfo("Merging completed.");
            }
        });
        toolBarSide.add(btnMergeToMulti);

        /* MERGE INTO SINGLE TRACK, SINGLE SEGMENT
         * --------------------------------------------------------------------------------------------------------- */
        btnMergeToSingle = new JButton("");
        btnMergeToSingle.setToolTipText("Merge visible track segments into a new track with a single segment and sort trackpoints by time.");
        btnMergeToSingle.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("merge-single.png"))));
        btnMergeToSingle.setEnabled(false);
        btnMergeToSingle.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("merge-single-disabled.png"))));
        btnMergeToSingle.setFocusable(false);
        btnMergeToSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// TODO ask for confirmation
            	GPXFile newFile = core.mergeIntoSingle(mapPanel.getGPXFiles());
            	if (newFile != null) {
            		newFile.updateAllProperties();
            		treeAddGpxFile(newFile);
            	}
               	msg.volatileInfo("Merging completed.");
            }
        });
        toolBarSide.add(btnMergeToSingle);

        /* MERGE PARALLEL SEGMENTS
         * --------------------------------------------------------------------------------------------------------- */
        btnMergeParallel = new JButton("");
        btnMergeParallel.setToolTipText("Interpolate a new track between two track segments.");
        btnMergeParallel.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("merge-parallel.png"))));
        btnMergeParallel.setEnabled(false);
        btnMergeParallel.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("merge-parallel-disabled.png"))));
        btnMergeParallel.setFocusable(false);
        btnMergeParallel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// TODO ask for confirmation
               	msg.volatileInfo("Merging completed.");
            }
        });
        toolBarSide.add(btnMergeParallel);


        /* TIMESHIFT BUTTON
         * --------------------------------------------------------------------------------------------------------- */
        btnTimeShift = new JButton("");
        btnTimeShift.setToolTipText("Timeshift GPX File [CTRL-T]");
        btnTimeShift.setIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("timeshift.png"))));
        btnTimeShift.setEnabled(false);
        btnTimeShift.setDisabledIcon(new ImageIcon(
                GpsMaster.class.getResource(iconPath.concat("timeshift-disabled.png"))));
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
        toolBarSide.add(btnTimeShift);

        toolBarSide.addSeparator();
	}

	/**
	 * Dynamically enables/disables certain toolbar buttons dependent on which type of {@link GPXObject} is active
	 * and what operations are allowed on that type of element.
	 * TODO rewrite, consolidate code
	 */
	private void updateButtonVisibility() {

		// unsorted
		btnFileSave.setEnabled(false);
	    btnObjectDelete.setEnabled(false);
	    tglAddPoint.setEnabled(false);
	    tglDelPoints.setEnabled(false);
	    tglSplitTrackseg.setEnabled(false);
	    btnEleChart.setEnabled(false);
	    btnSpeedChart.setEnabled(false);
	    btnEditProperties.setEnabled(false);
	    tglPathFinder.setEnabled(false);
	    tglMeasure.setEnabled(false);
	    tglProgress.setEnabled(false);
	    tglArrows.setEnabled(false);
	    btnTimeShift.setEnabled(false);
	    btnPrint.setEnabled(false);

	    // menuBar
	    btnFileNew.setEnabled(true);	// always on
	    btnFileOpen.setEnabled(true);	// always on

	    // downloadBar
	    btnDownloadOsm.setEnabled(true);	// always on
	    btnDownloadGpsies.setEnabled(true);	// always on

		// ToolBar
	    btnRemoveTime.setEnabled(false);
	    btnMergeOneToOne.setEnabled(false);
	    btnMergeToSingle.setEnabled(false);
	    btnMergeToMulti.setEnabled(false);
	    btnMergeParallel.setEnabled(false);
	    btnCleaning.setEnabled(false);
	    btnCorrectEle.setEnabled(false);


	    if (currSelection != null) {
	        btnFileSave.setEnabled(true);
	        btnObjectDelete.setEnabled(true);
	        btnPrint.setEnabled(true);

	        GPXObject o = activeGPXObject;

	        if (o.isRoute() || o.isWaypoints() || o.isGPXFileWithOneRoute() || o.isGPXFileWithNoRoutes()) {
	            tglAddPoint.setEnabled(true);
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
	            tglMeasure.setEnabled(true);
	        }
	        if (o.isGPXFile() || o.isRoute() || o.isTrack()) {
	            btnEditProperties.setEnabled(true);
	            btnCorrectEle.setEnabled(true);
	            tglSplitTrackseg.setEnabled(true);
	            btnRemoveTime.setEnabled(true);
	            // btnCleaning.setEnabled(true); // TODO support cleaning of multiple WaypointGroups
	        }
	        if (o.isRoute() || o.isGPXFileWithOneRoute() || o.isGPXFileWithNoRoutes()) {
	            tglPathFinder.setEnabled(true);
	        }
	        if (o.isTrackseg() || o.isTrack() || o.isGPXFile()) {
	            tglProgress.setEnabled(true);
	            tglArrows.setEnabled(true);
	            btnCorrectEle.setEnabled(true);
	            btnUploadGpsies.setEnabled(true);
	            // btnTimeShift.setEnabled(true); // as soon as "shift by delta" is implemented
	        }
	        if (o.isTrackseg()) {
	        	btnTimeShift.setEnabled(true);
	        	btnCleaning.setEnabled(true);
	        }

	        if (mapPanel.getGPXFiles().size() > 0) {
	        	btnMergeOneToOne.setEnabled(true);
	        	btnMergeToMulti.setEnabled(true);
	        	btnMergeToSingle.setEnabled(true);
	        	// btnMergeParallel.setEnabled(true);
	        	tglAddWaypoint.setEnabled(true);
	        }
	    }

	    if (fileIOHappening) {
	        btnFileNew.setEnabled(false);
	        btnFileOpen.setEnabled(false);
	        btnFileSave.setEnabled(false);
	        btnObjectDelete.setEnabled(false);
	    }
	    if (downloadHappening) {
	    	btnDownloadOsm.setEnabled(false);
	    }
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
            treeAddGpxFile(gpxFile);
/*
            DefaultMutableTreeNode gpxFileNode = new DefaultMutableTreeNode(gpxFile);

            treeModel.insertNodeInto(gpxFileNode, root, root.getChildCount());
            treeModel.insertNodeInto(new DefaultMutableTreeNode(gpxFile.getRoutes().get(0)), gpxFileNode, 0);

            setActiveGPXObject((GPXObject) gpxFile);
            TreeNode[] pathToFileNode = treeModel.getPathToRoot(gpxFileNode);
            tree.setSelectionPath(new TreePath(pathToFileNode));
            tree.scrollRectToVisible(new Rectangle(0, 999999999, 1, 1));
            */

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
            MultiLoader multiLoader = new MultiLoader(msg);
            // multiLoader.setMessageCenter(msg);
            multiLoader.setPropertyChangeListener(propertyListener);
            multiLoader.setShowFilenames(true);
            multiLoader.setShowWarnings(conf.getShowWarning());
            multiLoader.setFiles(chooserFileOpen.getSelectedFiles());
            multiLoader.load();
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

        SwingWorker<Void, Void> fileSaveWorker = new SwingWorker<Void, Void>() {

        	MessagePanel msgSave = null;
            @Override
            public Void doInBackground() {
				GpsLoader loader;
				GPXFile gpx = (GPXFile) activeGPXObject;
		        setFileIOHappening(true);
		        msgSave = msg.infoOn("Saving file ...", new Cursor(Cursor.WAIT_CURSOR));
				try {
					fileSave = chooserFileSave.getSelectedFile();
					preSave(gpx);
					loader = loaderFactory.getLoader(getFilenameExt(fileSave.getName()));
					loader.save(gpx, fileSave);
				} catch (ClassNotFoundException e) {
					msg.error("No writer for this file format available.");
				} catch (FileNotFoundException e) {
					msg.error(e);
				} catch (Exception e) {
					msg.error("unexpected error", e);
				}

                return null;
            }
            @Override
            protected void done() {
            	msg.infoOff(msgSave);
                setFileIOHappening(false);
            }
        };

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
		        conf.setLastSaveDirectory(chooserFileSave.getCurrentDirectory().getPath());
		        fileSaveWorker.execute();
		    }
	    }
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
    	updatePropsTable();
    	updatePropTableWidths();
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
            // TODO tree code hier vereinheitlichen
            GPXFile gpxFile = treeFindGPXFile(currSelection);
            treeModel.removeNodeFromParent(currentNode);

            if (activeGPXObject.isGPXFile()) { // this is a GPX file
                clearPropsTable();
            	mapPanel.removeGPXFile((GPXFile) activeGPXObject);
                activeGPXObject = null;
                if (activityHandler != null) {
                	activityHandler.setGpxFile(null);
                }
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
                tree.setSelectionPath(new TreePath(parentPath));
            }
            gpxFile.updateAllProperties();
            mapPanel.repaint();
        }
    }


    /**
     * TODO test, consolidate code
     * @param e
     */
    private void pathFinder(MouseEvent e) {
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
                    msgRouting = msg.infoOn("finding path ...", new Cursor(Cursor.WAIT_CURSOR));
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
                    msg.infoOff(msgRouting);
                    mapPanel.repaint();
                    updatePropsTable();
                }
            };
            pathFindWorker.execute();
        }
        mapPanel.repaint();
        updatePropsTable();
    }


    /**
     * Handler to re-enable menu buttons when corresponding window is closed
     * @param e
     */
    private void handleWindowEvent(WindowEvent e, int state) {

    	Object o = e.getSource();
    	if ((state == WindowEvent.WINDOW_CLOSED) || (state == WindowEvent.WINDOW_CLOSING)){
    		if (o instanceof DownloadGpsies) {
    			btnDownloadGpsies.setEnabled(true);
    		}
    		if (o instanceof UploadGpsies) {
    			btnUploadGpsies.setEnabled(true);
    		}
    		if (o instanceof DownloadOsm) {
    			btnDownloadOsm.setEnabled(true);
    		}
    		if (o instanceof GetWikipedia) {
    			btndownloadWiki.setEnabled(true);
    		}
    		if (o instanceof InfoDialog) {
    			btnInfo.setEnabled(true);
    		}
    		if (o instanceof CleaningDialog) {
    			btnCleaning.setEnabled(true);
    		}
    		if (o instanceof ImageViewer) {
    			if (imageViewer != null) {
    				imageViewer.dispose();
    				imageViewer = null;
    			}
    		}
    	}
    }

    /**
     * thread-safe (?) method to modify GPXObjects in the GPXFiles list
     * and synchronize with tree model.
     * called via PropertyChangeHandlers by user dialogs
     */
    private void handlePropertyChangeEvent(PropertyChangeEvent event) {
    	String command = event.getPropertyName();

    	if (event.getNewValue() != null) {
    		// System.out.println(command);
    		if (command.equals("newGpx")) {
    			// add a new GPXFile
    			handleNewGpx(event);
    		} else if (command.equals("updateGpx")) {
    			// update all properties for GPXFile
    			GPXFile gpxFile = treeFindGPXFile((GPXObject) event.getNewValue());
    			if (gpxFile == null) {
    				msg.error("Internal error: modifyGpxFilesTree: object not found");
    			} else {
    				// thread safe?
    				gpxFile.updateAllProperties();
    				updatePropsTable();
    			}
    		} else if (command.equals("deleteGpx")) {
    			msg.volatileError("handlePropertyChangeEvent: gpxDelete not implemented");
    		} else if (command.equals("dialogClosing")) {
    			// re-enable menu buttons
    			String dialog = (String) event.getNewValue();
    			if (dialog.equals("elevation")) {
    				btnCorrectEle.setEnabled(true);
    				// TODO mapPanel.remove(ElevationDialog)
    			}
    		} else if (command.equals("1click")) {
    			handle1Click(event.getNewValue());
    		} else if (command.equals("2click")) {
    			handle2Click(event.getNewValue());
    		} else if (command.equals("gpsiesUsername")) {
    			conf.setGpsiesUsername((String) event.getNewValue());
    		}
    	}
    	if (command.equals("repaintMapPanel")) {
			mapPanel.repaint();
		}

    }

    /**
     * handle the arrival of a new {@link GPXFile}
     * @param event
     */
    private void handleNewGpx(PropertyChangeEvent event) {
		GPXFile gpxFile = (GPXFile) event.getNewValue();
		File file = (File) event.getOldValue();

    	gpxFile.updateAllProperties();
        adjustColors(gpxFile); // make this configurable?
    	if (gpxFile.getMetadata().getName().isEmpty() && (file != null)) {
    		gpxFile.getMetadata().setName(file.getName());
    	}
    	if (conf.useExtensions()) {
    		postLoad(gpxFile);
    	}
        treeAddGpxFile(gpxFile);
    }

    /**
     * handle single click on a marker object
     * @param o
     */
    private void handle1Click(Object o) {
    	if ((o instanceof PhotoMarker) && (imageViewer != null)) {
    		imageViewer.showMarker((PhotoMarker) o);
    	} else if ((o instanceof Marker) && tglDelPoints.isSelected()) {
    		// System.out.println("1click marker");
    		mapPanel.getMarkerList().remove(o);
    		GPXFile gpx = treeFindGPXFile(activeGPXObject);
    		if (gpx != null) {
    			gpx.getWaypointGroup().removeWaypoint((Marker) o);
    			mapPanel.repaint();
    		}
    	}
    }

    /**
     * handle double click on a marker object
     * @param o
     */
    private void handle2Click(Object o) {
    	if (o instanceof WikiMarker) {
        	// wikipedia: open link to article
    		WikiMarker marker = (WikiMarker) o;
    		if (marker.getLink().size() > 0) {
    			BrowserLauncher.launchBrowser(marker.getLink().get(0).getHref());
    		}
    	} else if (o instanceof PhotoMarker) {
    		// photo: open imageviewer
    		if (imageViewer == null) {
    			imageViewer = new ImageViewer(frame);
    			imageViewer.setVisible(true);
    			imageViewer.setAlwaysOnTop(true);
    			imageViewer.addWindowListener(windowListener);
    		}
			imageViewer.showMarker((PhotoMarker) o);
    	} else if (o instanceof WaypointMarker) {
    		for (LinkType link : ((WaypointMarker) o).getLink()) {
    			BrowserLauncher.launchBrowser(link.getHref());
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
        if (autoFitToPanel) {
        	mapPanel.fitGPXObjectToPanel(gpxObject);
        }
        firePropertyChange("activeGpxObject", null, gpxObject);
        updatePropsTable();
        if (activityHandler != null) {
        	GPXFile rootGpx = treeFindGPXFile(gpxObject);
         	activityHandler.setGpxFile(rootGpx);
        }
        // updateActiveWptGrp();
    }

    private void updatePropsTable() {
    	propsTableModel.setGPXObject(activeGPXObject);
    	updatePropTableWidths();
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
    	propsTableModel.clear();
    }

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
                    propsTableModel.setTrackpoint(activeWpt, activeWptGrp.getWaypoints().indexOf(activeWpt));
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
     * TODO redesign/rewrite
     *
     */
    private void updateActiveWptGrp() {
    	// here: activeWptGrp is null
        activeTracksegNode = null;
        // TODO nullpointer exception here after deleting TRACK from gpxfile
        // TODO clean up this mess
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

                if (currSelection != null) {
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
        if (activeWptGrp != null) {
        	firePropertyChange("activeWptGrp", null, activeWptGrp);
        }
    }

    /**
     * Registers a list of {@link JToggleButton}s and deselects them all, optionally leaving one selected.
     * Used to prevent multiple toggles from being selected simultaneously.
     */
    private void deselectAllToggles(JToggleButton exceptThisOne) {

        for (JToggleButton toggle : toggles) {
            if (toggle != exceptThisOne && toggle.isSelected()) {
                toggle.setSelected(false);
            }
        }
    }

    /**
     * Builds the selected chart type and displays the new window parentFrame.
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
        	if (gpx.getExtensions().containsKey("activity")) {
        		gpx.getExtensions().remove("activity");
        	}
        	if (dlg.getActivity().isEmpty() == false) {
	        	gpx.getExtensions().put("activity", dlg.getActivity());
	        	if (activityHandler != null) {
	        		activityHandler.setActivity(dlg.getActivity());
	        	}
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
			for (OnlineTrack entry : move.getTracklist()) {
				System.out.println(entry.getId());
				if ((entry.getId() >= 166) && (entry.getId() <= 170)) {
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

    private void downloadGpsies() {
		btnDownloadGpsies.setEnabled(false);
		GenericDownloadDialog gpsies = new DownloadGpsies(frame, msg);
		gpsies.setGeoBounds(mapPanel.getVisibleBounds());
		gpsies.setPropertyChangeListener(propertyListener);
		gpsies.addWindowListener(windowListener);
		gpsies.begin();
	}

    private void uploadGpsies() {
    	GPXFile gpx = (GPXFile) activeGPXObject;
		btnUploadGpsies.setEnabled(false);

		UploadGpsies gpsies = new UploadGpsies(gpx, frame, msg);
		gpsies.setModalityType(ModalityType.MODELESS);
		if (conf.getGpsiesUsername() != null) {
			gpsies.setUsername(conf.getGpsiesUsername());
		}
		// gpsies.addPropertyChangeListener(propertyListener);
		gpsies.addWindowListener(windowListener);
	}

    private void downloadOsm() {
		btnDownloadOsm.setEnabled(false);
		GenericDownloadDialog osmDialog = new DownloadOsm(frame, msg);
		osmDialog.setGeoBounds(mapPanel.getVisibleBounds());
		osmDialog.setPropertyChangeListener(propertyListener);
		osmDialog.addWindowListener(windowListener);
		osmDialog.begin();
    }

	/**
	 *
	 */
	private void downloadWiki() {
		btndownloadWiki.setEnabled(false);
		GenericDownloadDialog getWiki = new GetWikipedia(frame, msg);
		getWiki.setGeoBounds(mapPanel.getVisibleBounds());
		getWiki.setPropertyChangeListener(propertyListener);
		getWiki.addWindowListener(windowListener);
		getWiki.begin();
	}

	/**
     *
     */
    private void showInfo() {
    	// TODO rewrite info dialog
    	btnInfo.setEnabled(false);
    	InfoDialog dlg = new InfoDialog(frame, PROGRAM_NAME + " v" + VERSION_NUMBER);
    	dlg.addWindowListener(windowListener);
    	dlg.setVisible(true);

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
    		msg.error(e);
    	}
    }

    /**
     * Open the track cleaning dialog
     */
    private void doCleaning() {
    	if (activeGPXObject.isTrackseg()) {
			CleaningDialog dlg = new CleaningDialog(frame, msg);
			dlg.setMarkerList(mapPanel.getMarkerList());
			dlg.setWaypointGroup((WaypointGroup) activeGPXObject);
			dlg.addPropertyChangeListener(propertyListener);

			dlg.begin();
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
    	String colorString = String.format("%02x%02x%02x%02x", c.getRed(),c.getGreen(), c.getBlue(), c.getAlpha());
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
    		gpx.setCreator(ORIGINATOR);
    	} else if (gpx.getCreator().equals(ORIGINATOR) == false) {
    		gpx.getExtensions().put("originator", gpx.getCreator());
    		gpx.setCreator(ORIGINATOR);
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
    		try {
	    		String[] rgba = colorString.split(",");
	    		if (rgba.length == 4) {
    				int r = Integer.parseInt(rgba[0]);
    				int g = Integer.parseInt(rgba[1]);
    				int b = Integer.parseInt(rgba[2]);
    				int a = Integer.parseInt(rgba[3]);
    				color = new Color(r, g, b, a);
	    		} else if (colorString.length() == 8) {
	    			System.out.println(colorString.substring(0, 2));
	    			System.out.println(colorString.substring(2, 4));
    				int r = Integer.parseInt(colorString.substring(0, 2), 16);
    				int g = Integer.parseInt(colorString.substring(2, 4), 16);
    				int b = Integer.parseInt(colorString.substring(4, 6), 16);
    				int a = Integer.parseInt(colorString.substring(6, 8), 16);
	    			color = new Color(r, g, b, a);
	    		} else {
	    			color = Color.getColor(colorString);
	    		}
			} catch (NumberFormatException e) {
				msg.volatileWarning("Unsupported color format", e);
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
    			msg.error(e);
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
			msg.error(e);
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
            msg.volatileWarning("Configuration file not found, using defaults.");
    	} catch (JAXBException e) {
    		// JAXBException.getMessage() == null !!
    		msg.volatileWarning("Unable to parse configuration file, using defaults. existing file will be overwritten on exit.");
		} catch (IOException e) {
			msg.error(e);
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
			outStream.flush();
			outStream.close();
			} catch (Exception e) {
				msg.volatileWarning("Error saving configuration", e);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ie) {
            }
    	}
    }

    /**
     * open and run elevation correction dialog
     */
    private void correctElevation() {
		btnCorrectEle.setEnabled(false);
    	ElevationDialog dlg = new ElevationDialog(activeGPXObject, msg);

    	dlg.setChangeListener(propertyListener);
    	dlg.setAlignmentY(TOP_ALIGNMENT);
    	mapPanel.add(dlg);
    	mapPanel.validate();
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
     * Sets a flag to synchronize download operations with {@link GPXFile}s.  Must be called before and after each download.
     */
    private void setDownloadHappening(boolean happening) {
        downloadHappening = happening;
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

    /**
     *
     * @param wptGrp
     */
    private void colorBySpeed(WaypointGroup wptGrp) {

    }

    private void debugMarkers() {
        MouseListener listener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                System.out.println(e.getSource().toString());
            }
        };

        ImageIcon icon = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/markers/photo.png"));
        GPXFile gpx = ((GPXFile) activeGPXObject);
        Track track = gpx.getTracks().get(0);
        WaypointGroup seg = track.getTracksegs().get(0);
        for (int i = 0; i < seg.getWaypoints().size(); i = i + 20) {
            Waypoint wpt = seg.getWaypoints().get(i);
            Point point = mapPanel.getMapPosition(wpt.getLat(), wpt.getLon(), false);
            JLabel label = new JLabel();
            label.setIcon(icon);
            label.setLocation(point);
            label.addMouseListener(listener);
            mapPanel.add(label);
        }
        mapPanel.validate();
    }

    /**
     *
     * @param gpxObject
     */
    private void colorByStatistics (GPXObject gpxObject) {

    	int windowSize = 5;  // sliding window size
    	double average = 0.0f;
    	long count = 0;

    	// pass 1: mittelwert errechnen

    	for (WaypointGroup group : core.getTrackSegments(gpxObject)) {
    		List<Waypoint> wpts = group.getWaypoints(); // shortcut
    		if (wpts.size() > windowSize) {
    			// ...
    		}

    	}
    }


    private void gpxIn() {
    	GpxType gpx = null;
    	String filename = "D:\\Projekte\\Touring\\GPSTracks\\Test\\Matt_sawpit.gpx";
    	// String filename = "D:\\Projekte\\Touring\\GPSTracks\\Test\\1641653_UA_15hr_157km.gpx";
    	// String filename = "D:\\Projekte\\Touring\\GPSTracks\\track_no123.gpx";
       	try {
       		FileInputStream inputStream = new FileInputStream(filename);
    		JAXBContext context = JAXBContext.newInstance(GpxType.class);
    		Unmarshaller u = context.createUnmarshaller();
    		JAXBElement o = (JAXBElement) u.unmarshal(inputStream);
    		gpx = (GpxType) o.getValue();
    		//JAXBElement<MetadataType> meta = u.unmarshal(element, MetadataType.class);
	        // gpx = gpxElement.getValue();
    		inputStream.close();

    		for (Object ext : gpx.getTrk().get(0).getExtensions().getAny()) {
    			System.out.println(ext.toString());

    		}
    	} catch (FileNotFoundException e) {
            msg.volatileWarning("Configuration file not found, using defaults.");
    	} catch (JAXBException e) {
    		// JAXBException.getMessage() == null !!
    		msg.volatileWarning("Unable to parse file");
		} catch (IOException e) {
			msg.error(e);
		}
    }

    public void gpxOut(GpxType gpx) {
    	StringWriter writer = new StringWriter();
    	// GPXFile gpx = (GPXFile) activeGPXObject;
    	try {
			JAXBContext context = JAXBContext.newInstance(GpxType.class);
			Marshaller m = context.createMarshaller();
			m.marshal(gpx, writer);
			System.out.println(writer);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    public void gpxOut(GPXFile gpx) {
    	StringWriter writer = new StringWriter();
    	// GPXFile gpx = (GPXFile) activeGPXObject;
    	try {
			JAXBContext context = JAXBContext.newInstance(GPXFile.class);
			Marshaller m = context.createMarshaller();
			m.marshal(gpx, writer);
			System.out.println(writer);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void doDebug() {

    	// gpxOut((GPXFile) activeGPXObject);
    	JColorChooser chooser = new JColorChooser();
    	// AbstractColorChooserPanel
    	// http://stackoverflow.com/questions/10793916/jcolorchooser-save-restore-recent-colors-in-swatches-panel

    	dbgClean();

    }

	/**
	 *
	 */
	private void dbgClean() {

		GpxLoader loader = new GpxLoader();
		loader.open(new File("D:\\Projekte\\Touring\\GPSTracks\\track_no222.gpx"));
		try {
			treeAddGpxFile(loader.load());
			WaypointGroup grp = mapPanel.getGPXFiles().get(0).getTracks().get(0).getTracksegs().get(0);
			CleaningDialog dlg = new CleaningDialog(frame, msg);
			dlg.setMarkerList(mapPanel.getMarkerList());
			dlg.setWaypointGroup(grp);
			dlg.addPropertyChangeListener(propertyListener);

			dlg.begin();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			msg.error(e);
		}


		// Gaussian cleaner = new Gaussian(grp);
    	// cleaner.setMarkerList(mapPanel.getMarkerList());
    	// cleaner.preview();
    	// cleaner.doClean();
	}
}
