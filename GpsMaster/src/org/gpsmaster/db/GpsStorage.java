package org.gpsmaster.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpsloader.GpsLoader;
import org.gpsmaster.gpsloader.GpxLoader;
import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Persistent storage of GPS data.
 * 
 * Whenever possible, GPS data is stored in raw format, as
 * delivered from a file or web service ({@link InputStream}). 
 * Additionally, the resulting {@link GPXFile} is needed to
 * provide GPS metadata, to be presented to the user.  
 *
 * @author rfu
 *
 */
public class GpsStorage {

	private DbLayer dbLayer = null;
	private DBConfig dbConfig = null;
	
	/**
	 * Constructor
	 * @param dbConfig
	 */
	public GpsStorage(DBConfig dbConfig) {
		dbLayer = new DbLayer(dbConfig);
		this.dbConfig = dbConfig;
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	public void connect() throws SQLException {
		dbLayer.connect();
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void disconnect() throws SQLException {
		dbLayer.disconnect();
	}
	
	/**
	 * 
	 * @param os
	 * @param loader
	 * @return
	 */
	public long add(OutputStream outStream, GpsLoader loader, GPXFile gpx) {
		
		long recordId = 0;
		MessageDigest md = null;
		GpsEntry gpsEntry = new GpsEntry();
		
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// should not happen
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		gpsEntry.setGpsData(outStream.toByteArray());
		gpsEntry.setLoaderClass(gpxLoader.getClass().getCanonicalName());
		gpsEntry.setProgVersion(GpsMaster.ME);
		DigestOutputStream dos = new DigestOutputStream(os, md);
		
		String md5 = new String(md.digest());
		dbEntry.setChecksum(md5);
		*/
		
		return recordId;
	}
	
	
	/**
	 * Add a new {@link GPXFile} to the database.
	 *  
	 * after return, {@link GPXFile}.getDbId() contains new record ID. 
	 * @param gpx
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public void add(GPXFile gpx) throws SQLException, IOException {
				
		GpsEntry gpsEntry = new GpsEntry(gpx);

		gpxToEntry(gpx, gpsEntry);
		dbLayer.addGpsEntry(gpsEntry);		
		gpx.setDbId(gpsEntry.getId());		

	}

	/**
	 * Update an existing {@link GpsEntry} in DB.
	 * {@link GPXFile}.DbId has to be set
	 * @param gpx
	 * @throws SQLException
	 * @throws IOException
	 */
	public void update(GPXFile gpx) throws SQLException, IOException {
		
		GpsEntry gpsEntry = new GpsEntry(gpx);

		if (gpx.getDbId() == -1) {
			throw new IllegalArgumentException("Database ID not set.");
		}
		gpxToEntry(gpx, gpsEntry);
		gpsEntry.setId(gpx.getDbId());
		dbLayer.updateGpsEntry(gpsEntry);				
	}

	/**
	 * Get a {@link GPXFile} from the database
	 * @param id of the GPX file to get
	 * @return {@link GPXFile} or null if record with given id does not exist 
	 * @throws Exception
	 */
	public GPXFile get(long id) throws Exception {
		GPXFile gpx = null;
		GpsLoader loader = null;
		ByteArrayInputStream inStream = null;
		
		GpsEntry gpsEntry = dbLayer.getGpsEntry(id);
		if (gpsEntry != null) {
			Class loaderClass = Class.forName(gpsEntry.getLoaderClass());
			loader = (GpsLoader) loaderClass.newInstance();
			inStream = new ByteArrayInputStream(gpsEntry.getGpsData());
			
			if (gpsEntry.isCompressed()) {
				ZipInputStream zis = new ZipInputStream(inStream);
				zis.getNextEntry();					 
			} 
			gpx = loader.load(inStream);
			inStream.close();				
			gpx.setDbId(gpsEntry.getId());
		}
		
		return gpx;
	}
	
	/**
	 * Delete a GPS record from the database
	 * 
	 * @param id
	 * @throws SQLException 
	 */
	public void delete(long id) throws SQLException {
		dbLayer.deleteGpsEntry(id);
	}
	
	/**
	 * 
	 * @param entries
	 * @throws SQLException 
	 */
	public void getEntries(List<GpsEntry> entries) throws SQLException {
		dbLayer.getGpsEntries(entries);
	}
	
	/**
	 * Save {@link GPXFile} as binary data into GpsEntry
	 * @param gpx
	 * @param entry
	 * @throws IOException 
	 */
	private void gpxToEntry(GPXFile gpx, GpsEntry gpsEntry) throws IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ZipOutputStream zipOut = null;				
		MessageDigest md = null;
		
		GpxLoader gpxLoader = new GpxLoader();			

		try {
			md = MessageDigest.getInstance("MD5");
			DigestOutputStream dos = new DigestOutputStream(outStream, md);
			
		} catch (NoSuchAlgorithmException e) {
			// should not happen
			e.printStackTrace();
		}
		
		if (dbConfig.isCompression()) {
			zipOut = new ZipOutputStream(outStream);
			zipOut.putNextEntry(new ZipEntry("gpsdata"));							
		} 
					
		gpxLoader.save(gpx, outStream);
		// String md5 = new String(md.digest());
		gpsEntry.setGpsData(outStream.toByteArray());
		gpsEntry.setCompressed(dbConfig.isCompression());		
		gpsEntry.setLoaderClass(gpxLoader.getClass().getCanonicalName());
		gpsEntry.setProgVersion(GpsMaster.ME);
		// gpsEntry.setChecksum(md.digest()); // TODO does not work
		outStream.close();
		if (dbConfig.isCompression()) {
			zipOut.closeEntry();
			zipOut.close();
		}
		
	}
}
