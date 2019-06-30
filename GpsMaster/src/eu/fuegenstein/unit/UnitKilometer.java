package eu.fuegenstein.unit;

/**
 * Unit representing a Kilometer.
 * 
 * provided for convenience. 
 * use {@link Unit} to construct exotic units.
 *   
 * @author rfu
 *
 */
public class UnitKilometer extends Unit {

	public UnitKilometer() {
		super("Kilometer", "km", 0.001f);
		
		// setLowerUnit(UnitFactory.METER);  // MAY BE NULL at init!!
		setLowerThreshold(1.0f);
	}
}
