package eu.fuegenstein.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A layout manager that places widgets into corners. 
 * 
 * Only getPreferredSize() is used to calculate the position
 * 
 * @author rfu
 *
 */
public class WidgetLayout implements LayoutManager2 {

	/**
	 * Helper class representing a corner
	 * 
	 * @author rfu
	 *
	 */
	private class Corner {
		
		private int myCorner = 0; // where am I?
		private Point offset = new Point(0, 0); 

		private List<Widget> widgets = new ArrayList<Widget>();
		
		/* since this is just a private helper class,
			we don't bother with getters/setters, but
			access some members directly.
		*/
		public int orientation = HORIZONTAL;
		
		/**
		 * Constructor
		 * @param where this corner is located
		 */
		public Corner(int where) {
			myCorner = where;
			
			switch(where) {
				case TOP_LEFT:
					break;
				case TOP_RIGHT:
					break;
				case BOTTOM_LEFT:
					break;
				case BOTTOM_RIGHT:
					break;
				default:
					throw new IllegalArgumentException("Corner.Corner");					
			}
		}
	
		/**
		 * 
		 * @param parent
		 */
		private void layoutTopRight(Container parent) {
			
			Point p = new Point(parent.getBounds().width, parent.getBounds().y);
			p.x += offset.x;
			p.y += offset.y;
			
			for (Widget widget : widgets) {
			
				int width = widget.getWidth();
				int height = widget.getHeight();
				p.x = p.x - width;
				
				if (orientation == VERTICAL) {
					p.y += height;
				}
				widget.setBounds(p.x, p.y, width, height);
			}
		}
		
		/**
		 * 
		 * @param parent
		 */
		private void layoutTopLeft(Container parent) {
			
			Point p = new Point(parent.getBounds().x, parent.getBounds().y);
			p.x += offset.x;
			p.y += offset.y;

			for (Widget widget : widgets) {				
				int width = widget.getPreferredSize().width;
				int height = widget.getPreferredSize().height;
				widget.setBounds(p.x, p.y, width, height);
				
				// determine reference point (upper left corner) for next widget
				if (orientation == HORIZONTAL) {
					p.x += width;
				} else {
					p.y += height;
				}
			}
		}

		/**
		 * 
		 * @param parent
		 */
		private void layoutBottomLeft(Container parent) {
			if (widgets.size() > 0) {								

				Point p = new Point(parent.getBounds().x, parent.getBounds().height);
				p.x += offset.x;
				p.y += offset.y;
			
				p.y = p.y - widgets.get(0).getPreferredSize().height;			
		
				for (Widget widget : widgets) {
					int width = widget.getPreferredSize().width;
					int height = widget.getPreferredSize().height;
					widget.setBounds(p.x, p.y, width, height);

					if (orientation == HORIZONTAL) {
						p.x += width;
					} else {
						p.y -= height;
					}
				}
			}
		}
		
		/**
		 * Layout all widgets in this corner
		 * 
		 * @param parent
		 */
		private void layout(Container parent) {
			switch(myCorner) {
				case TOP_RIGHT:
					layoutTopRight(parent);
					break;
				case TOP_LEFT:
					layoutTopLeft(parent);
					break;
				case BOTTOM_LEFT:
					layoutBottomLeft(parent);
					break;
			}			
		}
		
		/**
		 * 
		 * @param widget
		 */
		private void add(Widget widget) {
			widgets.add(widget);
		}
		
		/**
		 * 
		 * @param widget
		 */
		private void remove(Widget widget) {
			widgets.remove(widget);
		}
	}

	/*
	 * The corner in which to place the component
	 */
	public static final int TOP_LEFT = 0;
	public static final int TOP_RIGHT = 1;
	public static final int BOTTOM_LEFT = 2;
	public static final int BOTTOM_RIGHT = 3;
	
	/*
	 * The direction in which the components are laid out on screen
	 */
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	private Corner[] corners = new Corner[4]; // the 4 corners of the world
	
	
	/**
	 * Default Constructor
	 */
	public WidgetLayout() {
		super();
		
		corners[TOP_LEFT] = new Corner(TOP_LEFT);
		corners[TOP_RIGHT] = new Corner(TOP_RIGHT);
		corners[BOTTOM_LEFT] = new Corner(BOTTOM_LEFT);
		corners[BOTTOM_RIGHT] = new Corner(BOTTOM_RIGHT);
	}
	
	/**
	 * 
	 * @param corner the corner to set the orientation for
	 * @param orientation
	 */
	public void setCornerOrientation(int corner, int orientation) {
		if ((orientation != HORIZONTAL) && (orientation != VERTICAL)) {
			throw new IllegalArgumentException("Orientation");
		}
		
		
		corners[corner].orientation = orientation;
	}
	
	/**
	 * relocate 
	 * @param corner
	 * @param offset offset in pixels relative(!) to corner 
	 */
	public void setCornerOffset(int corner, Point offset) {
		checkCorner(corner);
		corners[corner].offset = offset;		
	}
	


	@Override
	public void addLayoutComponent(String name, Component comp) {
		checkComp(comp);
		Widget w = (Widget) comp;
		if (w.getSize().height == 0 || w.getSize().width == 0) {
			w.setSize(w.getMaximumSize()); // preferredSize() ?
		}
		corners[w.getCorner()].add(w);
	}

	/**
	 * 
	 */
	@Override
	public void layoutContainer(Container parent) {
		for (int i = 0; i < 4; i++) {
			corners[i].layout(parent);
		}		
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		System.out.println("minimumLayoutSize");
		return null;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		System.out.println("preferredLayoutSize");
		return null;
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		checkComp(comp);
		Widget w = (Widget) comp;
		corners[w.getCorner()].remove(w);		
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		addLayoutComponent("", comp);
		// throw new NotImplementedException();		
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		System.out.println("getLayoutAlignmentX");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		System.out.println("getLayoutAlignmentY");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
		// System.out.println("invalidateLayout");
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		System.out.println("maximumLayoutSize");
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Check if component is of type {@link Widget}
	 * @param comp
	 */
	private void checkComp(Component comp) {
		if (comp instanceof Widget == false) {
			throw new IllegalArgumentException("Only components of type "+Widget.class.getCanonicalName()+" supported.");
		}
	}
	
	/**
	 * 
	 * @param corner
	 */
	private void checkCorner(int corner) {
		if ((corner < TOP_LEFT) && (corner > BOTTOM_RIGHT)) {
			throw new IllegalArgumentException("Corner");
		}

	}
}
