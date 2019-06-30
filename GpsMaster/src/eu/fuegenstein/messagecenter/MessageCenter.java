package eu.fuegenstein.messagecenter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;


public class MessageCenter {

	private ActionListener timerListener = null;
	private MouseListener mouseListener = null;
	
	private Timer timer = null;
	private JFrame frame = null;
	private JPanel glassPane = null;	

	private Color infoColor = new Color(177, 177, 25, 192); // transparent yellow
	private Color warningColor = new Color(255, 180, 0, 192); // transparent orange
	private Color errorColor = new Color(177, 25, 25, 208); // transparent red
	private boolean storing = false;
	
	// private ImageIcon closeIcon = null;
	
	// private SpringLayout springLayout = new SpringLayout();
	private List<MessagePanel> panels = new ArrayList<MessagePanel>();	
	private int screenTime = 30; // default time in seconds

	// defaults
	private Color foregroundColor = Color.BLACK;
	

	/**
	 * Constructor
	 * @param glassPane
	 */
	public MessageCenter(JFrame frame) {
		this.frame = frame;
		
		timerListener = new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				timerCheck(e);			
			}
		};

	    timer = new Timer(1000, timerListener);
	    timer.setDelay(5000); // check every 5s for panels to close
	    // TODO let panels check time themselves, then fire "close me!" event
	    timer.start();
	
	    mouseListener = new MouseAdapter() {
	    	@Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	// System.out.println("mouse clicked");
                	removeClicked((Component) e.getSource());
                }
            }	
		};
		
	    // set up glasspane
	    glassPane = new JPanel();
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

	public boolean isStoring() {
		return storing;
	}

	public void setStoring(boolean storing) {
		this.storing = storing;
		if (storing == false) {
			paint();
		}
	}

	/**
	 * display info message until clicked by user
	 * @param text
	 */
	public void info(String text) {
		makePanel(infoColor, text, true, false);
		paint();
	}

	/**
	 * noch nicht wirklich durchdacht.
	 * @param panel
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
	 * @param panel
	 */
	public void infoOff(MessagePanel panel) {
		remove(panel);
		paint();
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
		
		if (e.getMessage() == null) {
			makePanel(errorColor, "(NULL) Exception", true, false);
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
	 * @param panel
	 */
	private synchronized void removeClicked(Component panel) {
		if (panels.contains(panel)) {
			panels.remove(panel);
		}
		paint();
	}
	
	/**
	 * 
	 * @param panel
	 */
	private synchronized void remove(MessagePanel panel) {
		if (panels.contains(panel)) {
			panels.remove(panel);
		}
		paint();
	}
	
	/**
	 * check for expired panels to be removed from display
	 * @param e timer event
	 */
	private void timerCheck(ActionEvent e) {
		List<MessagePanel> toDelete = new ArrayList<MessagePanel>();		
		for (MessagePanel panel : panels) {
			if ((panel.getExpireTime() > 0) && (System.currentTimeMillis() > panel.getExpireTime())) {
				toDelete.add(panel);
			}
		}
		for (MessagePanel panel : toDelete) {
			panels.remove(panel);
		}
		if (toDelete.size() > 0) {
			paint();
		}
	}
	 
	/**
	 * 
	 */
	private synchronized void paint() {
		SpringLayout springLayout = new SpringLayout();
		glassPane.setLayout(springLayout);
		if ((panels.size() > 0) && (storing == false)) {
			glassPane.removeAll();
			glassPane.setSize(frame.getSize());
			
			// check for volatile panels, set expire time
			for (MessagePanel panel : panels) {
				panel.setPanelWidth(frame.getWidth());
				if ((panel.getScreenTime() > 0) && (panel.getExpireTime() == 0)) {
					long expireTime = System.currentTimeMillis() + panel.getScreenTime() * 1000;
//					System.out.println(String.format("set expire time %d", expireTime));
					panel.setExpireTime(expireTime);								
				}
			}
			
			// paint panel
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
				// upper panel overlaps with lower panel for a few pixels:
				springLayout.putConstraint(SpringLayout.SOUTH, current, 3, SpringLayout.NORTH, prev);				
				glassPane.add(current);
				prev = current;
			}
			glassPane.validate();
			glassPane.setVisible(true);
			
		} else {
			glassPane.setVisible(false);
		}		
	}
		
	/**
	 * 
	 * @param color background color
	 * @param message message text
	 * @param isCloseable if the panel can be closed via mouseclick
	 * @param isVolatile if the panel will disappear after {@link onScreen} seconds
	 * @return
	 */
	private MessagePanel makePanel(Color color, String message, boolean isCloseable, boolean isVolatile) {
		
		// MessagePanel panel = new MessagePanel(glassPane.getWidth());
		MessagePanel panel = new MessagePanel();
		panel.setForeground(foregroundColor);
		panel.setBackground(color);
		panel.setCloseable(isCloseable);
		panel.setText(message);
		
		// TODO make whole PANEL clickable (not just X)
				
		if (isCloseable) {
			panel.addMouseListener(mouseListener);
		}
		if (isVolatile) {
			panel.setScreenTime(screenTime);
		}
		
		panels.add(panel);
		return panel;
	}

    /**
     * 
     * @param message
     */
//    private void showError(String message) {
//    	glassPaneVisible = glassPane.isVisible();
//    	frame.setGlassPane(errorPane);
//    	errorPane.setVisible(true);
//    	errorPaneStatus.setText(message);
//    	frame.repaint();
//    	lastErrorDisplay = System.currentTimeMillis();
//    }

}