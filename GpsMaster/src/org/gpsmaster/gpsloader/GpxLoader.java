package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.marker.Marker;
import org.gpsmaster.marker.NullMarker;
import org.gpsmaster.marker.PhotoMarker;
import org.gpsmaster.marker.WaypointMarker;
import org.gpsmaster.marker.WikiMarker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.topografix.gpx._1._1.LinkType;
import com.topografix.gpx._1._1.MetadataType;

public class GpxLoader extends XmlLoader {

	FileInputStream fis = null;

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
	 * @throws FileNotFoundException
	 *
	 */
	@Override
	public void open(File file) throws FileNotFoundException {
		this.file = file;
		fis = new FileInputStream(file);
		isOpen = true;
	}

	/**
	 *
	 * @param link
	 * @param element
	 */
	private LinkType parseLink(Element element) {
		LinkType link = new LinkType();
		link.setHref(element.getAttribute("href"));
		link.setText(getSubValue(element, "text"));
		link.setType(getSubValue(element, "type"));
		return link;
	}

	/**
	 *
	 * @param element
	 * @throws JAXBException
	 */
	private void parseMetadata(MetadataType metadata, Element element) {

		for (Element subElement : getSubElements(element)) {
			String nodeName = subElement.getNodeName();
			if (nodeName.equals("name")) {
				metadata.setName(subElement.getTextContent());
			}
			if (nodeName.equals("desc")) {
				metadata.setDesc(subElement.getTextContent());
			}
			// author
			// copyright
			if (nodeName.equals("keywords")) {
				metadata.setKeywords(subElement.getTextContent());
			}
			if (nodeName.equals("time")) {
				Calendar cal = DatatypeConverter.parseDateTime(subElement.getTextContent());
				metadata.setTime(cal.getTime());
			}
			if (nodeName.equals("link")) {
				metadata.getLink().add(parseLink(subElement));
			}
			if (nodeName.equals("extensions")) {
				 // !! parseExtensions(metadata.getExtensions(), subElement);
			}

		}

		// no need to read/parse bounds, since they will be determined automatically.
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
			// if (extensions.containsKey(nodeName)) {
			//	extensions.remove(nodeName);
			// }
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
				if (!content.isEmpty()) {
					wpt.setEle(Double.parseDouble(content));
				}
			} else if (nodeName.equals("time")) {
				// joda.time has problems with 2014-01-01T17:54:22.850Z  (....850Z)!
				// XTime dt = ISODateTimeFormat.dateTime().parseDateTime(content);
				Calendar cal = DatatypeConverter.parseDateTime(content);
				// TODO parse non-standard date like "2012-06-19 05:37:38"
				wpt.setTime(cal.getTime());
			} else if (nodeName.equals("name")) {
				wpt.setName(content);
			} else if (nodeName.equals("cmt")) {
				wpt.setCmt(content);
			} else if (nodeName.equals("desc")) {
				wpt.setDesc(content);
			} else if (nodeName.equals("src")) {
				wpt.setSrc(content);
			} else if (nodeName.equals("sym")) {
				wpt.setSym(content);
			} else if (nodeName.equals("fix")) {
				wpt.setFix(content);
			} else if (nodeName.equals("type")) {
				wpt.setType(content);
			} else if (nodeName.equals("link")) {
				wpt.getLink().add(parseLink(element));
			} else if (nodeName.equals("sat")) {
				wpt.setSat(Integer.parseInt(content));
			} else if (nodeName.equals("hdop")) {
				wpt.setHdop(Double.parseDouble(content));
			} else if (nodeName.equals("vdop")) {
				wpt.setVdop(Double.parseDouble(content));
			} else if (nodeName.equals("pdop")) {
				wpt.setPdop(Double.parseDouble(content));
			} else if (nodeName.equals("magvar")) {
				wpt.setMagvar(Double.parseDouble(content));
			} else if (nodeName.equals("geoidheight")) {
				wpt.setGeoidheight(Double.parseDouble(content));
			} else if (nodeName.equals("ageofdgpsdata")) {
				wpt.setAgeofdgpsdata(Double.parseDouble(content));
			} else if (nodeName.equals("dgpsid")) {
				wpt.setDgpsid(Integer.parseInt(content));
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
	 * @param segment
	 * @param segElement
	 */
	private void parseSegment(WaypointGroup segment, Element segElement) {
		for (Element element : getSubElements(segElement)) {
			String content = element.getTextContent().replace("\n", "");
			String nodeName = element.getNodeName();
			if (nodeName.equals("trkpt")) {
				Waypoint wpt = parseTrackPoint(element);
				segment.addWaypoint(wpt);
			} else if (nodeName.equals("extensions")) {
				parseExtensions(segment.getExtensions(), element);
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
			} else if (nodeName.equals("src")) {
				track.setSrc(content);
			} else if (nodeName.equals("link")) {
				LinkType link = parseLink(element);
				track.getLink().add(link);
			} else if (nodeName.equals("extensions")) {
				parseExtensions(track.getExtensions(), element);
			} else if (nodeName.equals("trkseg")) {
				WaypointGroup wptGrp = track.addTrackseg();
				parseSegment(wptGrp, element);
			}
		}
	}

	/**
	 * Create a Marker according to gpsm:type extension of the waypoint
	 * @param wpt
	 * @return instantiated subclass of {@link Marker} or {@link WaypointMarker} if type is unknown
	 */
	private Marker waypointToMarker(Waypoint wpt) {
		Marker marker = new WaypointMarker(wpt);
		if (wpt.getExtensions().containsKey("gpsm:type")) {
			// TODO instantiate marker dynamically via reflection from org.gpsmaster.marker
			String type = wpt.getExtensions().get("gpsm:type");
			if (type.equals("PhotoMarker")) {
				marker = new PhotoMarker(wpt);
			} else if (type.equals("WikiMarker")) {
				marker = new WikiMarker(wpt);
			} else if (type.equals("NullMarker")) {
				marker = new NullMarker(wpt);
			}
		}
		return marker;
	}

	@Override
	public GPXFile load() throws Exception {
		checkOpen();

		return load(fis);
	}

	@Override
	public GPXFile load(InputStream inputStream) throws Exception {
		gpx = new GPXFile();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse(inputStream);
		document.normalize();
		Element root = document.getDocumentElement();
		String creator = root.getAttribute("creator");
		if (!creator.isEmpty()) { gpx.setCreator(creator); }

		// metadata
		Element metadata = getSubElement(root, "metadata");
		if (metadata != null) {
			parseMetadata(gpx.getMetadata(), metadata);
		}

		// extensions
		Element extensions = getSubElement(root, "extensions");
		if (extensions != null) {
			parseExtensions(gpx.getExtensions(), extensions);
		}

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
			Marker marker = waypointToMarker(trkpt);
			gpx.getWaypointGroup().addWaypoint(marker);
		}

		return gpx;
	}


