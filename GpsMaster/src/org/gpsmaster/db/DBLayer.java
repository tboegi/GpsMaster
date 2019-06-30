package org.gpsmaster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 
 * Some kind of OR mapper for storing GPS data in a relational database.
 * 
 * This class stores GPS data in a relational database.
 * The database is accessed via JDBC.
 * 
 * @author rfu
 * 
 * 
 */
public class DBLayer {

	private DBConfig dbConfig = null;

	private Connection connection = null;
	
	/**
	 * Constructor
	 * @param config
	 */
	public DBLayer(DBConfig config) {
		this.dbConfig = config;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isConnected() {
		
		return false;
	}
	
	/**
	 * establish connection to database. DSN has to be set.
	 * @throws SQLException 
	 */
	public void connect() throws SQLException {
		
		connection = DriverManager.getConnection(dbConfig.getDSN(), dbConfig.getUsername(), dbConfig.getPassword());
		connection.setAutoCommit(false);
		
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void disconnect() throws SQLException {
		
		connection.close();
				
	}

	/**
	 * Adds a {@link GpsEntry} as new record to the database.
	 * When successful, {@link GpsEntry}.getId() contains the
	 * new record ID
	 * @param gps
	 * @throws SQLException 
	 */
	public void addGpsEntry(GpsEntry gps) throws SQLException {
		
		// TODO get next ID 
		long id = 0;
		
		String sql = "SELECT max(id) from dat_gps";
		Statement idStmt = connection.createStatement();
		ResultSet rs = idStmt.executeQuery(sql);
		if (rs.next()) {
			id = rs.getLong(1);
		}
		rs.close();
		idStmt.close();
		
		id++;
		
		PreparedStatement stmt = null;
		sql = "INSERT INTO dat_gps(id, name, color, start_dt, end_dt, distance, duration,"
				+ "min_lat, max_lat, min_lon, max_lon, loader_class, prog_version, data, "
				+ "source_urn, user_id, compressed, entry_dt, checksum, activity)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ? ,"
				+ "?, ?, ?, ?, ?, ?, "				
				+ "?, ?, ?, ?, ?, ?, ?)";
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, id);
			stmt.setString(2, gps.getName());
			stmt.setInt(3, gps.getRgbColor());
			stmt.setDate(4, new java.sql.Date(gps.getStartTime().getTime()));
			stmt.setDate(5, new java.sql.Date(gps.getEndTime().getTime()));
			stmt.setLong(6, gps.getDistance());
			stmt.setLong(7, gps.getDuration());
			stmt.setDouble(8, gps.getMinLat());
			stmt.setDouble(9, gps.getMaxLat());
			stmt.setDouble(10, gps.getMinLon());
			stmt.setDouble(11, gps.getMaxLon());
			stmt.setString(12, gps.getLoaderClass());
			stmt.setString(13, gps.getProgVersion());
			stmt.setBytes(14, gps.getGpsData());
			stmt.setString(15, gps.getSourceUrn());
			stmt.setLong(16, gps.getUserId());
			stmt.setBoolean(17, gps.isCompressed());
			stmt.setDate(18, new java.sql.Date(gps.getEntryDate().getTime()));
			stmt.setString(19, gps.getChecksum());
			stmt.setString(20, gps.getActivity());
			stmt.execute();
			connection.commit();
			gps.setId(id);
		} catch (SQLException e) {
			connection.rollback();
			throw(e);
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		
	}
	
	/**
	 * 
	 * @param id
	 * @return {@link GpsEntry} or NULL if not found
	 */
	public GpsEntry getGpsEntry(long id) throws SQLException {
		GpsEntry gps = null;
		
		String sqlStmt = "SELECT * FROM dat_gps where id = ?";		
		PreparedStatement stmt = connection.prepareStatement(sqlStmt);
		stmt.setLong(1, id);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			gps = new GpsEntry();
			rsToGpsEntry(rs, gps);
			gps.setGpsData(rs.getBytes("data"));
		}
		
		return gps;
		
	}
	
	/**
	 * 
	 * @param id
	 * @throws SQLException 
	 */
	public void deleteGpsEntry(long id) throws SQLException {
		
		String sqlStmt = "DELETE FROM dat_gps where id = ?";
		PreparedStatement stmt = connection.prepareStatement(sqlStmt);
		stmt.setLong(1, id);
		stmt.execute();
		connection.commit();		
	}
	
	/**
	 * Get all GPS entries in table dat_gps. 
	 * Gps Data Field is not filled! 
	 * @param gpsList list to add entries to
	 * @throws SQLException 
	 */	
	public void getGpsEntries(List<GpsEntry> gpsList) throws SQLException {
		
		String sqlStmt = "SELECT * FROM dat_gps";
		PreparedStatement stmt = connection.prepareStatement(sqlStmt);		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			GpsEntry gps = new GpsEntry();
			rsToGpsEntry(rs, gps);
			gpsList.add(gps);
		}
		rs.close();
	}
		
	/*
	 * private methods
	 */

	
	/**
	 * fill {@link GpsEntry} with values from {@link ResultSet}
	 * Gps Data field is NOT filled! 
	 * @param rs
	 * @param gps
	 * @throws SQLException 
	 */
	private void rsToGpsEntry(ResultSet rs, GpsEntry gps) throws SQLException {

		gps.setId(rs.getLong("id"));
		gps.setName(rs.getString("name"));
		gps.setRgbColor(rs.getInt("color"));
		gps.setStartTime(rs.getDate("start_dt"));
		gps.setEndTime(rs.getDate("end_dt"));
		gps.setDistance(rs.getLong("distance"));
		gps.setDuration(rs.getLong("duration"));
		gps.setMinLat(rs.getDouble("min_lat"));
		gps.setMaxLat(rs.getDouble("max_lat"));
		gps.setMinLon(rs.getDouble("min_lon"));
		gps.setMaxLon(rs.getDouble("max_lon"));
		gps.setActivity(rs.getString("activity"));
		gps.setLoaderClass(rs.getString("loader_class"));
		gps.setProgVersion(rs.getString("prog_version"));
		// data not set!!
		gps.setSourceUrn(rs.getString("source_urn"));
		gps.setUserId(rs.getLong("user_id"));
		gps.setCompressed(rs.getBoolean("compressed"));
		gps.setEntryDate(rs.getDate("entry_dt"));
		gps.setChecksum(rs.getString("checksum"));

	}


}

