package eu.fuegenstein.parameter;

import java.util.Locale;

import javax.swing.JTextField;

/**
 * 
 * @author rfu
 *
 */
public class IntegerParameter extends CommonParameter {

	private int value = 0;
	
	/**
	 * Default constructor
	 * @param value initial parameter value 
	 */
	public IntegerParameter(int value) {
		super();
		this.value = value;
		format = "%d";
		textAlignment = JTextField.RIGHT;		
	}


	/**
	 * 
	 * @return current parameter value
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Set parameter value
	 * @param value new value
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * 
	 */
	@Override
	public void setValueString(String textValue) {
		value = Integer.parseInt(textValue); // NumberFormatException
	}
	
	@Override
	protected void valueToString() {
		valueString = String.format(Locale.getDefault(), format, value);

	}

	@Override
	public String getValueString() {
		
		return String.format(Locale.getDefault(), format, value);	}

}
