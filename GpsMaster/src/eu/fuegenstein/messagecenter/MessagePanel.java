package eu.fuegenstein.messagecenter;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


@SuppressWarnings("serial")
public class MessagePanel extends JPanel {

	// time at which this message is expired
	
	protected boolean isVolatile = false;
	protected long expireTime = 10;
	protected String text = "";
	

	public void setVolatile(boolean isVolatile) {
		this.isVolatile = isVolatile;
	}
	
	public boolean isVolatile() {
		return isVolatile;
	}
	
	public long getExpireTime() {
		return expireTime;
	}
	
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		for (Component component : getComponents()) {
			if (component.getName().equals("TextPane")) {
				((JTextArea) component).setText(text);
				this.text = text;
				break;
			}						
		}
	}	
}
