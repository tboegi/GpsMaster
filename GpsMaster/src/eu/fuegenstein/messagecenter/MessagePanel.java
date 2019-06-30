package eu.fuegenstein.messagecenter;


import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;


@SuppressWarnings("serial")
public class MessagePanel extends JPanel {

	// time at which this message is expired
	
	protected SpringLayout springLayout = new SpringLayout(); // TODO rename -> layout
	protected JTextArea textArea = new JTextArea();
	protected Font font = new Font("Segoe UI", Font.PLAIN, 18);
	protected ImageIcon closeIcon = null;
	protected JLabel icon = new JLabel(); // TODO rename -> closeLabel
	
	private int width = 800;
	private int onScreen = 0;
	private long expireTime = 0;
	protected boolean isCloseable = true;

	protected String messageText = "";
	
	// BorderLayout nicht einfacher?
	
	/**
	 * Default Constructor
	 */
	public MessagePanel() {
		super();
		setVisible(true);
		setOpaque(true);
		setLayout(springLayout);
		
		// set up close button
	    closeIcon = new ImageIcon(MessageCenter.class.getResource("/eu/fuegenstein/icons/cancel.png"));
		icon.setIcon(closeIcon);
		icon.setVisible(isCloseable());
		add(icon);

		springLayout.putConstraint(SpringLayout.EAST, icon, 0, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, icon, 0, SpringLayout.NORTH, this);
		
		textArea.setName("TextPane");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		textArea.setFont(font);
		textArea.setSize(width, 1000);
		textArea.setEditable(false);
		textArea.setVisible(true);		
		add(textArea);
		
		springLayout.putConstraint(SpringLayout.EAST, textArea, 0, SpringLayout.WEST, icon);
		springLayout.putConstraint(SpringLayout.WEST, textArea, 3, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, textArea, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, textArea, 0, SpringLayout.NORTH, this);
	}

	/**
	 * if onScreen is set, this panel is volatile.
	 * @param onScreen number of seconds this panel is displayed on screen
	 */
	public void setScreenTime(int screenTime) {
		this.onScreen = screenTime;
	}
	
	public int getScreenTime() {
		return onScreen;
	}

	
	public void setCloseable(boolean isCloseable) {
		this.isCloseable = isCloseable;
		icon.setVisible(isCloseable);
	}
	
	public boolean isCloseable() {
		return isCloseable;
	}

	
	public long getExpireTime() {
		return expireTime;
	}
	
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
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

}