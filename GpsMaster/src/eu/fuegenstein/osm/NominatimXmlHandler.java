package eu.fuegenstein.osm;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML handler for dealing with results returned from Nominatim
 *  
 */
public class NominatimXmlHandler extends DefaultHandler
{

	private NominatimResult result = new NominatimResult();
	private NominatimPlace place = null;	
	
	private String value = null;
	

	/**
	 * React to the start of an XML tag
	 */
	public void startElement(String inUri, String inLocalName, String inTagName,
		Attributes inAttributes) throws SAXException
	{		
		if (inTagName.equals("searchresults")) {
			result.setAttribution(inAttributes.getValue("attribution"));
			result.setMoreUrl(inAttributes.getValue("more_url"));
		}
		
		if (inTagName.equals("addressparts") || inTagName.equals("place")) {
			place = new NominatimPlace();
			result.getPlaces().add(place);
			
			for (int i = 0; i < inAttributes.getLength(); i++) {
							
				String attribute = inAttributes.getLocalName(i);
				String attValue = inAttributes.getValue(i);		
				if (attribute.equals("display_name")) {
					place.setDisplayName(attValue);
				}
				if (attribute.equals("lat")) {
					place.setLat(Double.parseDouble(attValue));
				}
				if (attribute.equals("lon")) {
					place.setLon(Double.parseDouble(attValue));
				}
				// displayname
				// osm type
				// osm id
				place.getAll().put(attribute, attValue);
			}
		}
		value = null;
		super.startElement(inUri, inLocalName, inTagName, inAttributes);
	}

	/**
	 * React to the end of an XML tag
	 */
	public void endElement(String inUri, String inLocalName, String inTagName)
	throws SAXException
	{
		// System.out.println(inTagName);
		if (inTagName.equals("village")) {
			place.setVillage(value);
		}
		if (inTagName.equals("county")) {
			place.setCounty(value);
		}
		if (inTagName.equals("state")) {
			place.setState(value);
		}
		if (inTagName.equals("country")) {
			place.setCountry(value);
		}
		else if (inTagName.equals("addressparts") || inTagName.equals("place")) {
			place = null;
		}
		if (place != null) {
			place.getAll().put(inTagName, value);
		}
		super.endElement(inUri, inLocalName, inTagName);
	}

	/**
	 * React to characters received inside tags
	 */
	public void characters(char[] inCh, int inStart, int inLength)
	throws SAXException
	{
		String xmlValue = new String(inCh, inStart, inLength);
		value = (value==null?xmlValue:value+xmlValue);
		super.characters(inCh, inStart, inLength);
	}

	public NominatimResult getResult() {
		return result;
	}

}
