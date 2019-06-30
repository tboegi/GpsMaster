package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import org.gpsmaster.Const;
import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.IItemSource;
import org.gpsmaster.online.OnlineTrack;

import eu.fuegenstein.gis.GeoBounds;
import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.swing.ExtendedTable;
import eu.fuegenstein.unit.UnitConverter;

/**
 * @author tim.prune
 * @author rfu
 * 
 * Function to load track information from any source,
 * subclassed for special cases like gpsies or wikipedia
 * 
 * TODO restructure / rewrite
 */
public abstract class GenericDownloadDialog extends GenericDialog implements IItemSource
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2219627677527086720L;
	/** Dialog object */
	// protected JDialog dialog = null;
	/** list model */
	protected TransferableItemTableModel trackListModel = null;
	/** track table */
	protected ExtendedTable trackTable = null;
	/** Cancelled flag */
	protected boolean cancelled = false;
	/** error message */
	protected String errorMessage = null;
	/** Status label */
	protected JLabel statusLabel = null;
	/** Description box */
	private JTextArea descriptionBox = null;
	/** Load button */
	
	protected JButton refreshButton = null; // refresh tracklist - enable when map position (bounds) has changed
	protected JButton loadButton = null;
	protected JButton cancelButton = null;
	protected JButton showButton = null;
	
	protected JPanel mainPanel = new JPanel(); // main panel holding all components
	// NORTH
	protected JPanel northPanel = new JPanel();
	protected JPanel filterPanel = new JPanel();
	protected JPanel boundsPanel = new JPanel();
		
	// SOUTH
	protected JPanel southPanel = new JPanel();
	protected JPanel descPanel = new JPanel();
	protected JPanel itemTargetPanel = new JPanel();
	protected JPanel buttonPanel = new JPanel();
	protected JPanel statusPanel = new JPanel();  
			
	protected PropertyChangeListener changeListener = null;
	protected PropertyChangeListener fileHubListener = null;
	
	protected GeoBounds bounds = null;
	protected JLabel boundsLabel = new JLabel();
	
	protected FileHub fileHub = null;
	
	/**
	 * 
	 * @param frame
	 * @param filehub
	 */
	public GenericDownloadDialog(JFrame frame, MessageCenter msg, FileHub fileHub, UnitConverter unitConverter) {
		super(frame, msg);
		this.fileHub = fileHub;
		this.uc = unitConverter;
		
		setupListeners();
		setupMainPanel();
		setupNorthPanel();
		setupTableModel();
		setupTableModel();
		initializeTable();
		setupTable();
		setupTrackAndSouthPanel();
		setupButtonPanel();
		
		pack();
		setCenterLocation();
		
		// Clear list
		trackListModel.clear();
		loadButton.setEnabled(false);
		showButton.setEnabled(false);
		cancelled = false;
		descriptionBox.setText("");
		errorMessage = null;

		// Show dialog
		setVisible(true);
		
	}
		
	/**
	 * 
	 * @param propertyListener
	 */
	public void setPropertyChangeListener(PropertyChangeListener listener) {
		changeListener = listener;
	}
	
	/**
	 * 
	 * @return
	 */
	public PropertyChangeListener getPropertyChangeListener() {
		return changeListener;
	}
	
	/**
	 * 
	 * @return
	 */
	public GeoBounds getGeoBounds() {
		return bounds;
	}
	
	/**
	 * 
	 * @param bounds
	 * 
	 * TODO synchronize bounds with mapPanel while dialog is open
	 * 
	 */
	public void setGeoBounds(GeoBounds bounds) {
		this.bounds = bounds;
		boundsLabel.setText(bounds.toString());
	}
	
	public abstract void begin();
	
	/**
	 * 
	 */
	private void setupListeners() {
		fileHubListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String command = evt.getPropertyName();
				if (command.equals(Const.PCE_TRANSFERITEMSTATECHANGED)) {
					trackListModel.refreshTrack((OnlineTrack) evt.getNewValue());
				} else if (command.equals(Const.PCE_TRANSFERSTARTED)) {
					cancelButton.setEnabled(true);
				} else if (command.equals(Const.PCE_TRANSFERFINISHED)) {
					cancelButton.setEnabled(false);
				}
			}
		};
		fileHub.addChangeListener(fileHubListener);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// add closing propertyListener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// FileHub may keep running in the background. 
				// remove listener since we don't need notifications anymore
				fileHub.removeChangeListener(fileHubListener);
			}
		});
	}
		
	/**
	 * 
	 */
	private void setupMainPanel() {
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
			

		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		southPanel.add(buttonPanel);
				
		// set initial size
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		getContentPane().add(mainPanel);
	}
	
	/**
	 * 
	 */
	private void setupNorthPanel() {
		
		filterPanel.setVisible(false);
		northPanel.add(filterPanel);

		// setup bounds panel		
		boundsLabel.setText(" - - - -");		
		boundsPanel.add(boundsLabel);
		boundsPanel.setVisible(false);
		northPanel.add(boundsPanel);
		
	}
	
	/**
	 * South is everything below track table
	 */
	private void setupTrackAndSouthPanel() {		
		
		JScrollPane tablePane = new JScrollPane(trackTable);
		// tablePane.setPreferredSize(new Dimension(450, 200));
		
		// Panel to hold description label and box
		
		descPanel.setLayout(new BorderLayout());		
		descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
		descriptionBox = new JTextArea(5, 20);
		descriptionBox.setEditable(false);
		descriptionBox.setLineWrap(true);
		descriptionBox.setWrapStyleWord(true);
		JScrollPane descPane = new JScrollPane(descriptionBox);
		descPane.setPreferredSize(new Dimension(400, 80));
		descPanel.add(descPane, BorderLayout.CENTER);
		descPanel.setVisible(false);
		
		// Use split pane to split table from description
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePane, descPanel);
		splitPane.setResizeWeight(1.0);
		mainPanel.add(splitPane, BorderLayout.CENTER);
		
	}
	
	/**
	 * 
	 */
	private void setupButtonPanel() {
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		// show web page button
		showButton = new JButton("Show Webpage");
		showButton.setEnabled(false);
		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				showSelectedWebpage();
			}
		});
		buttonPanel.add(showButton);
		
		// load button
		loadButton = new JButton("Load");
		loadButton.setEnabled(false);
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				loadSelected();
			}
		});		
		buttonPanel.add(loadButton);
		
		// cancel button
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				cancelled = true;
				fileHub.cancel();				
			}
		});
		cancelButton.setEnabled(false);
		buttonPanel.add(cancelButton);
		
		// close button
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
		});
		buttonPanel.add(closeButton);
		
	}
	
	protected abstract void setupTableModel();
	
	/**
	 * Instantiate table and set properties common to all base classes
	 */
	protected void initializeTable() {

		trackTable = new ExtendedTable(trackListModel)
		{

			private static final long serialVersionUID = 7755143224915835381L;
			// renderer to set row color according to item state
			public Component prepareRenderer(
		            TableCellRenderer renderer, int row, int column)
		        {
		            Component c = super.prepareRenderer(renderer, row, column);		            
		            TransferableItemTableModel model = (TransferableItemTableModel) getModel();
		            if (!isRowSelected(row)) {
		            	c.setForeground(model.getRowColor(convertRowIndexToModel(row)));
		            }
		            return c;
		        }
		};
		trackTable.setAutoCreateRowSorter(true);
		
		/**
		 * Listener to set description and item state text on row selection
		 */
		trackTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
				{
					final int numSelected = trackTable.getSelectedRowCount();
					if (numSelected > 0)
					{
						OnlineTrack track = (OnlineTrack) trackListModel.getItem(trackTable.getSelectedRow());
						setDescription(track.getDescription());
						descriptionBox.setCaretPosition(0);
					}
					else {
						descriptionBox.setText("");
					}
					loadButton.setEnabled(numSelected > 0);
					showButton.setEnabled(numSelected == 1);
				}
			}
		});
	}
	
	protected abstract void setupTable();
	

	/**
	 * @param inColNum index of column, 0 or 1
	 * @return key for this column
	 */
	protected abstract String getColumnKey(int inColNum);

	/**
	 * Set the description in the box
	 * @param inDesc description to set, or null for no description
	 */
	private void setDescription(String inDesc)
	{
		String text = inDesc;
		if (inDesc == null || inDesc.length() < 2) {
			text = "";
			// text = I18nManager.getText("dialog.gpsies.nodescription");
		}
		descriptionBox.setText(text);
	}


	/**
	 * Load the selected track or point
	 */
	protected abstract void loadSelected();

	/**
	 * 
	 */
	protected void makeTargetPanels() {
		if (fileHub != null) {
			
		}
	}

	/**
	 * Show the webpage for the selected item
	 */
	private void showSelectedWebpage()
	{
		// Find the row selected in the table and show the corresponding url
		int rowNum = trackTable.convertRowIndexToModel(trackTable.getSelectedRow());
		if (rowNum >= 0 && rowNum < trackListModel.getRowCount())
		{
			String url = ((OnlineTrack) trackListModel.getItem(rowNum)).getWebUrl();
			BrowserLauncher.launchBrowser(url);
		}
	}
	
}
