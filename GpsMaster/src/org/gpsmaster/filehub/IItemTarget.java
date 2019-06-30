package org.gpsmaster.filehub;

import java.io.OutputStream;

import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Interface to be implemented by classes receiving GPS Data, either as {@link GPXFile} or via {@link OutputStream}
 * 
 * @author rfu
 *
 */
public interface IItemTarget extends Cloneable {
	
	/**
	 * Get a short, human readable name describing this target 
	 * @return
	 */
	String getName();

	/**
	 * 
	 * @return
	 */
	String getDescription();
	
	/**
	 * Get the {@link DataType} supported by the class
	 * implementing this interface
	 * @return
	 */
	DataType getDataType();
	
	/**
	 * determine if the GUI shall display some informational text
	 * while storing an item. may be ignored by some GUIs.
	 * 
	 * @return
	 */
	boolean doShowProgressText();
	
	/**
	 * get if this target is enabled. if not, don't send items to this target.
	 * @return
	 */
	boolean isEnabled();
	
	/**
	 * enable / disable this target
	 * @param enabled
	 */
	void setEnabled(boolean enabled);
	
	/**
	 * directly add a {@link GPXFile} to this target
	 * @param gpxFile {@link GPXFile} to add
	 * {@link @param item ITransferableItem} containing metadata (if required, may be ignored)
	 */
	void addGpxFile(GPXFile gpxFile, TransferableItem item);

	/**
	 * Open an item for processing
	 * @param file
	 * @throws Exception 
	 */
	void open(TransferableItem item) throws Exception;

	/**
	 * get the format of the (i.e. gpx, kml, ...) required by this target when delivering a stream.
	 * NULL means all formats are accepted.
	 * @return three letter format (file sourceFmt) or NULL 
	 */
	String getRequiredFormat();
	
	/**
	 * 
	 * @return
	 */
	OutputStream getOutputStream() throws Exception;
	
	/**
	 * @throws Exception 
	 * 
	 */
	void close() throws Exception;
}
