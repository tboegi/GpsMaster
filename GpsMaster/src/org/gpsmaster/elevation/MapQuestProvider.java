package org.gpsmaster.elevation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * 
 * @author rfu
 * @author Matt Hoover
 *
 */
public class MapQuestProvider implements ElevationProvider {

	private String baseUrl = "http://open.mapquestapi.com/elevation/v1/profile?";
	private final String NAME = "MapQuest";
	private final String ATTRIBUTION = "Elevation Data provided by MapQuest"; // TODO check site
	private int CHUNKSIZE = 400;
	private Locale fmtLocale = new Locale("en", "US");
	
	private int cleanseFailed = 0;
	
	boolean interpolate = true;

	@Override
	public String getName() {
		
		return NAME;
	}

	@Override
	public String getAttribution() {
		
		return  ATTRIBUTION;
	}

	@Override
	public void setInterpolation(boolean interpolate) {
		this.interpolate = interpolate;
		
	}

	@Override
	public boolean isInterpolation() {
		
		return interpolate;
	}

	@Override
	public int getChunkSize() {

		return CHUNKSIZE;
	}

	@Override
	public int getFailed() {
		return cleanseFailed;
	}
	
	@Override
	public void correctElevation(Waypoint waypoint) {
		
		throw new UnsupportedOperationException();
		
	}
	
	@Override
	public void correctElevation(List<Waypoint> waypoints) throws Exception {
		
		correct(waypoints);
		// cleanse(waypoints);
	}
	
	private void correct(List<Waypoint> waypoints) throws Exception {
	
		if (waypoints.size() > CHUNKSIZE) {
			throw new IllegalArgumentException("chunk size exceeded");
		}
		
		String latLngCollection = "";
		for (Waypoint wpt : waypoints) {			
	        latLngCollection += String.format(fmtLocale, "%.6f,%.6f,", wpt.getLat(), wpt.getLon());					
		}
		latLngCollection = latLngCollection.substring(0, latLngCollection.length()-1);

		// make request
        
        final String charset = "UTF-8";
        final String param1 = "kvp"; // inFormat
        final String param2 = latLngCollection;
        final String param3 = "xml"; // outFormat
        final String param4 = "true"; // useFilter
        String query = null;
        URLConnection connection = null;
        InputStream response = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        
        query = "key=" + Const.MAPQUEST_API_KEY + 
                String.format("&inFormat=%s" + "&latLngCollection=%s" + "&outFormat=%s" + "&useFilter=%s",
                URLEncoder.encode(param1, charset),
                URLEncoder.encode(param2, charset),
                URLEncoder.encode(param3, charset),
                URLEncoder.encode(param4, charset));
        String url = baseUrl + query;
        
        connection = new URL(url).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty(
                "Content-Type", "application/x-www-form-urlencoded;charset=" + charset);           
        response = connection.getInputStream();
        br = new BufferedReader((Reader) new InputStreamReader(response, "UTF-8"));
        for(String line=br.readLine(); line!=null; line=br.readLine()) {            	
            builder.append(line);
            builder.append('\n');
        }
        
		// process response
        // TODO check for error in response
        String responseStr = builder.toString();
        if (responseStr.contains("Given Route exceeds the maximum allowed distance")) { // ?!?!?!
        	// should not happen since we process in chunks
        	throw new IllegalArgumentException("Given Route exceeds the maximum allowed distance");
        }

        
        List<Double> eleList = getEleArrayFromXMLResponse(responseStr);
        if (eleList.size() != waypoints.size()) {
        	throw new IllegalArgumentException("Result size mismatch");
        }

        for (int i = 0; i < eleList.size(); i++) {
        	waypoints.get(i).setEle(eleList.get(i));
        }
	
	}

	
    /**
     * Cleanse the elevation data.  Any {@link Waypoint} with an elevation of -32768 needs to be interpolated.
     * 
     * @return  The status of the cleanse.
     */
    private void cleanse(WaypointGroup wptGrp) {
  		
    	List<Waypoint> waypoints = wptGrp.getWaypoints();
    	double eleStart = wptGrp.getStart().getEle();
        double eleEnd = wptGrp.getEnd().getEle();

        if (eleStart == -32768) {
            for (int i = 0; i < waypoints.size(); i++) {
                if (waypoints.get(i).getEle() != -32768) {
                    eleStart = waypoints.get(i).getEle();
                    break;
                }
            }
        }
        
        if (eleEnd == -32768) {
            for (int i = waypoints.size() - 1; i >= 0; i--) {
                if (waypoints.get(i).getEle() != -32768) {
                    eleEnd = waypoints.get(i).getEle();
                    break;
                }
            }
        }
        
        if (eleStart == -32768 && eleEnd == -32768) {
        	// hopeless! (impossible to correct)
        	cleanseFailed++;
            return;
        }
        
        waypoints.get(0).setEle(eleStart);
        waypoints.get(waypoints.size() - 1).setEle(eleEnd);
        
        for (int i = 0; i < waypoints.size(); i++) {
            if (waypoints.get(i).getEle() == -32768) {
                Waypoint neighborBefore = null;
                Waypoint neighborAfter = null;
                double distBefore = 0;
                double distAfter = 0;
                
                Waypoint curr = waypoints.get(i);
                Waypoint prev = waypoints.get(i);
                for (int j = i - 1; j >= 0; j--) {
                    prev = curr;
                    curr = waypoints.get(j);
                    distBefore += curr.getDistance(prev);
                    if (waypoints.get(j).getEle() != -32768) {
                        neighborBefore = waypoints.get(j);
                        break;
                    }
                }
    
                curr = waypoints.get(i);
                prev = waypoints.get(i);
                for (int j = i + 1; j < waypoints.size(); j++) {
                    prev = curr;
                    curr = waypoints.get(j);
                    distAfter += curr.getDistance(prev); 
                    if (waypoints.get(j).getEle() != -32768) {
                        neighborAfter = waypoints.get(j);
                        break;
                    }
                }
                
                if ((neighborBefore != null) && (neighborAfter != null)) {
	                double distDiff = distBefore + distAfter;
	                double eleDiff = neighborAfter.getEle() - neighborBefore.getEle();
	                double eleCleansed = ((distBefore / distDiff) * eleDiff) + neighborBefore.getEle();
	                waypoints.get(i).setEle(eleCleansed);
                }
            }
        }
    }

    /**
     * Parses an XML response string.
     * 
     * @return  A list of numerical elevation values.
     * @throws XMLStreamException 
     */
    private /* static */ List<Double> getEleArrayFromXMLResponse(String xmlResponse) throws XMLStreamException {
        List<Double> ret = new ArrayList<Double>();
        InputStream is = new ByteArrayInputStream(xmlResponse.getBytes());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = xif.createXMLStreamReader(is, "ISO-8859-1");
        while (xsr.hasNext()) {
            xsr.next();
            if (xsr.getEventType() == XMLStreamReader.START_ELEMENT) {
                if (xsr.getLocalName().equals("height")) {
                    xsr.next();
                    if (xsr.isCharacters()) {
                        ret.add(Double.parseDouble(xsr.getText()));
                    }
                }
            }
        }
        xsr.close();
        return ret;
    }

}
