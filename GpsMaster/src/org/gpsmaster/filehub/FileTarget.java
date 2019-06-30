package org.gpsmaster.filehub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.gpsmaster.gpxpanel.GPXFile;

/**
 * A target for writing to the filesystem.
 * Supports {@link DataType.STREAM}
 * Custom parameter: Directory
 * 
 * TODO escape / replace characters not allowed in filename  
 * @author rfu
 *
 */
public class FileTarget implements IItemTarget {

	private String directory = null;
	protected TransferableItem item = null;
	protected FileOutputStream fos = null;
	protected boolean enabled = true;
	
	@Override
	public String getName() {		
		return "Filesystem";
	}

	@Override
	public String getDescription() {		
		return "Save to Filesystem";
	}
	
	public DataType getDataType() {		
		return DataType.STREAM;
	}
	
	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/***
	 * usually writing to filesystem doesn't take long.
	 * no need to show progress text
	 */
	public boolean doShowProgressText() {
		return false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * get required format
	 */
	public String getRequiredFormat() {
		
		return null; // all formats are accepted
	}

	public void addGpxFile(GPXFile gpxFile, TransferableItem item) {
		throw new UnsupportedOperationException();
		
	}

	/**
	 * @throws Exception
	 * 
	 */
	public void open(TransferableItem transferableItem) throws Exception {
		item = (TransferableItem) transferableItem;
		String filePath = "";
		if (directory != null) {
			filePath = directory;
		}
		String filename = item.getName();
		String ext = item.getSourceFormat();
		if (ext != null) {
			if (!filename.endsWith(ext)) {
				filename = filename + "." + ext;
			}
		}
		fos = new FileOutputStream(filePath + File.separator + filename);		
	}

	/**
	 * 
	 */
	public OutputStream getOutputStream() {
		return fos;
	}

	/**
	 * 
	 */
	public void close() throws Exception {
		
		item = null;
		if (fos != null) {
			fos.flush();
			fos.close();
			fos = null;
		}
	}

}
