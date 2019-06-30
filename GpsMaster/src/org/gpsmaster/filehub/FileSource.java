package org.gpsmaster.filehub;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Class representing a source for files stored in the filesystem
 * 
 * @author rfu
 *
 */
public class FileSource implements IItemSource {
	
	private File file = null;
	private FileInputStream fis = null;
	
	public String getName() {
		return "Filesystem";
	}
	
	public void open(ITransferableItem transferableItem) {
		this.file = ((FileItem) transferableItem).getFile();
		
	}

	public InputStream getInputStream() throws Exception {	
		fis = new FileInputStream(file);
		return fis;
	}

	public void close() throws Exception {
		if (fis != null) {
			fis.close();
		}
		
	}




}
