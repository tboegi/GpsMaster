package org.gpsmaster.filehub;

import java.io.InputStream;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;


/**
 * Interface defining a source for {@link TransferableItem}s
 * @author rfu
 *
 */
public interface IItemSource {
		
	/**
	 * Get a short, human readable description of this source.
	 * 
	 * @return
	 */
	String getName();
		
	/**
	 * Get the {@link DataType} supported by the class
	 * implementing this interface
	 * @return
	 */
	DataType getDataType();
	
	/**
	 * determine if the GUI shall display some informational text
	 * while loading an item. may be ignored by some GUIs.
	 * 
	 * @return
	 */
	boolean doShowProgressText();
	
	/**
	 * get list of all items to transfer
	 * @return
	 */
	List<TransferableItem> getItems();
	
	/**
	 * Get the {@link GPXFile} described by the given {@link ITransferableItem}
	 * @param item
	 * @return
	 * @throws NotImplementedException
	 */
	GPXFile getGpxFile(TransferableItem item) throws Exception;
	
	/**
	 * Open an item for processing
	 * @param file
	 */
	void open(TransferableItem transferableItem);

	/**
	 * Get Stream to load data from the previously opened item
	 * @return
	 * @throws Exception
	 */
	InputStream getInputStream() throws Exception;
	
	/**
	 * @throws Exception 
	 * 
	 */
	void close() throws Exception;
}
