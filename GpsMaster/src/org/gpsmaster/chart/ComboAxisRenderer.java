package org.gpsmaster.chart;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * Renderer for the {@link JComboBox} of an {@link ChartAxis}
 * 
 * @author rfu
 *
 */
public class ComboAxisRenderer extends JLabel implements ListCellRenderer<ChartAxis> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3994208742679161249L;
	// private Color translucent = new Color(177, 177, 25, 80);
	private Color translucent = new Color(255, 255, 255, 192);
	/**
	 * Default Constructor
	 */
	public ComboAxisRenderer() {
		setOpaque(false);
	}

	@Override
	public Component getListCellRendererComponent(
		JList<? extends ChartAxis> list, ChartAxis axis, int index,
		boolean isSelected, boolean cellHasFocus) {
		setOpaque(false); // does not work
		ImageIcon icon = axis.getIcon();	
		setIcon(icon); 
		setText(axis.getTitle());
	
		setBackground(translucent); // does not work		
		
		if (axis instanceof ChartXAxis) {
			setHorizontalTextPosition(SwingConstants.LEFT);
			setHorizontalAlignment(SwingConstants.LEFT); //
		} else {
			setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		return this;
		
	}

}
