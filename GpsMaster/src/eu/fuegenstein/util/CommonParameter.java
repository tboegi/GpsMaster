package eu.fuegenstein.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
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
	protected int textAlignment = JTextField.LEFT; // textField content: left or right justified
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

	public abstract void setValue(String textValue);
	protected abstract void valueToString();

	/**
	 * propertyListener for JTextField checking validity
	 * of textual value entered by user.
	 *
	 */
	protected void makeListener() {
		textfieldListener = new FocusListener() {

			String previous = "";

			@Override
			public void focusGained(FocusEvent arg0) {
				previous = ((JTextField) arg0.getSource()).getText();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				JTextField textField = (JTextField) arg0.getSource();
				try {
					setValue(textField.getText());
				} catch (NumberFormatException e) {
					textField.setText(previous);
				}
			}
		};
	}


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
		// panel.setLayout(new GridLayout(0, 2));
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JLabel label = new JLabel(description);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label);
		JTextField textField = new JTextField();
		textField.setHorizontalAlignment(textAlignment);
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
