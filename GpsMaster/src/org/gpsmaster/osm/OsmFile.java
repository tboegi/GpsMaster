package org.gpsmaster.osm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.domain.root.Root;
import se.kodapan.osm.parser.xml.OsmXmlParserException;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

/**
 * temporary class for loading an OSM file
 * 
 * @author rfu
 *
 */
public class OsmFile {

	private InstantiatedOsmXmlParser parser = null;
	private PojoRoot osmRoot = null;
	
	public OsmFile() {
		
	}
	
	public void load(File file) throws FileNotFoundException, OsmXmlParserException {
		parser = InstantiatedOsmXmlParser.newInstance();
		parser.parse(new FileInputStream(file));
	}
	
	public Root getOsmRoot() {
		return parser.getRoot();
	}
}
