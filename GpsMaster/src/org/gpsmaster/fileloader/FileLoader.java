package org.gpsmaster.fileloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.ValidationException;



import org.gpsmaster.gpxpanel.GPXFile;
import org.xml.sax.SAXException;


/**
 * base class for file format specific loader classes
 * 
 * @author rfu
 *
 */
public abstract class FileLoader {

	protected boolean isDefault = false;
	protected boolean isOpen = false;
	protected File file = null;
	protected GPXFile gpx = null;

	protected List<String> extensions = new ArrayList<String>();
	protected List<GPXFile> gpxFiles = new ArrayList<GPXFile>();
	
	/**
	 * Constructor
	 */
	public FileLoader() {
		
	}
	
	/**
	 * Gets all GPX files loaded via loadCumulative() so far.
	 * @return
	 */
	public List<GPXFile> getFiles() {
		return gpxFiles;
	}
	
	/**
	 * 
	 * @param file
	 */
	public abstract void open(File file);
	
	/**
	 * 
	 * @param file
	 * @throws Exception 
	 */
	public abstract GPXFile load() throws Exception;
	
	/**
	 * Load current file and keep it internally. Use {@link getFiles()} to
	 * retrieve all files loaded so far.
	 */
	public abstract void loadCumulative();
	
	/**
	 * 
	 * @param gpx
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public abstract void save(GPXFile gpx, File file) throws FileNotFoundException;

	/**
	 * @throws IOException 
	 * @throws SAXException 
	 * 
	 * @param gpx
	 * @param file
	 * @throws  
	 */
	public abstract void validate() throws ValidationException, NotBoundException;

	/**
	 * returns a list of supported file types
	 * (as extension of filename, i.e. .gpx)  
	 * @return
	 */
	public List<String> getSupportedExtensions() {
	
		return extensions;
	}
	
	/**
	 * 
	 */
	public abstract void close();
	
	/**
	 * 
	 * @return
	 */
	public boolean isDefaultLoader() {
		return isDefault;
	}
	
	protected void checkOpen() throws NotBoundException
	{
		if (file == null) {
			throw new NotBoundException("file not specified, use Open() first");
		}
	}
}
