package org.gpsmaster.dialogs;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JFrame;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.unit.UnitConverter;

/**
 * 
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
	protected MessagePanel panel = null;
	protected UnitConverter uc = null;
	protected Color backgroundColor = Color.WHITE;

	/**
	 * Constructor
	 * @param inApp app object
	 */
	public GenericDialog(JFrame parentFrame, MessageCenter msg)	
	{
		// super(frame);
		this.msg = msg;
		this.parentFrame = parentFrame;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
	protected void setSizeAndCenter(int width, int height) {
		
	}
	
	/**
	 * Begin the function
	 */
	public abstract void begin();

	/**
	 * @return the key for the function name
	 */
	public abstract String getTitle();
	
}
