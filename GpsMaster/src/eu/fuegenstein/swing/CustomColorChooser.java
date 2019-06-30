package eu.fuegenstein.swing;

import java.util.List;

import javax.swing.JColorChooser;

public class CustomColorChooser extends JColorChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3338683845322271954L;

	private CustomColorChooserPanel customPanel = null;
	
	/**
	 * Default Constructor
	 */
	public CustomColorChooser() {
		super();
		customPanel = new CustomColorChooserPanel();
		this.addChooserPanel(customPanel);
	}
	
	public void setCustomColors(List<NamedColor> colors) {
		customPanel.setColors(colors);
	}
	
	public List<NamedColor> getCustomColors() {
		return customPanel.getColors();
	}
	
}