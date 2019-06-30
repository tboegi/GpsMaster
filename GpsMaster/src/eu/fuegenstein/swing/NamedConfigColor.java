package eu.fuegenstein.swing;

import java.awt.Color;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Helper/proxy class to marshal/unmarshal {@link NamedColor}s. 
 * 
 * @author rfu
 *
 */
public class NamedConfigColor {
	
	
	private String colorString = "";
	
	private String name = "";

	/**
	 * Default COnstructor
	 */
	public NamedConfigColor() {
		
	}
	/**
	 * Constructor with NamedColor
	 * @param color
	 */
	public NamedConfigColor(NamedColor color) {
		name = color.getName();
		colorString = String.format("%02x%02x%02x%02x", 
    			color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRGBA() {		
		return colorString;
	}	
	
	public void setRGBA(String rgba) {
		colorString = rgba;
	}
	
	@XmlTransient
	public NamedColor getNamedColor() {
		Color color;
		try {
			int r = Integer.parseInt(colorString.substring(0, 2), 16);
			int g = Integer.parseInt(colorString.substring(2, 4), 16);
			int b = Integer.parseInt(colorString.substring(4, 6), 16);
			int a = Integer.parseInt(colorString.substring(6, 8), 16);
			color = new Color(r, g, b, a);
		} catch (NumberFormatException e) {
			color = Color.WHITE;
		}	
		return new NamedColor(color, name);
	}

}
