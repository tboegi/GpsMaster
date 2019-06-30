package org.gpsmaster.fileloader;

import java.util.List;
import java.util.ArrayList;


public class FileLoaderFactory {
	
	// list of supported extensions
	List<String> extensions = new ArrayList<String>(); 
	List<FileLoader> loaders = new ArrayList<FileLoader>();
	
	public FileLoaderFactory() {
		loaders.clear();
		// TODO build list of available loader classes dynamically
		loaders.add(new GpxLoader());
		loaders.add(new KmlLoader());
		// loaders.add(new JaxbLoader());
		// loaders.add("org.gpsmaster.fileloader.XmlLoader");		
	}
	
	/**
	 * 
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public List<String> getExtensions() {
		getExtensionList();
		return extensions;
	}
	
	/**
	 * returns the loader class which supports the requested extension
	 * @param extension
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public FileLoader getLoader(String extension) throws ClassNotFoundException {
		for (FileLoader loader : loaders) {
			if (loader.getSupportedExtensions().contains(extension)) {
				return loader;
			}
		}
		throw new ClassNotFoundException(extension);			
	}
	
	/**
	 * 
	 * @throws ClassNotFoundException
	 */
	private void getExtensionList() {
		extensions.clear();
		for (FileLoader loader : loaders) {
			extensions.addAll(loader.getSupportedExtensions());
		}
		// if a ClassNotFoundException is thrown: ignore it.
		// extensions of classes not loadable will not be added.  
	}
	
	
	
}
