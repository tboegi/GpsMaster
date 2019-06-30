package org.gpsmaster.pathfinder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.gpsmaster.ConnectivityType;
import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.Waypoint;


/**
 * 
 * @author rfu
 * contains code by Matt Hoover
 * 
 * TODO consolidate common code into superclass
 *
 */
public class RouteProviderMapQuest extends RouteProvider {

	protected final Locale requestLocale = new Locale("en", "US");	
	protected List<Transport> routeTypes = null;
	
	@Override
	public String getName() {		
		return "MapQuest";
	}

	@Override
	public String getDescription() {

		return "http://www.mapquest.com/";
	}

	@Override
	public String getAttribution() {
		
		return "<attribution mapquest>";
	}

	@Override
	public long getMaxDistance() {
		
		return 10000;
	}

	@Override
	public ConnectivityType getConnectivityType() {
		
		return ConnectivityType.ONLINE;
	}

	@Override
	public List<Transport> getTransport() {
		if (routeTypes == null) {
			routeTypes = new ArrayList<Transport>();
			routeTypes.add(new Transport("Foot", TransportType.FOOT, "routeType=pedestrian"));
			routeTypes.add(new Transport("Bicycle", TransportType.BICYCLE, "routeType=bicycle&CyclingRoadFactor=10.0")); // does not work
			routeTypes.add(new Transport("Car (fastest)", TransportType.CAR, "routeType=fastest"));
			routeTypes.add(new Transport("Car (shortest)", TransportType.CAR, "routeType=shortest"));
		}
		return routeTypes;
	}

	@Override
	public void findRoute(List<Waypoint> resultRoute, double startLat, double startLon, double endLat, double endLon) throws Exception {
		if (transport == null) {
			throw new IllegalArgumentException("transport not set");
		}
		
		String xml = getXMLResponse(startLat, startLon, endLat, endLon);
		List<Waypoint> foundRoute = parseXML(xml);
		resultRoute.addAll(foundRoute); // TODO have parseXML append directly to resultRoute
	}

    /* (non-Javadoc)
     * @see org.gpsmaster.PathProvider#getXMLResponse(org.gpsmaster.PathProvider.PathFindType, double, double, double, double)
     */
    protected String getXMLResponse(double lat1, double lon1, double lat2, double lon2) throws Exception {
    	
        String url = "http://open.mapquestapi.com/directions/v1/route?key="+Const.MAPQUEST_API_KEY+"&narrative=none&" +
                "outFormat=xml&" + transport.urlParam + "&shapeFormat=raw&generalize=0&locale=en_US&unit=m&" +
                "from=" + String.format(requestLocale, "%.6f,%.6f", lat1, lon1) + "&" +
                "to="   + String.format(requestLocale, "%.6f,%.6f", lat2, lon2);
        String charset = "UTF-8";
        URLConnection connection = null;
        InputStream response = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        connection = new URL(url).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        response = connection.getInputStream();
        br = new BufferedReader((Reader) new InputStreamReader(response, charset));
        for(String line=br.readLine(); line!=null; line=br.readLine()) {
            builder.append(line);
            builder.append('\n');
        }
        br.close();
        return builder.toString();
    }

    /* (non-Javadoc)
     * @see org.gpsmaster.PathProvider#parseXML(java.lang.String)
     */    
    protected List<Waypoint> parseXML(String xml) throws Exception {
        List<Waypoint> ret = new ArrayList<Waypoint>();
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        boolean inShapePoints = false;
        boolean inLatLng = false;
        double lat = 0;
        double lon = 0;
        try {
            XMLStreamReader xsr = xif.createXMLStreamReader(is, "ISO-8859-1");
            while (xsr.hasNext()) {
                xsr.next();
                if (xsr.getEventType() == XMLStreamReader.START_ELEMENT && xsr.getLocalName().equals("shapePoints")) {
                    inShapePoints = true;
                }
                if (xsr.getEventType() == XMLStreamReader.END_ELEMENT && xsr.getLocalName().equals("shapePoints")) {
                    inShapePoints = false;
                }
                
                if (inShapePoints) {
                    if (xsr.getEventType() == XMLStreamReader.START_ELEMENT && xsr.getLocalName().equals("latLng")) {
                        inLatLng = true;
                    }
                    if (xsr.getEventType() == XMLStreamReader.END_ELEMENT && xsr.getLocalName().equals("latLng")) {
                        inLatLng = false;
                        Waypoint wpt = new Waypoint(lat, lon);
                        ret.add(wpt);
                    }
                }
                
                if (inLatLng) {
                    if (xsr.getEventType() == XMLStreamReader.START_ELEMENT && xsr.getLocalName().equals("lat")) {
                        xsr.next();
                        if (xsr.isCharacters()) {
                            lat = Double.parseDouble(xsr.getText());
                        }
                    }
                    if (xsr.getEventType() == XMLStreamReader.START_ELEMENT && xsr.getLocalName().equals("lng")) {
                        xsr.next();
                        if (xsr.isCharacters()) {
                            lon = Double.parseDouble(xsr.getText());
                        }
                    }
                }
            }
            xsr.close();
        }  catch (Exception e) {
            throw new Exception("There was a problem parsing the XML response.");
        }
        if (ret.size() > 0) {
        	ret.remove(ret.get(0)); // remove first point since the caller already has it
        }
        return ret;
    }

}
