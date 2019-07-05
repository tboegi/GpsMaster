package org.gpsmaster.fileloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.topografix.gpx._1._1.MetadataType;

public class GpxLoader extends XmlLoader {

	private boolean writeSpecials = true;
	/**
	 * Constructor
	 */
	public GpxLoader() {
		super();
		isDefault = true;
		extensions.add("gpx");
		xsdResource = "/org/gpsmaster/schema/gpx-1.1.xsd";
	}

	/**
	 *
	 */
	@Override
	public void Open(File file) {
		this.file = file;
		isOpen = true;
	}


	/**
	 *
	 * @param element
	 * @throws JAXBException
	 */
	private void parseMetadata(MetadataType metadata, Element element) throws JAXBException {
		// TODO test
		String name = getSubValue(element, "name");
		if (name != null) { metadata.setName(name); }
		String desc = getSubValue(element, "desc");
		if (desc != null) { metadata.setDesc(desc); }
		String time = getSubValue(element, "time");
		if (time != null) {
			Calendar cal = DatatypeConverter.parseDateTime(time);
			metadata.setTime(cal.getTime());
			// DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(time);
			// gpx.setTime(dt.toDate());
		}
		// TODO support & parse extensions
	}

	/**
	 *
	 * @param extensions
	 * @param element
	 */
	private void parseExtensions(Hashtable<String, String> extensions, Element element) {
		for (Element ext : getSubElements(element)) {
			String content = ext.getTextContent();
			String nodeName = ext.getNodeName();
			extensions.put(nodeName, content);
		}
	}

	/**
	 *
	 * @return
	 */
	private Waypoint parseTrackPoint(Element trkpt) {
		Waypoint wpt = null;
		double lat = Double.parseDouble(trkpt.getAttribute("lat"));
		double lon = Double.parseDouble(trkpt.getAttribute("lon"));
		wpt = new Waypoint(lat, lon);

		for (Element element : getSubElements(trkpt)) {
			String content = element.getTextContent().replace("\n", "");
			String nodeName = element.getNodeName();
			if (nodeName.equals("ele")) {
				wpt.setEle(Double.parseDouble(content));
			} else if (nodeName.equals("time")) {
				// joda.time has problems with 2014-01-01T17:54:22.850Z  (....850Z)!
				// DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(content);
				Calendar cal = DatatypeConverter.parseDateTime(content);
				wpt.setTime(cal.getTime());
			} else if (nodeName.equals("name")) {
				wpt.setName(content);
			} else if (nodeName.equals("desc")) {
				wpt.setDesc(content);
			} else if (nodeName.equals("sat")) {
				wpt.setSat(Integer.parseInt(content));
			} else if (nodeName.equals("hdop")) {
				wpt.setHdop(Double.parseDouble(content));
			} else if (nodeName.equals("vdop")) {
				wpt.setVdop(Double.parseDouble(content));
			} else if (nodeName.equals("pdop")) {
				wpt.setPdop(Double.parseDouble(content));
			} else if (nodeName.equals("extensions")) {
				parseExtensions(wpt.getExtensions(), element);
			} else {
				// for now: treat everything else as an extension
				wpt.getExtensions().put(nodeName, content);
			}
		}
		return wpt;
	}

	/**
	 *
	 * @param track
	 * @param element
	 */
	private void parseRoute(Route route, Element rteElement) {
		for (Element element : getSubElements(rteElement)) {
			String content = element.getTextContent();
			String nodeName = element.getNodeName();
			if (nodeName.equals("number")) {
				route.setNumber(Integer.parseInt(content));
			} else if (nodeName.equals("name")) {
				route.setName(content);
			} else if (nodeName.equals("desc")) {
				route.setDesc(content);
			} else if (nodeName.equals("type")) {
				route.setType(content);
			} else if (nodeName.equals("extensions")) {
				parseExtensions(route.getExtensions(), element);
			} else if (nodeName.equals("rtept")) {
				Waypoint wpt = parseTrackPoint(element);
				route.getPath().addWaypoint(wpt);
			}
		}
	}

