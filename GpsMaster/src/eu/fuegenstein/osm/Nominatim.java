package eu.fuegenstein.osm;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Nominatim {

	private String userAgent = "Java " + this.getClass().getCanonicalName();
	/**
	 *
	 */
	public Nominatim() {

	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 *
	 * @param node
	 * @throws Exception
	 */
	public NominatimResult ReverseLookup(double lat, double lon) throws Exception {

		InputStream inStream = null;

		String urlString = "http://nominatim.openstreetmap.org/reverse?lat="+lat+"&lon="+lon;
		NominatimXmlHandler xmlHandler = new NominatimXmlHandler();
		try
		{
			URL url = new URL(urlString);
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", userAgent);
			inStream = conn.getInputStream();
			saxParser.parse(inStream, xmlHandler);
		}
		catch (Exception e) {
			throw e;
		}
		return xmlHandler.getResult();
	}
}
