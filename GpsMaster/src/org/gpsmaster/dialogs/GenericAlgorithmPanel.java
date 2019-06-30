package org.gpsmaster.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.gpsmaster.GenericAlgorithm;

import eu.fuegenstein.parameter.CommonParameter;

/**
 * {@link JPanel} containing all GUI elements for a {@link GenericAlgorithm}
 * 
 * @author rfu
 *
 */
public class GenericAlgorithmPanel extends JPanel {

	protected JLabel nameLabel = new JLabel();
	protected JTextArea descLabel = new JTextArea();
	
	protected GenericAlgorithm algo = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8930736599707243527L;

	/**
	 * 
	 * @param algorithm
	 */
	public GenericAlgorithmPanel(GenericAlgorithm algorithm) {
		
		algo = algorithm;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	
		
		Font nameFont = new Font(getFont().getFamily(), Font.BOLD, getFont().getSize() + 2);
		nameLabel.setFont(nameFont);
		nameLabel.setText(algo.getName());
		nameLabel.setAlignmentX(0.0f);
		add(nameLabel);
		
		if (algo.getDescription().isEmpty() == false) {
			// descLabel = new JTextArea();
			descLabel.setFont(getFont());
			descLabel.setPreferredSize(new Dimension(220, 120));
			descLabel.setText(algo.getDescription());
			descLabel.setEditable(false);
			descLabel.setLineWrap(true);
			descLabel.setWrapStyleWord(true);
			descLabel.setAlignmentX(0.0f);
			
			add(descLabel);
			// algorithmPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		}
		
		for (CommonParameter p : algo.getParameters()) {
			JPanel paramPanel = p.getGuiComponent(new Dimension(40, 20));
			paramPanel.setAlignmentX(0.0f);
			add(paramPanel);
		}
		
	}
	
	/**
	 * 
	 */
	public void setBackground(Color color) {
		
		super.setBackground(color);	
		if (nameLabel != null) {
			nameLabel.setBackground(color);
		}
		if (descLabel != null) {
			descLabel.setBackground(color);
		}
		
	}
}
