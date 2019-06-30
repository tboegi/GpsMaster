package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gpsmaster.Const;
import org.gpsmaster.filehub.TransferableItem;

import eu.fuegenstein.swing.ExtendedTable;
import eu.fuegenstein.util.LogEntry;


public class TransferStatusDialog extends GenericDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6893640537199180024L;
	
	private int logLevel = LogEntry.WARNING; // default:
	private final double RESIZEWEIGHT = 0.66f;

	private TransferableItemLogPanel logPanel = null;	
	private ExtendedTable itemTable = null;
	private TransferableItemTableModel tableModel = null;

	/**
	 * Constructor
	 * @param parentFrame
	 */
	public TransferStatusDialog(JFrame parentFrame) {
		super(parentFrame);
		setIcon(Const.ICONPATH_DIALOGS, "state-warning.png");

		setup();		
	}

	/**
	 * 
	 * @param fileHub
	 */
	public void addItems(List<TransferableItem> items) {
						
		for (TransferableItem item : items) {			
			tableModel.addItem(item);			
		}
		itemTable.minimizeColumnWidth(0, ExtendedTable.WIDTH_MAX);
		tableModel.fireTableDataChanged();
		if (tableModel.getRowCount() > 0) {
			itemTable.setRowSelectionInterval(0, 0);			
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<TransferableItem> getItems() {
		return tableModel.getItemList();
	}
		
	@Override
	public void begin() {		
		
		pack();
		setCenterLocation();
		setVisible(false);
		
	}

	@Override
	public String getTitle() {
		return "Transfer Status";
	}
	
	private void setup() {
		
		setLayout(new BorderLayout());
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setResizeWeight(RESIZEWEIGHT);
		add(splitPane, BorderLayout.CENTER);
		
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

		// button panel at bottom
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		// clear button
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.clear();				
				tableModel.fireTableDataChanged();
				logPanel.setLog(null);
				firePropertyChange(Const.PCE_LOGCHANGED, null, null);
				// TODO reset show transfer log dialog button
			}
		});		
		buttonPanel.add(clearButton);

		// close button
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);				
			}
		});		
		buttonPanel.add(closeButton);
	}

}
