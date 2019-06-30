package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXExtension;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GpxMetadata;
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

public class GpxLoader extends XmlLoader {

	private FileInputStream fis = null;
	/**
	 * Constructor
	 */
	public GpxLoader() {
		super();
		isAdding = false;
		isDefault = true;
		extensions.add("gpx");
		xsdResource = "/org/gpsmaster/schema/gpx-1.1.xsd";
	}

	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	@Deprecated
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
	private void parseMetadata(GpxMetadata metadata, Element element) {
		
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
				 parseExtension(metadata.getExtension(), subElement);
				 // printTree(metadata.getExtension(), 0);
			}

		}
		
		// no need to read/parse bounds, since they will be determined automatically.
	}
	
	/**
	 * Recursively parse all sub elements of {@link Element} into 
	 * @param parent {@link GPXExtension} to add elements to 
	 * @param element parent element containing the sub elements to be parsed / added
	 */
	private void parseExtension(GPXExtension parent, Element element) {
				
		for (Element subElement : getSubElements(element)) {
			GPXExtension extension = new GPXExtension();
			String nodeName = subElement.getNodeName();
			extension.setKey(nodeName);
			if (subElement.getFirstChild() != null) {
				String nodeValue = subElement.getFirstChild().getNodeValue();
				if (nodeValue != null) {
					extension.setValue(nodeValue.replace("\n", "").trim());
				}			
			}
			parent.add(extension);
			parseExtension(extension, subElement);
		}		
	}

	/**
	 * 
	 * @param trkpt XML node containing waypoint
	 * @return waypoint containing data from trkpt or NULL if coordinates are invalid
	 */
	private Waypoint parseTrackPoint(Element trkpt) {
		Waypoint wpt = null;
		double lat = 0;
		double lon = 0;
		try {
			lat = Double.parseDouble(trkpt.getAttribute("lat"));
			lon = Double.parseDouble(trkpt.getAttribute("lon"));
		
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
					parseExtension(wpt.getExtension(), element);
				} else {
					// for now: treat everything else as an sourceFmt
					wpt.getExtension().add(new GPXExtension(nodeName, content));
				}			
			}
		}
		catch (NumberFormatException e) {
			// trackpoins with invalid coordinates are ignored and NULL is returned
			// TODO store such trackpoints anyway, since time field may provide an order 
			// TODO report NumberFormat warning back to caller
			System.out.println("caught numberFormat Exception");
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
				parseExtension(route.getExtension(), element);
			} else if (nodeName.equals("rtept")) {
				Waypoint wpt = parseTrackPoint(element);
				if (wpt != null) { 
					route.getPath().addWaypoint(wpt);
				}
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
			String nodeName = element.getNodeName();
			if (nodeName.equals("trkpt")) {
				Waypoint wpt = parseTrackPoint(element);
				if (wpt != null) {
					segment.addWaypoint(wpt);
				}
			} else if (nodeName.equals("extensions")) {				
				parseExtension(segment.getExtension(), element);
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
				parseExtension(track.getExtension(), element);
			} else if (nodeName.equals("trkseg")) {
				WaypointGroup wptGrp = track.addTrackseg();
				parseSegment(wptGrp, element);
			}
		}		
	}
	
	/**
	 * Create a Marker according to gpsm:type sourceFmt of the waypoint
	 * @param wpt
	 * @return instantiated subclass of {@link Marker} or {@link WaypointMarker} if type is unknown
	 */
	private Marker waypointToMarker(Waypoint wpt) {
		Marker marker = null;
		
		if (wpt.getExtension().containsKey(Const.EXT_MARKER)) {
			String className = wpt.getExtension().getSubValue(Const.EXT_MARKER);
			try {
				Class c = Class.forName(className);
				Constructor<Marker> con = c.getConstructor(Waypoint.class);
				marker = con.newInstance(wpt);
			} catch (Exception e) {
				// if this fails, marker will be created as WaypointMarker below.
				e.printStackTrace();
			}
		}
		if (wpt.getExtension().containsKey(Const.EXT_TYPE)) { // legacy
			String type = wpt.getExtension().getSubValue(Const.EXT_TYPE);
			if (type.equals("PhotoMarker")) {
				marker = new PhotoMarker(wpt);
			} else if (type.equals("WikiMarker")) {
				marker = new WikiMarker(wpt);
			} else if (type.equals("NullMarker")) {
				marker = new NullMarker(wpt);
			}
		}
		if (marker == null) {
			marker = new WaypointMarker(wpt);
		}
		return marker;
	}
	
	@Override
	public GPXFile load() throws Exception {
		checkOpen();
		
		return load(fis, null); // use sourceFmt from filename
	}
		
	/**
	 * 
	 */
	public GPXFile load(InputStream inputStream, String ext) throws Exception {
		gpx = new GPXFile();		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse(inputStream);
		document.normalize();
		Element root = document.getDocumentElement();
		String creator = root.getAttribute("creator");
		if (!creator.isEmpty()) { gpx.setCreator(creator); }
		
		// TODO check for defined namespaces and save them in the GPX file
		
		// metadata		
		Element metadata = getSubElement(root, "metadata");
		if (metadata != null) {
			parseMetadata(gpx.getMetadata(), metadata);
		} 
		
		// extensions
		Element extensions = getSubElement(root, "extensions");
		if (extensions != null) {
			parseExtension(gpx.getExtension(), extensions);
		} 
		
		// tracks
		for (Element element : getSubElementsByTagName(root, "trk")) {
			Track track = new Track(gpx.getColor());
			parseTrack(track, element);
			if (track.getTracksegs().size() > 0) {
				gpx.addTrack(track);
			}			
		}

		// routes
		for (Element element : getSubElementsByTagName(root, "rte")) {
			Route route = new Route(gpx.getColor());
			parseRoute(route, element);
			if (route.getPath().getWaypoints().size() > 0) {
				gpx.addRoute(route);
			}			
		}
		
		// waypoints
		for (Element wpt : getSubElementsByTagName(root, "wpt")) {
			Waypoint trkpt = parseTrackPoint(wpt);
			if (wpt != null) {
				Marker marker = waypointToMarker(trkpt);
				gpx.getWaypointGroup().addWaypoint(marker);
			}
		}

		return gpx;
	}
	
	@Override
	public void loadCumulative(InputStream inStream) throws Exception {
		throw new UnsupportedOperationException();
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
        writeAttribute("lat", wpt.getLat());
        writeAttribute("lon", wpt.getLon());
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
		writeExtension(wpt.getExtension());
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
		writeExtension(track.getExtension());
		for (WaypointGroup wptGrp : track.getTracksegs()) {
			writeStartElement("trkseg");
			writeWayPointGroup(wptGrp.getWaypoints(), "trkpt");
			writeExtension(wptGrp.getExtension());
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
		writeExtension(route.getExtension());
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
	 * Write sourceFmt hierarchy to XML
	 * @param sourceFmt the top-level {@link GPXExtension} element
	 * @throws XMLStreamException 
	 */
	private void writeExtension(GPXExtension extension) throws XMLStreamException {		
		if (extension.getExtensions().size() > 0) {
			writeSubtree(extension);
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
	private void writeMetadata(GpxMetadata metadata) throws XMLStreamException {
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
        writeAttribute("minlat", metadata.getBounds().getMinlat().doubleValue());
        writeAttribute("minlon", metadata.getBounds().getMinlon().doubleValue());
        writeAttribute("maxlat", metadata.getBounds().getMaxlat().doubleValue());
        writeAttribute("maxlon", metadata.getBounds().getMaxlon().doubleValue());
              
        writeExtension(metadata.getExtension());        
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
            
            // quick hack: write namespaces defined in GPXFile. assume GpsMaster URI
            for (String prefix : gpx.getExtensionPrefixes()) {
               writer.writeAttribute("xmlns:" + prefix, "http://www.gpsmaster.org/schema/" + prefix + "/v1");
            }
            // TODO write other namespaces, if used in file (hrm:, fl:, nmea:, ...)
            
            // METADATA
            writeMetadata(gpx.getMetadata()); // TODO writeSubTree
            
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
            	                 
            writeSubtree(gpx.getExtension());
            writer.writeEndElement();  // End Gpx
            writer.writeEndDocument();
            
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
