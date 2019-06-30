package org.gpsmaster.gpsloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

/**
 * Loader for IGC files (International Gliding Commission)
 * 
 * http://carrier.csi.cam.ac.uk/forsterlewis/soaring/igc_file_format/
 * http://carrier.csi.cam.ac.uk/forsterlewis/soaring/igc_file_format/igc_format_2008.html
 *  @author rfu
 *
 */
public class IgcLoader extends GpsLoader {

	private GPXFile gpx = null;
	private Waypoint wpt = null; // last read waypoint
	private WaypointGroup trackpoints = null;
	private Route route = null;
	private List<IgcExtension> bExtensions = new ArrayList<IgcExtension>();
	private List<IgcExtension> kExtensions = new ArrayList<IgcExtension>();
	private List<Waypoint> events = new ArrayList<Waypoint>(); // waypoints from E records
	
	private final SimpleDateFormat utcFormatter = new SimpleDateFormat("ddMMYY HHmmssZ"); // ("aabbcc xxyyzz");

	// helpers:
	// supported WGS84 datum codes
	private List<String> wgsDatum = new ArrayList<String>();
	private final String cIgnore = "C0000000N00000000E"; // ignore this C line
	
	// header fields
	private String utcDate = "010170"; // UTC date as of header record.
	private String pilot1 = "";
	private String pilot2 = "";
	private String glider = "";
	private String gliderId = "";
	
	// position of sourceFmt fields within B record

	/**
	 * default Constructor
	 */
	public IgcLoader() {
		isAdding = false;
		extensions.add("igc");	
		
		wgsDatum.add("WGS84");
		wgsDatum.add("WGS1984");
		wgsDatum.add("WGS-1984");
	}
	
	/**
	 * 
	 */
	@Override
	public GPXFile load(InputStream inputStream, String format) throws Exception {
		String line;
		gpx = new GPXFile();
		Track track = new Track(gpx.getColor());
 		trackpoints = track.addTrackseg();
		gpx.addTrack(track);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		while ((line = br.readLine()) != null) {
			char id = line.charAt(0);
			switch(id) {
			case 'A': 
				parseA(line);
				break;
			case 'B': 
				parseB(line);
				break;
			case 'C': 
				parseC(line);
				break;
			case 'E': 
				parseE(line);
				break;
			case 'H': 
				parseH(line);
				break;
			case 'I': 
				parseIJ(line, bExtensions);
				break;
			case 'J': 
				parseIJ(line, kExtensions);
				break;
			case 'K': 
				parseK(line);
				break;
			case 'L': 
				parseL(line);
				break;
			default:
				break;
			}
		}
		br.close();
		
		// postprocessing
		String desc = "";
		if (glider.isEmpty() == false) {
			desc = glider;
		}
		if (gliderId.isEmpty() == false) {
			if (desc.isEmpty() == false) {
				desc += " (" + gliderId + ")";
			} else {
				desc = gliderId;
			}
		}
		if (pilot1.isEmpty() == false) {
			if (desc.isEmpty() == false) {
				desc += " piloted by ";
			} else {
				desc = "Pilot(s): ";
			}
			desc += pilot1;
		}
		if (pilot2.isEmpty() == false) {
			desc += ", " + pilot2;
		}
		gpx.setDesc(desc);
		
		return gpx;
	}

	/**
	 * 
	 * @param string
	 * @param sep
	 * @return remaining text after the separator or empty string
	 */
	private String getAfter(String string, String sep) {
		return string.substring(string.indexOf(sep) + 1).trim();
	}
	
	/**
	 * parse a line containing a record of type A:
	 * Manufacturer and Unique ID
	 * Once per file 
	 */
	private void parseA(String line) {
		String manufacturer = line.substring(1, 3); // TODO implement Hashtable<Man.Code -> man.description
		String uniqueId = line.substring(4, 6);
		String rest = line.substring(7);
		gpx.setCreator(manufacturer + " (serial #" + uniqueId + ") " + rest);
	}
	
	/**
	 * parse a line containing a record of type B 
	 * Fix (GPS position etc.)
	 * multiple lines
	 * @throws ParseException 
	 */
	private void parseB(String line) throws ParseException {
		
		String latString = line.substring(7, 15);
		String lonString = line.substring(15, 24);
		char fixValidity = line.charAt(24);
		String pressAlt = line.substring(25, 30); // altitude by pressure sensor
		String gpsAlt = line.substring(30, 35); // altitude by GPS

		double lat = parseLat(latString);
		double lon = parseLon(lonString);		
		wpt = new Waypoint(lat, lon);
		
		// Elevation
		switch(fixValidity) {
			case 'A': // 3D fix, elevation from GPS
				wpt.setEle(Double.parseDouble(gpsAlt));
				break;
			case 'V': // 2D fix, elevation from pressure sensor
				wpt.setEle(Double.parseDouble(pressAlt));
				break;
		}

		// check if there are previous events, store them at the current coordinates
		// problem: overlapping waypoints don't show well on the map
		if (events.size() > 0) {
			for (Waypoint eventPoint : events) {
				eventPoint.setLat(lat);
				eventPoint.setLon(lon);
				eventPoint.setEle(wpt.getEle());
				gpx.getWaypointGroup().addWaypoint(eventPoint);
			}
			events.clear();
		}
		
		// UTC date
		wpt.setTime(parseDate(line.substring(1, 7)));
		
		// extensions within B record:		
		for (IgcExtension ext : bExtensions) {
			String code = ext.getCode();
			String value = line.substring(ext.getStart(), ext.getEnd());
			wpt.getExtension().add(code, value);
		}
		
		// save whole record as sourceFmt
		// wpt.getExtensions().put("igc:raw", line);
		trackpoints.addWaypoint(wpt);
	}

