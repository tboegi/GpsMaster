package eu.fuegenstein.util;

/**
 * Class representing information about a progress, 
 * i.e. for use in a progress bar
 * 
 * @author rfu
 *
 */
public class ProgressInfo {

	private String name = null;
	
	private int minValue = 0;
	private int maxValue = 0;
	private int value = 0;  // current value
	
	/**
	 * Constructor
	 */
	public ProgressInfo() {
		
	}
	
	/**
	 * Constructor
	 * @param min
	 * @param max
	 */
	public ProgressInfo(int min, int max) {
		setMinValue(min);
		setMaxValue(max);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the maxValue
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Increment the current value by one
	 */
	public void incrementValue() {
		this.value++;
	}
	
	/**
	 * @return the minValue
	 */
	public int getMinValue() {
		return minValue;
	}

	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
}
