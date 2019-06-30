package org.gpsmaster.dialogs;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.gpsmaster.gpxpanel.GPXExtension;

/**
 * Renderer for {@link GPXExtension} tree nodes
 *  
 * @author rfu
 *
 * TODO achieve the following layout:
 *  - key/value node: key left aligned, value right aligned 
 *  - single line (border) horizontally from far left to far right
 *  
 *  https://explodingpixels.wordpress.com/2008/06/02/making-a-jtreecellrenderer-fill-the-jtree/
 *  
 */
public class GpxExtensionTreeRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5487673706040692095L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
    	
    	GPXExtension ext = (GPXExtension) value;
    	String extKey = ext.getKey();
    	String extValue = ext.getValue();
    	
    	JPanel nodePanel = new JPanel();
    	nodePanel.setBackground(Color.WHITE);
    	nodePanel.setLayout(new BoxLayout(nodePanel, BoxLayout.X_AXIS));
    	// nodePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
    	
    	JLabel keyLabel = new JLabel();
    	keyLabel.setText(extKey);
    	keyLabel.setForeground(Color.BLUE);
    	keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);    	
    	nodePanel.add(keyLabel);
    	
    	if (extValue != null) {
    		nodePanel.add(Box.createHorizontalGlue());
    		JLabel valueLabel = new JLabel();
    		valueLabel.setText(" = " + extValue); // TODO right alignment
    		valueLabel.setForeground(Color.BLUE);
    		valueLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
    		nodePanel.add(valueLabel);
    	}
    	return nodePanel;
    }
}
