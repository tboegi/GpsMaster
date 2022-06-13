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

    private final boolean debug = false;



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
                String me = "LatLonPanel.itemStateChanged ";
                boolean found = false;
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // deselectAllToggles(tglLatLonFocus); // TODO re-enable
                    mapPanel.setCursor(Cursor.CROSSHAIR_CURSOR);
                    String latString = textFieldLat.getText().trim();
                    String lonString = textFieldLon.getText().trim();
                    /* The if (true) here and further down is only for (easier) debugging:
                       change to true to false to disable the different regexps */
                    if (debug) System.out.println(me + "Begin:"
                                                  + " latString='"  + latString + "'"
                                                  + " lonString='" + lonString + "'");
                    if (true && !found) {
                        // Latititude/Longitude in hours minutes.decimalofminute
                        // Something in this style:
                        // Lat. 51 1234,56 / Long. 11 1234,56
                        // Trying to explain the regexp:
                        // NoDigits Digits SpaceOrDegree Digits CommaOrDot NoDigits Digits SpaceOrDegree Digits
                        String latLonHourMinRegex = "^[^0-9]*([0-9]+[ °][0-9,.]+)[^0-9]+([0-9]+[ °][0-9,.]+)";
                        Pattern latLonHourMinPattern = Pattern.compile(latLonHourMinRegex);
                        Matcher latLonHourMinMatcher = latLonHourMinPattern.matcher(latString);
                        if (latLonHourMinMatcher.find()) {
                            String oldLatString = latString;
                            latString = latLonHourMinMatcher.group(1);
                            lonString = latLonHourMinMatcher.group(2);
                            if (debug) System.out.println(me + "latLonHourMinMatcher" +
                                                          " oldLatString=" + oldLatString +
                                                          " latString="  + latString +
                                                          " lonString=" + lonString);
                            found = true;
                        }
                    }
                    if (true && !found) {
                        // 2 Strings seperated by '/'
                        String latSlashLonRegex = "([^/]+)/([^/]+)";
                        Pattern latSlashLonPattern = Pattern.compile(latSlashLonRegex);
                        Matcher latSlashLonMatcher = latSlashLonPattern.matcher(latString);
                        if (latSlashLonMatcher.find()) {
                            String oldLatString = latString;
                            latString = latSlashLonMatcher.group(1);
                            lonString = latSlashLonMatcher.group(2);
                            if (debug) System.out.println(me + "latSlashLonMatcher" +
                                                          " oldLatString=" + oldLatString +
                                                          " latString="  + latString +
                                                          " lonString=" + lonString);
                            found = true;
                        }
                    }
                    if (true && !found) {
                        // 2 Strings seperated by ' ' and or ','
                        String latSpaceOrKommaLonRegex = "(^[^ ,]+)[ ,]+([^/ ,]+)$";
                        Pattern latSpaceOrKommaLonPattern = Pattern.compile(latSpaceOrKommaLonRegex);
                        Matcher latSpaceOrKommaLonMatcher = latSpaceOrKommaLonPattern.matcher(latString);
                        if (latSpaceOrKommaLonMatcher.find()) {
                            String oldLatString = latString;
                            latString = latSpaceOrKommaLonMatcher.group(1);
                            lonString = latSpaceOrKommaLonMatcher.group(2);
                            if (debug) System.out.println(me + "latSpaceOrKommaLonMatcher" +
                                                          " oldLatString=" + oldLatString +
                                                          " latString="  + latString +
                                                          " lonString=" + lonString);
                            found = true;
                        }
                    }
                    if (true && !found) {
                        // 2 Strings seperated by ' '
                        String latSpaceLonRegex = "(^[^ ]+) ([^ ]+)$";
                        Pattern latSpaceLonPattern = Pattern.compile(latSpaceLonRegex);
                        Matcher latSpaceLonMatcher = latSpaceLonPattern.matcher(latString);
                        if (latSpaceLonMatcher.find()) {
                            String oldLatString = latString;
                            latString = latSpaceLonMatcher.group(1);
                            lonString = latSpaceLonMatcher.group(2);
                            if (debug) System.out.println(me + "latSpaceLonMatcher" +
                                                          " oldLatString=" + oldLatString +
                                                          " latString="  + latString +
                                                          " lonString=" + lonString);
                            found = true;
                        }
                    }

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
        String me = "LatLonPanel.parseCoordinate ";
        coordStr = coordStr.replaceAll(",", ".");
        if (debug) System.out.println(me + "latOrLon=" + latOrLon + " coordStr='" + coordStr + "'");
        if (true) {
            // 51 1234,56
            // Replace '  ' with ' ' Replace '°' with ' '
            coordStr = coordStr.replaceAll("\\s+", " ").replaceAll("°", " ");
            String hoursMinutesDecimalsRegex = "^([-+]?\\d+) (\\d+[.]?\\d*)([EWNS]?)$";
            Pattern hoursMinutesDecimalsPattern = Pattern.compile(hoursMinutesDecimalsRegex);
            Matcher hoursMinutesDecimalsMatcher = hoursMinutesDecimalsPattern.matcher(coordStr);
            if (hoursMinutesDecimalsMatcher.find()) {
                String degreeStr  = hoursMinutesDecimalsMatcher.group(1);
                String minutesStr = hoursMinutesDecimalsMatcher.group(2);
                String eastStr    = hoursMinutesDecimalsMatcher.group(3);
                if (debug) System.out.println(me + " hoursMinutesDecimals"
                                              + " degreeStr=" + degreeStr
                                              + " minutesStr=" + minutesStr
                                              + " eastStr=" + eastStr);

                Double retDouble = Double.parseDouble(degreeStr) + Double.parseDouble(minutesStr) / 60.0;
                int sign = 1;
                if (eastStr.equals("W") || eastStr.equals("S")) {
                    sign = 0 -sign;
                }
                retDouble = retDouble * sign;
                if (debug) System.out.println(me
                                              + "hoursMinutesDecimalsMatcher: retDouble="
                                              + retDouble);
                return retDouble;

            } else {
                if (debug) System.out.println(me + "hoursMinutesDecimalsMatcher not found");
            }
        }
        // To test the different format, set the true to false below
        if (true) {
            /* 51° 28′ 38″ N*/
            /* This parser is a little bit relaxed:
               a sign ('+' or '-')
               followed by digits (degree)
               followed by non-digits, e.g. ' ', '°'
               followed by digits (minute)
               followed by non-digits, e.g. ' ', '"'
               followed by digits or a dot (second)
               may followed by '°'
               may followed by one of 'E' 'W' 'N' 'S', the direction
            */
            String hoursMinSecRegex = "([-+]?\\d+)\\D+(\\d+)\\D+([0-9.]+)([^0-9a-zA-Z.]*)°?([EWNS]?)";
            Pattern hoursMinSecPattern = Pattern.compile(hoursMinSecRegex);
            Matcher hoursMinSecMatcher = hoursMinSecPattern.matcher(coordStr);
            if (hoursMinSecMatcher.find()) {
                String degreeStr = hoursMinSecMatcher.group(1);
                String minuteStr = hoursMinSecMatcher.group(2);
                String secondStr = hoursMinSecMatcher.group(3);
                String eastStr   = hoursMinSecMatcher.group(5);

                if (debug) System.out.println(me + "hoursMinutesSecMatcher)" +
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
                if (debug) System.out.println(me +
                                              "(hoursMinutesSecMatcher)" +
                                              " degree=" + degree +
                                              " minute=" + minute +
                                              " second=" + second +
                                              " retDouble=" + retDouble);
                return retDouble;
            } else {
                if (debug) System.out.println(me + "hoursMinSecMatcher not found");
            }
        }
        // To test the different format, set the true to false
        if (true) {
            // Remove all whitespace and a possible '°'
            coordStr = coordStr.replaceAll("\\s+", "").replaceAll("°", "");
            String hoursDecimalsRegex = "^([-+]?\\d+[.]?\\d*)([EWNS]?)$";
            Pattern hoursDecimalsPattern = Pattern.compile(hoursDecimalsRegex);
            Matcher hoursDecimalsMatcher = hoursDecimalsPattern.matcher(coordStr);
            if (hoursDecimalsMatcher.find()) {
                String degreeStr = hoursDecimalsMatcher.group(1);
                String eastStr   = hoursDecimalsMatcher.group(2);
                if (debug) System.out.println(me + "degreeStr=" + degreeStr
                                              + " eastStr=" + eastStr);

                Double retDouble = Double.parseDouble(degreeStr);
                int sign = 1;
                if (eastStr.equals("W") || eastStr.equals("S")) {
                    sign = 0 -sign;
                }
                retDouble = retDouble * sign;
                if (debug) System.out.println(me
                                              + "hoursDecimalsMatcher: retDouble="
                                              + retDouble);
                return retDouble;

            } else {
                if (debug) System.out.println(me + "hoursDecimalsMatcher not found");
            }
        }
        // To test the different format, set true to false
        /* If we end up here: The format is not understood
           (or one of the Strings is empty) */
        if (debug) System.out.println(me + "throw new NumberFormatExceptiond");
        throw new NumberFormatException(coordStr);
    }
}
