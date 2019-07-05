package eu.fuegenstein.swing;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class Widget extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -453248357188458175L;

	protected Color transparentWhite = new Color(255, 255, 255, 192);

	protected int corner = WidgetLayout.TOP_LEFT;

	/**
	 *
	 */
	public Widget() {
		setBorder(new EmptyBorder(10, 0, 5, 10));
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
