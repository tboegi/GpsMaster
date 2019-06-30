package eu.fuegenstein.unit;


/**
 * Class representing a single unit of measure.
 * The standard units used internally throughout 
 * this package are (based on) SI units:
 * 
 * Distance:	Meter
 * Speed: 		Meters per Second
 *
 * inspired by GpsPrune
 * @author rfu
 *
 * TODO support default format string, i.e. "%.2f"
 */
public class Unit {

	protected String name = "";
	protected String symbol = "";
	protected boolean autoScale = false;
	
	// multiplication factor from standard unit
	protected double fromStd = 1f;

	protected Unit upperUnit = null;
	protected double upperThreshold = 0f; // TODO can be determined from stdFactors?

	protected Unit lowerUnit = null;
	protected double lowerThreshold = 0f; // TODO can be determined from stdFactors?
	
	/**
	 * Dummy Constructor
	 */
	public Unit() {
		
	}
	
	/**
	 * Constructor with a minimum set of params required for a unit
	 * @param name descriptive name of this unit
	 * @param symbol standardised unit symbol
	 * @param fromStd multiplication factor from standard unit
	 */
	public Unit(String name, String symbol, double fromStd) {
		this.name = name;
		this.symbol = symbol;
		this.fromStd = fromStd;
	}
	
	/**
	 * 
	 * @param name
	 * @param symbol
	 * @param fromStd
	 * @param parent
	 */
	public Unit(String name, String symbol, double fromStd, Unit parent) {
		this(name, symbol, fromStd * parent.getFromStd());
	}
	
	/**
	 * descriptive name of this unit
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * symbol of this unit
	 * @return
	 */
	public String getSymbol() {
		return symbol;
	}
	
	/**
	 * get multiplication factor to standard unit 
	 * @return
	 */
	public double getFromStd() {
		return fromStd;
	}
	
	/**
	 * get the next higher Unit
	 * @return Unit or NULL
	 */
	public Unit getUpperUnit() {
		return upperUnit;
	}

	public void setUpperUnit(Unit upperUnit) {
		this.upperUnit = upperUnit;
	}

	public double getUpperThreshold() {
		return upperThreshold;
	}

	public void setUpperThreshold(double upperThreshold) {
		this.upperThreshold = upperThreshold;
	}

	public Unit getLowerUnit() {
		return lowerUnit;
	}

	public void setLowerUnit(Unit lowerUnit) {
		this.lowerUnit = lowerUnit;
	}

	public double getLowerThreshold() {
		return lowerThreshold;
	}

	public void setLowerThreshold(double lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
	}
	
	/**
	 * determine the next (upper or lower) {@link Unit) that contains {@link value}
	 * within its upper and lower thresholds.
	 * i.e. for {@link UnitMeter}, a value of 1200m returns {@link UnitKilometer}
	 *    
	 * @param value
	 * @return next matching {@link Unit} or {@link this}, never {@link null} 
	 * TODO make recursive
	 * 
	 */	
	public Unit getNextUnit(double value) {
		if ((upperUnit != null) && (value >= upperThreshold)) {
			return upperUnit;
		}
		if ((lowerUnit != null) && (value < lowerThreshold)) {
			return lowerUnit;
		}		
		return this;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return symbol;
	}
}
