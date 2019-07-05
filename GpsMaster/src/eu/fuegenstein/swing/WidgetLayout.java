package eu.fuegenstein.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A layout manager that places widgets into corners.
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
		private List<Widget> widgets = new ArrayList<Widget>();
		private int xMult = 1;
		private int yMult = 1;


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
					xMult = 1;
					yMult = 1;
					break;
				case TOP_RIGHT:
					xMult = -1;
					yMult = 1;
					break;
				case BOTTOM_LEFT:
					xMult = 1;
					yMult = -1;
					break;
				case BOTTOM_RIGHT:
					xMult = -1;
					yMult = -1;
					break;
				default:
					throw new IllegalArgumentException("Corner.Corner");
			}
		}

		/**
		 * Layout all widgets in this corner
		 *
		 * @param parent
		 */
		private void layout(Container parent) {
			Dimension base = getBase(parent);
			for (Widget widget : widgets) {

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

		/**
		 *
		 */
		public void reset() {

		}

		/**
		 *
		 * @param parent
		 * @return
		 */
		private Dimension getBase(Container parent) {

			switch(myCorner) {
			case TOP_LEFT:
				return new Dimension(parent.getBounds().x, parent.getBounds().y);


			}

			return null;
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

		if ((corner < TOP_LEFT) && (corner > BOTTOM_RIGHT)) {
			throw new IllegalArgumentException("Corner");
		}

		corners[corner].orientation = orientation;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		checkComp(comp);
		Widget w = (Widget) comp;
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
		return null;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		// TODO Auto-generated method stub
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
		throw new NotImplementedException();
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
		// TODO Auto-generated method stub

	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
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
}
