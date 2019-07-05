package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;

import javax.xml.bind.ValidationException;

import org.gpsmaster.gpxpanel.GPXFile;

/**
 *
 * CicloNavic 50 .trk file format
 * http://ciclosport.com/en/downloads/category/33
 * @author rfu
 *
 */
public class TrkLoader extends GpsLoader {

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
	public GPXFile load(InputStream inputStream) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadCumulative() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(GPXFile gpx, File file) throws FileNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(GPXFile gpx, OutputStream outStream) {
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
