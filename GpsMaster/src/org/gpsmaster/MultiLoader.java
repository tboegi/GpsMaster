package org.gpsmaster;

import java.awt.Cursor;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.SwingWorker;
import javax.xml.bind.ValidationException;

import org.gpsmaster.dialogs.ProgressWidget;
import org.gpsmaster.gpsloader.GpsLoader;
import org.gpsmaster.gpsloader.GpsLoaderFactory;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.util.Filename;

/**
 * Class loading batch of files in background
 *  
 * @author rfu
 * 
 * TODO support loading in foreground
 * TODO support progress reporting / progress widget
 * 
 */
public class MultiLoader {

	private MessageCenter msg = null;
	private MessagePanel infoPanel = null;
	private ProgressWidget widget = null;

	private File[] files;
	private Hashtable<String, GpsLoader> loaders = new Hashtable<String, GpsLoader>();
	private Hashtable<File, GPXFile> gpxFiles = new Hashtable<File, GPXFile>();

	private int invalid = 0;
	private boolean showProgress = true;
	private boolean showWarnings = true;

	private SwingWorker<Void, Void> fileOpenWorker = new SwingWorker<Void, Void>() {
		
		@Override
		protected Void doInBackground() throws Exception {
			invalid = 0;
			
			if (msg != null) {
				infoPanel = msg.infoOn("Loading ...",  new Cursor(Cursor.WAIT_CURSOR));
			}
			// populate loader hashtable according to extensions
			populateLoaderTable(files);
			
			for (File file : files) {
				Filename filename = new Filename(file);
				GpsLoader loader = loaders.get(filename.extension());
				if (loader != null) {
					if (infoPanel != null) {
						infoPanel.setText("Loading ".concat(filename.fullname()));
					}
					try {
						loader.open(file);
						try {
							loader.validate();
						} catch (ValidationException e) {
							if (files.length == 1) {
								warning("Validation failed", e);
							} else {
								invalid++;
							}
						}
						if (loader.isCumulative()) {
							loader.loadCumulative();
						} else {
							GPXFile gpx = loader.load();
							firePropertyChange("newGpx", file, gpx);
						}
					} catch (NotBoundException e) {
						error("Internal error", e);
					} catch (Exception e) {
						error(e);
					} finally {
						loader.close();
					}
				}	
			}
	
			if ((invalid > 0) && (showWarnings)) {
				warning(String.format("Validation failed for %d file(s). These file(s) may not have loaded properly", invalid));
			}
	
			// finally: fire property change event for all remaining files
			Enumeration<String> keys = loaders.keys();
			while (keys.hasMoreElements()) {
				String ext = keys.nextElement();
				GpsLoader loader = loaders.get(ext);
				if (loader != null) {
					Enumeration<File> files = loader.getFiles().keys();
					while(files.hasMoreElements()) {
						File file = files.nextElement();
						GPXFile gpx = loader.getFiles().get(file);
						firePropertyChange("newGpx", file, gpx);
					}
					loader.clear();
				}
			}			
			return null;
		}
        @Override
        protected void done() {
			if (infoPanel != null) {
				msg.infoOff(infoPanel);
			}
			// firePropertyChange("loading finished", .....)
         	// setFileIOHappening(false);
        }

	};

	
	/**
	 * Default Constructor 
	 */
	public MultiLoader(MessageCenter msg) {
		this.msg = msg;		
	}

	public void setFiles(File[] files) {
		this.files = files;
	}


	/**
	 * 
	 * @return
	 */
	public boolean getShowWarnings() {
		return showWarnings;
	}

	/**
	 * determine if warnings will be displayed
	 * @param showWarnings true - will be displayed
	 */
	public void setShowWarnings(boolean showWarnings) {
		this.showWarnings = showWarnings;
	}

	/**
	 * 
	 * @param msg
	 */
	public void setMessageCenter(MessageCenter msg) {
		this.msg = msg;
	}
	
	/**
	 * 
	 * @return
	 */
	public MessageCenter getMessageCenter() {
		return msg;
	}
	
	/**
	 * 
	 * @param widget
	 */
	public void setProgressWidget(ProgressWidget widget) {
		this.widget = widget;
	}
	
	/**
	 * 
	 * @return current {@link ProgressWidget} or NULL if none set
	 */
	public ProgressWidget getProgressWidget() {
		return widget;
	}
	
	public void setPropertyChangeListener(PropertyChangeListener listener) {
		fileOpenWorker.addPropertyChangeListener(listener);
	}

	/**
	 * 
	 * @param files
	 */
	public void load() {
		fileOpenWorker.execute();
	}
		
	public void clear() {
		gpxFiles.clear();
		loaders.clear();
	}
	
	public boolean getShowFilenames() {
		return showProgress;
	}

	/**
	 * Show names of files as they are loaded
	 * @param showProgress
	 */
	public void setShowFilenames(boolean showProgress) {
		this.showProgress = showProgress;
	}
	
	public Hashtable<File, GPXFile> getFiles() {
		return gpxFiles;
	}

	/**
	 * 
	 * @param files
	 */
	private void populateLoaderTable(File[] files) {
		List<String> badExt = new ArrayList<String>();
		for (File file : files) {
			Filename filename = new Filename(file);
			String ext = filename.extension();
		
			if ((loaders.containsKey(ext) == false) && (badExt.contains(ext) == false)) {
				try {
					GpsLoader loader = GpsLoaderFactory.getLoader(ext);
					loaders.put(ext, loader);
				} catch (ClassNotFoundException e) {					
					error("No loader for filetype", e);
					badExt.add(ext);
				}
			}
		}
	}
	
	/**
	 * Shortcut to message center
	 * @param text
	 * @param e
	 */
	private void error(String text, Exception e) {
		if (msg != null) {
			msg.error(text, e);
		}
	}

	/**
	 * Shortcut to message center
	 * @param text
	 * @param e
	 */
	private void error(Exception e) {
		if (msg != null) {
			msg.error(e);
		}
	}

	/**
	 * Shortcut to message center
	 * @param text
	 */
	private void warning(String text) {
		if (msg != null) {
			msg.volatileWarning(text);
		}
	}

	/**
	 * Shortcut to message center
	 * @param text
	 */
	private void warning(String text, Exception e) {
		if (msg != null) {
			msg.volatileWarning(text, e);
		}
	}		
}
