package org.gpsmaster.online;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * XML handler for dealing with XML returned from the geonames api
 * 
 * @author tim.prune
 * Code taken from GpsPrune
 * http://activityworkshop.net/
 * 
 */

public class WikiXmlHandler extends DefaultHandler
{
	private String value = null;
	private OnlineTrack track = null;
	private String lat = null, lon = null;
	private String errorMessage = null;

	private WikiTableModel tableModel = null;

	/**
	 * Constructor
	 * @param model 
	 */
	public WikiXmlHandler(WikiTableModel model) {
		this.tableModel = model;
	}
	
	/**
	 * React to the start of an XML tag
	 */
	public void startElement(String inUri, String inLocalName, String inTagName,
		Attributes inAttributes) throws SAXException
	{
		if (inTagName.equals("entry")) {
			track = new OnlineTrack();
			lat = null;
			lon = null;
		}
		else if (inTagName.equals("status")) {
			errorMessage = inAttributes.getValue("message");
		}
		else value = null;
		super.startElement(inUri, inLocalName, inTagName, inAttributes);
	}

	/**
	 * React to the end of an XML tag
	 */
	public void endElement(String inUri, String inLocalName, String inTagName)
	throws SAXException
	{
//		System.out.println(inTagName+": "+value);
		
		if (inTagName.equals("entry")) {
			// end of the entry
			track.setDownloadLink(lat + "," + lon);
			tableModel.addItem(track);
		}
		else if (inTagName.equals("title")) {
			track.setName(value);
		}
		else if (inTagName.equals("summary")) {
			track.setDescription(value);
		}
		else if (inTagName.equals("lat")) {
			lat = value;
		}
		else if (inTagName.equals("lng")) {
			lon = value;
		}
		else if (inTagName.equals("distance")) {
			try {
				track.setLength((long) (Double.parseDouble(value) * 1000.0f)); // convert from km to m
			}
			catch (NumberFormatException nfe) {}
		}
		else if (inTagName.equals("wikipediaUrl")) {
			track.setWebUrl(value.replaceFirst("http://", "https://"));
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

	/**
	 * @return error message, if any
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
