package org.gpsmaster.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gpsmaster.filehub.DataType;
import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.IItemTarget;
import org.gpsmaster.filehub.TransferableItem;
import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Target storing {@link TransferableItem}s to a relational database
 * @author rfu
 *
 * TODO konzeptionelle probleme: 
 *  -	record ID geht beim laden aus DB verloren, daher kann auch kein UPDATE auf bestehenden record gemacht werden
 *  - 	woher file format / extension für GPX to stream in {@link FileHub} holen? 
 * 
 */
public class DbTarget implements IItemTarget {

	protected DbLayer dbLayer = null;
	protected TransferableItem currentItem =null;
	protected GpsRecord gpsRecord = null;
	protected ByteArrayOutputStream outStream = null;
	protected ZipOutputStream zipOut = null;
	protected boolean enabled = false;
	
	/**
	 * Constructor 
	 * @param dbLayer Database Layer, already connected to database 
	 */
	public DbTarget(DbLayer dbLayer) {
		this.dbLayer = dbLayer;
	}
	
	@Override
	public String getName() {
		return "GPS Database";
	}

	@Override
	public String getDescription() {
		return "Store in GPS Database";
	}
	
	/**
	 * 
	 */
	public DataType getDataType() {
		return DataType.STREAMGPX;
	}

	@Override
	public boolean doShowProgressText() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * 
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * {@link GPXFile} is required for metadata
	 * {@link TransferableItem} is ignored; has to be set via open() before
	 */
	public void addGpxFile(GPXFile gpxFile, TransferableItem item) {
		gpxFile.updateAllProperties();
		gpsRecord = new GpsRecord(gpxFile);		
		gpsRecord.setLoaderClassName(currentItem.getLoaderClassName());
		gpsRecord.setSourceFormat(currentItem.getSourceFormat());
		gpsRecord.setName(currentItem.getName());
		gpsRecord.setId(gpxFile.getDbId());
	}

	/**
	 * 
	 */
	public void open(TransferableItem transferableItem) throws Exception {
		currentItem = transferableItem;
		
	}

	/**
	 * TODO get format from DB record if it already exists in database
	 */
	public String getRequiredFormat() {
		return "gpx";
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		outStream = new ByteArrayOutputStream();
		if (dbLayer.useCompression()) {
			gpsRecord.setCompressed(true);
			zipOut = new ZipOutputStream(outStream);
			zipOut.putNextEntry(new ZipEntry(gpsRecord.getName()));
		}
		return outStream;
	}

	/**
	 * 
	 */
	public void close() throws Exception {
		// check for insert or update
		if (gpsRecord.getId() == -1) {
			dbLayer.addGpsRecord(gpsRecord, outStream.toByteArray());
		} else {
			dbLayer.updateGpsRecord(gpsRecord, outStream.toByteArray());
		}
		if (zipOut != null) {
			zipOut.closeEntry();
			zipOut.close();
		}
		outStream.close();
		
		currentItem = null;
		gpsRecord = null;
		zipOut = null;
		outStream = null;
		
		// TODO Fire Const.PCE_REFRESHDB event
		
	}

}
