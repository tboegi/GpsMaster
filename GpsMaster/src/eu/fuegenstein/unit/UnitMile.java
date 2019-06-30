package eu.fuegenstein.unit;

/**
 * Unit representing a Mile.
 * 
 * provided for convenience. 
 * use {@link Unit} to construct exotic units.
 *   
 * @author rfu
 *
 */
public class UnitMile extends Unit {

	public UnitMile() {
		super("Mile", "mi", 1.0f / 1609.3440f);
		
		// setLowerUnit(UnitFactory.METER);  // MAY BE NULL at init!!
		setLowerThreshold(1.0f);
	}
}
