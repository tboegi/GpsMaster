package org.gpsmaster.gpsloader;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.net.URI;
import java.util.List;

import javax.swing.TransferHandler;

import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.FileItem;

import eu.fuegenstein.messagecenter.MessageCenter;

/**
 * Class to listen for drag/drop events and react to dropping files on to the map panel
 * @author tim.prune
 * @author rfu
 */
public class FileDropHandler extends TransferHandler
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7547034606181097621L;

	/** Fixed flavour in case the java file list flavour isn't available */
	private static DataFlavor _uriListFlavour = null;
	
	private FileHub fileHub = null;
	private MessageCenter msg = null;
	
	/** Static block to initialise the list flavour */
	static
	{
		try {_uriListFlavour = new DataFlavor("text/uri-list;class=java.lang.String");
		} catch (ClassNotFoundException nfe) {}
	}

	/**
	 * Constructor
	 * @param fileHub properly configured {@link FileHub] to do the heavy lifting
	 */
	public FileDropHandler(FileHub fileHub, MessageCenter msg)
	{
		super();
		this.fileHub = fileHub;
		this.msg = msg;
	}

	/**
	 * Check if the object being dragged can be accepted
	 * @param inSupport object to check
	 */
	public boolean canImport(TransferSupport inSupport)
	{
		boolean retval = inSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
			// || inSupport.isDataFlavorSupported(_uriListFlavour);
		// Modify icon to show a copy, not a move (+ icon on cursor)
		if (retval) {
			inSupport.setDropAction(COPY);
		}
		return retval;
	}

	/**
	 * Accept the incoming data and pass it on to the App
	 * @param inSupport contents of drop
	 */
	public boolean importData(TransferSupport inSupport)
	{
		if (!canImport(inSupport)) {return false;} // not allowed

		boolean success = false;
		
		// Try a java file list flavour first
		try
		{
			@SuppressWarnings("unchecked")
			List<File> fileList = (List<File>) inSupport.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			success = true;

			for (File f : fileList)
			{
				addFile(f);
			}
		} catch (Exception e) {}  // exception caught, probably missing a javafilelist flavour - just continue

		// If that didn't work, try a list of strings instead
		if (!success)
		{
			try
			{
				String pathList = inSupport.getTransferable().getTransferData(_uriListFlavour).toString();
				success = true;

				for (String s : pathList.split("[\n\r]+"))
				{
					if (s != null && !s.equals(""))
					{
						File f = new File(new URI(s));
						addFile(f);
					}
				}
			} catch (Exception e) {
				msg.error(e);
				// System.err.println("exception: " + e.getClass().getName() + " - " + e.getMessage());
				return false;
			}
		}

		fileHub.run();
		return true;
	}
	
	/**
	 * Add file as {@link FileItem} to {@link FileHub}. recurses into sub directories 
	 * @param f
	 */
	private void addFile(File f) {
	
		try {
			if (f.exists() && f.canRead()) {
				if (f.isFile()) {
					FileItem item = new FileItem(f);
					fileHub.getItemSource().getItems().add(item);				
				} else if (f.isDirectory()) {
					for (String path : f.list()) {
						addFile(new File(f, path));
					}				
				}
			}
		} catch (Exception e) {
			msg.error(e);
		}
	}

	
}
