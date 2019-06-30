package eu.fuegenstein.unit;

/**
 * Unit representing a Foot.
 * 
 * provided for convenience. 
 * use {@link Unit} to construct exotic units.
 *   
 * @author rfu
 *
 */
public class UnitFoot extends Unit {

	public UnitFoot() {
		super("Feet", "ft", 3.28084f);
		
		// upperUnit = UnitFactory.MILE;
		upperThreshold = 1760.0f * 3.0f;
	}
		
}
