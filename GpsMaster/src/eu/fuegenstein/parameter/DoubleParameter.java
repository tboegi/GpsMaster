package eu.fuegenstein.parameter;

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
		textAlignment = JTextField.RIGHT;
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
	public void setValueString(String textValue) {
		value = Double.parseDouble(textValue);
	}

	@Override
	protected void valueToString() {
		valueString = String.format(Locale.getDefault(), format, value);		
	}


	@Override
	public String getValueString() {
		
		return String.format(Locale.getDefault(), format, value);
	}

	
}
