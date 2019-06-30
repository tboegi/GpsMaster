package eu.fuegenstein.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassUtils {

	public static List<String> getResources(String packageName) throws IOException, URISyntaxException {
	
		URL url = ClassLoader.getSystemClassLoader().getResource(packageName);
		if (url.toString().startsWith("jar:")) {
			return getResourcesFromJar(url, packageName);
		} else if (url.toString().startsWith("file:")) {
			return getResourcesFromFilesystem(url, packageName);
		} else {
			throw new FileNotFoundException(url.toString());
		}
		
	}
	
	private static List<String> getResourcesFromFilesystem(URL url, String packageName) {
		ArrayList<String> result = new ArrayList<String>();
		File dir = new File(url.getPath());
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isFile()) {
					result.add(file.getName());
				}
			}
		}
		return result;
	}
	
	private static List<String> getResourcesFromJar(URL url, String packageName) throws IOException, URISyntaxException {
		ArrayList<String> result = new ArrayList<String>();

		String path = url.toString().replace("jar:", "").replace("file:", "");
		int pos = path.indexOf("!");
		if (pos > 0) {
			path = path.substring(0, pos);
		}
		if (packageName.endsWith("/") == false) {
			packageName = packageName.concat("/");
		}
		ZipInputStream zip = new ZipInputStream(new FileInputStream(path));
		ZipEntry entry = zip.getNextEntry();
		while(entry != null) {
			String name = entry.getName();
			if (name.startsWith(packageName) && (name.equals(packageName) == false)){
				result.add(name.replaceFirst(packageName, ""));
			}
			entry = zip.getNextEntry();
		}
		zip.close();

		return result;
	}
}
