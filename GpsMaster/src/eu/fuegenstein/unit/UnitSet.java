package eu.fuegenstein.unit;

/**
 * 
 * inspired by GpsPrune
 * @author rfu
 *
 */
public class UnitSet {
	
	protected String name = "";
	protected Unit distanceUnit = null;
	protected Unit speedUnit = null;
	protected Unit elevationUnit = null;
	protected Unit vertSpeedUnit = null;
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** 
	 * 
	 */
	public Unit getDistanceUnit() {
		return distanceUnit;
	}
	
	/***
	 * 
	 * @param unit
	 */
	public void setDistanceUnit (Unit unit) {
		distanceUnit = unit;
	}
	
	/** 
	 * 
	 */
	public Unit getSpeedUnit() {
		return speedUnit;
	}
	
	
	/***
	 * 
	 * @param unit
	 */
	public void setSpeedUnit (Unit unit) {
		speedUnit = unit;
	}

	public Unit getElevationUnit() {
		return elevationUnit;
	}

	public void setElevationUnit(Unit elevationUnit) {
		this.elevationUnit = elevationUnit;
	}

	public Unit getVerticalSpeedUnit() {
		return vertSpeedUnit;
	}

	public void setVerticalSpeedUnit(Unit vertSpeedUnit) {
		this.vertSpeedUnit = vertSpeedUnit;
	}
}
