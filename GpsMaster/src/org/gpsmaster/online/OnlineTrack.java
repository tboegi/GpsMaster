package org.gpsmaster.online;

import java.util.Date;

import org.gpsmaster.filehub.TransferableItem;

/**
 * Generic Class to hold a single track from online services
 * Extends {@link ITransferableItem} with online-specific members
 * 
 * @author rfu
 * @author tim.prune
 * Initial code taken from GpsPrune
 * http://activityworkshop.net/
 * 
 */
 public class OnlineTrack extends TransferableItem
{
	 private long id = 0;

	/** Track name or label */
	private String trackName = null;
	/** Description */
	private String description = null;
	/** Type or Activity **/
	private String type = null;
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

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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

}