	// Region 	private helper methods

	@Override
	public void loadCumulative() throws Exception {
		gpxFiles.put(file, load());
	}

	/**
	 * write a {@link LinkType} object
	 * @param link
	 * @throws XMLStreamException
	 */
	private void writeLink(LinkType link) throws XMLStreamException {
		writeStartElement("link");
		writer.writeAttribute("href", link.getHref());
		if (link.getText() != null) {
			writeSimpleElement("text", link.getText());
		}
		if (link.getType() != null) {
			writeSimpleElement("type", link.getType());
		}
		writeEndElement();
	}

	/**
	 * write a single waypoint. order of elements is important.
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
        if (wpt.getMagvar() > 0) {
        	writeSimpleElement("magvar", wpt.getMagvar());
        }
        if (wpt.getGeoidheight() > 0) {
        	writeSimpleElement("geoidheight", wpt.getGeoidheight());
        }
       	writeSimpleElement("name", wpt.getName());
		writeSimpleElement("cmt", wpt.getCmt());
		writeSimpleElement("desc", wpt.getDesc());
		writeSimpleElement("src", wpt.getSrc());
        for (LinkType link : wpt.getLink()) {
        	writeLink(link);
        }
		writeSimpleElement("sym", wpt.getSym());
		writeSimpleElement("type", wpt.getType());
		writeSimpleElement("fix", wpt.getFix());
        if (wpt.getSat() > 0) {
        	writeSimpleElement("sat", wpt.getSat());
        }
        if (wpt.getHdop() > 0) {
        	writeSimpleElement("hdop", wpt.getHdop());
        }
        if (wpt.getVdop() > 0) {
        	writeSimpleElement("vdop", wpt.getVdop());
        }
        if (wpt.getPdop() > 0) {
        	writeSimpleElement("pdop", wpt.getPdop());
        }
        if (wpt.getAgeofdgpsdata() > 0) {
        	writeSimpleElement("ageofdgpsdata", wpt.getAgeofdgpsdata());
        }
        if (wpt.getDgpsid() > 0) {
        	writeSimpleElement("dgpsid", wpt.getDgpsid());
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
		writeLinks(track.getLink());
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

	/**
	 * @throws XMLStreamException
	 *
	 */
	private void writeLinks(List<LinkType> links) throws XMLStreamException {
		for (LinkType link : links) {
			writeLink(link);
		}
	}

