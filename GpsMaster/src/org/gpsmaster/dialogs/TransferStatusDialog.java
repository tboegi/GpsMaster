package org.gpsmaster.dialogs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gpsmaster.Const;
import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.TransferableItem;

import eu.fuegenstein.swing.ExtendedTable;
import eu.fuegenstein.util.LogEntry;


public class TransferStatusDialog extends GenericDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6893640537199180024L;
	
	private final double RESIZEWEIGHT = 0.66f;

	private TransferableItemLogPanel logPanel = null;	
	private ExtendedTable itemTable = null;
	private TransferableItemTableModel tableModel = null;

	private FileHub fileHub = null;
	
	/**
	 * Constructor
	 * @param parentFrame
	 */
	public TransferStatusDialog(JFrame parentFrame) {
		super(parentFrame);
		setIcon(Const.ICONPATH_DIALOGS, "state-warning.png");

		setup();
		
		addWindowListener(new WindowAdapter() {
			// clear list on closing
			public void windowClosing(WindowEvent e) {
				if (fileHub != null) {
					fileHub.getProcessedItems().clear();
				}
			}
		});
	}

	/**
	 * 
	 * @param fileHub
	 */
	public void setFileHub(FileHub fileHub) {
		this.fileHub = fileHub;
		
		tableModel.clear();
		for (TransferableItem item : fileHub.getProcessedItems()) {
			if (item.getLog().getFailureState() != LogEntry.INFO) {
				tableModel.addItem(item);
			}
		}
		itemTable.minimizeColumnWidth(0, ExtendedTable.WIDTH_MAX);
		tableModel.fireTableDataChanged();
		if (tableModel.getRowCount() > 0) {
			itemTable.setRowSelectionInterval(0, 0);
		}
	}
	
	
	@Override
	public void begin() {		
		
		pack();
		setCenterLocation();
		setVisible(true);
		
	}

	@Override
	public String getTitle() {
		return "Transfer Status";
	}
	
	private void setup() {
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setResizeWeight(RESIZEWEIGHT);
		add(splitPane);
		
		tableModel = new TransferableItemStatusTableModel();
		
		// setup table
		itemTable = new ExtendedTable(tableModel);
		itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
				{
					final int numSelected = itemTable.getSelectedRowCount();
					if (numSelected > 0)
					{
						// TODO check: proper row when sorted?
						TransferableItem item = tableModel.getItem(itemTable.getSelectedRow());
						logPanel.setLog(item.getLog());
					}
				}				
			}
		});
		itemTable.setFillsViewportHeight(true);
		itemTable.getColumnModel().getColumn(0).setPreferredWidth(20); // transfer state
		splitPane.setTopComponent(new JScrollPane(itemTable));
	
		logPanel = new TransferableItemLogPanel();
		logPanel.setAutoHide(false);
		splitPane.setBottomComponent(logPanel);				
	}

}
