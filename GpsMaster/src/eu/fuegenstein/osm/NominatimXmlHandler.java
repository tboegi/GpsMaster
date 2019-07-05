package eu.fuegenstein.osm;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML handler for dealing with XML returned from Nominatim
 *
 */
public class NominatimXmlHandler extends DefaultHandler
{

	private NominatimResult result = new NominatimResult();
	private String value = null;
	private boolean inAddressParts = false;

	/**
	 * React to the start of an XML tag
	 */
	public void startElement(String inUri, String inLocalName, String inTagName,
		Attributes inAttributes) throws SAXException
	{
		if (inTagName.equals("addressparts")) {
			inAddressParts = true;
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
		if (inTagName.equals("village")) {
			result.setVillage(value);
		}
		if (inTagName.equals("county")) {
			result.setCounty(value);
		}
		if (inTagName.equals("state")) {
			result.setState(value);
		}
		if (inTagName.equals("country")) {
			result.setCountry(value);
		}
		else if (inTagName.equals("addressparts")) {
			inAddressParts = false;
		}
		if (inAddressParts) {
			result.getAll().put(inTagName, value);
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
