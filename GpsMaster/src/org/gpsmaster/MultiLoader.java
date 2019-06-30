package org.gpsmaster;

import java.awt.Cursor;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.gpsmaster.dialogs.ProgressWidget;
import org.gpsmaster.fileloader.FileLoader;
import org.gpsmaster.fileloader.FileLoaderFactory;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.util.Filename;

/**
 * Class handling batch loading of files 
 * @author rfu
 *
 */
public class MultiLoader {

	private MessageCenter msg = null;
	private MessagePanel infoPanel = null;
	private ProgressWidget widget = null;

	private PropertyChangeListener listener = null;
	private Hashtable<String, FileLoader> loaders = new Hashtable<String, FileLoader>();
	private Hashtable<File, GPXFile> gpxFiles = new Hashtable<File, GPXFile>();

	private int inValid = 0;
	private boolean showProgress = true;
	private boolean showWarnings = true;
	private boolean runInBackground = true;
	private boolean reportPerFile = false; // fire event for each successfully loaded file (durchdenken!!)
	
	
	/**
	 * Default Constructor 
	 */
	public MultiLoader() {
		
		
	}

	public boolean isRunInBackground() {
		return runInBackground;
	}

	public void setRunInBackground(boolean runInBackground) {
		this.runInBackground = runInBackground;
	}

	public boolean getShowWarnings() {
		return showWarnings;
	}

	public void setShowWarnings(boolean showWarnings) {
		this.showWarnings = showWarnings;
	}

	public boolean isReportPerFile() {
		return reportPerFile;
	}

	public void setReportPerFile(boolean reportPerFile) {
		this.reportPerFile = reportPerFile;
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
	
	public PropertyChangeListener getListener() {
		return listener;
	}

	public void setPropertyChangeListener(PropertyChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @param files
	 */
	public void load(File[] files) {

		inValid = 0;
		
		if (msg != null) {
			infoPanel = msg.infoOn("Loading ...",  new Cursor(Cursor.WAIT_CURSOR));
		}
		// populate loader hashtable according to extensions
		populateLoaderTable(files);
		
		for (File file : files) {
			Filename filename = new Filename(file);
			FileLoader loader = loaders.get(filename.extension());
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
							inValid++;
						}
					}
					loader.loadCumulative();
				} catch (NotBoundException e) {
					error("Internal error", e);
				} catch (Exception e) {
					error(e);
				} finally {
					loader.close();
				}
			}

			// TODO if (enabled): fire an event to tell panel to 
			//		get latest GPX file and paint it
			// durchdenken!
		}

		if ((inValid > 0) && (showWarnings)) {
			warning(String.format("Validation failed for %d file(s). These file(s) may not have loaded properly", inValid));
		}

		// finally: gather GPXFiles from all loaders into {@link gpxFiles}
		Enumeration<String> e = loaders.keys();
		while (e.hasMoreElements()) {
			String ext = e.nextElement();
			FileLoader loader = loaders.get(ext);
			if (loader != null) {
				gpxFiles.putAll(loader.getFiles());
				loader.clear();
			}
		}

		if (infoPanel != null) {
			msg.infoOff(infoPanel);
		}
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
					FileLoader loader = FileLoaderFactory.getLoader(ext);
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
