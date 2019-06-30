package eu.fuegenstein.unit;

/**
 * 
 * Inspired by GpsPrune
 * @author rfu
 *
 */
public class UnitFactory {
	// BUG when using upperUnit/lowerUnit, order matters!!
	//     (circular references!!)

	// metric	
	public static final Unit METER = new UnitMeter();
	public static final Unit KILOMETER = new UnitKilometer();		
	public static final Unit METERS_PER_SECOND = new Unit("Meters per second", "m/s", 1.0f, UnitFactory.METER);	
	public static final Unit KILOMETERS_PER_HOUR = new Unit("Kilometers per hour", "km/h", 60.0f * 60.0f, KILOMETER);
	public static final Unit METERS_PER_HOUR = new Unit("Meters per hour", "m/hr", 60.0f * 60.0f, METER);
	
	// imperial
	public static final Unit FOOT = new UnitFoot();
	public static final Unit MILE = new UnitMile();
	public static final Unit MILES_PER_HOUR = new Unit("Miles per hour", "mph", 60.0f * 60.0f, MILE);
	public static final Unit FEET_PER_HOUR = new Unit("Feet per hour", "ft/hr", 60.0f * 60.0f, FOOT);
	
	// nautical
	public static final Unit NAUTICAL_MILE = new UnitNauticalMile();
	public static final Unit KNOTS = new Unit("Knots", "kn", 60.0f * 60.0f, NAUTICAL_MILE);
	
	// sets
	public static UnitSet getMetricSet() {
		final UnitSet metricSet = new UnitSet();
		metricSet.setDistanceUnit(METER);
		metricSet.setSpeedUnit(METERS_PER_SECOND);
		metricSet.setElevationUnit(METER);
		metricSet.setVerticalSpeedUnit(METERS_PER_SECOND);
		return metricSet;
	}
}
