package org.gpsmaster.online;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML handler for dealing with XML returned from gpsies.com
 *
 * @author tim.prune
 * Code taken from GpsPrune
 * http://activityworkshop.net/
 *
 */
public class GpsiesXmlHandler extends DefaultHandler
{
	private String value = null;
	private ArrayList<OnlineTrack> trackList = null;
	private OnlineTrack track = null;


	/**
	 * React to the start of an XML tag
	 */
	public void startElement(String inUri, String inLocalName, String inTagName,
		Attributes inAttributes) throws SAXException
	{
		if (inTagName.equals("tracks")) {
			trackList = new ArrayList<OnlineTrack>();
		}
		else if (inTagName.equals("track")) {
			track = new OnlineTrack();
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
		if (inTagName.equals("track")) {
			trackList.add(track);
		}
		else if (inTagName.equals("title")) {
			track.setTrackName(value);
		}
		else if (inTagName.equals("description")) {
			track.setDescription(value);
		}
		else if (inTagName.equals("fileId")) {
			track.setWebUrl("http://gpsies.com/map.do?fileId=" + value);
		}
		else if (inTagName.equals("trackLengthM")) {
			try {
				track.setLength((long) Double.parseDouble(value));
			}
			catch (NumberFormatException nfe) {}
		}
		else if (inTagName.equals("downloadLink")) {
			track.setDownloadLink(value);
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
	 * @return the list of tracks
	 */
	public ArrayList<OnlineTrack> getTrackList()
	{
		return trackList;
	}
}
