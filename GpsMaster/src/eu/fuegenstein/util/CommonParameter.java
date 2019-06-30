package eu.fuegenstein.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Base class to pass "generic" parameters between classes and GUIs
 * 
 * @author rfu
 *
 *	TODO re-implement this with generics
 */
public abstract class CommonParameter {

	protected String name = "";
	protected String description = "";
	protected String format = "";
	protected String valueString = "";
	
	protected FocusListener textfieldListener = null;
	
	/**
	 * Empty default constructor
	 */
	public CommonParameter() {
	
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Short description, 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description which is used as label in the GUI Component
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	public abstract void setValue(String value);
	protected abstract void valueToString();
	protected abstract void makeListener();
	
	/**
	 * 
	 */
	public JPanel getGuiComponent() {
		return getGuiComponent(null);
	}
	
	/**
	 * 
	 * @return
	 */
	public JPanel getGuiComponent(Dimension dimension) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new GridLayout(0, 2));
		JLabel label = new JLabel(description);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label);
		JTextField textField = new JTextField();
		if (dimension != null) {
			textField.setPreferredSize(dimension);
		}
		valueToString();
		textField.setText(valueString);
		if (textfieldListener == null) {
			makeListener();
			textField.addFocusListener(textfieldListener);
		}
		panel.add(textField);						
		return panel;
	}
}
