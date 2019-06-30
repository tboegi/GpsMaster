package eu.fuegenstein.osm;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Class for sending queries (and reverse lookups) to Nominatim
 * 
 * @author rfu
 *
 */
public class Nominatim {

	private String userAgent = "Java " + this.getClass().getCanonicalName();
	private NominatimResult result = null;
	private int limit = 20;

	private String queryString = "";
	private String moreUrl = null;
	
	/**
	 * 
	 */
	public Nominatim() {
		result = new NominatimResult();
	}
	
	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 * @throws UnsupportedEncodingException 
	 */
	public void setQueryString(String queryString) {
		clear();
		try {
			this.queryString = URLEncoder.encode(queryString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UnsupportedEncodingException should not happen, since utf-8 is fixed.
			e.printStackTrace();
		}
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}


	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @param max. number of results a query will return.
	 * 
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * look up the defined search term.
	 *  
	 * @return {@link NominatimResult} containing the result. 
	 * @see setQueryString() define the search term
	 * @see setLimit() number of results to return per invocation
	 * @see hasMore() indicates if there are more results. use lookup() consecutively to retrieve them.
	 * @throws Exception 
	 */
	public NominatimResult lookup() throws Exception {
		String urlString;
				
		if (moreUrl == null) {
			urlString = "http://nominatim.openstreetmap.org/search/"+queryString+"?format=xml&addressdetails=1&limit="+limit;
		} else {
			urlString = moreUrl;
		}

		result.clear();
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
	 */
	public void clear() {
		moreUrl = null;
	}
	
	/**
	 * 
	 * @return {@link true} if there are more results, false otherwise
	 */
	public boolean hasMore() {
		return (moreUrl != null);
	}

	/**
	 * 
	 * @param urlString
	 * @throws Exception 
	 */
	private void doSearch(String urlString) throws Exception {

		InputStream inStream = null;
		NominatimXmlHandler xmlHandler = new NominatimXmlHandler();
		result.clear();
		
		try
		{
			URL url = new URL(urlString);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser saxParser = factory.newSAXParser();
			// SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			URLConnection conn = url.openConnection();							
			conn.setRequestProperty("User-Agent", userAgent);
			inStream = conn.getInputStream();
			saxParser.parse(inStream, xmlHandler);
		}
		catch (Exception e) {
			throw e;
		}
		result = xmlHandler.getResult();
		// nominatim returns a more_url, even if there are no more results to fetch.
		// workaround: set moreUrl to NULL if result set was empty
		if (result.getPlaces().size() > 0) {
			moreUrl = result.getMoreUrl();
		} else {
			moreUrl = null;
		}
				
	}
}
