package org.gpsmaster.dialogs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.gpsmaster.filehub.IItemTarget;

/**
 * Panel for the selection (enable/disable) of item targets 
 * @author rfu
 *
 */
public class ItemTargetSelectionPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8966004222072535995L;
	
	private List<ItemTargetPanel> targetPanels = new ArrayList<ItemTargetPanel>();
	
	/**
	 * Constructor
	 * @param targets
	 */
	public ItemTargetSelectionPanel(List<IItemTarget> targets) {

		// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// setAlignmentX(Component.LEFT_ALIGNMENT);
		
		for (IItemTarget target : targets) {
			ItemTargetPanel panel = new ItemTargetPanel(target);
			targetPanels.add(panel);
			
			add(panel);
		}	   	   
	}

}
