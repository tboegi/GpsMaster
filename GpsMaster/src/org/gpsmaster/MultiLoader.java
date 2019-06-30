package org.gpsmaster;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.gpsmaster.fileloader.FileLoader;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;

/**
 * Class handling batch loading of files 
 * @author rfu
 *
 */
public class MultiLoader {

	private MessageCenter msg = null;
	// HashTable<extension, loader> :
	private Hashtable<String, FileLoader> loaders = new Hashtable<String, FileLoader>();
	private List<GPXFile> gpxFiles = new ArrayList<GPXFile>();
	
	private boolean showProgress = true;
	private boolean reportPerFile = false; // fire event for each successfully loaded file (durchdenken!!)
	
	/**
	 * Default Constructor 
	 */
	public MultiLoader(MessageCenter msg) {
		this.msg = msg;
		
	}
	
	/**
	 * 
	 * @param files
	 */
	public void load(String[] files) {
		
		// pass 1: iterate through all filenames and
		// populate loader hashtable according to extensions 
	}
	
	
	public void clear() {
		gpxFiles.clear();
		loaders.clear();
	}

	public boolean getShowProgress() {
		return showProgress;
	}

	public void setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
	}
	
	public List<GPXFile> getFiles() {
		return gpxFiles;
	}
}
