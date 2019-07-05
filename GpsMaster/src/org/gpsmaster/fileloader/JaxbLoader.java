package org.gpsmaster.fileloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gpsmaster.Config;
import org.gpsmaster.gpxpanel.GPXFile;

import com.topografix.gpx._1._1.GpxType;

public class JaxbLoader extends XmlLoader {

	private GpxType gpx = null;

	public JaxbLoader() {
		super();
		extensions.add("gpx");
		xsdResource = "/org/gpsmaster/schema/gpx-1.1.xsd";
	}

	@Override
	public void Open(File file) {
		// TODO Auto-generated method stub
		this.file = file;
		isOpen = true;
	}

	@Override
	public GPXFile Load() throws Exception {
		// TODO Auto-generated method stub
		// JAXBContext context = JAXBContext.newInstance(GpxType.class);
		JAXBContext context = JAXBContext.newInstance("com.topografix.gpx._1._1");
		Unmarshaller u = context.createUnmarshaller();
		gpx = (GpxType) u.unmarshal(file);

		System.out.println(gpx.getMetadata().getName());

		return null;
	}

	@Override
	public void Save(GPXFile gpx, File file) throws FileNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void Close() {
		isOpen = false;
	}

}
