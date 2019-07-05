package org.gpsmaster;

import java.awt.Cursor;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.SwingWorker;
import javax.xml.bind.ValidationException;

import org.gpsmaster.db.GpsEntry;
import org.gpsmaster.db.GpsStorage;
import org.gpsmaster.gpsloader.GpsLoader;
import org.gpsmaster.gpsloader.GpsLoaderFactory;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.widget.ProgressWidget;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.util.Filename;

/**
 * Class loading batch of files in background
 *
 * @author rfu
 *
 * ATTENTION
 * 		due to stupid design of {@link SwingWorker}, load() may
 * 		only be called once! If necessary, re-instantiate this
 * 		class again
 *
 * TODO support loading in foreground
 * TODO support progress reporting / progress widget
 * TODO rewrite to allow running background tasks (swingworkers) more than once
 *
 * TODO rewrite this whole mess
 *
 */
public class MultiLoader {

	private MessageCenter msg = null;
	private MessagePanel infoPanel = null;
	private ProgressWidget widget = null;
	private GpsStorage db = null;
	private boolean addToMap = true;
	private boolean addToStorage = false;

	private File[] files;
	private List<GpsEntry> gpsEntries = null;

	private Hashtable<String, GpsLoader> loaders = new Hashtable<String, GpsLoader>();
	private Hashtable<File, GPXFile> gpxFiles = new Hashtable<File, GPXFile>();

	private int invalid = 0;
	private boolean showProgress = true;
	private boolean showWarnings = true;

	private SwingWorker<Void, Void> fileWorker = new SwingWorker<Void, Void>() {

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
							if ((files.length == 1) && showWarnings) {
								warning("Validation failed", e);
							} else {
								invalid++;
							}
						}
						if (loader.isCumulative()) {
							loader.loadCumulative();
						} else {
							GPXFile gpx = loader.load();
					    	if (gpx.getMetadata().getName().isEmpty() ) {
					    		gpx.getMetadata().setName(filename.filename());
					    	}
							GpsMaster.active.newGpxFile(gpx);
							loader.clear();
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
				    	if (gpx.getMetadata().getName().isEmpty() ) {
				    		Filename filename = new Filename(file);
				    		gpx.getMetadata().setName(filename.filename());
				    	}
						GpsMaster.active.newGpxFile(gpx);
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
        }

	};

	/**
	 * background task to load files from database
	 *
	 * TODO unify with fileWorker()
	 * TODO support validation
	 * TODO support cumulative loading
	 *
	 */
	private SwingWorker<Void, Void> dbWorker = new SwingWorker<Void, Void>() {

		@Override
		protected Void doInBackground() throws Exception {
			invalid = 0;

			if (msg != null) {
				infoPanel = msg.infoOn("Getting ...",  new Cursor(Cursor.WAIT_CURSOR));
			}

			for (GpsEntry entry : gpsEntries) {
				if (infoPanel != null) {
					infoPanel.setText("Getting ".concat(entry.getName()));
				}
				try {
					GPXFile gpx = db.get(entry.getId());
					GpsMaster.active.newGpxFile(gpx);
				} catch (Exception e) {
					error(e);
				}
			}
			return null;
		}

        @Override
        protected void done() {
			if (infoPanel != null) {
				msg.infoOff(infoPanel);
			}
        }

	};

	/**
	 * Import files into database
	 */
	private SwingWorker<Void, Void> importWorker = new SwingWorker<Void, Void>() {

		@Override
		protected Void doInBackground() throws Exception {
			for (File file : files) {
				InputStream inStream = new FileInputStream(file);
				// ...
				inStream.close();
			}
			return null;
		}

	};

	/**
	 * Default Constructor
	 */
	public MultiLoader(MessageCenter msg) {
		this.msg = msg;
	}

	/**
	 * set list of files to be loaded
	 * @param files
	 */
	public void setFiles(File[] files) {
		this.files = files;
	}


	/**
	 * List of {@link GpsEntry}s determining {@link GPXFile}s to load from DB
	 * @return the gpsEntries
	 */
	public List<GpsEntry> getGpsEntries() {
		return gpsEntries;
	}

	/**
	 * List of {@link GpsEntry}s determining {@link GPXFile}s to load from DB
	 * @param gpsEntries the gpsEntries to set
	 */
	public void setGpsEntries(List<GpsEntry> gpsEntries) {
		this.gpsEntries = gpsEntries;
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

	/**
	 * @return the db
	 */
	public GpsStorage getStorage() {
		return db;
	}

	/**
	 * @param db the db to set
	 */
	public void setStorage(GpsStorage db) {
		this.db = db;
	}

	/**
	 *
	 * @return the addToMap
	 */
	public boolean isAddToMap() {
		return addToMap;
	}

	/**
	 * Determine if files loaded from disk or database are to be added
	 * to the map panel (and tree, etc.).
	 * This is done by firing a {@link Const.PCE_NEWGPX} event.
	 *
	 * @param addToMap the addToMap to set
	 */
	public void setAddToMap(boolean addToMap) {
		this.addToMap = addToMap;
	}

	/**
	 * @return the addToStorage
	 */
	public boolean isAddToStorage() {
		return addToStorage;
	}

	/**
	 * Determine if GPS files loaded from disk are to be added to the database.
	 *
	 * @param addToStorage the addToStorage to set
	 */
	public void setAddToStorage(boolean addToStorage) {
		this.addToStorage = addToStorage;
	}

	public void setPropertyChangeListener(PropertyChangeListener listener) {
		fileWorker.addPropertyChangeListener(listener);
	}

	/**
	 * Put files set via setFiles() on the map.
	 */
	public void load() {
		if ((files != null) && (files.length > 0)) {
			fileWorker.execute();
		}
	}

	/**
	 *
	 */
	private void dbImport() {

	}
	/**
	 *
	 */
	public void clear() {
		gpxFiles.clear();
		loaders.clear();
		gpsEntries.clear();
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
	 * Fill {@link loaders} with all loader classes required
	 * to load given files
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
					error("No loader for filetype ." + ext, e);
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