	/**
	 *
	 * @param track
	 * @param element
	 */
	private void parseTrack(Track track, Element trkElement) {
		for (Element element : getSubElements(trkElement)) {
			String content = element.getTextContent().replace("\n", "");
			String nodeName = element.getNodeName();
			if (nodeName.equals("number")) {
				track.setNumber(Integer.parseInt(content));
			} else if (nodeName.equals("name")) {
				track.setName(content);
			} else if (nodeName.equals("desc")) {
				track.setDesc(content);
			} else if (nodeName.equals("type")) {
				track.setType(content);
			} else if (nodeName.equals("extensions")) {
				parseExtensions(track.getExtensions(), element);
			} else if (nodeName.equals("trkseg")) {
				WaypointGroup wptGrp = track.addTrackseg();
				for (Element trkpt : getSubElementsByTagName(element, "trkpt")) {
					Waypoint wpt = parseTrackPoint(trkpt);
					wptGrp.addWaypoint(wpt);
				}
				try {
					Element extensions = getSubElement(element, "extensions");
					parseExtensions(wptGrp.getExtensions(), extensions);
				} catch (NoSuchElementException e) {
					// ignore
				}
			}
		}
	}

	@Override
	public GPXFile Load() throws Exception {
		checkOpen();
		gpx = new GPXFile();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse(new FileInputStream(file));
		document.normalize();
		Element root = document.getDocumentElement();
		String creator = root.getAttribute("creator");
		if (!creator.isEmpty()) { gpx.setCreator(creator); }

		// metadata
		try {
			Element metadata = getSubElement(root, "metadata");
			parseMetadata(gpx.getMetadata(), metadata);
			Element extensions = getSubElement(root, "extensions");
			parseExtensions(gpx.getExtensions(), extensions);
		} catch (NoSuchElementException e) {}

		// tracks
		for (Element element : getSubElementsByTagName(root, "trk")) {
			Track track = new Track(gpx.getColor());
			parseTrack(track, element);
			if (track.getTracksegs().size() > 0) {
				gpx.getTracks().add(track);
			}
		}

		// routes
		for (Element element : getSubElementsByTagName(root, "rte")) {
			Route route = new Route(gpx.getColor());
			parseRoute(route, element);
			if (route.getPath().getWaypoints().size() > 0) {
				gpx.getRoutes().add(route);
			}
		}

		// waypoints
		for (Element wpt : getSubElementsByTagName(root, "wpt")) {
			Waypoint trkpt = parseTrackPoint(wpt);
			gpx.getWaypointGroup().addWaypoint(trkpt);
		}

		return gpx;
	}


	// Region 	private helper methods

	/**
	 * write a single waypoint
	 * @param wpt
	 * @param tag
	 * @throws XMLStreamException
	 */
	private void writeWaypoint(Waypoint wpt, String tag) throws XMLStreamException {

		writeStartElement(tag);
        writer.writeAttribute("lat", String.format("%.8f", (Double) wpt.getLat()));
        writer.writeAttribute("lon", String.format("%.8f", (Double) wpt.getLon()));
        if (wpt.getEle() > 0) {
        	writeSimpleElement("ele", wpt.getEle());
        }
       	writeSimpleElement("time", wpt.getTime());
		writeSimpleElement("name", wpt.getName());
		writeSimpleElement("desc", wpt.getDesc());
		writeSimpleElement("type", wpt.getType());
        if (wpt.getSat() > 0) {
        	writeSimpleElement("sat", wpt.getSat());
        }
        if (wpt.getHdop() > 0) {
        	writeSimpleElement("hdop", wpt.getHdop());
        }
        if (wpt.getPdop() > 0) {
        	writeSimpleElement("pdop", wpt.getPdop());
        }
        if (wpt.getVdop() > 0) {
        	writeSimpleElement("vdop", wpt.getVdop());
        }
		writeExtensions(wpt.getExtensions());
		writeEndElement();
	}

	/**
	 *
	 * @param track
	 * @throws XMLStreamException
	 */
	private void writeTrack(Track track) throws XMLStreamException {
		writeStartElement("trk");
		if (track.getNumber() > 0) {
			writeSimpleElement("number", track.getNumber());
		}
		writeSimpleElement("name", track.getName());
		writeSimpleElement("desc", track.getDesc());
		writeSimpleElement("type", track.getType());
		writeExtensions(track.getExtensions());
		for (WaypointGroup wptGrp : track.getTracksegs()) {
			writeStartElement("trkseg");
			writeWayPointGroup(wptGrp.getWaypoints(), "trkpt");
			writeExtensions(wptGrp.getExtensions());
			writeEndElement(); // End trkseg
		}
		writeEndElement();
	}

