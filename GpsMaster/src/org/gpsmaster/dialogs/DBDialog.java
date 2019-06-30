package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
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
	private List<GpsEntry> gpsEntries = null;
	private MessagePanel infoPanel = null;
	
	// table & related objects
	private ExtendedTable dbTable = null;
	private DbTableModel tableModel = null;

	private JButton loadButton = null;
	private JButton importButton = null;
	private JButton exportButton = null;
	private JButton deleteButton = null;
	private JButton refreshButton = null;
		
	/**
	 * 
	 * @author rfu
	 *
	 */
	private class DistanceRenderer extends DefaultTableCellRenderer {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4716470880604010041L;
		private UnitConverter uc = null;
		
		/**
		 * Constructor 
		 * @param uc
		 */
		public DistanceRenderer(UnitConverter uc) {
			this.uc = uc;
			
		}
		
		/**
		 * 
		 */
	    public void setValue(Object value) {
	    	Long longDist = (Long) value;
	    	String dist = uc.dist(longDist.longValue(), Const.FMT_DIST);
	    	setText(dist);	    	
	    }
	}
	
	/**
	 * 
	 * @param parentFrame
	 * @param msg
	 */
	public DBDialog(JFrame parentFrame, MessageCenter msg) {
		super(parentFrame, msg);
	}

	/**
	 * 
	 * @param parentFrame
	 * @param msg
	 * @param db
	 */
	public DBDialog(JFrame parentFrame, MessageCenter msg, GpsStorage db) {
		this(parentFrame, msg);
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
		gpsEntries = new ArrayList<GpsEntry>();
		tableModel = new DbTableModel(gpsEntries, uc);
		dbTable = new ExtendedTable(tableModel) {
			// show activity as tooltip on MouseOver
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				Point p = e.getPoint();
				int rat = rowAtPoint(p); 
				if (rat > -1) {
			        int rowIndex = convertRowIndexToModel(rat);
			        int colIndex = columnAtPoint(p);		        
			        if ((colIndex == 5) && (rowIndex > -1)) {
			        	tip = gpsEntries.get(rowIndex).getActivity();
			        }
				}
				return tip;
			}
		};    
				
		dbTable.setGridColor(Color.LIGHT_GRAY);		
		dbTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		dbTable.setAutoCreateRowSorter(true);
		
		dbTable.getColumnModel().getColumn(0).setMaxWidth(16);		
		// dbTable.setRowHeight(18);
		
	    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
	    rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
	    
	    DistanceRenderer distRenderer = new DistanceRenderer(uc);
	    distRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
	    
	    dbTable.getColumnModel().getColumn(3).setCellRenderer(distRenderer);	   
	    dbTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
	    
		JScrollPane scrollPane = new JScrollPane(dbTable);
		dbTable.setFillsViewportHeight(true);
		
		// buttons
		loadButton = new JButton();
		loadButton.setText("Load");
		loadButton.setToolTipText("Load selected entries from database");
		loadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				load();				
			}
		});
		buttonPanel.add(loadButton);
		
		importButton = new JButton();
		importButton.setText("Import");
		importButton.setToolTipText("import files into database");
		importButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				importFiles();				
			}
		});
		// buttonPanel.add(importButton);
		
		deleteButton = new JButton();
		deleteButton.setText("Delete");
		deleteButton.setToolTipText("Delete selected entries from database");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				delete();				
			}
		});
		buttonPanel.add(deleteButton);

		refreshButton = new JButton();
		refreshButton.setText("Refresh");
		refreshButton.setToolTipText("Refresh table");
		refreshButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTable();				
			}
		});
		buttonPanel.add(refreshButton);

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
							
				try {
					busyOn();
					disableButtons();
					gpsEntries.clear(); // TODO fill delta
					db.getEntries(gpsEntries);
					dbTable.minimizeColumnWidth(1, ExtendedTable.WIDTH_PREFERRED);
					dbTable.minimizeColumnWidth(2, ExtendedTable.WIDTH_PREFERRED);
					dbTable.minimizeColumnWidth(3, ExtendedTable.WIDTH_PREFERRED);
					dbTable.minimizeColumnWidth(5, ExtendedTable.WIDTH_PREFERRED);
					
					tableModel.fireTableDataChanged();
				} catch(Exception e) {
					msg.error(e);
				}
			
				return null;
			}
			
				
	        @Override
	        protected void done() {
				enableButtons();
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
				
				loadButton.setEnabled(false);
				infoPanel = msg.infoOn("Getting ...",  new Cursor(Cursor.WAIT_CURSOR));
				for (int i : dbTable.getSelectedRows()) {
					try {
						int idx = dbTable.convertRowIndexToModel(i);
						infoPanel.setText("Getting ".concat(gpsEntries.get(idx).getName()));
						GPXFile gpx = db.get(gpsEntries.get(idx).getId());
						GpsMaster.active.newGpxFile(gpx, null);
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
	        	loadButton.setEnabled(true);
	        }
		};
		worker.execute();
	}

	/**
	 * 
	 * @param id
	 */
	private void delete() {
		
		infoPanel = msg.infoOn("Getting ...",  new Cursor(Cursor.WAIT_CURSOR));
		
		for (int i : dbTable.getSelectedRows()) {
			try {
				int idx = dbTable.convertRowIndexToModel(i);
				// infoPanel.setText("Deleting ".concat(gpsEntries.get(idx).getName()));
				db.delete(gpsEntries.get(idx).getId());				
			} catch(Exception e) {
				msg.error(e);
				e.printStackTrace();
			}
			refreshTable();
		}
		msg.infoOff(infoPanel);
		
	}
	
	/**
	 * 
	 */
	private void disableButtons() {
		loadButton.setEnabled(false);
		importButton.setEnabled(false);
		deleteButton.setEnabled(false);
		refreshButton.setEnabled(false);
	}

	/**
	 * 
	 */
	private void enableButtons() {
		loadButton.setEnabled(true);
		importButton.setEnabled(true);
		deleteButton.setEnabled(true);
		refreshButton.setEnabled(true);
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
}
