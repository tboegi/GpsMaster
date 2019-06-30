package eu.fuegenstein.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JToolBar;
import javax.swing.JComponent;

/**
 * "Intelligent" Toolbar
 * helps to encapsulate Buttons and reactions to events
 * from the in- and outside into a single class
 * 
 *  * @author rfu
 *
 */
public class IToolbar extends JToolBar {

	private List<JComponent> components = new ArrayList<JComponent>();
	
	private String iconDirectory = null;
	private String toolbarPrefix = "";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4497453813363356727L;

	public String getIconDirectory() {
		return iconDirectory;
	}

	public void setIconDirectory(String iconDirectory) {
		this.iconDirectory = iconDirectory;
	}

	public String getPrefix() {
		return toolbarPrefix;
	}

	public void setPrefix(String toolbarPrefix) {
		this.toolbarPrefix = toolbarPrefix;
	}

}
