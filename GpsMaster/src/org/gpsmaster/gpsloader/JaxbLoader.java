package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gpsmaster.gpxpanel.GPXFile;

import com.topografix.gpx._1._1.GpxType;

/**
 * Load .gpx files via Jaxb/(un)marshalling
 * (for testing purposes, as of now)
 * @author rfu
 *
 */
public class JaxbLoader extends XmlLoader {

	private GpxType gpx = null;
	
	public JaxbLoader() {
		super();
		extensions.add("gpx");
		xsdResource = "/org/gpsmaster/schema/gpx-1.1.xsd";
	}
	
	@Override
	public void loadCumulative(InputStream inStream) throws Exception {
		throw new UnsupportedOperationException();
	}	

	@Override
	public void save(GPXFile gpx, OutputStream out) {
		throw new UnsupportedOperationException();		
	}
	
	@Override
	public GPXFile load(InputStream inputStream, String format) {
		// TODO Auto-generated method stub
		return null;
	}


}
