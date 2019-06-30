package org.gpsmaster.pathfinder;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author rfu
 *
 */
public class RouteProviderFactory {

	/**
	 * TODO create list of providers dynamically via "reflection"
	 * 
	 * @return List of available RouteProviders 
	 */
	public static List<RouteProvider> getAllProviders() {
		
		List<RouteProvider> providerList = new ArrayList<RouteProvider>();
		
		providerList.add(new RouteProviderMapQuest());
		providerList.add(new RouteProviderYOURS());
		providerList.add(new RouteProviderGraphHopper());
		
		return providerList;
	}
}
