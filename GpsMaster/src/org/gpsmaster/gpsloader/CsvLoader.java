package org.gpsmaster.gpsloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.rmi.NotBoundException;

import javax.xml.bind.ValidationException;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.marker.WaypointMarker;

import net.sf.marineapi.nmea.util.Waypoint;

/**
 * 
 * first line is required to hold column (field) names
 * 
 * @author rfu
 *
 */
public class CsvLoader extends GpsLoader {

	private int latIdx = -1;
	private int lonIdx = -1;
	private int altIdx = -1;
	
	// tmp
	private int devIdx = -1;
	private int ouiIdx = -1;
	private int macIdx = -1;
	
	private WaypointMarker wpt = null;
	
	public CsvLoader() {
		super();
		isAdding = false;
		isDefault = false;
		extensions.add("csv");
	}
	
	@Override
	public GPXFile load(InputStream inStream, String format) throws Exception {
		
		String line = null;
		GPXFile gpx = new GPXFile();
		Track track = new Track(gpx.getColor());
		gpx.addTrack(track);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		
		// read first line and build index assignments
		line = br.readLine();
		String[] fields = line.split(",");
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals("latitude")) {
				latIdx = i;
			}
			if (fields[i].equals("longitude")) {
				lonIdx = i;
			}
			if (fields[i].equals("altitude")) {
				altIdx = i;
			}
			
			// tmp
			if (fields[i].equals("deviceName")) {
				devIdx = i;
			}
			if (fields[i].equals("oui_name")) {
				ouiIdx = i;
			}
			if (fields[i].equals("deviceAddress")) {
				macIdx = i;
			}

			
		}
		
		if ((latIdx == -1) || (lonIdx == -1)) {
			throw new Exception("missing lat/lon column name"); // find more suitable exception
		}
		
		while ((line = br.readLine()) != null) {
			fields = line.split(",");
			
			double lat = Double.parseDouble(fields[latIdx]);
			double lon = Double.parseDouble(fields[lonIdx]);
			
			wpt = new WaypointMarker(lat, lon);
			
			String name = "";
			
			if (devIdx != -1) {
				name += fields[devIdx];
			}
			if (ouiIdx != -1) {
				name += " " + fields[ouiIdx];
			}
			
			wpt.setName(name);
			gpx.getWaypointGroup().addWaypoint(wpt);
		}
					
		return gpx;
	}

	@Override
	public void loadCumulative(InputStream inStream) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(GPXFile gpx, OutputStream outStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void validate(InputStream inStream) throws ValidationException, NotBoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
