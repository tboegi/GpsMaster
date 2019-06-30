package org.gpsmaster.dialogs;

import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import eu.fuegenstein.parameter.CommonParameter;

/**
 * Class wrapping GUI elements around a {@link CommonParameter}
 * @author rfu
 *
 */
public class CommonParameterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6300127981013202377L;

	protected CommonParameter param = null;
	
	protected JLabel label = null;
	protected JTextField textField = null;
	protected FocusListener textfieldListener = new FocusListener() {

		String previous = "";
		
		@Override
		public void focusGained(FocusEvent arg0) {
			previous = ((JTextField) arg0.getSource()).getText();			
		}

		@Override
		public void focusLost(FocusEvent arg0) {

			JTextField textField = (JTextField) arg0.getSource();
			try {
				param.setValueString(textField.getText());
			} catch (NumberFormatException e) {
				textField.setText(previous);
			}				
		}		
	};	

	
	/**
	 * Constructor
	 * @param param
	 */
	public CommonParameterPanel(CommonParameter param) {
		
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		label = new JLabel(param.getDescription());
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		add(label);
		JTextField textField = new JTextField();
		textField.setHorizontalAlignment(param.getAlignment());
		/*
		if (dimension != null) {
			textField.setPreferredSize(dimension);
		}
		*/
		textField.setText(param.getValueString());		
		textField.addFocusListener(textfieldListener);
		
		add(textField);	
	}
	
}
