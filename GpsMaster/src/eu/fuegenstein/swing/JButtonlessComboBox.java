package eu.fuegenstein.swing;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 * A {@link JComboBox} without the "arrow down" button next to the selected item.
 * Selection of an item is done by clicking on the selected item first.
 * 
 * @author rfu
 * @param <E>
 *
 * TODO hidden button still shows an empty frame
 */
public class JButtonlessComboBox<E> extends JComboBox<E>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2726318575505863481L;
	
	/**
	 * 
	 */
	public JButtonlessComboBox() {
		super();
		for (Component c : getComponents()) {
			if (c instanceof JButton) {
				c.setSize(new Dimension(1, 1));
				c.setVisible(false);
			}
		}
	}
}
