package org.gpsmaster.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.gpsmaster.marker.MeasureMarker;

import eu.fuegenstein.swing.Widget;
import eu.fuegenstein.swing.WidgetLayout;

/**
 * 
 * @author rfu
 *
 */
public class DistanceWidget extends Widget {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7099367537255790218L;
	
	private DistanceTableModel tableModel = new DistanceTableModel();	
	private JTable table = new JTable(tableModel);
	
	public DistanceWidget() {
		super(WidgetLayout.TOP_LEFT);
		setLayout(new BorderLayout());
		
		/* default render for alignment: RIGHT */
	    DefaultTableCellRenderer rightRenderer = (DefaultTableCellRenderer) table.getDefaultRenderer(String.class);
	    rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		
	    /* 1st and 2nd column: CENTER */	    
	    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	    centerRenderer.setHorizontalAlignment( SwingConstants.CENTER);
	    table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
	    table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
	    
		/* setup table */
		table.setEnabled(false);
		table.setGridColor(Color.LIGHT_GRAY);
		JTableHeader header = table.getTableHeader();
		header.setBackground(BACKGROUNDCOLOR);		
				
		/* setup layout */
		setBackground(BACKGROUNDCOLOR);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		add(header, BorderLayout.NORTH);
		add(table, BorderLayout.CENTER);
		minimizeTable();
	}

	/**
	 * 
	 * @param m1
	 * @param m2
	 * @param dist
	 * @param direct
	 * @param speed
	 */
	public void addValues(MeasureMarker m1, MeasureMarker m2, String dist, String direct, String duration, String speed) {
		tableModel.addValues(m1.getName(), m2.getName(), dist, direct, duration, speed);
		minimizeTable();
		// setMaximumSize(table.getPreferredSize());
	}
	
	/**
	 * 
	 */
	public void clear() {
		tableModel.clear();
	}


	/**
	 * 
	 * @author mKorbel @ stackoverflow.com
	 * 
	 */
	private void minimizeTable() {
	    TableColumnModel columnModel = table.getColumnModel();
	    for (int col = 0; col < table.getColumnCount(); col++) {
	        int maxWidth = 0;
	        for (int row = 0; row < table.getRowCount(); row++) {
	            TableCellRenderer rend = table.getCellRenderer(row, col);
	            Object value = table.getValueAt(row, col);
	            Component comp = rend.getTableCellRendererComponent(table, value, false, false, row, col);
	            maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
	        }
	        TableColumn column = columnModel.getColumn(col);
	        TableCellRenderer headerRenderer = column.getHeaderRenderer();
	        if (headerRenderer == null) {
	            headerRenderer = table.getTableHeader().getDefaultRenderer();
	        }
	        Object headerValue = column.getHeaderValue();
	        Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, col);
	        maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);
	        // note some extra padding
	        column.setPreferredWidth(maxWidth + 6);//IntercellSpacing * 2 + 2 * 2 pixel instead of taking this value from Borders
	    }
	    // table.setPreferredScrollableViewportSize(table.getPreferredSize());
	    // setSize(table.getPreferredSize());
	    // System.out.println("widget.height:" + getSize().height);
	}
}
