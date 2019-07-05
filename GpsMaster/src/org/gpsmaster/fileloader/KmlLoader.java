package org.gpsmaster.fileloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


// https://developers.google.com/kml/documentation/?csw=1

/**
 *
 * @author rfuegen
 *
 *
 */

// TODO support .kmz files

public class KmlLoader extends XmlLoader {


	private boolean hasGx = false; // if this document contains google GX extensions

	public KmlLoader() {
		super();
		extensions.add("kml");
		extensions.add("kmz");
		xsdResource = "/org/gpsmaster/schema/ogckml22.xsd";
	}


	public void Open(File file) {

		this.file = file;
		isOpen = true;

	}

	/**
	 *
	 * @param Waypoint
	 * @param coordinates string containing coordinates "long,lat,ele"
	 */
	private Waypoint parseCoordinateLine(String coordinates) {

		String[] items = coordinates.split(",");
		if (items.length < 2) {
			return null;
		}
		double lon = Double.parseDouble(items[0]);
		double lat = Double.parseDouble(items[1]);
		Waypoint wpt = new Waypoint(lat, lon);
		if (items[2].isEmpty() == false) {
			double ele = Double.parseDouble(items[2]);
			if (ele > 0) {
				wpt.setEle(ele);
			}
		}
		return wpt;

	}

	/**
	 *
	 * @param wptGrp
	 * @param folder
	 */
	private void parseWaypoints(WaypointGroup wptGrp, Element folder) {
		NodeList nodes = folder.getElementsByTagName("Placemark");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);
			String name = getSubElement(element, "name").getTextContent();
			Element point = getSubElement(element, "Point");
			String coord = getSubElement(point, "coordinates").getTextContent();
			Waypoint wpt = parseCoordinateLine(coord);
			if (wpt != null) {
				wpt.setName(name);
				wptGrp.addWaypoint(wpt);
			}
		}

	}

	/**
	 *
	 * @param wptGrp
	 * @param placemark
	 */
	private void parseCoordinateSection(WaypointGroup wptGrp, Element placemark) {
		Element lineString = getSubElement(placemark, "LineString");
		Element coord = getSubElement(lineString, "coordinates");
		String coordinates = coord.getTextContent();
		for (String line : coordinates.split("\\r?\\n")) {
			Waypoint wpt = parseCoordinateLine(line);
			if (wpt != null) {
				wptGrp.addWaypoint(wpt);
			}
		}
	}


	/**
	 * parse coordinates in GX extensions
	 * @param wptGrp Track segment to add coordinates to
	 * @param placemark <Placemark> element
	 */
	private void parseGxCoordinates(WaypointGroup wptGrp, Element placemark) {
		Element trackSeg = getSubElement(placemark, "gx:Track");
		NodeList times = trackSeg.getElementsByTagName("when");
		NodeList coordinates = trackSeg.getElementsByTagName("gx:coord");
		if (times.getLength() != coordinates.getLength()) {
			throw new IllegalArgumentException("coordinates & time count mismatch");
		}
		for (int i = 0; i < times.getLength(); i++) {
			String time = times.item(i).getTextContent();
			String coord = coordinates.item(i).getTextContent();
			String[] items = coord.split("[ ]+");
			double lon = Double.parseDouble(items[0]);
			double lat = Double.parseDouble(items[1]);
			double ele = Double.parseDouble(items[2]);
			Waypoint wpt = new Waypoint(lat, lon);
			if (ele > 0) {
				wpt.setEle(ele);
			}
			DateTime dt = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(time);
            wpt.setTime(dt.toDate());
			wptGrp.addWaypoint(wpt);
		}
	}

	/**
	 * <Folder>/<PlaceMark>/<gx:Track>
	 * @param track
	 * @param folder
	 */
	private void parseTrack(Track track, Element folder) {

		// http://javarevisited.blogspot.co.at/2011/12/parse-xml-file-in-java-example-tutorial.html

		Element placemark = getSubElement(folder, "Placemark");
		track.setName(getSubValue(placemark, "name"));
		String desc = getSubValue(placemark, "description");
		if (desc != null) { track.setDesc(desc); }
		WaypointGroup trkSeg = new WaypointGroup(track.getColor(), WptGrpType.TRACKSEG);
		if (hasGx) {
			parseGxCoordinates(trkSeg, placemark);
		} else {
			parseCoordinateSection(trkSeg, placemark);
		}

		if (trkSeg.getWaypoints().size() > 0) {
			track.getTracksegs().add(trkSeg);
		}

	}

	/**
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 *
	 */
	public GPXFile Load() throws Exception {

		gpx = new GPXFile();
		ZipFile zipFile = null;
		InputStream fis = null;

		if (file.getName().endsWith(".kml")) {
			fis = new FileInputStream(file);
		} else if (file.getName().endsWith(".kmz")) {
			zipFile = new ZipFile(file);
			ZipEntry entry = zipFile.getEntry("doc.kml");
			if (entry == null) {
				zipFile.close();
				throw new FileNotFoundException("KMZ file does not contain doc.kml");
			}
			fis = zipFile.getInputStream(entry);
		} else {
			throw new UnsupportedOperationException("unsupported file type");
		}

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		Document xml = builder.parse(fis);

		Element root = xml.getDocumentElement();
		String gx = root.getAttribute("xmlns:gx");
		hasGx = !gx.isEmpty();
		Element document = getSubElement(root, "Document");

		// set GPXFile name to <Document>/<name>
		String name = getSubValue(document, "name");
		gpx.getMetadata().setName(name);
		// TODO gpx.SetTime(...)
		// TODO read style information & set track/gpx color
		// <Document>/<Folder>/<Tracks>
		for (Element element : getSubElementsByTagName(document, "Folder")) {
			if (getSubValue(element, "name").toLowerCase().equals("tracks")) {
			   Track track = new Track(gpx.getColor());
			   parseTrack(track, element);
			   gpx.getTracks().add(track);
			}
			if (getSubValue(element, "name").toLowerCase().equals("waypoints")) {
				parseWaypoints(gpx.getWaypointGroup(), element);
			}

		}
		fis.close();
		if (zipFile != null) {
			zipFile.close();
		}
		return gpx;
	}


	public void Save(GPXFile gpx, File file) {
		// TODO Auto-generated method stub

	}

	public void Close() {
		this.file = null;
		isOpen = false;
	}


}
