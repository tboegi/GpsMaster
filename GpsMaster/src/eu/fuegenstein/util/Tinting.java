package eu.fuegenstein.util;

import java.awt.Color;
import java.util.LinkedHashMap;

public class Tinting {

	protected LinkedHashMap<Integer, Color> colorTable = new LinkedHashMap<Integer, Color>();
	
	public Tinting() {
		// nothing yet
	}
	
	public Color getColor(int value) {
			int prev = 0;
			
			for (int c : colorTable.keySet()) {
				if ((value >= prev) && (value <= c)) {
					return colorTable.get(c);
				}
				prev = c;
			}
	
			return Color.BLACK;
	}
}
