package eu.fuegenstein.parameter;

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
 * Base class to pass "generic" params between classes, 
 * GUI elements and (batch) config files 
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
	
	protected Color backgroundColor = Color.WHITE;
	
	/**
	 * Empty default constructor
	 */
	public CommonParameter() {
	
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAlignment() {
		return textAlignment;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
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
	 * Set the description of this parameter
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

	
	public abstract void setValueString(String textValue);
	
	protected abstract void valueToString();
	
	/**
	 * Get the parameters value as String
	 */
	public abstract String getValueString();
	
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
					setValueString(textField.getText());
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
		panel.setBackground(backgroundColor);
		// msgPanel.setLayout(new GridLayout(0, 2));
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
