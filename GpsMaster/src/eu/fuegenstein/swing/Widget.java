package eu.fuegenstein.swing;

import java.awt.Color;

import javax.swing.JPanel;

public class Widget extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -453248357188458175L;
	
	protected final Color BACKGROUNDCOLOR = new Color(255, 255, 255, 160); // transparent white
	
	protected int corner = WidgetLayout.TOP_LEFT;
	
	/**
	 * 
	 */
	public Widget() {
		super();
	}
	
	/**
	 * Constructor
	 * @param corner the corner this widget is placed in.
	 * see {@link WidgetLayout} TOP_*, BOTTOM_*
	 */
	protected Widget(int corner) {
		this();
		this.corner = corner;					
	}
	
	/**
	 * Get the corner in which this widget is displayed 
	 * @return CORNER_*
	 * 
	 * There is no setter, since current {@link WidgetLayout}
	 * implementation does not handle corner changes during runtime.
	 */
	public int getCorner() {
		return corner;
	}
}
