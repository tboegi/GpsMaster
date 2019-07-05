package org.gpsmaster.widget;

import javax.swing.BorderFactory;
import javax.swing.JTable;

import eu.fuegenstein.swing.Widget;
import eu.fuegenstein.swing.WidgetLayout;

/**
 *
 * @author rfu
 *
 */
public class DistanceWidget extends Widget {

	/**
	 *
	 */
	private static final long serialVersionUID = -7099367537255790218L;

	private JTable distanceTable = new JTable();

	public DistanceWidget() {
		super(WidgetLayout.TOP_LEFT);

		setBackground(transparentWhite);
		setBorder(BorderFactory.createLineBorder(transparentWhite, 5));
		add(distanceTable);
	}

}
