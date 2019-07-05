package org.gpsmaster.markers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.Waypoint;

/**
 * Abstract Class representing a Marker; containing common code
 * A Marker basically is a {@link Waypoint} with an icon.
 *
 * @author rfu
 *
 * TODO if the underlying {@link Waypoint} has one or more links,
 *		overlay the icon with a small globe (or similar) in the
 *		lower right corner
 */
public class Marker extends Waypoint {

	protected ImageIcon icon = null;
	protected ImageIcon webIcon = null;
	protected Color iconColor = Color.BLUE;
	protected Color foregroundColor = Color.BLACK;
	protected Color backgroundColor = new Color(255, 255, 255, 192); // transparent white
	protected Font font = null;

	protected Point iconLocation = new Point(); // on-screen location of the icon
	private Rectangle iconBounds = new Rectangle(); // boundaries of the icon image
	private Rectangle labelBounds = new Rectangle(); // boundaries of the label text

	protected JLabel label = new JLabel();
	protected boolean drawBounds = false;
	private boolean isSelected = false;

	protected int offset = 2; // distance between icon & Waypoint position if POSITION != CENTER

	// label/marker positions:
	// for marker: position in relation to GPS point
	// for label: position in relation to marker
	public static final int POSITION_ABOVE = 1;
	public static final int POSITION_BELOW = 2;
	public static final int POSITION_LEFT = 3;
	public static final int POSITION_RIGHT = 4;
	public static final int POSITION_CENTER = 5;

	protected final String resourcePath = "/org/gpsmaster/icons/markers/";

	protected int labelPosition = POSITION_BELOW;
	protected int markerPosition = POSITION_ABOVE;
	protected boolean showWebIcon = false;

	/**
	 *
	 * @param lat Latitude
	 * @param lon Longitude
	 */
	public Marker(double lat, double lon) {
		super(lat, lon);
		setup();
		setType();
	}

	/**
	 *
	 * @param waypoint
	 */
	public Marker(Waypoint waypoint) {
		super(waypoint);
		setup();
		setType();
	}

