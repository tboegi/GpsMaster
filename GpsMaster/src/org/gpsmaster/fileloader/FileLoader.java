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

	/**
	 * Constructor
	 */
	public FileLoader() {

	}


	public abstract void Open(File file);

	/**
	 *
	 * @param file
	 * @throws Exception
	 */
	public abstract GPXFile Load() throws Exception;


	/**
	 *
	 * @param gpx
	 * @param file
	 * @throws FileNotFoundException
	 */
	public abstract void Save(GPXFile gpx, File file) throws FileNotFoundException;

	/**
	 * @throws IOException
	 * @throws SAXException
	 *
	 * @param gpx
	 * @param file
	 * @throws
	 */
	public abstract void Validate() throws ValidationException, NotBoundException;

	/**
	 * returns a list of supported file types
	 * (as extension of filename, i.e. .gpx)
	 * @return
	 */
	public List<String> getSupportedExtensions() {

		return extensions;
	}

	public abstract void Close();

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
