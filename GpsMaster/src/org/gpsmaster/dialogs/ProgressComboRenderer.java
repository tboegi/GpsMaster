package org.gpsmaster.dialogs;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author rfu
 *
 */
public class ProgressComboRenderer implements ListCellRenderer<ProgressComboItem>{

	@Override
	public Component getListCellRendererComponent(
			JList<? extends ProgressComboItem> arg0, ProgressComboItem item,
			int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		return item;
	}

}
