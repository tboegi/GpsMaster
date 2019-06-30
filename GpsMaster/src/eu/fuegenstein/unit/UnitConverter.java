package eu.fuegenstein.unit;

/**
 * Class to convert values from one unit system to another.
 * 
 * @author rfu
 *
 * TODO generalise convert methods, i.e.:
 *  dist(value) --> convert(DIST, value);
 *  
 */
public class UnitConverter {

	private UnitSet sourceSet = null;
	private UnitSet targetSet = null;
	private String symbolPrefix = "";
		
	/**
	 * 
	 * @return
	 */
	public UnitSet getSourceSet() {
		return sourceSet;
	}
	public void setSourceSet(UnitSet sourceSet) {
		this.sourceSet = sourceSet;
	}
	
	/**
	 * 
	 * @return
	 */
	public UnitSet getTargetSet() {
		return targetSet;
	}
	public void setTargetSet(UnitSet targetSet) {
		this.targetSet = targetSet;
	}
	
	public String getSymbolPrefix() {
		return symbolPrefix;
	}
	public void setSymbolPrefix(String symbolPrefix) {
		this.symbolPrefix = symbolPrefix;
	}

	/**
	 * Convert distance
	 * @param value
	 * @return
	 */
	public double dist(double value) {
		return convert(sourceSet.getDistanceUnit(), targetSet.getDistanceUnit(), value);
	}
	
	/**
	 * Convert distance to string
	 * @param value
	 * @param format
	 * @return
	 * @see setSymbolPrefix
	 */
	public String dist(double value, String format) {
		return resultString(dist(value), format, targetSet.getDistanceUnit());
	}

	/**
	 * Convert speed
	 * @param value
	 * @return
	 */
	public double speed(double value) {
		return convert(sourceSet.getSpeedUnit(), targetSet.getSpeedUnit(), value);
	}

	/**
	 * Convert speed to string
	 * @param value
	 * @param format
	 * @return
	 * @see setSymbolPrefix
	 */
	public String speed(double value, String format) {
		return resultString(speed(value), format, targetSet.getSpeedUnit());
	}

	/**
	 * Convert elevation
	 * @param value
	 * @return
	 */
	public double ele(double value) {
		return convert(sourceSet.getElevationUnit(), targetSet.getElevationUnit(), value);
	}

	/**
	 * Convert speed to string
	 * @param value
	 * @param format
	 * @return
	 * @see setSymbolPrefix
	 */
	public String ele(double value, String format) {
		return resultString(ele(value), format, targetSet.getElevationUnit());
	}

	/**
	 * Convert vertical speed
	 * @param value
	 * @return
	 */
	public double vertSpeed(double value) {
		return convert(sourceSet.getVerticalSpeedUnit(), targetSet.getVerticalSpeedUnit(), value);
	}

	/**
	 * Convert vertical speed to string
	 * @param value
	 * @param format
	 * @return
	 * @see setSymbolPrefix
	 */
	public String vertSpeed(double value, String format) {
		return resultString(vertSpeed(value), format, targetSet.getVerticalSpeedUnit());
	}

	// PRIVATE METHODS
	
	/**
	 * convert value from source unit to target unit. 
	 * @param sourceUnit
	 * @param targetUnit
	 * @param value
	 * @return
	 */
	public double convert(Unit sourceUnit, Unit targetUnit, double value) {
		if (sourceUnit.equals(targetUnit)) {
			return value;
		}
		return 1.0f / sourceUnit.getFromStd() * value * targetUnit.getFromStd();
	}
	/**
	 * create a string containing the formatted value and the unit symbol.
	 * @param value 
	 * @param format {@link String.format} for value
	 * @param unit 
	 * @return
	 */
	private String resultString(double value, String format, Unit unit) {
		return String.format(format + getSymbolPrefix() + unit.getSymbol(), value);
	}
}
