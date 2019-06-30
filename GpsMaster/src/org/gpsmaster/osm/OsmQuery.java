package org.gpsmaster.osm;

import java.util.HashMap;
import java.util.Map;

import eu.fuegenstein.gis.GeoBounds;

public class OsmQuery {

	// boundaries
	
	public final static int NODE = 0;
	public final static int WAY = 1;
	public final static int RELATION = 2;
	
	private boolean useRegExp = false;
	private boolean caseSensitive = false;
	private int type = 0;
	private int radius = 0;
	private GeoBounds bounds = null;
	private Map<String, String> tags = new HashMap<String, String>();
	
	/**
	 * 
	 */
	public OsmQuery() {
		
	}
	
	/**
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}


	/**
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}



	public int getRadius() {
		return radius;
	}


	public void setRadius(int radius) {
		this.radius = radius;
	}


	public GeoBounds getGeoBounds() {
		return bounds;
	}


	public void setGeoBounds(GeoBounds bounds) {
		this.bounds = bounds;
	}


	public void addTag(String key, String value) {
		tags.put(key, value);
	}
	
	public Map<String, String> getTags() {
		return tags;
	}


	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}


	public boolean isUseRegExp() {
		return useRegExp;
	}


	public void setUseRegExp(boolean useRegExp) {
		this.useRegExp = useRegExp;
	}


	public boolean isCaseSensitive() {
		return caseSensitive;
	}


	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}


	/**
	 * 
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<query type=\"");

		switch(type) {
		case NODE:
			sb.append("node");
			break;
		case WAY:
			sb.append("way");
			break;
		case RELATION:
			sb.append("relation");
			break;
		}
		sb.append("\">");
						
		if (radius > 0) {
			sb.append(String.format("<around radius=\"%d\"/>", radius));
		}
		
		for (String key : tags.keySet()) {
			sb.append("<has-kv k=\"" + key + "\" ");
			String value = "";
			if (useRegExp || caseSensitive) {
				sb.append("regv=");
			} else {
				sb.append("v=");
			}
			if (caseSensitive) {
				for (char c : tags.get(key).toCharArray()) {
					value += "[" + c + "]";// TODO:  uU aA ... 
					
				}
			} else {
				 value = tags.get(key);
			}
			sb.append("\""+value+"\"/>");
		}
		if (bounds != null) {
			sb.append(String.format("<bbox-query e=\"%.4f\" n=\"%.4f\" s=\"%.4f\" w=\"%.4f\"/>", 
					bounds.getE(), bounds.getN(), bounds.getS(), bounds.getW()));
		}
		
		sb.append("</query>");		
		return sb.toString();
	}
}
