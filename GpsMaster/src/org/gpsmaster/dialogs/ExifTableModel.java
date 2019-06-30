package org.gpsmaster.dialogs;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.drew.metadata.Tag;

/**
 * table model for EXIF data table in ImageViewer
 * @author rfu
 *
 */
public class ExifTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8552472242855217804L;
	private List<Tag> tags = null;
	
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() { 
		if (tags == null) {
			return 0;
		}
		return tags.size();
	}

	/**
	 * @param inColNum column number
	 * @return column label for given column
	 */
	public String getColumnName(int inColNum)
	{
		switch(inColNum) {
		case 0:
			return "Directory";
		case 1:
			return "Tag Name";
		case 2:
			return "Description";
		default:
			return "----";
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row < tags.size()) {
			switch(col) {
			case 0:
				return tags.get(row).getDirectoryName();
			case 1:
				return tags.get(row).getTagName();
			case 2:
				return tags.get(row).getDescription();
			default:
				return "----";
			}
		}
		
		return null;
	}	
}
