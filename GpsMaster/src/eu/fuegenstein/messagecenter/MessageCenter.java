package eu.fuegenstein.messagecenter;

import java.awt.Color;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 * Class handling the display of messages in a non-intrusive way.
 * Messages are displayed in panels at the bottom edge of the frame.
 * 
 * @author rfu
 *
 */
public class MessageCenter {

	private PropertyChangeListener changeListener = null;
	
	private JFrame frame = null;
	private JPanel glassPane = null;	
	private SpringLayout springLayout = new SpringLayout();
	
	private Color infoColor = new Color(177, 177, 25, 192); // transparent yellow
	private Color warningColor = new Color(255, 180, 0, 192); // transparent orange
	private Color errorColor = new Color(177, 25, 25, 208); // transparent red
	private boolean storing = false;
	
	private List<MessagePanel> panels = new ArrayList<MessagePanel>();	
	private int screenTime = 30; // default on-screen time in seconds

	// defaults
	private Color foregroundColor = Color.BLACK;
	

	/**
	 * Constructor
	 * @param parentFrame parentFrame to hold/show messages
	 */
	public MessageCenter(JFrame frame) {
		this.frame = frame;
		
		changeListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(MessagePanel.PCE_CLOSE)) {
					remove((MessagePanel) evt.getSource());
					paint();
				}				
			}
		};
	    // set up glasspane
	    glassPane = new JPanel();
	    glassPane.setLayout(springLayout);
	    glassPane.setOpaque(false);	    
	    frame.setGlassPane(glassPane);
	}

	public Color getInfoColor() {
		return infoColor;
	}

	public void setInfoColor(Color infoColor) {
		this.infoColor = infoColor;
	}

	public Color getWarningColor() {
		return warningColor;
	}

	public void setWarningColor(Color warningColor) {
		this.warningColor = warningColor;
	}

	public Color getErrorColor() {
		return errorColor;
	}

	public void setErrorColor(Color errorColor) {
		this.errorColor = errorColor;
	}

	public int getScreenTime() {
		return screenTime;
	}
	
	/**
	 * set time for which volatile messages are displayed on screen
	 * (minimum of 5 seconds)
	 * @param seconds display time in seconds
	 */
	public void setScreenTime(int seconds) {
		screenTime = seconds;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isStoring() {
		return storing;
	}

	/**
	 * Determine if messages will be kept internally 
	 * instead of displaying them immediately.
	 *
	 * @param storing {@link true} don't display but store messages, 
	 * {@link false} display messages immediately. Stored messages
	 * will also be displayed immediately if set to {@link false}   
	 */
	public void setStoring(boolean storing) {
		this.storing = storing;
		if (storing == false) {
			paint();
		}
	}

	/**
	 * 
	 * @return
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Set the parentFrame on which to display messages
	 * WARNING: DO NOT change the parentFrame during class runtime.
	 * results are unpredictable.
	 * @param parentFrame
	 */
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * display info message until closed by user
	 * @param text
	 */
	public void info(String text) {
		makePanel(infoColor, text, true, false);
		paint();
	}

	/**
	 * noch nicht wirklich durchdacht.
	 * @param msgPanel
	 */
	public void infoOn(MessagePanel panel) {
		panels.add(panel);
		paint();
	}
	/**
	 * 
	 * @param text
	 * @return
	 */
	public MessagePanel infoOn(String text) {		
		MessagePanel panel = makePanel(infoColor, text, false, false);
		paint();
		return panel;
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public MessagePanel infoOn(String text, Cursor cursor) {		
		MessagePanel panel = makePanel(infoColor, text, false, false);
		panel.setCursor(cursor);
		paint();
		return panel;
	}
	
	/**
	 * remove an info message from display
	 * @param msgPanel
	 */
	public void infoOff(MessagePanel panel) {
		if (panel != null) {
			remove(panel);
			paint();
		}
	}
	
	/**
	 * display info message until closed by timer
	 * @param text
	 */
	public void volatileInfo(String text) {
		makePanel(infoColor, text, true, true);
		paint();
	}

	/**
	 * display warning message until clicked by user
	 * @param text
	 */
	public void warning(String text) {
		makePanel(warningColor, text, true, false);
	}

	/**
	 * display warning message until closed by timer
	 * @param text
	 */
	public void volatileWarning(String text) {
		makePanel(warningColor, text, true, true);
		paint();
	}

	/**
	 * 
	 * @param text
	 * @param e
	 */
	public void volatileWarning(String text, Exception e) {
		makePanel(warningColor, text + ": " + e.getMessage(), true, true);
		paint();		
	}

	/**
	 * display error message until clicked by user
	 * @param text
	 */
	public void error(String text) {
		makePanel(errorColor, text, true, false);
		paint();		
	}

	/**
	 * 
	 * @param e
	 */
	public void error(Exception e) {
		// TODO folgenden code generalisieren, in private method auslagern
		// 		und von anderen (Exception e) methoden aufrufen
		if (e.getMessage() == null) {
			makePanel(errorColor, e.getClass().getName(), true, false);
		} else {
			makePanel(errorColor, e.getMessage(), true, false);
		}
		paint();
	}

	/**
	 * display error message until closed by timer
	 * @param text
	 */
	public void volatileError(String text) {
		makePanel(errorColor, text, true, true);
		paint();
	}
	
	/**
	 * 
	 * @param e
	 */
	public void volatileError(Exception e) {
		makePanel(errorColor, e.getMessage(), true, true);
		paint();
	}

	/**
	 * 
	 * @param text
	 * @param e
	 */
	public void error(String text, Exception e) {
		makePanel(errorColor, text + ": " + e.getMessage(), true, false);
		paint();
	}
	
	/**
	 * 
	 * @param text
	 * @param e
	 */
	public void volatileError(String text, Exception e) {
		makePanel(errorColor, text + ": " + e.getMessage(), true, false);
		paint();		
	}
	
	/**
	 * 
	 * @param msgPanel
	 */
	private synchronized void remove(MessagePanel panel) {
		if (panels.contains(panel)) {
			panels.remove(panel);
			springLayout.removeLayoutComponent(panel);
			glassPane.remove(panel);
		}
	}
		 
	/**
	 * 
	 */
	private synchronized void paint() {
		
		for (MessagePanel panel : panels) {
			springLayout.removeLayoutComponent(panel);
			glassPane.remove(panel);
		}
		
		// SpringLayout springLayout = new SpringLayout();
		// glassPane.setLayout(springLayout);
		if ((panels.size() > 0) && (storing == false)) {			
			glassPane.setSize(frame.getSize());
						
			// paint msgPanel
			MessagePanel firstPanel = panels.get(0);
			springLayout.putConstraint(SpringLayout.WEST, firstPanel, 1, SpringLayout.WEST, glassPane);
			springLayout.putConstraint(SpringLayout.EAST, firstPanel, -1, SpringLayout.EAST, glassPane);
			springLayout.putConstraint(SpringLayout.SOUTH, firstPanel, -1, SpringLayout.SOUTH, glassPane);			
			glassPane.add(firstPanel);
			
			MessagePanel prev = firstPanel;
			for (int i = 1; i < panels.size(); i++) {
				MessagePanel current = panels.get(i);
				springLayout.putConstraint(SpringLayout.WEST, current, 1, SpringLayout.WEST, glassPane);
				springLayout.putConstraint(SpringLayout.EAST, current, -1, SpringLayout.EAST, glassPane);
				// upper msgPanel overlaps with lower msgPanel for a few pixels:
				springLayout.putConstraint(SpringLayout.SOUTH, current, 3, SpringLayout.NORTH, prev);				
				glassPane.add(current);
				prev = current;
			}
			glassPane.revalidate();
			glassPane.repaint();
			glassPane.setVisible(true);
			// frame.repaint();
			
		} else {
			glassPane.setVisible(false);
		}		
	}
		
	/**
	 * 
	 * @param color background color
	 * @param message message text
	 * @param isCloseable if the msgPanel can be closed via mouseclick
	 * @param isVolatile if the msgPanel will disappear after {@link onScreen} seconds
	 * @return
	 */
	private MessagePanel makePanel(Color color, String message, boolean isCloseable, boolean isVolatile) {
		
		// MessagePanel msgPanel = new MessagePanel(glassPane.getWidth());
		MessagePanel panel = new MessagePanel();
		panel.setPanelWidth(frame.getWidth());
		panel.setForeground(foregroundColor);
		panel.setBackground(color);
		panel.setCloseable(isCloseable);
		panel.setText(message);
		panel.addPropertyChangeListener(changeListener);
		
		if (isVolatile) {
			panel.setScreenTime(screenTime);
		}
		
		panels.add(panel);
		return panel;
	}

}