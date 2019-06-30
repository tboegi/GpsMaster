package eu.fuegenstein.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Class implementing a square Icon that 
 * just consists of a single color.
 * 
 * @author rfu
 *
 */
public class PlainColorIcon implements Icon {

	private int width = 16;
	private int height = 16;
	
	private Color color = null;
	
	/**
	 * Instantiate a {@link PlainColorIcon} with a default size of 16x16
	 * @param color
	 */
	public PlainColorIcon(Color color) {
		this.color = color;
	}

	/**
	 * 
	 * @param color
	 * @param size
	 */
	public PlainColorIcon(Color color, int size) {
		this.color = color;
		width = size;
		height = size;		
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	/**
	 * 
	 * @return
	 */
	public Color getColor() {
		return color;
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(x, y, width, height);		
	}

}
