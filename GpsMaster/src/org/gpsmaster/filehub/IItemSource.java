package org.gpsmaster.filehub;

import java.io.InputStream;

public interface IItemSource {
	
	/**
	 * Get a short, human readable description of this source.
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * 
	 * @param file
	 */
	void open(ITransferableItem transferableItem);

	/**
	 * 
	 * @return
	 * @throws FileNotFoundException 
	 */
	InputStream getInputStream() throws Exception;
	
	/**
	 * @throws Exception 
	 * 
	 */
	void close() throws Exception;
}
