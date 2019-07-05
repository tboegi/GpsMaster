package eu.fuegenstein.osm;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Nominatim {

	private String userAgent = "Java " + this.getClass().getCanonicalName();
	private NominatimResult result = null;

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
	 * @param query
	 * @return
	 */
	public NominatimResult lookup(String query) throws Exception {

		result = new NominatimResult();

		String urlString = "http://nominatim.openstreetmap.org/search/"+query+"?format=xml&addressdetails=1";
		doSearch(urlString);

		return result;
	}

	/**
	 * Find the OSM object that is closest to the given coordinates
	 * @param lat
	 * @param lon
	 * @return {@link NominatimResult} with just one place
	 * @throws Exception
	 */
	public NominatimResult reverseLookup(double lat, double lon) throws Exception {

		result = new NominatimResult();
		result.setLat(lat);
		result.setLon(lon);

		String urlString = "http://nominatim.openstreetmap.org/reverse?lat="+lat+"&lon="+lon;
		doSearch(urlString);

		return result;
	}

	/**
	 *
	 * @param urlString
	 * @throws Exception
	 */
	private void doSearch(String urlString) throws Exception {

		InputStream inStream = null;
		NominatimXmlHandler xmlHandler = new NominatimXmlHandler();
		// TODO loop over searchresults.more_url
		try
		{
			URL url = new URL(urlString);
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", userAgent);
			inStream = conn.getInputStream();
			System.out.println(inStream.toString());
			saxParser.parse(inStream, xmlHandler);
		}
		catch (Exception e) {
			throw e;
		}
		result = xmlHandler.getResult();

	}
}
