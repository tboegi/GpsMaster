package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
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
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
 * Base dialog to load track information from any source,
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
	protected JPanel centerPanel = new JPanel();
	protected JPanel boundsPanel = new JPanel();
		
	// SOUTH
	protected JPanel southPanel = new JPanel();
	protected JPanel descPanel = new JPanel();
	protected ItemTargetSelectionPanel targetPanel = null;
	protected JPanel buttonPanel = new JPanel();
	
	protected TransferableItemLogPanel statusPanel = new TransferableItemLogPanel();  
			
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
		initializeTable();
		setupTable();
		setupCenterPanel();
		setupTargetPanel();
		setupSouthPanel();
		
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
				
		// set initial size
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		getContentPane().add(mainPanel);
	}
	
	/**
	 * 
	 */
	private void setupNorthPanel() {
		
		// setup bounds panel		
		boundsLabel.setText(" - - - -");		
		boundsPanel.add(boundsLabel);
		boundsPanel.setVisible(true);
		northPanel.add(boundsPanel);
		
	}
	
	/**
	 * Center panel holds track table
	 */
	private void setupCenterPanel() {
		JScrollPane tablePane = new JScrollPane(trackTable);
		mainPanel.add(tablePane, BorderLayout.CENTER);
		
	}
	
	/**
	 * target panel contains the list of selectable item targets
	 * (when a track is loaded from the source, it is also sent to the selected targets) 
	 */
	protected void setupTargetPanel() {
		if (fileHub != null) {
			targetPanel = new ItemTargetSelectionPanel(fileHub.getItemTargets());
			targetPanel.setVisible(true);
		}
	}
	
	/**
	 * South is everything below track table
	 */
	private void setupSouthPanel() {		
		
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

		//
		// transfer status panel
		//
		statusPanel.setVisible(false);
		statusPanel.setAutoHide(true);
		southPanel.add(statusPanel);
		
		//
		// Panel to hold description label and box
		//
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
		southPanel.add(descPane);
		
		//
		// Button panel at bottom
		//
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
		southPanel.add(buttonPanel);
		if (targetPanel != null) {
			southPanel.add(targetPanel);
		}
		
	}
	

	protected abstract void setupTableModel();
	
	/**
	 * Instantiate table and set properties common to all base classes
	 */
	protected void initializeTable() {

		trackTable = new ExtendedTable(trackListModel);
		trackTable.setAutoCreateRowSorter(true);
		
		/**
		 * Listener to set description and item state panel on row selection
		 */
		trackTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
				{
					final int numSelected = trackTable.getSelectedRowCount();
					if (numSelected > 0)
					{
						// TODO check: proper row when sorted?
						OnlineTrack track = (OnlineTrack) trackListModel.getItem(trackTable.getSelectedRow());
						statusPanel.setLog(track.getLog());
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
