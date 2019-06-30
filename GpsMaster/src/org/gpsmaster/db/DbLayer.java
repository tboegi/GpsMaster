package org.gpsmaster.db;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import eu.fuegenstein.util.NamedParameterStatement;

/**
 * This class persistently stores GPS data in a relational database.
 * The database is accessed via JDBC.
 *
 * Internally, a byte[] is used to store & transfer GPS data, since not
 * all JDBC drivers support BLOBs and input/output streams (Sqlite!)
 *
 * long term goal is to use only BLOBs internally.
 *  
 * @author rfu
 */
public class DbLayer {

	private DBConfig dbConfig = null;
	private Connection connection = null;
	
	/**
	 * Constructor
	 * @param config
	 */
	public DbLayer(DBConfig config) {
		this.dbConfig = config;
	}
	
	/**
	 * get connection state (opened or closed)
	 * @return true if connection is open, false otherwise
	 */
	public boolean isConnected() {		
		return false;
	}

	/**
	 * Get if compression is preferred (as defined in {@link DBConfig}.
	 * It is up to the caller to perform the compression. To check if
	 * data in a {@link GpsRecord} is actually compressed, call {@link GpsRecord}.isCompressed()
	 * This class does not perform compression or decompression.
	 * 
	 * TODO transparently handle compression in this class as soon as all
	 * JDBC drivers support BLOBS / streams 
	 * @return true if compression is preferred
	 */
	public boolean useCompression() {
		return dbConfig.isCompression();
	}
	/**
	 * 
	 * @return
	 */
	public Connection getConnection() {
		return connection;
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
	 * 
	 * @param gps
	 * @throws SQLException 
	 */
	public void updateGpsRecord(GpsRecord gps, byte[] data) throws SQLException {
		
		String sql = "UPDATE dat_gps SET "
				+ "name = :name, color = :color, start_dt = start_dt, end_dt = end_dt, "
				+ "distance = :distance, duration = :duration, "
				+ "min_lat = :min_lat, max_lat = :max_lat, min_lon = :min_lon, max_lon = :max_lon,"
				+ "loader_class = :loader_class, prog_version = :prog_version, data = :data, "
				+ "source_urn = :source_urn, user_id = :user_id, compressed = :compressed, "
				+ "entry_dt = :entry_dt, checksum = :checksum, activity = :activity, fileformat = :format"
				+ " WHERE ID = :id";
		
		NamedParameterStatement stmt = new NamedParameterStatement(connection, sql);		
		try {			
			stmt.setLong("id", gps.getId());
			// entry_dt = now()? versioning?
			itemToStmt(gps, stmt, data);
			stmt.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw(e);
		} finally {
			if (stmt != null) { stmt.close(); }
		}		
	}
	
	/**
	 * Adds a {@link GpsRecord} as new record to the database.
	 * When successful, {@link GpsRecord}.getId() contains the
	 * new record ID
	 * @param gps
	 * @throws SQLException 
	 */
	public void addGpsRecord(GpsRecord gps, byte[] data) throws SQLException {
		
		long id = 0;
		// begin transaction
		String sql = "SELECT max(id) from dat_gps";
		Statement idStmt = connection.createStatement();
		ResultSet rs = idStmt.executeQuery(sql);
		if (rs.next()) {
			id = rs.getLong(1);
		}
		rs.close();
		idStmt.close();
		
		id++;
						
		sql = "INSERT INTO dat_gps(id, name, color, start_dt, end_dt, distance, duration,"
				+ "min_lat, max_lat, min_lon, max_lon, loader_class, prog_version, data, "
				+ "source_urn, user_id, compressed, entry_dt, checksum, activity, fileformat)"
				+ " VALUES (:id, :name, :color, :start_dt, :end_dt, :distance, :duration, "
				+ ":min_lat, :max_lat, :min_lon, :max_lon, :loader_class, :prog_version, :data, "				
				+ ":source_urn, :user_id, :compressed, :entry_dt, :checksum, :activity, :fileformat)";
		NamedParameterStatement stmt = new NamedParameterStatement(connection, sql);
		try {			
			stmt.setLong("id", id);
			itemToStmt(gps, stmt, data);
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
	 * Get {@link GpsRecord} from database.
	 * ATTENTION!! Does NOT read GPS data!!
	 * 
	 * @param id
	 * @return {@link GpsRecord} or NULL if not found
	 */
	public GpsRecord getGpsRecord(long id) throws SQLException {
		GpsRecord gpsRecord = null;
		
		String sqlStmt = "SELECT * FROM dat_gps where id = ?";		
		PreparedStatement stmt = connection.prepareStatement(sqlStmt);
		stmt.setLong(1, id);
		
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			gpsRecord = new GpsRecord();
			rsToItem(rs, gpsRecord);
		}
		
		return gpsRecord;		
	}
	
	/**
	 * 
	 * @param id
	 * @throws SQLException 
	 */
	public void deleteGpsRecord(long id) throws SQLException {
		
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
	public void getGpsRecords(List<GpsRecord> gpsList) throws SQLException {
		
		// required column names explicitly listed to prevent loading [data].
		// [data] is retrieved by getGpsData()
		String sqlStmt = "SELECT id, name, color, start_dt, end_dt, distance, duration, " 
				+ "min_lat, max_lat, min_lon, max_lon, activity, loader_class, fileformat, " 
				+ " prog_version, source_urn, user_id, compressed, entry_dt, checksum "
				+ " FROM dat_gps";
		PreparedStatement stmt = connection.prepareStatement(sqlStmt);		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			GpsRecord gps = new GpsRecord();
			rsToItem(rs, gps);
			gpsList.add(gps);
		}
		rs.close();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public InputStream getGpsData(long id) throws SQLException {
		ByteArrayInputStream inStream = null;

		String sqlStmt = "SELECT data FROM dat_gps where id = ?";		
		PreparedStatement stmt = connection.prepareStatement(sqlStmt);
		stmt.setLong(1, id);
		
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			inStream = new ByteArrayInputStream(rs.getBytes("data"));
		}
		return inStream;
	}
			
	/**
	 * fill {@link GpsRecord} with values from {@link ResultSet}
	 * Gps Data field is NOT filled! 
	 * @param rs
	 * @param gps
	 * @throws SQLException 
	 */
	private void rsToItem(ResultSet rs, GpsRecord gps) throws SQLException {

		gps.setId(rs.getLong("id"));
		gps.setName(rs.getString("name"));
		gps.setRgbColor(rs.getInt("color"));
		gps.setStartTime(rs.getTimestamp("start_dt"));
		gps.setEndTime(rs.getTimestamp("end_dt"));
		gps.setDistance(rs.getLong("distance"));
		gps.setDuration(rs.getLong("duration"));
		gps.getBounds().setMinlat(new BigDecimal(rs.getDouble("min_lat")));
		gps.getBounds().setMaxlat(new BigDecimal(rs.getDouble("max_lat")));
		gps.getBounds().setMinlon(new BigDecimal(rs.getDouble("min_lon")));
		gps.getBounds().setMaxlon(new BigDecimal(rs.getDouble("max_lon")));
		gps.setActivity(rs.getString("activity"));
		gps.setLoaderClassName(rs.getString("loader_class"));
		gps.setSourceFormat(rs.getString("fileformat"));
		gps.setProgVersion(rs.getString("prog_version"));
		// data not set!!
		gps.setSourceUrn(rs.getString("source_urn"));
		gps.setUserId(rs.getLong("user_id"));
		gps.setCompressed(rs.getBoolean("compressed"));
		gps.setEntryDate(rs.getDate("entry_dt"));
		gps.setChecksum(rs.getString("checksum"));

	}
	
	/**
	 * Fill a {@link NamedParameterStatement} with values from a {@link GpsRecord} (table dat_gps)
	 * (for INSERT / UPDATE statements)
	 * ATTENTION!! ID is not set!
	 * @param gps
	 * @param stmt
	 * @param data
	 * @throws SQLException
	 */
	private void itemToStmt(GpsRecord gps, NamedParameterStatement stmt, byte[] data) throws SQLException {
		stmt.setString("name", gps.getName());
		stmt.setInt("color", gps.getRgbColor());
		if (gps.getStartTime() != null) {
			stmt.setTimestamp("start_dt", new java.sql.Timestamp(gps.getStartTime().getTime()));
		}
		if (gps.getEndTime() != null) {
			stmt.setTimestamp("end_dt", new java.sql.Timestamp(gps.getEndTime().getTime()));
		}
		stmt.setLong("distance", gps.getDistance());
		stmt.setLong("duration", gps.getDuration());
		stmt.setDouble("min_lat", gps.getBounds().getMinlat().doubleValue());
		stmt.setDouble("max_lat", gps.getBounds().getMaxlat().doubleValue());
		stmt.setDouble("min_lon", gps.getBounds().getMinlon().doubleValue());
		stmt.setDouble("max_lon", gps.getBounds().getMaxlon().doubleValue());
		stmt.setString("loader_class", gps.getLoaderClassName());
		stmt.setString("prog_version", gps.getProgVersion());
		stmt.setBytes("data", data);
		stmt.setString("source_urn", gps.getSourceUrn());
		stmt.setLong("user_id", gps.getUserId());
		stmt.setBoolean("compressed", gps.isCompressed());
		stmt.setDate("entry_dt", new java.sql.Date(gps.getEntryDate().getTime()));
		stmt.setString("checksum", gps.getChecksum());
		stmt.setString("activity", gps.getActivity());
		stmt.setString("fileformat", gps.getSourceFormat());
	}
}
