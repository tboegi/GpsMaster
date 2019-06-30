package org.gpsmaster.gpxpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.gpsmaster.Const;

/**
 * Class to hold a GPX Extension hierarchy
 * basically, this is just an XML element with 
 * a list of XML elements as children 
 * @author rfu
 *
 */
public class GPXExtension implements TreeNode {

	protected String key = null;
	protected String value = null;
	private String namespace = null; // XML namespace prefix
	protected GPXExtension parent = null;
	
	// TODO replace ArrayList with Hashtable<GPXExtension.key, GPXExtension>
	protected List<GPXExtension> subs = new ArrayList<GPXExtension>();
	
	/***
	 * Create the "top level" <extensions> element
	 */
	public GPXExtension() {
		this.key = Const.TAG_EXTENSIONS;
	}
	
	/**
	 * 
	 * @param key
	 */
	public GPXExtension(String key) {
		this.key = key;
	}
	
	/**
	 * Instantiate this class as clone of an existing {@link GPXExtension} object
	 * @param source
	 */
	public GPXExtension(GPXExtension source) {
		this.key = source.key;
		this.value = source.value;
		this.namespace = source.namespace;
		
		for (GPXExtension sourceSub : source.getExtensions()) {
			subs.add(sourceSub);
		}
	}
	/**
	 * Constructor
	 * @param key
	 * @param value
	 */
	public GPXExtension(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value of this element
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @param key
	 * @return value or NULL if not found
	 */
	public String getSubValue(String key) {
		GPXExtension ext = getExtension(key);
		if (ext != null) {
			return ext.getValue();
		}
		return null;
	}
	
	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Add the given element as sub element
	 * An existing element with the same key as the given element will be overwritten.
	 * @param extension
	 */
	public void add(GPXExtension extension) {
		remove(extension.getKey());		
		subs.add(extension);
	}
	
	/**
	 * Convenience method to add a key/value pair as subelement
	 * @param key
	 * @param value
	 */
	public void add(String key, String value) {
		add(new GPXExtension(key, value));
	}
	
	/**
	 * Remove the element with the given key from the list of sub elements
	 * If no element exists with the given key, no action is taken.
	 * @param key
	 */
	public void remove(String key) {
		GPXExtension ext = getExtension(key);
		if (ext != null) {
			subs.remove(ext);
		}
	}
	
	/**
	 * Get the sub element containing the given key
	 * @param key
	 * @return {@link GPXExtension} or NULL if not found
	 */
	public GPXExtension getExtension(String key) {
		for (GPXExtension ext : subs) {
			if (ext.getKey().equals(key)) {
				return ext;
			}
		}
		return null;
	}
	
	/**
	 * check if one of the elements in the next level contains the given key
	 * (non-recursive)
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {		
		return (getExtension(key) != null);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<GPXExtension> getExtensions() {
		return subs;
	}

	// methods implementing TreeNode interface
	
	@Override
	public Enumeration<GPXExtension> children() {
		return Collections.enumeration(subs);
	}

	@Override
	public boolean getAllowsChildren() {
		return (value == null); // ?
	}

	@Override
	public TreeNode getChildAt(int pos) {
		return subs.get(pos);
	}

	@Override
	public int getChildCount() {
		return subs.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return subs.indexOf(node);
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {		
		return (value != null);
	}
	
	@Override
	public String toString() {
		return key + " " + value;
	}
}
