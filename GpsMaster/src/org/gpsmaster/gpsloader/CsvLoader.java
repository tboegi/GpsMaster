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
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import net.sf.marineapi.nmea.util.Waypoint;

/**
 *
 * first line is required to hold column (field) names
 *
 * @author rfu
 *
 */
public class CsvLoader extends GpsLoader {

    private static final boolean debug = false;
    private int datIdx = -1;
    private int timIdx = -1;
    private int latIdx = -1;
    private int lonIdx = -1;
    private int altIdx = -1;

    // tmp
    private int devIdx = -1;
    private int ouiIdx = -1;
    private int macIdx = -1;
    private int heightIdx = -1;
    private int headingIdx = -1;
    private int speedIdx = -1;
    private int tagIdx = -1;

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
        org.gpsmaster.gpxpanel.WaypointGroup trackSeg = track.addTrackseg();

        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

        // read first line and build index assignments
        line = br.readLine();
        String[] fields = line.split(",");
        for (int i = 0; i < fields.length; i++) {
            String fieldLowerCase = fields[i].toLowerCase();
            if (debug) {
                System.out.println("CsvLoader: fieldLowerCase[" + i + "]=" + "'" + fieldLowerCase + "'");
            }
            if (fieldLowerCase.equals("date")) {
                datIdx = i;
            } else if (fieldLowerCase.equals("time")) {
                timIdx = i;
            } else if (fieldLowerCase.equals("latitude")) {
                latIdx = i;
            } else if (fieldLowerCase.startsWith("latitude ")) {
                latIdx = i;
            }  else if (fieldLowerCase.equals("longitude")) {
                lonIdx = i;
            } else if (fieldLowerCase.startsWith("longitude ")) {
                lonIdx = i;
            } else if (fieldLowerCase.equals("altitude")) {
                altIdx = i;
            } else if (fields[i].equals("deviceName")) {
                devIdx = i;
            } else if (fieldLowerCase.equals("oui_name")) {
                ouiIdx = i;
            } else  if (fieldLowerCase.equals("deviceAddress")) {
                macIdx = i;
            } else  if (fieldLowerCase.equals("height")) {
                heightIdx = i;
            } else  if (fieldLowerCase.equals("heading")) {
                headingIdx = i;
            } else  if (fieldLowerCase.equals("speed")) {
                speedIdx = i;
            } else  if (fieldLowerCase.equals("tag")) {
                tagIdx = i;
            }
        }
        if (debug) {
            System.out.println("CsvLoader: heightIdx=" + heightIdx
                               + " headingIdx=" + headingIdx
                               + " speedIdx=" + speedIdx
                               + " tagIdx=" + tagIdx);
        }

        if ((latIdx == -1) || (lonIdx == -1)) {
            throw new Exception("missing lat/lon column name"); // find more suitable exception
        }
        boolean bad_date_time = false;
        while ((line = br.readLine()) != null) {
            line = line.toLowerCase();
            if (debug) {
                System.out.println("CsvLoader: line toLowerCase=" + "'" + line + "'");
            }
            fields = line.split(",");
            String latStr = fields[latIdx];
            String lonStr = fields[lonIdx];
            boolean latSouth = false;
            boolean lonWest = false;
            if (latStr.endsWith("n")) {
                int len = latStr.length();
                latStr = latStr.substring(0, len - 1);
            } else if (latStr.endsWith("s")) {
                int len = latStr.length();
                latStr = latStr.substring(0, len - 1);
                latSouth = true;
            }
            if (lonStr.endsWith("e")) {
                int len = lonStr.length();
                lonStr = lonStr.substring(0, len - 1);
            } else if (lonStr.endsWith("w")) {
                int len = lonStr.length();
                lonStr = lonStr.substring(0, len - 1);
                lonWest = true;
            }
            double lat = Double.parseDouble(latStr);
            if (latSouth) lat = 0.0 - lat;
            double lon = Double.parseDouble(lonStr);
            if (lonWest) lon = 0.0 - lon;
            if (debug) {
                System.out.println("CsvLoader: latStr=" + latStr + " lat=" + lat + "lonStr=" + lonStr + " lon=" + lon);
            }

            wpt = new WaypointMarker(lat, lon);

            String name = "";
            String dat = null;
            String tim = null;
            String tag = null;

            if (devIdx != -1) {
                name += fields[devIdx];
            }
            if (ouiIdx != -1) {
                name += " " + fields[ouiIdx];
            }
            if (headingIdx != -1) {
                wpt.getExtension().add(org.gpsmaster.Const.EXT_HEADING, fields[headingIdx]);
            }
            if (heightIdx != -1) {
                wpt.setEle(Double.parseDouble(fields[heightIdx]));
            }
            if (speedIdx != -1) {
                wpt.getExtension().add(org.gpsmaster.Const.EXT_SPEED, fields[speedIdx]);
            }
            if (datIdx != -1) {
                dat = fields[datIdx];
            }
            if (timIdx != -1) {
                tim = fields[timIdx];
            }
            if (tagIdx != -1) {
                tag = fields[tagIdx];
            }
            wpt.setName(name);
            if (debug) {
                System.out.println("CsvLoader: dat=" + "'" + (dat != null ? dat : "null")
                                   + "' tim='" + (tim != null ? tim : "null") + "'");
            }
            if (dat != null && tim != null) {
                if (dat.length() == 6 && tim.length() == 6) {
                    // dat="240518" tim="142319"
                    String date_time = "20" + dat.substring(0,2) + "-"
                        + dat.substring(2,4) + "-" + dat.substring(4,6) + "T"
                        + tim.substring(0,2) + ":"
                        + tim.substring(2,4) + ":" + tim.substring(4,6) + "Z";
                    //2024-05-18T14:23:19Z
                    DateTime dt = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(date_time);
                    if (debug) {
                        System.out.println("CsvLoader: date_time='" + date_time
                                           + "' dt=" + dt);
                    }
                    wpt.setTime(dt.toDate());
                } else if (!bad_date_time) {
                    bad_date_time = true;
                    System.out.println("CsvLoader: illegal dat='" + dat
                                       + "' tim='" + tim + "'");
                }
            }
            if (debug) {
                System.out.println("CsvLoader: tag='"
                                   + ((tag != null) ? tag : "null") + "'");
            }

            // lowercase 'c' or 'd'
            if ((tag != null) && (tag.equals("c") || tag.equals("d"))) {
                gpx.getWaypointGroup().addWaypoint(wpt);
            } else {
                trackSeg.addWaypoint(wpt);
            }
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

}
