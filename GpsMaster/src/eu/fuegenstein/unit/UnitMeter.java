package eu.fuegenstein.unit;

/**
 * Unit representing a Meter.
 * 
 * provided for convenience. 
 * use {@link Unit} to construct exotic units.
 *   
 * @author rfu
 *
 */
public class UnitMeter extends Unit {

	public UnitMeter() {
		super("Meter", "m", 1.0f);
		
		// setUpperUnit(UnitFactory.KILOMETER); // MAY BE NULL at init!!
		setUpperThreshold(1000.0f);
	}
		
}
