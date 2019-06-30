package org.gpsmaster;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.gpsmaster.gpxpanel.Waypoint;


/**
 * 
 * Pathfinder that uses the YourNavigation.org routing API.
 * 
 * @author Matt Hoover
 *
 */
public class PathFinderYOURS implements PathFinder {
    /* (non-Javadoc)
     * @see org.gpsmaster.PathFinder#getXMLResponse(org.gpsmaster.PathFinder.PathFindType, double, double, double, double)
     */
    @Override
    public String getXMLResponse(PathFindType type, double lat1, double lon1, double lat2, double lon2) {
        String typeParam = "";
        switch (type) {
            case FOOT:
                typeParam = "foot";
                break;
            case BIKE:
                typeParam = "bicycle";
                break;
        }
        
        String url = "http://www.yournavigation.org/api/1.0/gosmore.php?format=kml&" +
                "flat=" + String.format("%.6f", lat1) + "&flon=" + String.format("%.6f", lon1) + "&" +
                "tlat=" + String.format("%.6f", lat2) + "&tlon=" + String.format("%.6f", lon2) + "&" +
                "v=" + typeParam + "&fast=0";
        String charset = "UTF-8";
        URLConnection connection = null;
        InputStream response = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        try {
            connection = new URL(url).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("X-Yours-client", "www.gpsmaster.org");
            response = connection.getInputStream();
            br = new BufferedReader((Reader) new InputStreamReader(response, "UTF-8"));
            for(String line=br.readLine(); line!=null; line=br.readLine()) {
                builder.append(line);
                builder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /* (non-Javadoc)
     * @see org.gpsmaster.PathFinder#parseXML(java.lang.String)
     */
    @Override
    public List<Waypoint> parseXML(String xml) {
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
            System.err.println("There was a problem parsing the XML response.");
            e.printStackTrace();
        }
        ret.remove(ret.get(0)); // remove first point since the caller already has it
        return ret;
    }
}