	/*
	 * PUBLIC PROPERTIES
	 */

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
		iconBounds.width = icon.getIconWidth();
		iconBounds.height = icon.getIconHeight();
	}

	/**
	 *
	 * @param filename
	 */
	public void setIcon(String filename) {
		icon = new ImageIcon(GpsMaster.class.getResource(resourcePath.concat(filename)));
		iconBounds.width = icon.getIconWidth();
		iconBounds.height = icon.getIconHeight();
	}

	/**
	 *
	 * @return
	 */
	public Color getIconColor() {
		return iconColor;
	}

	/**
	 * Set the color of the icon, if possible.
	 * This is done by re-coloring the bitmap
	 * representing the icon.
	 * @param color
	 */
	public void setIconColor(Color color) {
		iconColor = color;
	}


	/**
	 *
	 * @return
	 */
	public Color getForeground() {
		return foregroundColor;
	}

	/**
	 * Set label text color
	 * @param color
	 */
	public void setForeground(Color color) {
		foregroundColor = color;
	}

	/**
	 *
	 * @return
	 */
	public Color getBackground() {
		return backgroundColor;
	}

	/**
	 * Set label background color
	 * @param color
	 */
	public void setBackground(Color color) {
		backgroundColor = color;
	}

	protected boolean isSelected() {
		return isSelected;
	}

	protected void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	/**
	 *
	 * @return
	 */
	public int getMarkerPosition() {
		return markerPosition;
	}

	/**
	 * Set the location of the marker in relation to its coordinates
	 */
	public void setMarkerPosition(int position) {
		// check range of param
		markerPosition = position;
	}

	/**
	 *
	 * @return
	 */
	public int getLabelPosition() {
		return labelPosition;
	}

	/**
	 * Set the location of the label in relation to the icon
	 *
	 * @param position
	 */
	public void setLabelPosition(int position) {
		// check range of param
		labelPosition = position;
	}


	/**
	 * determines if the icon or label is located over the given point
	 * @param p
	 * @return {@link false} if icon or label contains {@link p}, {@link false} otherwise
	 */
	public boolean contains(Point p) {
		if (p != null) {
			return (iconBounds.contains(p) || labelBounds.contains(p));
		}
		return false;
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 *
	 * @param g2d
	 */
	public void paint(Graphics2D g2d, Point point) {

		if (icon != null) {
			paintIcon(g2d, point);

			if ((links != null) && (links.size() > 0) && showWebIcon) {
				paintWebIcon(g2d, point);
			}
		}

		// paint text label
		if (name != null) {
			paintLabel(g2d, point);
		}

		if ((drawBounds || isSelected()) && (icon != null)) {
			g2d.drawRect(iconBounds.x, iconBounds.y, iconBounds.width, iconBounds.height);
		}
	}

	/**
	 *
	 * @param g2d
	 * @param point
	 */
	private void paintWebIcon(Graphics2D g2d, Point point) {
		if (webIcon == null) {
			webIcon = new ImageIcon(GpsMaster.class.getResource(resourcePath.concat("link-small.png")));
		}
		int posx = iconBounds.x + iconBounds.width - webIcon.getIconWidth();
		int posy = iconBounds.y + iconBounds.height - webIcon.getIconHeight();
		webIcon.paintIcon(null, g2d, posx, posy);
	}

	/**
	 * @param g2d
	 * @param point
	 * @param labelPoint
	 */
	private void paintLabel(Graphics2D g2d, Point point) {

		Point labelPoint = new Point();
		FontMetrics metrics = g2d.getFontMetrics();
		Rectangle2D box = null;
		box = metrics.getStringBounds(name, g2d);

		switch(labelPosition) {
		default: // TODO below icon!! default = below
			labelPoint.x = point.x - (int) (box.getWidth() / 2);
			labelPoint.y = point.y + 2;
			break;
		}

		g2d.setColor(backgroundColor);
		g2d.fillRoundRect(
			labelPoint.x - 2,
			labelPoint.y - 2,
			(int) (box.getWidth() + 4),
			(int) (box.getHeight() + 4),
			2, 2);

		g2d.setColor(Color.BLACK);
		g2d.drawString(name, labelPoint.x, (int) (labelPoint.y + box.getHeight() - 1));
	}

	/**
	 * @param g2d
	 * @param point
	 */
	private void paintIcon(Graphics2D g2d, Point point) {
		switch(markerPosition) {

		case POSITION_CENTER:
			iconLocation.x = point.x - icon.getIconWidth() / 2;
			iconLocation.y = point.y - icon.getIconHeight() / 2;
			break;

		case POSITION_RIGHT:
			iconLocation.x = point.x + offset;
			iconLocation.y = point.y - icon.getIconHeight() / 2;
			break;

		case POSITION_ABOVE:
		default:  // default = ABOVE
			iconLocation.x = point.x - (icon.getIconWidth() / 2);
			iconLocation.y = point.y - icon.getIconHeight() - offset;
			break;
		}

		// g2d.drawImage(icon.getImage(), iconLocation.x, iconLocation.y, null);
		icon.paintIcon(null, g2d, iconLocation.x, iconLocation.y);
		iconBounds.x = iconLocation.x;
		iconBounds.y = iconLocation.y;
	}

	/**
	 * to be overriden by subclasses
	 */
	protected void setup() {

	}

	/**
	 * Set the type of this marker as an extension.
	 * Used on re-loading the file to instantiate the proper class
	 */
	private void setType() {
		getExtensions().put("gpsm:type", getClass().getSimpleName());
	}

	/**
	 * compare by timestamp
	 * @param arg0
	 * @return
	 */
	public int compareTo(Marker m) {
		if ((getTime() == null) || (m.getTime() == null)) {
			return 0;
		}
		return getTime().compareTo(m.getTime());
	}
}
