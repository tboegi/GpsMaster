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
 * Pathfinder that uses the MapQuest Open Directions API.
 *
 * @author Matt Hoover
 *
 */
public class PathFinderMapQuest implements PathFinder {
    /* (non-Javadoc)
     * @see org.gpsmaster.PathFinder#getXMLResponse(org.gpsmaster.PathFinder.PathFindType, double, double, double, double)
     */
    @Override
    public String getXMLResponse(PathFindType type, double lat1, double lon1, double lat2, double lon2) {
        String typeParam = "";
        switch (type) {
            case FOOT:
                typeParam = "pedestrian";
                break;
            case BIKE:
                typeParam = "bicycle";
                break;
        }

        String url = "http://open.mapquestapi.com/directions/v1/route?key=Fmjtd%7Cluub2lu12u%2Ca2%3Do5-96y5qz&" +
                "outFormat=xml&routeType=" + typeParam + "&shapeFormat=raw&generalize=0&locale=en_US&unit=m&" +
                "from=" + String.format("%.6f", lat1) + "," + String.format("%.6f", lon1) + "&" +
                "to="   + String.format("%.6f", lat2) + "," + String.format("%.6f", lon2);
        String charset = "UTF-8";
        URLConnection connection = null;
        InputStream response = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        try {
            connection = new URL(url).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
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
            System.err.println("There was a problem parsing the XML response.");
            e.printStackTrace();
        }
        ret.remove(ret.get(0)); // remove first point since the caller already has it
        return ret;
    }
}
