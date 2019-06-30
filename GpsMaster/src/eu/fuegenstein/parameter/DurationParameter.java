package eu.fuegenstein.parameter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * time span / duration in seconds
 * @author rfu
 *
 */
public class DurationParameter extends CommonParameter {
	
	private int hr = 1; // default: 1 hour
	private int min = 0;
	private int sec = 0;
	
	public DurationParameter(long value) {
		super();		
		// format = "dd HH:mm:ss";		
		format = "HH:mm:ss";
		
	}
	
	/**
	 * set the duration in seconds
	 * @param value
	 * @return
	 */
	public void setValue(long value) {
		hr = (int) (value / 3600);
		min = (int) ((value % 3600) / 60);
		sec = (int) (value % 60);
	}
	
	/**
	 * get the duration value
	 * @return duration in seconds
	 */
	public long getValue() {
		return (3600 * hr + 60 * min + sec);
	}
	
	@Override
	public void setValueString(String textValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void valueToString() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getValueString() {
		
		return String.format("mm:hh:ss", hr, min, sec);
	}

	/**
	 * calculate value in seconds) from hr min sec 
	 */
	private void hmsToValue() {
		
	}

	@Override
	public JPanel getGuiComponent(Dimension dimension) {
		JPanel panel = new JPanel();
		
		final JSpinner hrSpinner = new JSpinner(new SpinnerNumberModel(hr, 0, 24, 1));
		final JSpinner minSpinner = new JSpinner(new SpinnerNumberModel(min, 0, 59, 1));
		final JSpinner secSpinner = new JSpinner(new SpinnerNumberModel(sec, 0, 59, 1));
		
		ChangeListener changeListener = new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				hr = ((Integer) hrSpinner.getValue()).intValue();
				min = ((Integer) minSpinner.getValue()).intValue();
				sec = ((Integer) secSpinner.getValue()).intValue();
				hmsToValue();
			}
		};
		
		panel.setBackground(Color.WHITE);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JLabel label = new JLabel(description);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label);
			
		hrSpinner.setEditor(new JSpinner.NumberEditor(hrSpinner, "00"));
		hrSpinner.addChangeListener(changeListener);
		panel.add(hrSpinner);
		panel.add(new JLabel("hr"));
				
		minSpinner.setEditor(new JSpinner.NumberEditor(minSpinner, "00"));
		minSpinner.addChangeListener(changeListener);
		panel.add(minSpinner);
		panel.add(new JLabel("min"));
				
		secSpinner.setEditor(new JSpinner.NumberEditor(secSpinner, "00"));
		secSpinner.addChangeListener(changeListener);
		panel.add(secSpinner);	
		panel.add(new JLabel("sec"));
		return panel;
	}
}