	/**
	 *
	 * @param metadata
	 * @throws XMLStreamException
	 */
	private void writeMetadata(MetadataType metadata) throws XMLStreamException {
        // Metadata
        writeStartElement("metadata");
       	writeSimpleElement("name", metadata.getName());
       	writeSimpleElement("desc", metadata.getDesc());
       	writeSimpleElement("keywords", metadata.getKeywords());
       	// TODO author
       	// TODO copyright

        if (metadata.getTime() != null) {
        	writeSimpleElement("time", metadata.getTime());
        }
        // links
        writeLinks(metadata.getLink());

        writer.writeEmptyElement("bounds");
        writer.writeAttribute("minlat", String.format("%.8f", metadata.getBounds().getMinlat()));
        writer.writeAttribute("minlon", String.format("%.8f", metadata.getBounds().getMinlon()));
        writer.writeAttribute("maxlat", String.format("%.8f", metadata.getBounds().getMaxlat()));
        writer.writeAttribute("maxlon", String.format("%.8f", metadata.getBounds().getMaxlon()));

        //
        // TODO extension

        writeEndElement();  // End Metadata
	}
	// EndRegion


	public void save(GPXFile gpx, File file) throws FileNotFoundException {

		FileOutputStream fos = new FileOutputStream(file);
		save(gpx, fos);
		try {
			fos.close();
		} catch (IOException e) {

		}
	}

	/**
	 * @throws FileNotFoundException
	 *
	 */
	@Override
	public void save(GPXFile gpx, OutputStream out) {

        // make sure that "." is used as decimal separator
        Locale prevLocale = Locale.getDefault();
        Locale.setDefault(new Locale("en", "US"));

        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
			writer = factory.createXMLStreamWriter(out, "UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n\n");

            writer.writeStartElement("gpx");
            writer.writeAttribute("version", "1.1");
            if (gpx.getCreator().isEmpty()) {
            	writer.writeAttribute("creator", GpsMaster.ME);
            } else {
            	writer.writeAttribute("creator", gpx.getCreator());
            }
            writer.writeAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
            writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi:schemaLocation",
                    "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
            writer.writeAttribute("xmlns:gpsm", "http://www.gpsmaster.org/schema/gpsm/v1");
            // TODO write other namespaces, if used in file (hrm:, fl:, nmea:, ...)

            // METADATA
            writeMetadata(gpx.getMetadata());

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
	}

	@Override
	public void close() {
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fis = null;
		}
		this.file = null;
		isOpen = false;
	}


}
