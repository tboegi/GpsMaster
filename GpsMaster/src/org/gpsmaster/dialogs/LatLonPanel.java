package org.gpsmaster.dialogs;

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXPanel;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.OsmMercator;

/**
 * panel encapsulating lat/lon input seeker located in main toolbar
 *  
 * @author rfu
 * @author tboegi
 *
 */
@SuppressWarnings("serial")
public class LatLonPanel extends JPanel {
	
	private GPXPanel mapPanel = null;
	
	private JTextField textFieldLat = null;
	private JTextField textFieldLon = null;
	
	private JToggleButton tglLatLonFocus = null;

	private final boolean debug = true;


	
	/**
	 * Constructor
	 * 
	 * @param mapViewer
	 */
	public LatLonPanel(GPXPanel gpxPanel) {

		mapPanel = gpxPanel;
		
		setupSeeker();
		setupToggle();
	}

	/**
	 * 
	 */
	private void setupSeeker() {
        JLabel lblLat = new JLabel(" Lat ");
        lblLat.setFont(new Font("Tahoma", Font.PLAIN, 11));
        add(lblLat);

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
        add(textFieldLat);

        JLabel lblLon = new JLabel(" Lon ");
        lblLon.setFont(new Font("Tahoma", Font.PLAIN, 11));
        add(lblLon);

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
        add(textFieldLon);

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


	}
		
	private void setupToggle() {

		tglLatLonFocus = new JToggleButton("");
        tglLatLonFocus.setToolTipText("Focus on latitude/longitude");
                
        ImageIcon icon = new ImageIcon(GpsMaster.class.getResource(Const.ICONPATH_MENUBAR + "crosshair.png"));
        
        tglLatLonFocus.setIcon(icon);
        tglLatLonFocus.setFocusable(false);
        tglLatLonFocus.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // deselectAllToggles(tglLatLonFocus); // TODO re-enable
                    mapPanel.setCursor(Cursor.CROSSHAIR_CURSOR); 
                    String latString = textFieldLat.getText();
                    String lonString = textFieldLon.getText();
                    try {
                        double latDouble = parseCoordinate("lat", latString);
                        double lonDouble = parseCoordinate("lon", lonString);
                        mapPanel.setShowCrosshair(true);
                        mapPanel.setCrosshairLat(latDouble);
                        mapPanel.setCrosshairLon(lonDouble);
                        Point p = new Point(mapPanel.getWidth() / 2, mapPanel.getHeight() / 2);
                        mapPanel.setDisplayPosition(p, new Coordinate(latDouble, lonDouble), mapPanel.getZoom());
                    } catch (Exception ex) {
                        // TODO show as warning in MsgCenter
                        if (debug) {
                            System.err.println("Failed latStringe=" + latString +
                                               " lonString=" + lonString +
                                               " : " + ex.getMessage());
                        }
                    }
                    mapPanel.repaint();
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    mapPanel.setCursor(Cursor.DEFAULT_CURSOR);
                    mapPanel.setShowCrosshair(false);
                    mapPanel.repaint();
                }
            }
        });
        add(tglLatLonFocus);
        
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tglLatLonFocus.isSelected() && !mapPanel.isMouseOverLink()) {
                    int zoom = mapPanel.getZoom();
                    int x = e.getX();
                    int y = e.getY();
                    Point mapCenter = mapPanel.getCenter();
                    int xStart = mapCenter.x - mapPanel.getWidth() / 2;
                    int yStart = mapCenter.y - mapPanel.getHeight() / 2;
                    double lat = OsmMercator.MERCATOR_256.yToLat(yStart + y, zoom);
                    double lon = OsmMercator.MERCATOR_256.xToLon(xStart + x, zoom);
                    textFieldLat.setText(String.format("%.6f", lat).replace(',','.'));
                    textFieldLon.setText(String.format("%.6f", lon).replace(',','.'));
                    mapPanel.setShowCrosshair(true);
                    mapPanel.setCrosshairLat(lat);
                    mapPanel.setCrosshairLon(lon);
                    mapPanel.repaint();
                }
            }
        });

	}
	
    /**
     * Utility to parse GPS typ-ish strings into a double
     */
    private double parseCoordinate(String latOrLon,
                                        String coordStr) throws NumberFormatException {
        boolean debug = false;

        if (debug) System.out.println("tglLatLonFocus parseLatOrLon " + latOrLon + "=" + coordStr);
        
            /* 51° 28′ 38″ N*/
            /* This parser is a little bit relaxed:
               a sign ('+' or '-')
               followed by digits (degree)
               followed by non-digits, e.g. ' ', '°'
               followed by digits (minute)
               followed by non-digits, e.g. ' ', '"'
               followed by digits or a dot (second)
               may followed by none of 'E' 'W' 'N' 'S'
               may followed by one of 'E' 'W' 'N' 'S', the direction
            */
            String hoursMinSecRegex = "([-+]?\\d+)\\D+(\\d+)\\D+([0-9.]+)([^0-9a-zA-Z.]*)([EWNS]?)";
            Pattern hoursMinSecPattern = Pattern.compile(hoursMinSecRegex);
            Matcher hoursMinSecMatcher = hoursMinSecPattern.matcher(coordStr);
            if (hoursMinSecMatcher.find()) {
                String degreeStr = hoursMinSecMatcher.group(1);
                String minuteStr = hoursMinSecMatcher.group(2);
                String secondStr = hoursMinSecMatcher.group(3);
                String eastStr   = hoursMinSecMatcher.group(5);

                if (debug) System.out.println("tglLatLonFocus parseLatOrLon (hoursMinutesSecMatcher)" +
                                              " degreeStr=" + degreeStr +
                                              " minuteStr=" + minuteStr +
                                              " secondStr=" + secondStr +
                                              " eastStr=" + eastStr);
                /* Simple case: a double */
                int sign = 1;
                Double degree =  Double.parseDouble(degreeStr);
                if (degree < 0) {
                    /* The sign is for all digits; not only the degrees */
                    degree = 0 - degree;
                    sign = 0 -sign;
                }
                Double minute = Double.parseDouble(minuteStr);
                Double second = Double.parseDouble(secondStr);
                Double retDouble = degree + minute / 60 + second / 3600;
                if (eastStr.equals("W") || eastStr.equals("S")) {
                    sign = 0 -sign;
                }
                retDouble = retDouble * sign;
                if (debug) System.out.println("tglLatLonFocus parseLatOrLon (hoursMinutesSecMatcher)" +
                                              " degree=" + degree +
                                              " minute=" + minute +
                                              " second=" + second +
                                              " retDouble=" + retDouble);
                return retDouble;
            }
        
            String hoursMinutesRegex = "^([-+]?\\d+[.]?\\d*)$";
            Pattern hoursMinutesPattern = Pattern.compile(hoursMinutesRegex);
            Matcher hoursMinutesMatcher = hoursMinutesPattern.matcher(coordStr);
            if (hoursMinutesMatcher.find()) {
                String  degreeStr = hoursMinutesMatcher.group(1);
                Double retDouble = Double.parseDouble(degreeStr);
                if (debug) System.out.println("tglLatLonFocus parseLatOrLon retDouble(hoursMinutesMatcher)="
                                              + retDouble);
                return retDouble;

        }
        /* If we end up here: The format is not understood
           (or one of the Strings is empty) */
        throw new NumberFormatException(coordStr);
    }

}
