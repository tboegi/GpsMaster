package org.gpsmaster.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.gpsmaster.GpsMaster;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.unit.UnitConverter;

/**
 *
 * @author tim.prune
 * @author rfu
 * 
 */
/**
 * Generic function class for launching from the app
 */
public abstract class GenericDialog extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2979483355856594216L;
	/** Reference to message center */
	protected MessageCenter msg = null;
	/** Reference to parent parentFrame */
	protected JFrame parentFrame = null;
	protected MessagePanel msgPanel = null;
	protected Container contentPane = null; // shortcut to getContentPane()
	protected UnitConverter uc = null;
	protected Color backgroundColor = Color.WHITE;

	/**
	 * 
	 * @param parentFrame
	 */
	public GenericDialog(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		contentPane = getContentPane();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);		
	}
	
	/**
	 * 
	 * @param parentFrame
	 * @param msg
	 */
	public GenericDialog(JFrame parentFrame, MessageCenter msg)	{
		this(parentFrame);
		this.msg = msg;
	}

	/**
	 * @return the uc
	 */
	public UnitConverter getUnitConverter() {
		return uc;
	}

	/**
	 * @param uc the uc to set
	 */
	public void setUnitConverter(UnitConverter uc) {
		this.uc = uc;
	}

	/**
	 * position the dialog in the center of the parent frame
	 * 
	 */
	protected void setCenterLocation() {
		setLocationRelativeTo(parentFrame);
		Point location = new Point();
		location.x = parentFrame.getLocation().x + parentFrame.getWidth() / 2 - getWidth() / 2;
		location.y = parentFrame.getLocation().y + parentFrame.getHeight() / 2 - getHeight() / 2;
		setLocation(location);		
	}
	
	/**
	 * 
	 * @param width
	 * @param height
	 */
	protected void xsetSizeAndCenter(int width, int height) {
		setSize(width, width);
		setCenterLocation();
	}
	
	/**
	 * Begin the function
	 */
	public abstract void begin();

	/**
	 * @return the key for the function name
	 */
	public abstract String getTitle();
	
	/**
	 * set the icon of this dialog
	 * 
	 * @param iconPath path to icon directory
	 * @param fileName name of icon file
	 */
	protected void setIcon(String iconPath, String fileName) {
		setIconImage(new ImageIcon(GpsMaster.class.getResource(iconPath + fileName)).getImage());
	}
	
	/**
	 * 
	 */
	protected void busyOn() {
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}
	
	/**
	 * 
	 */
	protected void busyOff() {
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
}
