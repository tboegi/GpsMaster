package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gpsmaster.online.TrackListModel;

import eu.fuegenstein.gis.GeoBounds;
import eu.fuegenstein.messagecenter.MessageCenter;

/**
 * @author tim.prune
 * @author rfu
 * 
 * Function to load track information from any source,
 * subclassed for special cases like gpsies or wikipedia
 */
public abstract class GenericDownloadDialog extends GenericDialog implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2219627677527086720L;
	/** Dialog object */
	// protected JDialog dialog = null;
	/** list model */
	protected TrackListModel trackListModel = null;
	/** track table */
	protected JTable trackTable = null;
	/** Cancelled flag */
	protected boolean cancelled = false;
	/** error message */
	protected String errorMessage = null;
	/** Status label */
	protected JLabel statusLabel = null;
	/** Description box */
	private JTextArea descriptionBox = null;
	/** Load button */
	protected JButton loadButton = null;
	/** Show button */
	private JButton showButton = null;
	/** button panel - already instantiated to allow subclasses
	 *  to add custom buttons before begin() is invoked. 
	 */
	protected JPanel buttonPanel = new JPanel();
	
	protected PropertyChangeListener changeListener = null;
	protected GeoBounds bounds = null;

	/**
	 * Constructor
	 * @param inApp App object
	 */
	public GenericDownloadDialog(JFrame frame, MessageCenter msg) {
		super(frame, msg);				
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
	}
	
	/**
	 * Begin the function
	 */
	public void begin()
	{
		setDefaultSize();			
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// add closing propertyListener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancelled = true;
			}
		});
		getContentPane().add(makeDialogComponents());
		pack();
		
		// Clear list
		trackListModel.clear();
		loadButton.setEnabled(false);
		showButton.setEnabled(false);
		cancelled = false;
		descriptionBox.setText("");
		errorMessage = null;
		// Start new thread to load list asynchronously
		new Thread(this).start();

		// Show dialog
		setVisible(true);
	}


	/**
	 * Create dialog components
	 * @return Panel containing all gui elements in dialog
	 */
	private Component makeDialogComponents()
	{
		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BorderLayout());

		// Status label
		// statusLabel = new JLabel(I18nManager.getText("confirm.running"));
		// dialogPanel.add(statusLabel, BorderLayout.NORTH);
		// Main panel with track list
		trackListModel = new TrackListModel(getColumnKey(0), getColumnKey(1));
		trackTable = new JTable(trackListModel);
		trackTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
				{
					final int numSelected = trackTable.getSelectedRowCount();
					if (numSelected > 0)
					{
						setDescription(trackListModel.getTrack(trackTable.getSelectedRow()).getDescription());
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
		trackTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		if (trackListModel.getColumnCount() > 1) {
			trackTable.getColumnModel().getColumn(1).setPreferredWidth(70);
		}
		JScrollPane tablePane = new JScrollPane(trackTable);
		tablePane.setPreferredSize(new Dimension(450, 200));
		// Panel to hold description label and box
		JPanel descPanel = new JPanel();
		descPanel.setLayout(new BorderLayout());
		JLabel descLabel = new JLabel("Description:");
		descPanel.add(descLabel, BorderLayout.NORTH);
		descriptionBox = new JTextArea(5, 20);
		descriptionBox.setEditable(false);
		descriptionBox.setLineWrap(true);
		descriptionBox.setWrapStyleWord(true);
		JScrollPane descPane = new JScrollPane(descriptionBox);
		descPane.setPreferredSize(new Dimension(400, 80));
		descPanel.add(descPane, BorderLayout.CENTER);
		// Use split pane to split table from description
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePane, descPanel);
		splitPane.setResizeWeight(1.0);
		dialogPanel.add(splitPane, BorderLayout.CENTER);

		// button panel at bottom
		// buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		loadButton = new JButton("Load");
		loadButton.setEnabled(false);
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				loadSelected();
			}
		});
		buttonPanel.add(loadButton);
		showButton = new JButton("Show Web Page");
		showButton.setEnabled(false);
		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				showSelectedWebpage();
			}
		});
		buttonPanel.add(showButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				cancelled = true;
				dispose();
			}
		});
		buttonPanel.add(cancelButton);
		dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
		dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
		return dialogPanel;
	}

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
		int rowNum = trackTable.getSelectedRow();
		if (rowNum >= 0 && rowNum < trackListModel.getRowCount())
		{
			String url = trackListModel.getTrack(rowNum).getWebUrl();
			BrowserLauncher.launchBrowser(url);
		}
		// Don't close the dialog
	}
	
	protected void busyOn() {
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}
	
	protected void busyOff() {
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
}
