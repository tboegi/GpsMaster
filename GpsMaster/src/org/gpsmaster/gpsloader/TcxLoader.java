package org.gpsmaster.gpsloader;

import org.gpsmaster.gpxpanel.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Calendar;
import java.util.List;

/**
 * Created 19. March 2016
 * Author: Karsten Ensinger
 */
public class TcxLoader extends XmlLoader {

	private FileInputStream fis = null;
	private int currentLoadColor = 0;

	/**
	 * Constructor
	 */
	public TcxLoader() {
		super();
		isAdding = false;
		isDefault = false;
		extensions.add("tcx");
		xsdResource = "/org/gpsmaster/schema/TrainingCenterDatabasev2.xsd";
	}

	/**
	 * Open the {@link File} to read the TCX data from.
	 * @param file The {@link File} to read.
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
	 * Parse the TCX-Trackpoint.
	 * A TCX-Trackpoint transforms to a {@link Waypoint}.
	 * In case of missing longitude or latitude, no {@link Waypoint} gets returned.
	 *
	 * @param trkpt The {@link Element} to read the TCX-Trackpoint from
	 * @return The {@link Waypoint} to add to the {@link WaypointGroup}. Can be null!!
	 */
	private Waypoint parseTrackPoint(Element trkpt) {
		Waypoint wpt = null;

		Double lat = null;
		Double lon = null;

		Element position = getSubElement(trkpt, "Position");
		if (position != null) {
			Element latitude = getSubElement(position, "LatitudeDegrees");
			if (latitude != null) {
				lat = Double.parseDouble(latitude.getTextContent());
			}
			Element longitude = getSubElement(position, "LongitudeDegrees");
			if (longitude != null) {
				lon = Double.parseDouble(longitude.getTextContent());
			}
		}
		if (lat == null || lon == null) {
			return null;
		}

		wpt = new Waypoint(lat, lon);

		Element altitude = getSubElement(trkpt, "AltitudeMeters");
		if (altitude != null) {
			double alt = Double.parseDouble(altitude.getTextContent());
			wpt.setEle(alt);
		}
		Element time = getSubElement(trkpt, "Time");
		if (time != null) {
			Calendar cal = DatatypeConverter.parseDateTime(time.getTextContent());
			wpt.setTime(cal.getTime());
		}
		Element beats = getSubElement(trkpt, "HeartRateBpm");
		if (beats != null) {
			Element value = getSubElement(beats, "Value");
			if (value != null) {
				wpt.getExtension().add(beats.getNodeName(), value.getTextContent());
			}
		}

		return wpt;
	}
	
	/**
	 * Parse the TCX-Track.
	 * A TCX-Trackpoint transforms to a {@link Waypoint}.
	 *
	 * @param track The {@link Track} to add the {@link WaypointGroup} to
	 * @param trkElement The {@link Element} to read the TCX-Tracks from
	 */
	private void parseTrack(Track track, Element trkElement) {
		WaypointGroup wptGrp = track.addTrackseg();
		List<Element> tracks = getSubElementsByTagName(trkElement, "Track");
		for (Element element : tracks) {
			List<Element> trackpoints = getSubElementsByTagName(element, "Trackpoint");
			for (Element trackpoint : trackpoints) {
				Waypoint wpt = parseTrackPoint(trackpoint);
				if (wpt != null) {
					wptGrp.addWaypoint(wpt);
				}
			}
		}
	}

	/**
	 * Parses the TCX-Activity.
	 * A TCX-Lap transforms to a {@link Track} and a TCX-Track transforms to a
	 * {@link WaypointGroup}.
	 *
	 * @param activity The {@link Element} to read the TCX-Activity from
	 * @param gpx The {@link GPXFile} to add the information to
     */
	private void parseActivity(final Element activity, final GPXFile gpx) {
		final Element creator = getSubElement(activity, "Creator");
		if (creator != null) {
			final Element name = getSubElement(creator, "Name");
			if (name != null) {
				gpx.setCreator(name.getTextContent());
			}
		}
		final List<Element> laps = getSubElementsByTagName(activity, "Lap");
		for (Element lap : laps) {
			Track track = new Track(gpx.getColor(currentLoadColor++));
			track.setName(lap.getAttribute("StartTime"));
			parseTrack(track, lap);
			if (track.getTracksegs().size() > 0) {
				gpx.addTrack(track);
			}
		}
	}

	/**
	 * Checks if a {@link File} is available and passes to {@link #load(InputStream)}.
	 * @return The {@link GPXFile} to display
	 * @throws Exception
     */
	@Override
	public GPXFile load() throws Exception {
		checkOpen();
		
		return load(fis, null);
	}

	/**
	 *
	 * <Activities>
	 *     -- zero or more
	 *     <Activity>
	 *         -- one or more
	 *         <Lap>
	 *             -- zero or more
	 *             <Track>
	 *                 -- zero or more
	 *                 <Trackpoint></Trackpoint>
	 *             </Track>
	 *         </Lap>
	 *     </Activity>
	 *     -- zero or more
	 *     <MultiSportSession>
	 *         <FirstSport>
	 *             <Activity></Activity>
	 *         </FirstSport>
	 *         -- zero or more
	 *         <NextSport>
	 *             <Activity></Activity>
	 *         </NextSport>
	 *     </MultiSportSession>
	 * </Activities>
	 *
	 * @param inputStream The data to parse
	 * @return The {@link GPXFile} to display
	 * @throws Exception
     */
	public GPXFile load(InputStream inputStream, String format) throws Exception {
		gpx = new GPXFile();
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		final Document document = builder.parse(inputStream);
		document.normalize();
		final Element root = document.getDocumentElement();
		final List<Element> activitiesList = getSubElementsByTagName(root, "Activities");
		if (activitiesList.size() != 1) {
			throw new UnsupportedOperationException();
		}
		final List<Element> activities = getSubElements(activitiesList.get(0));
		for (Element activity : activities) {
			String tagName = activity.getTagName();
			if (tagName.equals("Activity")) {
				parseActivity(activity, gpx);
			}
			if (tagName.equals("MultiSportSession")) {
				final List<Element> firstSport = getSubElementsByTagName(activity, "FirstSport");
				if (firstSport.size() != 1) {
					throw new UnsupportedOperationException();
				}
				final List<Element> fsActivitiesList = getSubElementsByTagName(firstSport.get(0), "Activity");
				if (fsActivitiesList.size() != 1) {
					throw new UnsupportedOperationException();
				}
				parseActivity(fsActivitiesList.get(0), gpx);
				final List<Element> nextSport = getSubElementsByTagName(activity, "NextSport");
				for (Element ns : nextSport) {
					final List<Element> nsActivitiesList = getSubElementsByTagName(ns, "Activitiy");
					if (nsActivitiesList.size() != 1) {
						throw new UnsupportedOperationException();
					}
					parseActivity(nsActivitiesList.get(0), gpx);
				}
			}
		}
		return gpx;
	}

	/**
	 * Not implemented.
	 * @param inStream
	 * @throws Exception
     */
	@Override
	public void loadCumulative(InputStream inStream) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not implemented.
	 * @param gpx
	 * @param file
	 * @throws FileNotFoundException
     */
	public void save(GPXFile gpx, File file) throws FileNotFoundException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not implemented.
	 * @param gpx
	 * @param out
	 * @throws FileNotFoundException 
	 * 
	 */
	@Override
	public void save(GPXFile gpx, OutputStream out) {
		throw new UnsupportedOperationException();
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
