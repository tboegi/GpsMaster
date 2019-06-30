package eu.fuegenstein.swing;

import java.awt.Color;

/**
 * A color with a name
 * @author rfu
 *
 */
public class NamedColor extends Color {
	
	private String name = "";

	/**
	 * 
	 */
	private static final long serialVersionUID = 7891574718061707373L;
	
	/**
	 * 
	 */
	public NamedColor() {
		super(0, 0, 0);						
	}
	
	// custom constructors
	public NamedColor(Color color, String name) {
		super(color.getRGB());
		this.name = name;
	}

	public NamedColor(int rgb, String name) {
		super(rgb);
		this.name = name;
	}
	
	public NamedColor(Color color) {
		super(color.getRGB());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;		
	}

}
