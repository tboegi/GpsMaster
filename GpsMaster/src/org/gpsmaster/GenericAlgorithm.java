package org.gpsmaster;

import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.WaypointGroup;

import eu.fuegenstein.parameter.CommonParameter;

/**
 * Base Class defining an algorithm to be used either in the user 
 * interface, via batch processing or other means. Parameters are
 * passed via {@link CommonParameter}s.  
 * 
 * An algorithm works on a set of {@link WaypointGroup}s.
 * 
 * Actual functionality to be implemented by child classes
 * 
 * @author rfu
 *
 */
public abstract class GenericAlgorithm {

	protected List<CommonParameter> params = new ArrayList<CommonParameter>();
	protected List<WaypointGroup> waypointGroups = new ArrayList<WaypointGroup>();
	
	protected String name = "";
	protected String description = "";
	
	
	/**
	 * Set the name of thios algorithm.
	 * for subclasses only.
	 * @param name
	 */
	protected void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the description of this algorithm.
	 * for subclasses only.
	 * @param name
	 */
	
	protected void setDescription(String desc) {
		description = desc;;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<CommonParameter> getParameters() {
		return params;
	}

	/**
	 * 
	 * @param group
	 */
	public void addWaypointGroup(WaypointGroup group) {
		waypointGroups.add(group);
	}
	
	/**
	 * 
	 * @param groups
	 */
	public void setWaypointGroups(List<WaypointGroup> groups) {
		waypointGroups.clear();
		waypointGroups.addAll(groups);
	}
	
	
	/**
	 * 
	 * @param groups
	 */
	public void addWaypointGroups(List<WaypointGroup> groups) {
		waypointGroups.addAll(groups);
	}
	
	/**
	 * Determine if this algorithm is applicable on the current set 
	 * of {@link WaypointGroup}s resp. active {@link GPXObject}s
	 * 
	 * default behaviour: applicable if at least one {@link WaypointGroup} is set
	 * Override if necessary
	 * 
	 * @return true if applicable, false otherwise
	 */
	public boolean isApplicable() {
		
		return (waypointGroups.size() > 0);
	}
	
	/**
	 * apply the algorithm
	 */
	public abstract void apply();
	
	/**
	 * 
	 */
	// public abstract void undo();
	
	/**
	 * 
	 */
	
	public void clear() {
		
		waypointGroups.clear();
	}

}
