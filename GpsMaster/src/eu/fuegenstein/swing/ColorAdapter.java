package eu.fuegenstein.swing;

import java.awt.Color;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Class implementing an adapter required for 
 * (un)marshaling java.awt.Color objects
 * 
 * @author rfu
 * https://jaxb.java.net/guide/XML_layout_and_in_memory_data_layout.html
 *
 */
public class ColorAdapter extends XmlAdapter<String,NamedColor> {

	public NamedColor unmarshal(String s) {
	    return new NamedColor(Color.decode(s));
	  }
	
	  public String marshal(NamedColor c) {
	    return "#"+Integer.toHexString(c.getRGB());
	  }
}
