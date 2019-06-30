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
import org.gpsmaster.gpxpanel.Waypoint;

/**
 * 
 * @author rfu
 *
 * TODO consolidate common code into superclass
 */
public class RouteProviderYOURS extends RouteProvider {

	protected final Locale requestLocale = new Locale("en", "US");
	protected List<Transport> transports = null;
	
	@Override
	public String getName() {
		return "YOURS";
	}

	@Override
	public String getDescription() {		
		return "http://www.yournavigation.org/";
	}

	@Override
	public String getAttribution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getMaxDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ConnectivityType getConnectivityType() {
		return ConnectivityType.ONLINE;
	}

	@Override
	public List<Transport> getTransport() {
		if (transports == null) {
			transports = new ArrayList<Transport>();
			transports.add(new Transport("Foot", TransportType.FOOT, "v=foot"));
			Transport b1 = new Transport("Bicycle", TransportType.BICYCLE, "v=bicycle");
			b1.setDescription("Bicycle. Use all allowed roads");
			transports.add(b1);

			Transport b2 = new Transport("Bicycle (Routes)", TransportType.BICYCLE, "v=bicycle&layer=cn");
			b2.setDescription("Bicycle. Only cycle route networks");
			// transports.add(b2); // disabled, does not provider proper routes

			transports.add(new Transport("Car", TransportType.CAR, "v=motorcar"));
		}
		return transports;
	}	

	@Override
	public void findRoute(List<Waypoint> resultRoute, double startLat, double startLon, double endLat, double endLon)
			throws Exception {
		if (transport == null) {
			throw new IllegalArgumentException("transport not set");
		}
		
		String xml = getXMLResponse(startLat, startLon, endLat, endLon);
		List<Waypoint> foundRoute = parseXML(xml);
		resultRoute.addAll(foundRoute); // TODO have parseXML append directly to resultRoute
	
	}

	/**
	 * TODO move this code to shared super class
	 * @param type
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 * @throws Exception
	 */
    public String getXMLResponse(double lat1, double lon1, double lat2, double lon2) throws Exception {
   
    	        String url = "http://www.yournavigation.org/api/1.0/gosmore.php?format=kml&" +
                String.format(requestLocale, "flat=%.6f&flon=%.6f", lat1, lon1) + "&" +
                String.format(requestLocale, "tlat=%.6f&tlon=%.6f", lat2, lon2) + "&" + transport.urlParam + "&fast=0";
        String charset = "UTF-8";
        URLConnection connection = null;
        InputStream response = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        connection = new URL(url).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("X-Yours-client", "www.gpsmaster.org");
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
        boolean inLineString = false;
        boolean inCoordinates = false;
        String coords = "";
        double lat = 0;
        double lon = 0;
        try {
            XMLStreamReader xsr = xif.createXMLStreamReader(is, "ISO-8859-1");
            while (xsr.hasNext()) {
                xsr.next();
                if (xsr.getEventType() == XMLStreamReader.START_ELEMENT && xsr.getLocalName().equals("LineString")) {
                    inLineString = true;
                }
                if (xsr.getEventType() == XMLStreamReader.END_ELEMENT && xsr.getLocalName().equals("LineString")) {
                    inLineString = false;
                }
                
                if (inLineString) {
                    if (xsr.getEventType() == XMLStreamReader.START_ELEMENT
                            && xsr.getLocalName().equals("coordinates")) {
                        inCoordinates = true;
                        xsr.next();
                        if (xsr.isCharacters()) {
                            coords += xsr.getText();
                            xsr.next();
                        }
                    }
                    if (xsr.getEventType() == XMLStreamReader.END_ELEMENT
                            && xsr.getLocalName().equals("coordinates")) {
                        inCoordinates = false;
                    }
                }
                
                if (inCoordinates) {
                    if (xsr.isCharacters()) {
                        coords += xsr.getText();
                    }
                }
            }
            String[] coordsSplit = coords.split("\n");
            for (int i = 0; i < coordsSplit.length; i++) {
                if (!coordsSplit[i].contains(",")) {
                    continue;
                }
                String[] latLon = coordsSplit[i].split(",");
                lon = Double.parseDouble(latLon[0]);
                lat = Double.parseDouble(latLon[1]);
                Waypoint wpt = new Waypoint(lat, lon);
                ret.add(wpt);
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
