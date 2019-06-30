package org.gpsmaster.dialogs;

import java.awt.Point;

import javax.swing.JFrame;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;

/**
 * 
 * derzeit etwas unglücklich. entweder alle dialoge darauf aufbauen
 * oder diese klasse in GenericDownloadDialog einarbeiten
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


	/**
	 * Constructor
	 * @param inApp app object
	 */
	public GenericDialog(JFrame parentFrame, MessageCenter msg)	
	{
		// super(frame);
		this.msg = msg;
		this.parentFrame = parentFrame;					
	}

	/**
	 * set default size and position on screen
	 * in relation to parent frame
	 */
	protected void setDefaultSize() {
		setLocationRelativeTo(parentFrame);
		Point location = new Point();
		location.x = parentFrame.getLocation().x + parentFrame.getWidth() / 2 - getWidth() / 2;
		location.y = parentFrame.getLocation().y + parentFrame.getHeight() / 2 - getHeight() / 2;
		setLocation(location);		
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