	/**
	 * 
	 * @param line
	 */
	private void parseC(String line) {
		
		// first C line is different from the following ones
		if (route == null) {
			route = new Route(gpx.getColor());
			String defDate = line.substring(1, 7) + " " + line.substring(7, 13)+"-0000";
			route.setCmt("defined at " + defDate + " UTC");
			
			String flightDate = line.substring(13, 19);
			int taskId = Integer.parseInt(line.substring(19, 23));
			String name = line.substring(25);
			route.setName(flightDate + " " + name);
			route.setNumber(taskId);
			gpx.getRoutes().add(route);
		} else if (line.equals(cIgnore) == false){
			/* TODO erster record = startflugplatz
			   letzer record = landeflugplatz
			   besser handhaben 
			 */
			
			double lat = parseLat(line.substring(1, 9));
			double lon = parseLon(line.substring(9, 18));
			String name = line.substring(18);
			Waypoint wpt = new Waypoint(lat, lon);
			wpt.setName(name);
			route.getPath().addWaypoint(wpt);			
		}
	}
	
	/**
	 * parse a line containing a record of type E
	 * Events
	 * converted to Waypoints
	 * @param line
	 * @throws ParseException malformed date/time string
	 */
	private void parseE(String line) throws ParseException { // UNTESTED
		Waypoint event = new Waypoint(0, 0); // Better use special IGC marker
		event.setTime(utcFormatter.parse(line.substring(1, 7)));
		event.setType("IGC:Event");
		event.setName(line.substring(8, 11));
		event.setCmt(line.substring(12));
		events.add(event);
	}

	/**
	 * parse a line containing a record of type K
	 * Events logged less frequently than E
	 * added to last read waypoint
	 * @param line
	 */
	private void parseK(String line) {
		if (wpt != null) {		
			for (IgcExtension ext : kExtensions) {
				String code = ext.getCode();
				String value = line.substring(ext.getStart(), ext.getEnd());
				wpt.getExtension().add(code, value);
			}
		}
	}
	/**
	 * Logbook
	 * 
	 * @param line
	 */
	private void parseL(String line)  {
		// not sure what to do with this record
		// since it contains neither time nor position
		System.out.println(line);
	}
	
	/**
	 * parse a line containing a record of type H 
	 * Header
	 * multiple lines
	 * put in Gpx.Extensions
	 */
	private void parseH(String line) {
		// we use only a few record types, all others are ignored
		
		if (line.startsWith("HFDTE")) { // UTC date
			utcDate = line.substring(5);
		} else if (line.startsWith("HFPLT")) { // pilot1
			pilot1 = getAfter(line, ":");
		} else if (line.startsWith("HFCM2")) { // pilot2
			pilot2 = getAfter(line, ":");
		} else if (line.startsWith("HFGTY")) { // glider type
			glider = getAfter(line, ":");
		} else if (line.startsWith("HFGID")) { // glider ID
			gliderId = getAfter(line, ":");
		} else if (line.startsWith("HFFTY")) { // logger
			gpx.setCreator(getAfter(line, ":"));
		} else if (line.startsWith("HFDTM")) { // GPS datum
			String datum = getAfter(line, ":");
			if (wgsDatum.contains(datum) == false) {
				throw new IllegalArgumentException("Unsupported GPS datum " + datum);
			}
		}
		
	}

	/**
	 * parse a line containing a record of type I:
	 * Definition of extensions to the B record
	 * Once per file
	 * @param line
	 */
	private void parseIJ(String line, List<IgcExtension> extensions) {	
		int count = Integer.parseInt(line.substring(1, 3));
		int offset = 3;
		for (int i = 0; i < count; i++) {
			String block = line.substring(offset, offset + 7);
			IgcExtension ext = new IgcExtension(block);
			extensions.add(ext);
			offset += 7;
		}
	}
	
	/**
	 * 
	 * @param latString
	 * @return
	 */
	private double parseLat(String latString) {
		int latDeg = Integer.parseInt(latString.substring(0, 2));
		double latMin = (double) Integer.parseInt(latString.substring(2, 7)) / 1000f;
		double lat = latDeg + (double) latMin / 60f;
		if (latString.endsWith("S")) {
			lat = lat * -1;
		}
		return lat;
	}
	
	/**
	 * 
	 * @param lonString
	 * @return
	 */
	private double parseLon(String lonString) {
		int lonDeg = Integer.parseInt(lonString.substring(0, 3));
		double lonMin = (double) Integer.parseInt(lonString.substring(3, 8)) / 1000f;
		double lon = lonDeg + (double) lonMin / 60f;
		if (lonString.endsWith("W")) {
			lon = lon * -1;
		}		
		return lon;
	}

	/**
	 * 
	 * @param timeString UTC time as HHMMSS
	 * Global variable utcDate has to be set.
	 * @return
	 * @throws ParseException 
	 */
	private Date parseDate(String timeString) throws ParseException {
		String utcTime = utcDate + " " + timeString + "-0000";
		return utcFormatter.parse(utcTime);
		
	}

	@Override
	public void loadCumulative(InputStream inStream) throws Exception {
		throw new UnsupportedOperationException();
	}	
	
	@Override
	public void save(GPXFile gpx, OutputStream outStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canValidate() {		
		return false;
	}

	@Override
	public void validate(InputStream inStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		file = null;
		isOpen = false;
		bExtensions.clear();
	}
	
	public void clear() {
		super.clear();
		events.clear();
		route = null;
		bExtensions.clear();
	}

}
