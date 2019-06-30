package org.gpsmaster;

import eu.fuegenstein.unit.UnitSystem;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * small class for unit conversion.  
 * main purpose is to keep method calls short. input system
 * is always METRIC, input unit is always meters
 * 
 * @author rfu
 *
 */
public class UnitConverter {

	private UnitSystem inSystem = UnitSystem.METRIC;
	private UnitSystem outSystem = UnitSystem.METRIC;
	
	/**
	 * possible input units
	 * @author rfuegen
	 *
	 */
	public enum UNIT {
		// distance
		M,	// meter
		KM,	// kilometer
		// speed
		KMPH,	// kilometer per hour
		MHR	// meter per hour		
	}
	
	public UnitConverter() {
		inSystem = UnitSystem.METRIC;
	}
	
	/**
	 * supported systems of measurements
	 * @author rfuegen
	 *
	 */

	public void setInputSystem(UnitSystem system) {
		throw new NotImplementedException();
		// inSystem = system;
	}
	
	public UnitSystem getInputSystem() {
		return inSystem;
	}
	
	public void setOutputSystem(UnitSystem system) {
		outSystem = system;
	}

	public UnitSystem getOutputSystem() {
		return outSystem;
	}
	

	/**
	 * 
	 * @return
	 */
	public String getUnit(UNIT inUnit) {
		String outUnit = "";
		switch (outSystem) {  // input system is always METRIC (for now)
		case METRIC:
			switch(inUnit) {
			case M:
				outUnit = "m";
				break;
			case KM:
				outUnit = "km";
				break;
			case MHR:
				outUnit = "m/hr";
				break;
			case KMPH:
				outUnit = "km/h";
				break;
			default:
				throw new NotImplementedException();
			}
			break;
		case IMPERIAL:
			switch(inUnit) {
			case M:
				outUnit = "f";
				break;
			case KM:
				outUnit = "mi";
				break;
			case MHR:
				outUnit = "f/hr";
				break;
			case KMPH:
				outUnit = "mph";
				break;
			default:
				throw new NotImplementedException();
			}
			break;		
		case NAUTICAL:
			switch(inUnit) {
			case M:
				outUnit = "f";
				break;
			case KM:
				outUnit = "M";
				break;
			case MHR:
				outUnit = "f/hr";
				break;
			case KMPH:
				outUnit = "knots";
				break;
			default:
				throw new NotImplementedException();
			}
		default:
			throw new NotImplementedException();
		}

		return outUnit;
	}
	
	
	/**
	 * converts the given distance value (always in meters)
	 * to an equivalent of the given unit  
	 * @param inValue
	 * @param inUnit
	 * @return
	 */
	public double dist(double inValue, UNIT inUnit) {
		double outValue = 0;
		switch (outSystem) {  // input system is always METRIC (for now)
		case METRIC:
			switch(inUnit) {
			case M:
				outValue = inValue;
				break;
			case KM:
				outValue = inValue / 1000;
				break;
			default:
				throw new NotImplementedException();
			}
			break;
		case IMPERIAL:
			switch(inUnit) {
			case M:
				outValue = inValue * 3.28084;
				break;
			case KM:
				outValue = inValue * 0.000621371;
				break;
			default:
				throw new NotImplementedException();
			}
			break;		
		case NAUTICAL:
			switch(inUnit) {
			case M:
				outValue = inValue * 3.28084;
				break;
			case KM:
				outValue = inValue * 0.00054;
				break;
			default:
				throw new NotImplementedException();
			}
		default:
			throw new NotImplementedException();
		}
		
		return outValue;
	}

	/**
	 * converts the given distance value (always in meters/hour)
	 * to an equivalent of the given unit  
	 * 
	 * @param inValue
	 * @param inUnit
	 * @return
	 */
	public double speed(double inValue, UNIT inUnit) {
		double outValue = 0;
		switch (outSystem) {  // input system is always METRIC (for now)
		case METRIC:
			switch(inUnit) {
			case MHR:
				outValue = inValue;
				break;
			case KMPH:
				outValue = inValue;
				break;
			default:
				throw new NotImplementedException();
			}
			break;
		case IMPERIAL:
			switch(inUnit) {
			case MHR:
				outValue = inValue * 3.28084;
				break;
			case KMPH:
				outValue = inValue * 0.621371;
				break;
			default:
				throw new NotImplementedException();
			}
			break;		
		case NAUTICAL:
			switch(inUnit) {
			case MHR:
				outValue = inValue * 3.28084;
				break;
			case KMPH:
				outValue = inValue * 0.54;
				break;
			default:
				throw new NotImplementedException();
			}
		default:
			throw new NotImplementedException();
		}
		
		return outValue;
	}
	
}
