package eu.fuegenstein.messagecenter;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.Timer;

/**
 * A msgPanel for displaying messages via the {@link MessageCenter}
 * 
 * @author rfu
 *
 * TODO implement this functionality in subclasses for each type
 * 		(info, warning, error)
 * 
 */
@SuppressWarnings("serial")
public class MessagePanel extends JPanel {

	public static final String PCE_CLOSE = "CLOSE"; // close me!
	
	protected SpringLayout springLayout = new SpringLayout(); // TODO rename -> layout
	protected JTextArea textArea = new JTextArea();
	protected Font font = new Font("Segoe UI", Font.PLAIN, 18);

	
	private int width = 800;
	private int screenTime = 0; // total time in seconds	
	protected boolean isCloseable = true;

	protected String messageText = "";
	
	// waste of resources, but better to handle:
	private JProgressBar countdownBar = null;
	private Timer timer = null;
	private ActionListener timerListener = null;
	private MouseAdapter mouseListener = null;
	
	protected JLabel icon = new JLabel(); // TODO rename -> closeLabel
		
	/**
	 * Default Constructor
	 */
	public MessagePanel() {
		super();
		setVisible(true);
		setOpaque(true);
		setLayout(springLayout);
				
		// set up countdown bar
		countdownBar = new JProgressBar();
		countdownBar.setOrientation(JProgressBar.VERTICAL);
		countdownBar.setMinimum(0);
		countdownBar.setOpaque(true);
		countdownBar.setString("");
		countdownBar.setStringPainted(true);
		countdownBar.setBorder(BorderFactory.createEmptyBorder());
		
		// set width, color, ...

	    mouseListener = new MouseAdapter() {
	    	@Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	firePropertyChange(PCE_CLOSE, null, null);
                }
            }	
		};
		
		// set up close button
		icon.setIcon(new ImageIcon(MessageCenter.class.getResource("/eu/fuegenstein/icons/cancel.png")));
		// icon.setVisible(false);
		icon.addMouseListener(mouseListener);
		
		timerListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				screenTime += -2;
				countdownBar.setValue(screenTime);
				if (screenTime <= 0) {
					firePropertyChange(PCE_CLOSE, null, null);
					timer.stop();
				}
			}
		};
		// 
		// set up timer
		timer = new Timer(1000, timerListener);
		timer.setDelay(2000);
		textArea.setName("TextPane");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		textArea.setFont(font);
		textArea.setSize(width, 1000);
		textArea.setEditable(false);
		textArea.setVisible(true);		
		add(textArea);
		
		layoutComponents();
	}

	/**
	 * if onScreen is set, this msgPanel is volatile.
	 * @param onScreen number of seconds this msgPanel is displayed on screen
	 */
	public void setScreenTime(int screenTime) {
		this.screenTime = screenTime;
		if (screenTime > 0) {
			countdownBar.setMaximum(screenTime);
			countdownBar.setValue(screenTime);			
			countdownBar.setBackground(getBackground());
			countdownBar.setForeground(getBackground().darker());			
			add(countdownBar);
			timer.start();
		} else {
			remove(countdownBar);
			timer.stop();
		}
		layoutComponents();
	}
	
	public int getScreenTime() {
		return screenTime;
	}

	/**
	 * determine if this msgPanel is closeable by the user
	 * @param isCloseable
	 */
	public void setCloseable(boolean isCloseable) {
		this.isCloseable = isCloseable;
		if (isCloseable) {
			add(icon);
		} else {
			remove(icon);
		}
		layoutComponents();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCloseable() {
		return isCloseable;
	}

	public String getText() {
		return this.messageText;
	}

	/**
	 * make sure to call setPanelWidth() before setText()
	 * @return
	 */
	public int getPanelWidth() {
		return width;
	}

	public void setPanelWidth(int width) {
		this.width = width;
		textArea.setSize(width, 1000);
		validate();		
		Dimension panelSize = new Dimension(textArea.getPreferredSize().width, textArea.getPreferredSize().height+4);
		setPreferredSize(panelSize);				
	}	

	/**
	 * 
	 * @param text text to be displayed
	 */
	public void setText(String text) {

		this.messageText = text;
		textArea.setText(text);
		validate();		
		Dimension panelSize = new Dimension(textArea.getPreferredSize().width, textArea.getPreferredSize().height+4);
		setPreferredSize(panelSize);						
	}

	/**
	 * Position the components within the msgPanel
	 */
	private void layoutComponents() {

		boolean isCountdown = (screenTime > 0);
		
		// remove all previous constraints		
		springLayout.removeLayoutComponent(icon);
		springLayout.removeLayoutComponent(countdownBar);
		springLayout.removeLayoutComponent(textArea);

		// the constants
		springLayout.putConstraint(SpringLayout.WEST, textArea, 3, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, textArea, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, textArea, 0, SpringLayout.NORTH, this);

		
		// the depending ones
		if (isCloseable) { // close icon in the upper right corner
			springLayout.putConstraint(SpringLayout.EAST, icon, 0, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.NORTH, icon, 0, SpringLayout.NORTH, this);			
		}
		
		if (isCountdown) {
			springLayout.putConstraint(SpringLayout.EAST, countdownBar, 0, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, countdownBar, 0, SpringLayout.SOUTH, this);
		}
		
		// close icon only: textarea ends at icon
		if (isCloseable && !isCountdown) {
			springLayout.putConstraint(SpringLayout.EAST, textArea, 0, SpringLayout.WEST, icon);
		}

		// countdown only: textarea ends at bar
		if (isCountdown && !isCloseable) {
			springLayout.putConstraint(SpringLayout.EAST, textArea, 0, SpringLayout.WEST, icon);
			springLayout.putConstraint(SpringLayout.NORTH, countdownBar, 0, SpringLayout.NORTH, this);
		}
		
		// both: 
		if (isCountdown && isCloseable) {
			springLayout.putConstraint(SpringLayout.EAST, textArea, 0, SpringLayout.WEST, icon);
			springLayout.putConstraint(SpringLayout.NORTH, countdownBar, 0, SpringLayout.SOUTH, icon);
		}
	}
}