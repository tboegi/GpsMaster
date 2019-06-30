package eu.fuegenstein.swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


/**
 * Class extending the standard {@link JTable} with additional functionality
 * 
 * @author rfu
 *
 */
public class ExtendedTable extends JTable {
	
	public final static int WIDTH_MIN = 0;
	public final static int WIDTH_MAX = 1;
	public final static int WIDTH_PREFERRED = 2;
	
	private int padding = 6;  
	
	private static final long serialVersionUID = 4503840868557738083L;

	/**
	 * 
	 * @param tableModel
	 */
	public ExtendedTable(TableModel tableModel) {
		super(tableModel);
	}

	/**
	 * @return the padding
	 */
	public int getColumnWidthPadding() {
		return padding;
	}

	/**
	 * @param padding number of pixels added to calculated width
	 * 
	 */
	public void setColumnWidthPadding(int padding) {
		this.padding = padding;
	}

	/**
	 * set the width of a column according to the content.
	 * @param col column index, zero based
	 * @param setSize which size property to set 
	 * TODO on subsequent calls, maxWidth is smaller than actual max width of column content 
	 * 		(works on first call only)
	 */
	public void minimizeColumnWidth(int col, int setWidth) {
		TableColumnModel columnModel = getColumnModel();
		
		int maxWidth = 0;
        for (int row = 0; row < getRowCount(); row++) {
            TableCellRenderer cellRenderer = getCellRenderer(row, col);
            Object value = getValueAt(row, col);
            Component comp = cellRenderer.getTableCellRendererComponent(this, value, false, false, row, col);
            maxWidth = Math.max(comp.getSize().width, maxWidth);
            maxWidth = Math.max(comp.getMaximumSize().width, maxWidth);
        }
        TableColumn column = columnModel.getColumn(col);
        TableCellRenderer headerRenderer = column.getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = getTableHeader().getDefaultRenderer();
        }        
        Object headerValue = column.getHeaderValue();
        Component headerComp = headerRenderer.getTableCellRendererComponent(this, headerValue, false, false, 0, col);
        maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);
        
        // note some extra padding
        switch(setWidth) {
        	case WIDTH_MAX:
        		column.setMaxWidth(maxWidth + padding);
        		break;
        	case WIDTH_MIN:
        		column.setMinWidth(maxWidth + padding);
        		break;
        	case WIDTH_PREFERRED:
        		column.setPreferredWidth(maxWidth + padding);
        		break;	        			        	
        }
        //IntercellSpacing * 2 + 2 * 2 pixel instead of taking this value from Borders
        // System.out.println(maxWidth);
	}
}
