package eu.fuegenstein.parameter;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

public class RadioButtonParameter extends CommonParameter {

	private String selected = null;
	private List<String> values = new ArrayList<String>();

	private ActionListener actionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton rb = (JRadioButton) e.getSource();
			setValue(rb.getText());			
		}
	};
	
	/**
	 * 
	 * @param name
	 */
	public void addValue(String name) {
		values.add(name);
	}
	
	/**
	 * set the current (selected) value. must have been added before
	 * @param value
	 */
	public void setValue(String value) {
		setValueString(value);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return getValueString();
	}
	
	@Override
	public void setValueString(String textValue) {
		
			if (values.contains(textValue) == false) {
				throw new IllegalArgumentException(textValue);
			}
			selected = textValue;				
		
	}

	@Override
	protected void valueToString() {
		// nothing to do
		
	}

	@Override
	public String getValueString() {
		
		return selected;
	}
	
	/**
	 * 
	 */
	@Override
	public JPanel getGuiComponent(Dimension dimension) {
		JPanel panel = new JPanel();
		ButtonGroup group = new ButtonGroup();
		
		panel.setBackground(backgroundColor);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JLabel label = new JLabel(description);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label);
		
		for (String value : values) {
			JRadioButton rb = new JRadioButton(value);		
			rb.addActionListener(actionListener);
			rb.setBackground(backgroundColor);
			if (values.indexOf(value) == 0) {
				rb.setSelected(true);
			}
			panel.add(rb);
			group.add(rb);
		}
		return panel;
	}

}
