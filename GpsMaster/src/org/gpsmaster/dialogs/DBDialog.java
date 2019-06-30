package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.swing.JButton;
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
import org.gpsmaster.db.DbLayer;
import org.gpsmaster.db.GpsRecord;
import org.gpsmaster.filehub.DataType;
import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.IItemSource;
import org.gpsmaster.filehub.TransferableItem;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.swing.ExtendedTable;

/**
 * Dialog to load GPS data from a relational database via {@link FileHub}
 * 
 * 
 * @author rfu
 *
 * TODO button enable/disable je nach state reparieren
 * TODO SelectItem(): center to track if it is shown on the map
 * TODO unify with {@link GenericDownloadDialog}
 * 
 */
public class DBDialog extends GenericDialog implements Runnable, IItemSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1422533436670399564L;

	private final byte COL_COLOR = 0;
	private final byte COL_STARTDATE = 1;
	private final byte COL_DISTANCE = 3;
	private final byte COL_DURATION = 4;
	
	private DbLayer dbLayer = null;
	private FileHub fileHub = null;
	private MessagePanel infoPanel = null;
		
	// table & related objects
	private ExtendedTable dbTable = null;
	private DbTableModel dbModel = null;

	private JButton btnLoad = null;
	private JButton btnImport = null;
	private JButton btnExport = null;
	private JButton btnDelete = null;
	private JButton btnRefresh = null;
	private JButton btnCancel = null;
	private JButton btnClose = null;
	
	private TransferableItem currentItem = null;
	private final List<TransferableItem> items = Collections.synchronizedList(new ArrayList<TransferableItem>());
	private InputStream dbInputStream = null;
	
	private PropertyChangeListener fileHubListener = null;
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
	 * Constructor
	 * @param parentFrame
	 * @param msg
	 * @param db
	 * @param fileHub
	 */
	public DBDialog(JFrame parentFrame, MessageCenter msg, DbLayer db, FileHub fileHub) {
		super(parentFrame, msg);
		this.dbLayer = db;
		this.fileHub = fileHub;
	}

	/**
	 * @return the dbLayer
	 */
	public DbLayer getStorage() {
		return dbLayer;
	}

	/**
	 * @param dbLayer the dbLayer to set
	 */
	public void setStorage(DbLayer db) {
		this.dbLayer = db;	
	}

	
	/**
	 * @return the changeListener
	 */
	public PropertyChangeListener getChangeListener() {
		return changeListener;
	}

	/**
	 * {@link IItemSource} method
	 */
	public String getName() {
		return "GPS Database";
	}
	
	public DataType getDataType() {
		return DataType.STREAM;
	}

	public boolean doShowProgressText() {
		return true;
	}

	public List<TransferableItem> getItems() {
		return items;
	}

	public GPXFile getGpxFile(TransferableItem item) throws Exception {
		throw new UnsupportedOperationException();
	}

	public void open(TransferableItem transferableItem) {
		currentItem = transferableItem;
		
	}

	public InputStream getInputStream() throws Exception {
		 
		GpsRecord gpsRecord = (GpsRecord) currentItem;
		dbInputStream = dbLayer.getGpsData(gpsRecord.getId());		
		if (gpsRecord.isCompressed()) {
			ZipInputStream zis = new ZipInputStream(dbInputStream);
			zis.getNextEntry();					 
		}		
		return dbInputStream;
	}

	@Override
	public void close() throws Exception {
		if (dbInputStream != null) {
			dbInputStream.close();
		}
		// release BLOB blob.free()
		currentItem = null;		
	}

	/**
	 * Setup swing components
	 * 
	 */
	@SuppressWarnings("serial")
	private void setup() {
		
		JPanel filterPanel = new JPanel();		
		JPanel buttonPanel = new JPanel();
		
		// handle notifications received from FileHub
		fileHubListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String command = evt.getPropertyName();
				if (command.equals(Const.PCE_TRANSFERITEMSTATECHANGED)) {
					// dbModel.refreshTrack((OnlineTrack) evt.getNewValue());
				} else if (command.equals(Const.PCE_TRANSFERSTARTED)) {
					btnCancel.setEnabled(true);
				} else if (command.equals(Const.PCE_TRANSFERFINISHED)) {
					btnCancel.setEnabled(false);
				}
			}
		};
		fileHub.addChangeListener(fileHubListener);
		
		getContentPane().setLayout(new BorderLayout());
		
		setIcon(Const.ICONPATH_DLBAR, "database.png");
				
		// initialise table		
		dbModel = new DbTableModel(dbLayer);
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
				
		dbTable.setColumnWidthPadding(10);
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
		
		dbTable.getColumnModel().getColumn(COL_COLOR).setMaxWidth(16);		
		// dbTable.setRowHeight(18);
		
		dbTable.minimizeColumnWidth(COL_STARTDATE, ExtendedTable.WIDTH_MIN);
		dbTable.minimizeColumnWidth(COL_DISTANCE, ExtendedTable.WIDTH_MAX);
		dbTable.minimizeColumnWidth(COL_DURATION, ExtendedTable.WIDTH_MAX);
		
	    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
	    rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
	    
	    DistanceRenderer distRenderer = new DistanceRenderer(uc);	    	    
	    dbTable.getColumnModel().getColumn(COL_DISTANCE).setCellRenderer(distRenderer);	   
	    dbTable.getColumnModel().getColumn(COL_DURATION).setCellRenderer(rightRenderer);
	    
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
		btnImport.setEnabled(true);
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
		// buttonPanel.add(btnExport);
		
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
		
		// cancel button
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				fileHub.cancel();				
			}
		});
		btnCancel.setEnabled(false);
		buttonPanel.add(btnCancel);
		
		// close button
		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
		});
		buttonPanel.add(btnClose);
		
		getContentPane().add(filterPanel, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
				
		pack();
		//msetSize(new Dimension((int) (parentFrame.getSize().width * 0.5), getSize().height));
		setSize(800, 600);
		setCenterLocation();
		setVisible(true);
	}

	@Override
	public String getTitle() {
		
		return "GPS Database";
	}

	// TODO consolidate run() and begin() like in other dialogs
	@Override
	public void begin() {		
		setup();
		new Thread(this).start();
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
					dbTable.minimizeColumnWidth(3, ExtendedTable.WIDTH_MIN);
					dbTable.minimizeColumnWidth(4, ExtendedTable.WIDTH_MIN);
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
				
		for (int i : dbTable.getSelectedRows()) {
			int idx = dbTable.convertRowIndexToModel(i);
			items.add(dbModel.get(idx));
		}
		fileHub.run();		
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
				dbLayer.deleteGpsRecord(dbModel.get(idx).getId());				
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
		// btnImport.setEnabled(false);
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
		/*
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
        */
	}
	
	/**
	 * Export selected entries to file. GPS data in records
	 * is expected to be in "native" format.
	 * 
	 */
	private void export() {
		
	}
}
