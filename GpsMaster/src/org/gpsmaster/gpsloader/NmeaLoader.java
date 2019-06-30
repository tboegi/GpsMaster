package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;

import javax.xml.bind.ValidationException;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.WPLSentence;
import net.sf.marineapi.nmea.util.Time;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.joda.time.DateTime;

/**
 * Loader für NMEA 0183 data.
 * uses marine-api by Kimmo Tuukkanen
 *  
 * @author rfu
 *
 * http://www.gpsinformation.org/dale/nmea.htm
 * 
 * http://www.bosunsmate.org/news/article/258/Decoding-AIS-Data/
 * 
 */
public class NmeaLoader extends GpsLoader implements SentenceListener {

	// TODO define NMEA events/messages which create waypoints 
	
	private FileInputStream fis = null;
	private SentenceReader reader = null;
	
	private Waypoint wpt = null;
	private WaypointGroup trackSeg = null;
	private net.sf.marineapi.nmea.util.Date gpsDate = null;
	private boolean running = false;
		
	/**
	 * Default Constructor
	 */
	public NmeaLoader() {
		super();
		extensions.add("nmea");
		
		// required for some non-metric units in NMEA/GPS data
		
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
	public GPXFile load(InputStream inputStream, String format) throws Exception {
		gpx = new GPXFile();
		gpx.addExtensionPrefix(Const.EXT_NMEA_PRE);
		Track track = new Track(gpx.getColor());
		gpx.addTrack(track);
		trackSeg = track.addTrackseg();
				
		reader = new SentenceReader(inputStream);
		reader.addSentenceListener(this, SentenceId.GGA);
		reader.addSentenceListener(this, SentenceId.GSA);
		reader.addSentenceListener(this, SentenceId.RMC);
		reader.addSentenceListener(this, SentenceId.GLL);
		reader.addSentenceListener(this, SentenceId.VTG);
		reader.addSentenceListener(this, SentenceId.WPL);
		// reader.addSentenceListener(this, SentenceId.AAM); // not supported by lib
		reader.addSentenceListener(this, SentenceId.MTW); // TBI water temperature
		reader.addSentenceListener(this, SentenceId.DPT); // TBI depth
		reader.addSentenceListener(this, SentenceId.DBT); // TBI depth below transducer
		
		running = true;
		reader.start();
		while (running) {
			Thread.sleep(1000, 0);
			
		}
		
		return gpx;
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

	@Override
	public void readingPaused() {
		// TODO Auto-generated method stub
		running = false;
		System.out.println("-- Paused --");
		
	}

	@Override
	public void readingStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readingStopped() {
		// TODO Auto-generated method stub
		running = false;
		System.out.println("-- Stopped --");
	}

	/**
	 * NMEA Sentence Dispatcher
	 */
	@Override
	public void sentenceRead(SentenceEvent event) {
		Sentence s = event.getSentence();
		String id = s.getSentenceId();

		System.out.println(s.toString());
		if (id.equals("GGA")) {
			procSentence((GGASentence) event.getSentence());
		} else if (id.equals("GSA")) {			
			procSentence((GSASentence) event.getSentence());
		} else if (id.equals("RMC")) {			
			procSentence((RMCSentence) event.getSentence());
		} else if (id.equals("GLL")) {			
			// position again. redundant with GGA?
			
		} else if (id.equals("WPL")) {
			procSentence((WPLSentence) event.getSentence());
		} else if (id.equals("AAM")) {
			// waypoint arrival alarm
			// use lat/lon of current wpt 
		}
		
	}
	
	/**
	 * Global Positioning System Fixed Data
	 * @param gga
	 */
	private void procSentence(GGASentence gga) {
		wpt = new Waypoint(gga.getPosition().getLatitude(), gga.getPosition().getLongitude());
		wpt.setEle(gga.getAltitude());
		
		if (gpsDate != null) {
			// TODO bug: first trackpoint doesn't get a UTC date
			Time time = gga.getTime(); 
			DateTime utcDate = new DateTime(gpsDate.getYear(), gpsDate.getMonth(), gpsDate.getDay(),
					time.getHour(), time.getMinutes(), (int) time.getSeconds());				
			wpt.setTime(utcDate.toDate());
			trackSeg.addWaypoint(wpt);
		}
		wpt.setHdop(gga.getHorizontalDOP()); // red
		wpt.setSat(gga.getSatelliteCount());
		wpt.setGeoidheight(gga.getGeoidalHeight());		
		switch(gga.getFixQuality()) {
			case NORMAL:
				wpt.setFix("sps");  // standard position service 
				break;
			default:
				wpt.setFix(gga.getFixQuality().toString().toLowerCase());  // TODO
				break;
		}
		
	}
	
	/**
	 * GNSS DOP and Active Satellites
	 * @param gsa
	 */
	private void procSentence(GSASentence gsa) {
		try {
			wpt.setHdop(gsa.getHorizontalDOP());
		} catch (DataNotAvailableException e) {}
		try {
			wpt.setVdop(gsa.getVerticalDOP());
		} catch (DataNotAvailableException e) {}
		try {
			wpt.setPdop(gsa.getPositionDOP());	
		} catch (DataNotAvailableException e) {}
	}
	
	/**
	 * Recommended Minimum Specific GNSS Data
	 * @param rmc
	 */
	private void procSentence(RMCSentence rmc) {
		try {
			wpt.setMagvar(rmc.getVariation());
		} catch (DataNotAvailableException e) {}
		try {
			wpt.getExtension().add(Const.EXT_HEADING, Double.toString(rmc.getCorrectedCourse()));
		} catch (DataNotAvailableException e) {}
		try {
			wpt.getExtension().add(Const.EXT_SPEED, Double.toString(rmc.getSpeed())); // in knots!!
		} catch (DataNotAvailableException e) {}
		gpsDate = rmc.getDate();					
	}
	
	/**
	 * Waypoint Location Information
	 * @param wpl
	 */
	private void procSentence(WPLSentence wpl) {
		Waypoint waypoint = new Waypoint(wpl.getWaypoint().getLatitude(), wpl.getWaypoint().getLongitude());
		waypoint.setName(wpl.getWaypoint().getDescription());
		gpx.getWaypointGroup().addWaypoint(waypoint);

	}
}
