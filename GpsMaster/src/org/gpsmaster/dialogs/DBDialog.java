package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.MultiLoader;
import org.gpsmaster.db.GpsEntry;
import org.gpsmaster.db.GpsStorage;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.swing.ExtendedTable;
import eu.fuegenstein.unit.UnitConverter;

/**
 * 
 * @author rfu
 *
 */
public class DBDialog extends GenericDialog implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1422533436670399564L;
	
	private GpsStorage db = null;
	private MessagePanel infoPanel = null;
	
	// table & related objects
	private ExtendedTable dbTable = null;
	private DbTableModel dbModel = null;

	private JButton btnLoad = null;
	private JButton btnImport = null;
	private JButton btnExport = null;
	private JButton btnDelete = null;
	private JButton btnRefresh = null;
	
	private PropertyChangeListener changeListener = new PropertyChangeListener() {
		// TODO does not work, is not called
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Const.PCE_REFRESHDB)) {
				refreshTable();
			}			
		}
	}; 			

	/**
	 * 
	 * @param parentFrame
	 * @param msg
	 * @param db
	 */
	public DBDialog(JFrame parentFrame, MessageCenter msg, GpsStorage db) {
		super(parentFrame, msg);
		this.db = db;
	}

	/**
	 * @return the db
	 */
	public GpsStorage getStorage() {
		return db;
	}

	/**
	 * @param db the db to set
	 */
	public void setStorage(GpsStorage db) {
		this.db = db;	
	}

	
	/**
	 * @return the changeListener
	 */
	public PropertyChangeListener getChangeListener() {
		return changeListener;
	}

	/**
	 * Setup swing components
	 * 
	 */
	@SuppressWarnings("serial")
	private void setup() {
		
		JPanel filterPanel = new JPanel();
		
		JPanel buttonPanel = new JPanel();
		
		getContentPane().setLayout(new BorderLayout());
		
		setIcon(Const.ICONPATH_DLBAR, "database.png");
				
		// initialise table		
		dbModel = new DbTableModel(db);
		dbTable = new ExtendedTable(dbModel) {
			// show activity as tooltip on MouseOver
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				Point p = e.getPoint();
				int rat = rowAtPoint(p); 
				if (rat > -1) {
			        int rowIndex = convertRowIndexToModel(rat);
			        int colIndex = columnAtPoint(p);		        
			        if ((colIndex == 5) && (rowIndex > -1)) {
			        	tip = dbModel.get(rowIndex).getActivity();
			        }
				}
				return tip;
			}
		};    
				
		dbTable.setGridColor(Color.LIGHT_GRAY);		
		dbTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		dbTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			// does not work
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				selectionButtonsEnabled(!lsm.isSelectionEmpty());
			}
		});

		dbTable.setAutoCreateRowSorter(true);
		
		dbTable.getColumnModel().getColumn(0).setMaxWidth(16);		
		// dbTable.setRowHeight(18);
		
	    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
	    rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
	    
	    DistanceRenderer distRenderer = new DistanceRenderer(uc);	    	    
	    dbTable.getColumnModel().getColumn(3).setCellRenderer(distRenderer);	   
	    dbTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
	    
		JScrollPane scrollPane = new JScrollPane(dbTable);
		dbTable.setFillsViewportHeight(true);
		
		// buttons
		btnLoad = new JButton();
		btnLoad.setText("Load");
		btnLoad.setToolTipText("Load selected entries from database");
		btnLoad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				load();				
			}
		});
		buttonPanel.add(btnLoad);
		
		btnImport = new JButton();
		btnImport.setText("Import");
		btnImport.setToolTipText("import files into database");
		btnImport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				importFiles();				
			}
		});
		// buttonPanel.add(btnImport);
		
		btnExport = new JButton();
		btnExport.setText("Export");
		btnExport.setToolTipText("Export to file");
		btnExport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				export();
				
			}
		});
		buttonPanel.add(btnExport);
		
		btnDelete = new JButton();
		btnDelete.setText("Delete");
		btnDelete.setToolTipText("Delete selected entries from database");
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int n = JOptionPane.showConfirmDialog(parentFrame, "Delete selected entries?", "Title",
						JOptionPane.OK_CANCEL_OPTION);
				if (n == JOptionPane.OK_OPTION) {
					delete();	
				}								
			}
		});
		buttonPanel.add(btnDelete);

		btnRefresh = new JButton();
		btnRefresh.setText("Refresh");
		btnRefresh.setToolTipText("Refresh table");
		btnRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTable();				
			}
		});
		buttonPanel.add(btnRefresh);
		selectionButtonsEnabled(false);
		
		getContentPane().add(filterPanel, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
				
		pack();
		setPreferredSize(new Dimension((int) (parentFrame.getSize().width * 0.75), getSize().height));
		setCenterLocation();
		setVisible(true);
	}

	@Override
	public void begin() {
		
		setup();
		new Thread(this).start();
		
		
	}

	@Override
	public String getTitle() {
		
		return "GPS Database";
	}
	
	@Override
	public void run() {
		refreshTable();
		
	}
	
	/**
	 * refresh table with content from database
	 */
	private synchronized void refreshTable() {
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
							
				selectionButtonsEnabled(false);
				btnRefresh.setEnabled(false);
				try {
					busyOn();
					disableAllButtons();
					dbModel.refresh();
					dbTable.minimizeColumnWidth(1, ExtendedTable.WIDTH_PREFERRED);
					dbTable.minimizeColumnWidth(2, ExtendedTable.WIDTH_PREFERRED);
					dbTable.minimizeColumnWidth(3, ExtendedTable.WIDTH_PREFERRED);
					dbTable.minimizeColumnWidth(5, ExtendedTable.WIDTH_PREFERRED);										
				} catch(Exception e) {
					msg.error(e);
				}
			
				return null;
			}
			
				
	        @Override
	        protected void done() {
	        	btnRefresh.setEnabled(true);
	        	busyOff();
	        }
		};
		worker.execute();
		
	}
	
	/**
	 * load selected entries from database
	 */
	private void load() {
				
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				
				btnLoad.setEnabled(false);
				infoPanel = msg.infoOn("Getting ...",  new Cursor(Cursor.WAIT_CURSOR));
				for (int i : dbTable.getSelectedRows()) {
					try {
						int idx = dbTable.convertRowIndexToModel(i);
						infoPanel.setText("Getting ".concat(dbModel.get(idx).getName()));
						GPXFile gpx = db.get(dbModel.get(idx).getId());
						GpsMaster.active.newGpxFile(gpx);
					} catch(Exception e) {
						msg.error(e);
					}
				}

				return null;
			}
			
				
	        @Override
	        protected void done() {
				
	        	if (infoPanel != null) {
	        		msg.infoOff(infoPanel);
	        	}
	        	btnLoad.setEnabled(true);
	        }
		};
		worker.execute();
	}

	/**
	 * 
	 * @param id
	 */
	private void delete() {
		
		infoPanel = msg.infoOn("Deleting ...",  new Cursor(Cursor.WAIT_CURSOR));
		
		for (int i : dbTable.getSelectedRows()) {
			try {
				int idx = dbTable.convertRowIndexToModel(i);
				// infoPanel.setText("Deleting ".concat(gpsEntries.get(idx).getName()));
				db.delete(dbModel.get(idx).getId());				
			} catch(Exception e) {
				msg.error(e);
				e.printStackTrace();
			}			
		}
		refreshTable();
		msg.infoOff(infoPanel);
		
	}
	
	/**
	 * 
	 */
	private void disableAllButtons() {
		btnLoad.setEnabled(false);
		btnImport.setEnabled(false);
		btnDelete.setEnabled(false);
		btnRefresh.setEnabled(false);
	}

	/**
	 * 
	 */
	private void enableAllButtons() {
		btnLoad.setEnabled(true);
		btnImport.setEnabled(true);
		btnDelete.setEnabled(true);
		btnRefresh.setEnabled(true);
	}

	/**
	 * enable/disable all buttons that require one or
	 * more rows to be selected
	 * 
	 * @param enabled
	 */
	private void selectionButtonsEnabled(boolean enabled) {
		btnLoad.setEnabled(enabled);
		btnDelete.setEnabled(enabled);
		btnExport.setEnabled(enabled);
	}
	
	/**
	 * import files into database
	 */
	private void importFiles() {
		JFileChooser chooserFileOpen = new JFileChooser();
		// chooserFileOpen.setCurrentDirectory(new File(conf.getLastOpenDirectory()));
		chooserFileOpen.setMultiSelectionEnabled(true);		
        int returnVal = chooserFileOpen.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // conf.setLastOpenDirectory(chooserFileOpen.getCurrentDirectory().getPath());
            MultiLoader multiLoader = new MultiLoader(msg);
            multiLoader.setShowFilenames(true);
            // multiLoader.setShowWarnings(conf.getShowWarning());
            multiLoader.setFiles(chooserFileOpen.getSelectedFiles());
            multiLoader.setAddToMap(false);
            multiLoader.setAddToStorage(true);
            multiLoader.load();        	            
        }
	}
	
	/**
	 * Export selected entries to file. GPS data in records
	 * is expected to be in "native" format.
	 * 
	 */
	private void export() {
		
	}
}
