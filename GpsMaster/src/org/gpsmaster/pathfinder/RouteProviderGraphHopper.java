package org.gpsmaster.pathfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.gpsmaster.ConnectivityType;
import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpsloader.GpsLoader;
import org.gpsmaster.gpsloader.GpsLoaderFactory;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;

/**
 * https://graphhopper.com/api/1/docs/routing/
 * 
 * @author rfu
 *
 */
public class RouteProviderGraphHopper extends RouteProvider {

	protected final Locale requestLocale = new Locale("en", "US");	
	protected List<Transport> routeTypes = null;
	
	@Override
	public String getName() {
		return "GraphHopper";
	}

	@Override
	public String getDescription() {
		return "https://graphhopper.com/";
	}

	@Override
	public String getAttribution() {
		return "Powered by <a href=\"https://graphhopper.com/#directions-api\">GraphHopper API</a>";
	}

	@Override
	public long getMaxDistance() {
		return 400; // TODO check
	}

	@Override
	public List<Transport> getTransport() {
		if (routeTypes == null) {
			routeTypes = new ArrayList<Transport>();
			routeTypes.add(new Transport("Foot", TransportType.FOOT, "vehicle=foot"));
			routeTypes.add(new Transport("Bicycle", TransportType.BICYCLE, "vehicle=bike"));
			routeTypes.add(new Transport("Car", TransportType.FOOT, "vehicle=car"));
			routeTypes.add(new Transport("Truck", TransportType.FOOT, "vehicle=truck"));
		}
		return routeTypes;
	}

	@Override
	public void findRoute(List<Waypoint> resultRoute, double lat1, double lon1, double lat2, double lon2)
			throws Exception {

		String url = "https://graphhopper.com/api/1/route?key=" + Const.GRAPHHOPPER_API_KEY + "&" + 
        "point=" + String.format(requestLocale, "%.6f,%.6f", lat1, lon1) + "&" +
        "point=" + String.format(requestLocale, "%.6f,%.6f", lat2, lon2) + "&" +
        transport.urlParam + "&instructions=false&calc_points=true&type=gpx";
		
        String charset = "UTF-8";
        URLConnection connection = null;
        InputStream response = null;

        connection = new URL(url).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        // connection.setRequestProperty("X-Yours-client", "www.gpsmaster.org");
        GpsLoader loader = GpsLoaderFactory.getLoader("gpx");
        response = connection.getInputStream();
		GPXFile gpx = loader.load(response);
		response.close();
		
		// we assume that the first track of the returned GPXFile contains the resulting route
		// do some checks
		if (gpx.getTracks().size() != 1) {
			throw new IllegalArgumentException("Invalid number of resulting tracks");
		}
		Track track = gpx.getTracks().get(0);
		if (track.getTracksegs().size() != 1) {
			throw new IllegalArgumentException("Invalid number of track segments");
		}		
		resultRoute.addAll(track.getTracksegs().get(0).getWaypoints());
		
	}

	@Override
	public ConnectivityType getConnectivityType() {
		
		return ConnectivityType.ONLINE;
	}

	
}