	/**
	 *
	 * @param track
	 * @throws XMLStreamException
	 */
	private void writeRoute(Route route) throws XMLStreamException {
		// TODO testen!!
		writeStartElement("rte");
		if (route.getNumber() > 0) {
			writeSimpleElement("number", route.getNumber());
		}
		writeSimpleElement("name", route.getName());
		writeSimpleElement("desc", route.getDesc());
		writeSimpleElement("type", route.getType());
		writeExtensions(route.getExtensions());
		writeWayPointGroup(route.getPath().getWaypoints(), "rtept");
		writeEndElement();
	}

	/**
	 *
	 * @param waypoints
	 * @throws XMLStreamException
	 */
	private void writeWayPointGroup(List<Waypoint> waypoints, String tag) throws XMLStreamException {

		for (Waypoint wpt : waypoints) {
			writeWaypoint(wpt, tag);
		}

	}

	/**
	 *
	 * @param extensions
	 * @throws XMLStreamException
	 */
	private void writeExtensions(Hashtable<String, String> extensions) throws XMLStreamException {
		if (extensions.size() > 0) {
			writeStartElement("extensions");
			Iterator<String> i = extensions.keySet().iterator();
			while (i.hasNext()) {
				String key = i.next();
				writeSimpleElement(key, extensions.get(key));
			}
			writeEndElement();
		}
	}

	// EndRegion


	/**
	 * @throws FileNotFoundException
	 *
	 */
	@Override
	public void Save(GPXFile gpx, File file) throws FileNotFoundException {

        // make sure that "." is used as decimal separator
        Locale prevLocale = Locale.getDefault();
        Locale.setDefault(new Locale("en", "US"));

        FileOutputStream fos = new FileOutputStream(file);
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
			writer = factory.createXMLStreamWriter(fos, "UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n\n");

            writer.writeStartElement("gpx");
            writer.writeAttribute("version", "1.1");
            if (gpx.getCreator().isEmpty() == false) {
            	writer.writeAttribute("creator", gpx.getCreator());
            } else {
            	writer.writeAttribute("creator", "www.gpxcreator.com");
            }
            writer.writeAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
            writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi:schemaLocation",
                    "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
            // writer.writeCharacters("\n\n");

            // Metadata
            writeStartElement("metadata");
           	writeSimpleElement("name", gpx.getMetadata().getName());
           	writeSimpleElement("desc", gpx.getMetadata().getDesc());

            // TODO if link was already contained in source file, write it here
            // and write GpsMaster into metadata
            writeStartElement("link");
            writer.writeAttribute("href", "http://www.gpxcreator.com");
            writeStartElement("text");
            writer.writeCharacters("GPX Creator");
            writeEndElement();
            writeEndElement();

            if (gpx.getMetadata().getTime() != null) {
            	writeSimpleElement("time", gpx.getMetadata().getTime());
            }

            writer.writeEmptyElement("bounds");
            writer.writeAttribute("minlat", String.format("%.8f", gpx.getMinLat()));
            writer.writeAttribute("minlon", String.format("%.8f", gpx.getMinLon()));
            writer.writeAttribute("maxlat", String.format("%.8f", gpx.getMaxLat()));
            writer.writeAttribute("maxlon", String.format("%.8f", gpx.getMaxLon()));

            writeEndElement();  // End Metadata

            // WAYPOINTS
            writeWayPointGroup(gpx.getWaypointGroup().getWaypoints(), "wpt");

            // TRACKS
            for (Track track : gpx.getTracks()) {
            	writeTrack(track);
            }

            // ROUTES
            for (Route route : gpx.getRoutes()) {
            	writeRoute(route);
            }

            writeExtensions(gpx.getExtensions());
            writer.writeEndElement();  // End Gpx
            writer.writeEndDocument();

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Locale.setDefault(prevLocale);
        try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void Close() {
		this.file = null;
		isOpen = false;
	}

}
