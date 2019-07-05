package org.gpsmaster.gpsloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Waypoint;

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
	private List<IgcBExtension> bExtensions = new ArrayList<IgcBExtension>();

	private SimpleDateFormat utcFormatter = new SimpleDateFormat(); // ("aabbcc xxyyzz");


	// header fields
	private String utcDate = "010170"; // UTC date as of header record.
	private String pilot1 = "";
	private String pilot2 = "";
	private String glider = "";
	private String gliderId = "";

	// position of extension fields within B record

	/**
	 * default Constructor
	 */
	public IgcLoader() {
		isAdding = false;
		extensions.add("igc");
	}

	@Override
	public void open(File file) {
		this.file = file;
		isOpen = true;
	}

	@Override
	public GPXFile load() throws Exception {
		checkOpen();

		return load(new FileInputStream(file));
	}

	/**
	 *
	 */
	@Override
	public GPXFile load(InputStream inputStream) throws Exception {
		String line;
		gpx = new GPXFile();

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
			case 'H':
				parseH(line);
				break;
			case 'I':
				parseI(line);
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
		return string.substring(string.indexOf(sep) + 1);
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
		gpx.setCreator(manufacturer + " " + uniqueId + " " + rest);
	}

	/**
	 * parse a line containing a record of type B
	 * Fix (GPS position etc.)
	 * multiple lines
	 */
	private void parseB(String line) {
		Waypoint wpt = null;
		String utcTime = utcDate + " " + line.substring(1, 7);
		String latString = line.substring(7, 15);
		String lonString = line.substring(15, 24);
		String fixValidity = line.substring(24, 25);
		String pressAlt = line.substring(25, 30); // altitude by pressure sensor
		String gpsAlt = line.substring(30, 35); // altitude by pressure sensor


		// save whole record as extension
		wpt.getExtensions().put("igc:b", line);
	}

	/**
	 * parse a line containing a record of type H
	 * Header
	 * multiple lines
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
			if (datum.equals("WGS-1984") == false) {
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
	private void parseI(String line) {

		int count = Integer.parseInt(line.substring(1, 3));
		int offset = 3;
		for (int i = 0; i < count; i++) {
			String block = line.substring(offset, offset + 7);
			IgcBExtension bExt = new IgcBExtension(block);
			bExtensions.add(bExt);
			offset += 7;
		}
	}

	@Override
	public void loadCumulative() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(GPXFile gpx, File file) throws FileNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(GPXFile gpx, OutputStream outStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void validate() throws ValidationException, NotBoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		file = null;
		isOpen = false;
		bExtensions.clear();

	}

}
