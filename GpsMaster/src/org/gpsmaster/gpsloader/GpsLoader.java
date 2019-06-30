package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.bind.ValidationException;





import org.gpsmaster.gpxpanel.GPXFile;
import org.xml.sax.SAXException;


/**
 * base class for file format specific loader classes
 * 
 * @author rfu
 *
 */
public abstract class GpsLoader {

	protected boolean isDefault = false;
	protected boolean isOpen = false;
	protected boolean isAdding = false;
	private boolean keepRawData = false;
	
	protected File file = null;
	protected GPXFile gpx = null;
	protected List<String> extensions = new ArrayList<String>();
	protected Hashtable<File, GPXFile> gpxFiles = new Hashtable<File, GPXFile>();
	
	protected Locale numLocale = new Locale("en", "US"); // locale for . as comma separator 
	
	/**
	 * Constructor
	 */
	public GpsLoader() {
		
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDefaultLoader() {
		return isDefault;
	}

	/**
	 * 
	 * @return {@link true} if the loader is adding; meaning that the content of multiple
	 *  files being loaded is added to a single GPX file, i.e. for geo-referenced images.
	 */
	public boolean isCumulative() {
		return isAdding;
	}
	/**
	 * @return the keepRawData
	 */
	public boolean isKeepRawData() {
		return keepRawData;
	}

	/**
	 * @param keepRawData the keepRawData to set
	 */
	public void setKeepRawData(boolean keepRawData) {
		this.keepRawData = keepRawData;
	}

	/**
	 * Gets all GPX files loaded via loadCumulative() so far.
	 * @return
	 */
	public Hashtable<File, GPXFile> getFiles() {
		return gpxFiles;
	}
	
	/**
	 * 
	 * @param file
	 * @throws Exception 
	 */
	public abstract void open(File file) throws Exception;
	
	/**
	 * 
	 * @param file
	 * @throws Exception 
	 */
	public abstract GPXFile load() throws Exception;
	
	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public abstract GPXFile load(InputStream inStream) throws Exception;
	
	/**
	 * Load current file and keep it internally. Use {@link getFiles()} to
	 * retrieve all files loaded so far.
	 */
	public abstract void loadCumulative() throws Exception;
	
	/**
	 * 
	 * @param inStream
	 * @throws Exception
	 */
	public abstract void loadCumulative(InputStream inStream) throws Exception;
			
	/**
	 * 
	 * @param gpx
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public abstract void save(GPXFile gpx, File file) throws FileNotFoundException;

	/**
	 * 
	 * @param gpx
	 * @param outStream
	 */
	public abstract void save(GPXFile gpx, OutputStream outStream);
	
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
	 */
	public void clear() {
		gpxFiles.clear();
	}
	
	protected void checkOpen() throws NotBoundException
	{
		if (file == null) {
			throw new NotBoundException("file not specified, use Open() first");
		}
	}
}
