package eu.fuegenstein.messagecenter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
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
	private ImageIcon closeIcon = null;
	
	private SpringLayout springLayout = new SpringLayout();
	private List<MessagePanel> panels = new ArrayList<MessagePanel>();	
	private int screenTime = 30; // default time in seconds

	// defaults
	private Color foregroundColor = Color.BLACK;
	private Font font = new Font("Segoe UI", Font.PLAIN, 18);

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
	    glassPane.setLayout(springLayout);
	    glassPane.setOpaque(false);
	    
	    frame.setGlassPane(glassPane);
	    closeIcon = new ImageIcon(MessageCenter.class.getResource("/eu/fuegenstein/icons/cancel.png"));

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
	 * display info message until clicked by user
	 * @param text
	 */
	public void Info(String text) {
		makePanel(infoColor, text, true, false);
		paint();
	}

	/**
	 * noch nicht wirklich durchdacht.
	 * @param panel
	 */
	public void InfoOn
	(MessagePanel panel) {
		panels.add(panel);
		paint();
	}
	/**
	 * 
	 * @param text
	 * @return
	 */
	public MessagePanel InfoOn(String text) {		
		MessagePanel panel = makePanel(infoColor, text, false, false);
		paint();
		return panel;
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public MessagePanel InfoOn(String text, Cursor cursor) {		
		MessagePanel panel = makePanel(infoColor, text, false, false);
		panel.setCursor(cursor);
		paint();
		return panel;
	}
	
	/**
	 * remove an info message from display
	 * @param panel
	 */
	public void InfoOff(MessagePanel panel) {
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
	public void Warning(String text) {
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
	public void Error(String text) {
		makePanel(errorColor, text, true, false);
		paint();		
	}

	/**
	 * 
	 * @param e
	 */
	public void Error(Exception e) {
		makePanel(errorColor, e.getMessage(), true, false);
		paint();
	}

	/**
	 * display error message until closed by timer
	 * @param text
	 */
	public void VolatileError(String text) {
		makePanel(errorColor, text, true, true);
		paint();
	}
	
	/**
	 * 
	 * @param e
	 */
	public void VolatileError(Exception e) {
		makePanel(errorColor, e.getMessage(), true, true);
		paint();
	}

	/**
	 * 
	 * @param text
	 * @param e
	 */
	public void Error(String text, Exception e) {
		makePanel(errorColor, text + ": " + e.getMessage(), true, false);
		paint();
	}
	
	/**
	 * 
	 * @param text
	 * @param e
	 */
	public void VolatileError(String text, Exception e) {
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
			if (panel.isVolatile() && (System.currentTimeMillis() > panel.getExpireTime())) {
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
		if (panels.size() > 0) {
			glassPane.removeAll();
			glassPane.setSize(frame.getSize());
						
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
	 * @param isVolatile if the panel will disappear after {@link screenTime} seconds
	 * @return
	 */
	private MessagePanel makePanel(Color color, String message, boolean isCloseable, boolean isVolatile) {
		SpringLayout springLayout = new SpringLayout();
		MessagePanel panel = new MessagePanel();

		panel.setLayout(springLayout);
		panel.setOpaque(true);
		panel.setForeground(foregroundColor);
		panel.setBackground(color);
		panel.setVisible(true);
		panel.setVolatile(isVolatile);
		// panel.addMouseListener(mouseListener); // TODO make panel clickable
		
		JTextArea textArea = new JTextArea();
		textArea.setName("TextPane");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		textArea.setFont(font);
		textArea.setText(message);
		textArea.setSize(glassPane.getWidth(), 1000);
		textArea.setEditable(false);
		textArea.setVisible(true);
		
		panel.validate();		
		Dimension panelSize = new Dimension(textArea.getPreferredSize().width, textArea.getPreferredSize().height+4);
		panel.setPreferredSize(panelSize);
		panel.add(textArea);		

		springLayout.putConstraint(SpringLayout.WEST, textArea, 3, SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.SOUTH, textArea, 0, SpringLayout.SOUTH, panel);
		springLayout.putConstraint(SpringLayout.NORTH, textArea, 0, SpringLayout.NORTH, panel);
		
		if (isCloseable) {
			JLabel icon = new JLabel();
			icon.setIcon(closeIcon);
			panel.add(icon);			
			panel.addMouseListener(mouseListener);
			
			springLayout.putConstraint(SpringLayout.EAST, icon, 0, SpringLayout.EAST, panel);
			springLayout.putConstraint(SpringLayout.NORTH, icon, 0, SpringLayout.NORTH, panel);
			springLayout.putConstraint(SpringLayout.EAST, textArea, 0, SpringLayout.WEST, icon);
		} else {
			springLayout.putConstraint(SpringLayout.EAST, textArea, 0, SpringLayout.EAST, panel);
		}
		if (isVolatile) {
			long expireTime = System.currentTimeMillis() + screenTime * 1000;
//			System.out.println(String.format("set expire time %d", expireTime));
			panel.setExpireTime(expireTime);			
		}
		
		panels.add(panel);
		return panel;
	}

}