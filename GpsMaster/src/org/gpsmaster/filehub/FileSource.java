package org.gpsmaster.filehub;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.gpxpanel.GPXFile;

/**
 * Class representing a source for files stored in the filesystem
 * 
 * @author rfu
 *
 */
public class FileSource implements IItemSource {
	
	private final List<TransferableItem> items = new ArrayList<TransferableItem>();
	
	private File file = null;	
	private FileInputStream fis = null;
	
	public String getName() {
		return "Filesystem";
	}
	
	public DataType getDataType() {
		return DataType.STREAM;
	}

	/**
	 * advise GUI to show progress text
	 */
	public boolean doShowProgressText() {
 
		return true;
	}

	public void addItem(TransferableItem item) {
		items.add(item);		
	}

	@Override
	public List<TransferableItem> getItems() {		
		return items;
	}

	public void open(TransferableItem transferableItem) {
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

	public GPXFile getGpxFile(TransferableItem item) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}



}
