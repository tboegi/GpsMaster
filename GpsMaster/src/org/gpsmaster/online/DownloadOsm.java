package org.gpsmaster.online;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gpsmaster.Const;
import org.gpsmaster.dialogs.GenericDownloadDialog;
import org.gpsmaster.filehub.DataType;
import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.TransferableItem;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.osm.Osm;
import org.gpsmaster.osm.OsmQuery;

import se.kodapan.osm.domain.Relation;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.parser.xml.OsmXmlParserException;
import se.kodapan.osm.services.overpass.OverpassException;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;
import eu.fuegenstein.swing.ExtendedTable;
import eu.fuegenstein.unit.UnitConverter;

/**
 * Download relations from OSM
 * @author rfu
 *
 */
public class DownloadOsm extends GenericDownloadDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7474104853346542164L;

	private JButton getListButton = new JButton();
	private JComboBox<String> typeCombo = new JComboBox<String>();
	protected JTextField idField = null; 
	private JTextField nameField = null;
	private Osm osm = null;
	private OsmQuery osmQuery = new OsmQuery();
	private long customId = 0;

	private final List<TransferableItem> items = Collections.synchronizedList(new ArrayList<TransferableItem>());
	
	public DownloadOsm(JFrame frame, MessageCenter msg, FileHub fileHub, UnitConverter uc) {	
		super(frame, msg, fileHub, uc);	
		osm = new Osm(msg);
		idField = new JTextField();
		setIcon(Const.ICONPATH_DLBAR, "download-osm.png");
		setupLists();
		setupFilterPanel();
		addIdField();
		pack();
	}

	private void setupLists() {
		
		// subtypes for "Route"
		typeCombo.addItem("<All>");
		typeCombo.addItem("bicycle");
		typeCombo.addItem("hiking");
		typeCombo.addItem("road");
		typeCombo.addItem("train");
		typeCombo.addItem("tram");
		typeCombo.addItem("foot");		
		typeCombo.addItem("bus");		
		typeCombo.addItem("detour");
		typeCombo.addItem("horse");
		typeCombo.addItem("ski");
		
	}
	
	/**
	 * add "download by id" field to buttonpanel before all other buttons  
	 * SHIT: constructor of this class has not been invoked when this
	 * method is called, therefore idField == null :-(
	 */
	// @Override
	// protected void addPreButtons() {
	private void addIdField() {
		buttonPanel.add(new JLabel("download by ID:"));	
		idField.setPreferredSize(new Dimension(50, 20));
		idField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_ENTER)) {
                	checkCustomId();                	
                }
            }
        });			
		idField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				checkCustomId();				
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		buttonPanel.add(idField);		
	}
	
	/**
	 * set up filter msgPanel
	 */
	private void setupFilterPanel() {	
		JPanel filterPanel = new JPanel();
		filterPanel.setVisible(true);
		filterPanel.setLayout(new FlowLayout());		
		filterPanel.add(new JLabel("Type:"));		
		filterPanel.add(typeCombo);
								
		filterPanel.add(new JLabel("name contains:"));		
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(100, 20));
		filterPanel.add(nameField);
		
		getListButton.setText("Get List");
		getListButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getRelationList();				
			}
		});
		
		filterPanel.add(getListButton);
		
		// add(filterPanel, BorderLayout.NORTH);
		northPanel.add(filterPanel);
		pack();
		
	}
	
	private void checkCustomId() {
		try {
			customId = Long.parseLong(idField.getText());
			loadButton.setEnabled(true);
			loadButton.requestFocus();
		} catch (NumberFormatException e) {
			if (trackListModel.getRowCount() == 0) {
				loadButton.setEnabled(false);
			}
			idField.setText("0");
			customId = 0;
		}		
	}
	
	/**
	 * Retrieve list of relations based on map boundaries and user input
	 * TODO run in background? 
	 */
	private void getRelationList() {

		busyOn();
		MessagePanel panel = msg.infoOn("Retrieving list of relations for current map view ...");
		trackListModel.clear();
				
		osmQuery.setType(OsmQuery.RELATION);
		osmQuery.setCaseSensitive(false);
		osmQuery.setUseRegExp(true);
		osmQuery.setGeoBounds(getGeoBounds());
		
		if (typeCombo.getSelectedIndex() > 0) {
			osmQuery.addTag("route", (String) typeCombo.getSelectedItem());
		}
		if (nameField.getText().isEmpty() == false) {
			osmQuery.addTag("name", nameField.getText());
		}
		
		try {
			osm.addQuery(osmQuery);
			PojoRoot root = osm.runQuery();
			for (long relationId : root.getRelations().keySet()) {
				Relation relation =  root.getRelations().get(relationId);
				OnlineTrack track = new OnlineTrack();
				track.setId(relationId);
				track.setName(relation.getTag("name"));
				track.setType(relation.getTag("type"));  // TODO figure out best way to show type
				if (track.getName() == null) {
					track.setName("(" + relationId + ")");
				}
				track.setWebUrl("http://www.openstreetmap.org/relation/" + relationId);
				// more ...
				trackListModel.addItem(track);				
			}
		} catch (OverpassException e) {
			msg.volatileError(e);
		} catch (OsmXmlParserException e) {
			msg.volatileError(e);
		}
		trackTable.minimizeColumnWidth(1, ExtendedTable.WIDTH_MIN);					
		// trackTable.minimizeColumnWidth(2, ExtendedTable.WIDTH_PREFERRED);
		msg.infoOff(panel);
		msg.volatileInfo(trackListModel.getRowCount() + " relations found.");
		busyOff();

	}


	@Override
	protected String getColumnKey(int inColNum) {
		if (inColNum == 0) {
			return "Relation Name";
		}
		return "----";
	}
 
	/**
	 * load relations selected by user via {@link FileHub}
	 */
	@Override
	protected void loadSelected() {

		if (customId != 0) {
			OnlineTrack track = new OnlineTrack();
			track.setName("OSM Relation " + customId);
			track.setId(customId);
			items.add(track);
		}
		
		// add selected relations
		int numSelected = trackTable.getSelectedRowCount();
		if (numSelected > 0) {
    		int[] rowNums = trackTable.getSelectedRows();
    		for (int i=0; i<numSelected; i++)
    		{
    			int rowNum = trackTable.convertRowIndexToModel(rowNums[i]);
    			if (rowNum >= 0 && rowNum < trackListModel.getRowCount())
    			{
    				TransferableItem item = trackListModel.getItem(rowNum);
    				item.setTransferState(TransferableItem.STATE_QUEUED);
    				items.add(item);
    			}
    		}
		}
		trackTable.clearSelection();
		fileHub.run();
	}

	@Override
	public String getName() {
		return "OpenStreetMap";
	}

	@Override
	public String getTitle() {
		return "Download Route from OpenStreetMap";
	}

	@Override
	public boolean doShowProgressText() {
		return true;
	}

	public DataType getDataType() {
		return DataType.GPXFILE; 
	}

	public List<TransferableItem> getItems() {
		return items;
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public GPXFile getGpxFile(TransferableItem item) throws Exception {
		GPXFile gpx = new GPXFile();
		OnlineTrack track = (OnlineTrack) item;
		gpx.setName(track.getName());
		gpx.setDesc("OSM Relation " + track.getId());
    	osm.downloadRelation(track.getId(), gpx);
		return gpx;
	}
	
	public void open(TransferableItem transferableItem) {
		// n/a
	}

	public InputStream getInputStream() throws Exception {
		throw new UnsupportedOperationException();
	}

	public void close() throws Exception {
		
	}

	@Override
	protected void setupTableModel() {
		trackListModel = new OsmTableModel(uc);
		
	}

	@Override
	protected void setupTable() {
		trackTable.getColumnModel().getColumn(0).setPreferredWidth(20); // transfer state (does not work)
	}

	@Override
	public void begin() {
		
		// description box is not needed
		descPanel.setVisible(false);
		
	}

}
