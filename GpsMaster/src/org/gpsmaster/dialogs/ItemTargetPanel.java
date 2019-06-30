package org.gpsmaster.dialogs;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.gpsmaster.filehub.IItemTarget;

/**
 * Panel containing a single {@link IItemTarget} for enabling/disabling
 * @author rfu
 *
 */
public class ItemTargetPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3361993406556682667L;
	private IItemTarget target = null;
	private JCheckBox chkEnable = null;
	
	/**
	 * Constructor
	 * @param target
	 */
	public ItemTargetPanel(IItemTarget target) {
		this.target = target;
		setup();
	}
	
	/**
	 * 
	 * @return
	 */
	public IItemTarget getTarget() {
		return target;
	}
	
	private void setup() {
		
		setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
			
		chkEnable = new JCheckBox(target.getDescription());
		chkEnable.setSelected(target.isEnabled());
		chkEnable.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				target.setEnabled(!target.isEnabled());
				chkEnable.setSelected(target.isEnabled());
			}
		});
		add(chkEnable);
	}
}
