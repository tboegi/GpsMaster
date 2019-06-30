package org.gpsmaster.filehub;

import java.io.File;

import eu.fuegenstein.util.Filename;

/**
 * Transferable item representing a file in the filesystem
 * 
 * @author rfu
 *
 */
public class FileItem extends TransferableItem {


	protected Filename filename = null;	
	protected File file = null;
	
	/**
	 * 
	 * @param file
	 */
	public FileItem(File file) {
		this.file = file;
		filename = new Filename(file);
	}

	/**
	 * 
	 * @return
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * 
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * 
	 */
	public String getSourceFormat() {		
		return filename.extension();
	}

	@Override
	public void setSourceFormat(String extension) {
		throw new UnsupportedOperationException();		
	}

}
