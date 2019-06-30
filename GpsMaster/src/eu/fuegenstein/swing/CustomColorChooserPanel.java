package eu.fuegenstein.swing;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * Panel for {@link JColorChooser}, allowing the selection
 * of a color out of a list of predefined colors.
 * 
 * @author rfu
 */
public class CustomColorChooserPanel extends AbstractColorChooserPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5669951982671747823L;

	private List<NamedColor> colors = new ArrayList<NamedColor>();
	private String displayName = "Palette";
	
	private MouseAdapter mouseListener = null;
		
	/**
	 * Default constructor
	 */
	public CustomColorChooserPanel() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
		mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	if (e.getSource() instanceof JLabel) {
                		PlainColorIcon icon = (PlainColorIcon) ((JLabel) e.getSource()).getIcon();
                		getColorSelectionModel().setSelectedColor(icon.getColor());
                	}                    
                }
            }
		};
	}
	
	/**
	 * 
	 * @return
	 */
	public List<NamedColor> getColors() {
		return colors;
	}
	/**
	 * Set list of colors to choose from 
	 * @param colors
	 */
	public void setColors(List<NamedColor> colors) {
		this.colors = colors;
		make();
	}
	
	/**
	 * 
	 * @param color
	 */
	public void addColor(NamedColor color) {
		colors.add(color);
	}
	
	@Override
	protected void buildChooser() {
		
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 
	 * @param name
	 */
	public void setDisplayName(String name) {
		displayName = name;
	}
	
	@Override
	public Icon getLargeDisplayIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getSmallDisplayIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateChooser() {
		// TODO Auto-generated method stub
		
	}

	private void make() {
		for (NamedColor color : colors) {
			JLabel label = new JLabel(new PlainColorIcon(color));
			label.setToolTipText(color.getName());
			label.setBorder(BorderFactory.createEtchedBorder());
			label.addMouseListener(mouseListener);
			add(label);			
		}
		
	}
}
