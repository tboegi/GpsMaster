package org.gpsmaster.online;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.gpsmaster.dialogs.GenericDownloadDialog;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.osm.Osm;
import org.gpsmaster.osm.OsmQuery;

import se.kodapan.osm.domain.Relation;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.parser.xml.OsmXmlParserException;
import se.kodapan.osm.services.overpass.OverpassException;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;

/**
 * 
 * @author rfu
 *
 */
public class DownloadOsm extends GenericDownloadDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7474104853346542164L;

	private JPanel filterPanel = new JPanel();
	private JButton getListButton = new JButton();
	private JComboBox<String> typeCombo = new JComboBox<String>();
	private JTextField idField = null;
	private JTextField nameField = null;
	private Osm osm = null;
	private OsmQuery osmQuery = new OsmQuery();
	private long customId = 0;
	
	
	public DownloadOsm(JFrame frame, MessageCenter msg) {
		super(frame, msg);	
		osm = new Osm(msg);
		setupLists();
		setupFilterPanel();
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
	 * set up filter panel
	 */
	private void setupFilterPanel() {
		// TODO make this more elegant
		
		filterPanel.setLayout(new FlowLayout());		
		filterPanel.add(new JLabel("Type:"));		
		filterPanel.add(typeCombo);
								
		buttonPanel.add(new JLabel("download by ID:"));
		idField = new JTextField();
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
		
		add(filterPanel, BorderLayout.NORTH);
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
	
	private void getRelationList() {

		busyOn();
		MessagePanel panel = msg.infoOn("Retrieving list of relations for current map view ...");

		ArrayList<OnlineTrack> trackList = new ArrayList<OnlineTrack>();
		
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
				track.setTrackName(relation.getTag("name"));
				if (track.getTrackName() == null) {
					track.setTrackName("(" + relationId + ")");
				}
				track.setWebUrl("http://www.openstreetmap.org/relation/" + relationId);
				// more ...
				trackList.add(track);				
			}
		} catch (OverpassException e) {
			msg.volatileError(e);
		} catch (OsmXmlParserException e) {
			msg.volatileError(e);
		}
				
		trackListModel.clear();
		trackListModel.addTracks(trackList);
		msg.infoOff(panel);
		msg.volatileInfo(trackListModel.getRowCount() + " relations found.");
		busyOff();

	}
	
	@Override
	public void run() {

	}

	@Override
	protected String getColumnKey(int inColNum) {
		if (inColNum == 0) {
			return "Relation Name";
		}
		return "----";
	}
 

	
	@Override
	protected void loadSelected() {
		
	    SwingWorker<Void, Void> downloadWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() {
	        	// TODO handle "download by ID" better:
				// for each ID entered by the user, get the name of the relation via Overpass 
				// and add it to the trackList
				panel = msg.infoOn("Downloading from OSM");
				
				if (customId != 0) {
					String name = "OSM Relation " + customId;
					GPXFile gpx = new GPXFile();
					gpx.setName(name);
					gpx.setDesc(name);
		        	osm.downloadRelation(customId, gpx);
		        	gpx.updateAllProperties();
					if (changeListener != null) {
						firePropertyChange("newGpx", null, gpx);
					}
				}
				
				loadButton.setEnabled(false);
        		int numSelected = trackTable.getSelectedRowCount();
        		if (numSelected > 0) {
		    		int[] rowNums = trackTable.getSelectedRows();
		    		for (int i=0; i<numSelected; i++)
		    		{
		    			int rowNum = rowNums[i];
		    			if (rowNum >= 0 && rowNum < trackListModel.getRowCount() && !cancelled)
		    			{
		    				OnlineTrack track = trackListModel.getTrack(rowNum);
		    				String name = track.getTrackName();
	    					panel.setText("Downloading \"" + name +"\"");
	    					GPXFile gpx = new GPXFile();
	    					gpx.setName(name);
	    					gpx.setDesc("OSM Relation " + track.getId());
				        	osm.downloadRelation(track.getId(), gpx);
				        	gpx.updateAllProperties();
	    					if (changeListener != null) {
	    						firePropertyChange("newGpx", null, gpx);
	    					}
	    					
		    			}
		    		}
        		}
				return null;
			}
            @Override
            protected void done() {
            	msg.infoOff(panel);
        		cancelled = true;
        		dispose();        
            }	    	
	    };

        if (changeListener != null) {
        	downloadWorker.addPropertyChangeListener(changeListener);
        }    	    	
   	    downloadWorker.execute();		
	}

	@Override
	public String getTitle() {
		return "Download Relation from OpenStreetMap";
	}

}
