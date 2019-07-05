package org.gpsmaster.dialogs;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eu.fuegenstein.unit.Unit;

/**
 *
 * @author rfu
 *
 */
public class ProgressComboItem extends JLabel {

	/**
	 *
	 */
	private static final long serialVersionUID = 5933310464499417852L;
	protected JComboBox<Unit> unitCombo = new JComboBox<Unit>();
	protected JTextField valueField = new JTextField();
	protected Unit unit = null;
	protected ImageIcon icon = null;

	/**
	 *
	 */
	public ProgressComboItem() {
		setLayout(new BorderLayout());
		add(valueField, BorderLayout.CENTER);
		add(unitCombo, BorderLayout.EAST);
	}

	/**
	 *
	 * @param unit
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;

		Unit u = unit.getUpperUnit();
		while (u != null) {
			unitCombo.addItem(u);
			u = u.getUpperUnit();
		}
		unitCombo.addItem(unit);

		u = unit.getLowerUnit();
		while (u != null) {
			unitCombo.addItem(u);
			u = u.getLowerUnit();
		}

	}

	/**
	 *
	 * @return
	 */
	public Unit getUnit() {
		return unit;
	}
}
