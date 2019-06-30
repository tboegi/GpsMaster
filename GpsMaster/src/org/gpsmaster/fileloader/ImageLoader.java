package org.gpsmaster.fileloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;

import javax.xml.bind.ValidationException;

import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Loader Class for georeferenced images
 * @author rfu
 *
 */
public class ImageLoader extends FileLoader {

	/**
	 * Default Constructor
	 */
	public ImageLoader() {
		super();
		extensions.add("jpg");
		extensions.add("jpeg");
	}
	
	@Override
	public void open(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GPXFile load() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadCumulative() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(GPXFile gpx, File file) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validate() throws ValidationException, NotBoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
