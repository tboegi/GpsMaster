package org.gpsmaster.device;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.online.OnlineTrack;


/**
 * GpsDevice class for loading tracks saved by Move!BikeComputer
 * via MPT
 * 
 * @author rfuegen
 *
 */
public class MoveBikeCompMPT extends MTPLoader {

	// http://code.google.com/p/jmtp/

	Connection sqlConn = null;
	
	public MoveBikeCompMPT() throws ClassNotFoundException {
		super();
		key = "MBCMPT"; // class path?
		name = "Move!BikeComputer via MPT";
		description = "Load tracks recorded with Move!BikeComputer via MTP from Android device";
		canDelete = false;
		
		Class.forName("org.sqlite.JDBC");		
	}
	
	/**
	 * establish a connection to the GPS device 
	 */
	public void connect() throws SQLException 
	{
	    if (super.isConnected == false) {
	    	
	    	if (connectionParams.containsKey("DBFILE") == false) {
	    		throw new NoSuchElementException("Connection Parameter DBFILE");
	    	}
	    	
	    	sqlConn = DriverManager.getConnection("jdbc:sqlite:"+connectionParams.get("DBFILE"));
	    	
	    	
	    	isConnected = true;
	    }
	}

	/**
	 * Returns a list of all tracks stored on the device
	 */
	public List<OnlineTrack> getTracklist() throws Exception {

		checkConnection();
		
		String trackStatement = "SELECT _id, start_time, track_name from tracks ORDER BY start_time DESC";		
		List<OnlineTrack> tracklist = new ArrayList<OnlineTrack>();
		try {
			Statement stmt = sqlConn.createStatement();
			ResultSet rs = stmt.executeQuery(trackStatement);
			while(rs.next()) {
				Integer id = rs.getInt("_ID");
				OnlineTrack entry = new OnlineTrack();
				entry.setId(id);
				entry.setDate(new Date(rs.getLong("start_time")));
				entry.setName(rs.getString("track_name"));				
				tracklist.add(entry);
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
				
		return(tracklist);
	}

	/**
	 * 
	 */
	public GPXFile load(OnlineTrack entry) throws Exception {
		
		checkConnection();
		
		GPXFile gpx = new GPXFile();
		gpx.getMetadata().setTime(entry.getDate());
		gpx.getMetadata().setName(String.format("%1$tY%1$tm%1$td_%1$tH%1$tm", entry.getDate()));
		gpx.getMetadata().setDesc(String.format("Move!BikeComputer Track #%d", entry.getId()));
		
		Track track = new Track(gpx.getColor());
		track.setNumber(1);
		track.setName(entry.getName());
		gpx.addTrack(track);
		
		WaypointGroup wptGroup = new WaypointGroup(gpx.getColor(), WaypointGroup.WptGrpType.TRACKSEG);
		track.addTrackseg(wptGroup);
		
		String trackpointStmt = "SELECT latitude, longitude, altitude, time, speed, bearing FROM track_points WHERE track_id = ? ORDER BY time";
		try {
			PreparedStatement stmt = sqlConn.prepareStatement(trackpointStmt);
			stmt.setLong(1, entry.getId());
			ResultSet rs = stmt.executeQuery(); // stmt.getResultSet();
			while(rs.next()) {
				Waypoint wpt = new Waypoint(rs.getDouble("latitude"), rs.getDouble("longitude"));
				wpt.setEle(rs.getDouble("altitude"));
				// TODO: fix MBC bug: time in local timezone, not UTC 
				long time = new Double(rs.getDouble("time")).longValue();
				wpt.setTime(new Date(time));
				wpt.setEle(rs.getDouble("altitude"));
				// extended: speed, heading
				if (getExtended) {
					wpt.getExtension().add(Const.EXT_SPEED, String.format("%.2f", rs.getDouble("speed")));
					wpt.getExtension().add(Const.EXT_HEADING, String.format("%.2f", rs.getDouble("bearing")));
				}
				wptGroup.addWaypoint(wpt);				
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
		
		
		 // Coordinated Universal Time (UTC) using ISO 8601 format.
		
		return gpx;
	}
	
	/**
	 * 
	 */
	public void disconnect() throws SQLException {
	   
		if (sqlConn != null) {
			sqlConn.close();
		}
	
		super.isConnected = false;
	}
}
