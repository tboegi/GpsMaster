package eu.fuegenstein.util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;

import javax.swing.JTextField;

/**
 * 
 * @author rfu
 *
 */
public class DoubleParameter extends CommonParameter {

	private double value = 0.0f;

	public DoubleParameter(double value) {
		super();
		this.value = value;
		format = "%.2f";
	}
	
		
	/**
	 * Get parameter value
	 * @return
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Set parameter value
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * 
	 */
	public void setValue(String textValue) {
		value = Double.parseDouble(textValue);
	}

	@Override
	protected void valueToString() {
		valueString = String.format(Locale.getDefault(), format, value);		
	}

	/**
	 * listener for JTextFields checking the validity of double values
	 */
	@Override
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
	

	
}
