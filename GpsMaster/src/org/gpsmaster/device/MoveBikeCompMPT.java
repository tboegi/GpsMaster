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

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;


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
		description = "Load tracks recorded with Move!BikeComputer via MTP from an Android device";
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
	public List<DeviceTrack> getTracklist() throws Exception {

		checkConnection();
		
		String trackStatement = "SELECT _id, start_time, track_name from tracks ORDER BY start_time DESC";		
		List<DeviceTrack> tracklist = new ArrayList<DeviceTrack>();
		try {
			Statement stmt = sqlConn.createStatement();
			ResultSet rs = stmt.executeQuery(trackStatement);
			while(rs.next()) {
				Integer id = rs.getInt("_ID");
				DeviceTrack entry = new DeviceTrack(id);				
				entry.SetDate(new Date(rs.getLong("start_time")));
				entry.SetName(rs.getString("track_name"));				
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
	public GPXFile load(DeviceTrack entry) throws Exception {
		
		checkConnection();
		
		GPXFile gpx = new GPXFile();
		gpx.getMetadata().setTime(entry.GetDate());
		gpx.getMetadata().setName(String.format("%1$tY%1$tm%1$td_%1$tH%1$tm", entry.GetDate()));
		gpx.getMetadata().setDesc(String.format("Move!BikeComputer Track #%d", entry.GetId()));
		
		Track track = new Track(gpx.getColor());
		track.setNumber(1);
		track.setName("Track #1");
		gpx.getTracks().add(track);
		
		WaypointGroup wptGroup = new WaypointGroup(gpx.getColor(), WaypointGroup.WptGrpType.TRACKSEG);
		track.getTracksegs().add(wptGroup);
		
		String trackpointStmt = "SELECT latitude, longitude, altitude, time, speed, bearing FROM track_points WHERE track_id = ? ORDER BY time";
		try {
			PreparedStatement stmt = sqlConn.prepareStatement(trackpointStmt);
			stmt.setInt(1, entry.GetId());
			ResultSet rs = stmt.executeQuery(); // stmt.getResultSet();
			while(rs.next()) {
				Waypoint wpt = new Waypoint(rs.getDouble("latitude"), rs.getDouble("longitude"));
				wpt.setEle(rs.getDouble("altitude"));
				long time = new Double(rs.getDouble("time")).longValue();
				wpt.setTime(new Date(time));
			
				// extended: speed, heading
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
