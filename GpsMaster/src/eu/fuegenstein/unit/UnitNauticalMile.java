package eu.fuegenstein.unit;

/**
 * Unit representing a Nautical Mile.
 * 
 * provided for convenience. 
 * use {@link Unit} to construct exotic units.
 *   
 * @author rfu
 *
 */
public class UnitNauticalMile extends Unit {

	public UnitNauticalMile() {
		super("Nautical Mile", "nmi", 1.0f / 1852.0f);
		
		// setLowerUnit(UnitFactory.METER);  // MAY BE NULL at init!!
		setLowerThreshold(1.0f);
	}
}
