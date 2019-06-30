package org.gpsmaster.online;

import java.util.Date;

import org.gpsmaster.filehub.ITransferableItem;

/**
 * Generic Class to hold a single track from online services
 * 
 * @author rfu
 * @author tim.prune
 * Initial code taken from GpsPrune
 * http://activityworkshop.net/
 * 
 */
 public class OnlineTrack implements ITransferableItem
{
	 private long id = 0;
	 private int state = ITransferableItem.STATE_PENDING;
	/** Track name or label */
	private String trackName = null;
	/** Description */
	private String description = null;
	/** Web page for more details */
	private String webUrl = null;
	/** Track length in metres */
	private long trackLength = 0;
	/** Download link */
	private String downloadLink = null;
	private Date date = null;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param inName name of track
	 */
	public void setName(String inName)
	{
		trackName = inName;
	}

	/**
	 * @return track name
	 */
	public String getName()
	{
		return trackName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @param inDesc description
	 */
	public void setDescription(String inDesc)
	{
		description = inDesc;
	}

	/**
	 * @return track description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param inUrl web page url
	 */
	public void setWebUrl(String inUrl)
	{
		webUrl = inUrl;
	}

	/**
	 * @return web url
	 */
	public String getWebUrl()
	{
		return webUrl;
	}

	/**
	 * @param inLength length of track
	 */
	public void setLength(long inLength)
	{
		trackLength = inLength;
	}

	/**
	 * @return track length in meters
	 */
	public long getLength()
	{
		return trackLength;
	}

	/**
	 * @param inLink link to download track
	 */
	public void setDownloadLink(String inLink)
	{
		downloadLink = inLink;
	}

	/**
	 * @return download link
	 */
	public String getDownloadLink()
	{
		return downloadLink;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setState(int state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setException(Exception ex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Exception getException() {
		// TODO Auto-generated method stub
		return null;
	}
}
