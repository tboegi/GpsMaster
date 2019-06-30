package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.marker.PinMarker;
import org.gpsmaster.marker.WaypointMarker;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.osm.Nominatim;
import eu.fuegenstein.osm.NominatimPlace;
import eu.fuegenstein.osm.NominatimResult;

public class NameSearchPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1503013835813804411L;
			
	// NORTH: text field & search button 
	private JPanel entryPanel = null;
	private JTextField textField = null;
	private ActionListener actionListener = null;
	private JButton wptButton = null;
	private JButton searchButton = null;
	private JButton rptButton = null; // button to add points to route
	
	private ImageIcon searchIcon = null;
	private ImageIcon searchMoreIcon = null;
	private final String tooltip ="Search place by name";
	private final String moreTooltip ="get more results";
	
	// CENTER: result table
	private JScrollPane scrollPane = null;
	private JTable resultTable = null;
	private NameResultModel resultModel = null;
		
	// SOUTH: attribution label
	private JLabel attributionLabel = null;
		
	private PinMarker pin = null;
	private Nominatim nominatim = new Nominatim();
	private NominatimResult result = null;
	private MessageCenter msg = null;
	private boolean isBusy = false;
	private boolean addRoutePoints = false;
	
	private final int minLength = 3;  // minimum length required for search term
	private final int sizeRows = 5; // set default size of result table to show this amount of rows

	private Dimension emptySize = new Dimension(65535, 26);
	private final String iconPath = Const.ICONPATH_DIALOGS;
	
	/**
	 * 
	 * @param msg
	 */
	public NameSearchPanel(MessageCenter messageCenter) {
		
		pin = new PinMarker(0, 0);
		msg = messageCenter;
		setLayout(new BorderLayout());
		
		// icons are 16x16. add a few pixels for cosmetical reasons
		Dimension iconSize = new Dimension(26, 22);
		
		entryPanel = new JPanel();
		entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.LINE_AXIS));
		
		textField = new JTextField();
		// textField.setPreferredSize(new Dimension(50, 22));
		textField.setMaximumSize(new Dimension(65535, 22));
		textField.setToolTipText(tooltip);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) { // TODO handle backspace?
				if (textField.getText().length() >= minLength) {
					searchButton.setEnabled(true);
				} else {
					searchButton.setEnabled(false);
				}
			}
		});
		entryPanel.add(textField);
		
		searchIcon = new ImageIcon(GpsMaster.class.getResource(iconPath+"search.png"));
		searchMoreIcon = new ImageIcon(GpsMaster.class.getResource(iconPath+"search-more.png"));
				
		// clear search text box and hide result table
		JButton clearButton = new JButton();
		clearButton.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath+"search-clear.png")));
		clearButton.setToolTipText("clear search");
		clearButton.setMinimumSize(iconSize);
		clearButton.setPreferredSize(iconSize);
		clearButton.setMaximumSize(iconSize);
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				clearEntryPanel();
				tableOff();				
				resizePanel();
			}
		});
		entryPanel.add(clearButton);

		// set selected result as waypoint
		wptButton = new JButton();
		wptButton.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath+"search-add.png")));
		wptButton.setToolTipText("add as waypoint");
		wptButton.setMinimumSize(iconSize);
		wptButton.setPreferredSize(iconSize);
		wptButton.setMaximumSize(iconSize);
		wptButton.setEnabled(false);
		wptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addWaypoint(resultModel.getPlace(resultTable.getSelectedRow()));
			}

		});
		entryPanel.add(wptButton);

		// add selected result as point to route
		rptButton = new JButton();
		rptButton.setIcon(new ImageIcon(GpsMaster.class.getResource(iconPath+"route-add.png")));
		rptButton.setToolTipText("add as point to current route");
		rptButton.setMinimumSize(iconSize);
		rptButton.setPreferredSize(iconSize);
		rptButton.setMaximumSize(iconSize);
		rptButton.setVisible(false);
		rptButton.setEnabled(false);
		rptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addRoutepoint(resultModel.getPlace(resultTable.getSelectedRow()));
			}

		});
		entryPanel.add(rptButton);

		// search button
		searchButton = new JButton();
		searchButton.setIcon(searchIcon);
		searchButton.setToolTipText(tooltip);
						
		searchButton.setMinimumSize(iconSize);
		searchButton.setPreferredSize(iconSize);
		searchButton.setMaximumSize(iconSize);
		entryPanel.add(searchButton);

		actionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (isBusy == false) {
					SwingWorker<Void, Void> searchWorker = new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							busyOn();
							result = nominatim.lookup();	
							attributionLabel.setText(result.getAttribution());
							if (result.getPlaces().size() > 0) {						
								for (NominatimPlace place : result.getPlaces()) {
									resultModel.add(place);									
								}
								tableOn();
								resizePanel();
							}				
							return null;
						}
						
						@Override protected void done() {
							if (nominatim.hasMore()) {
								searchButton.setIcon(searchMoreIcon);
								searchButton.setToolTipText(moreTooltip);
							} else {
								searchButton.setIcon(searchIcon);
								searchButton.setToolTipText(tooltip);						
							}							
							busyOff();
						}			
					};
					
					String queryString = textField.getText();
					if (queryString.equals(nominatim.getQueryString()) == false) {
						nominatim.setQueryString(queryString);
						resultModel.clear();
						resultTable.clearSelection();
						wptButton.setEnabled(false);
						rptButton.setEnabled(false);
					}
					try {						
						searchWorker.execute();
					} catch (Exception e) {
						tableOff();
						clearEntryPanel();
						msg.error(e);
					}					
				}
			}
		};
		textField.addActionListener(actionListener);
		searchButton.addActionListener(actionListener);
		
		add(entryPanel, BorderLayout.NORTH);
		
		// set up table
		
		resultModel = new NameResultModel();
		resultTable = new JTable();
		resultTable.setModel(resultModel);
		resultTable.setTableHeader(null);
		resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
	        public void valueChanged(ListSelectionEvent event) {
				int idx = resultTable.getSelectedRow();
				if (event.getValueIsAdjusting() == false) {					
					if ((idx < resultModel.getRowCount()) && (idx >= 0)) {
						NominatimPlace place = resultModel.getPlace(idx);
						wptButton.setEnabled(true);
						rptButton.setEnabled(true);
						GpsMaster.active.removeMarker(pin);
						pin.setLat(place.getLat());
						pin.setLon(place.getLon());						
						GpsMaster.active.addMarker(pin); 
						GpsMaster.active.centerMap(pin);
					}
				}
	        }
	    });
		scrollPane = new JScrollPane(resultTable);
		scrollPane.setVisible(false);
		
		// attribution label
		attributionLabel = new JLabel();
		add(scrollPane, BorderLayout.CENTER);
		add(attributionLabel, BorderLayout.SOUTH);
		
		setMaximumSize(emptySize);		
	}
	
	/**
	 * give the parent container information about the current layout requirements 
	 * @return {@link true}: result table is showing, {@link false}: just the search msgPanel is showing 
	 */
	public boolean isResultsShowing() {
		return scrollPane.isVisible();
	}

	/**
	 * allow the user to add the location of a nominatim result 
	 * to the currently planned route. notification is done via
	 * a property change event.
	 * 
	 * @param enabled
	 */
	public void setRoutepointEnabled(boolean enabled) {
		addRoutePoints = enabled;
		if (enabled) {
			rptButton.setVisible(true);
		} else {
			rptButton.setVisible(false);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isRoutepointEnabled() {
		return addRoutePoints;
	}
	
	/**
	 * add given place as waypoint to current {@link GPXFile}
	 * if no {@link GPXFile} is selected, create a new one.
	 * @param place
	 */
	private void addWaypoint(NominatimPlace place) {
		WaypointMarker wpt = new WaypointMarker(place.getLat(), place.getLon());
		// TODO place.getDisplayName is too long.
		// find better/shorter solution
		wpt.setName(place.getDisplayName());
		wpt.setSrc("Nominatim");
		GPXFile gpx = GpsMaster.active.getGpxFile();
		if (gpx == null) {
			gpx = new GPXFile();
			gpx.setName("Nominatim"); // TODO find better name
			GpsMaster.active.addGpxFile(gpx);
			GpsMaster.active.refreshTree();
		}
		// wpt.getExtensions().putAll(place.getAll()); // doesn't make much sense
		gpx.getWaypointGroup().addWaypoint(wpt);
		GpsMaster.active.refresh();		
		GpsMaster.active.repaintMap();
	}

	/**
	 * add given place as point to a route
	 * @param place
	 */
	private void addRoutepoint(NominatimPlace place) {
		Waypoint wpt = new Waypoint(place.getLat(), place.getLon());
		wpt.setName(place.getDisplayName());
		firePropertyChange(Const.PCE_ADDROUTEPT, null, wpt);
	}
	
	/**
	 * 
	 */
	private void tableOn() {	
		scrollPane.setVisible(true);
		attributionLabel.setVisible(true);
	}
	
	/**
	 * 
	 */
	private void tableOff() {
		scrollPane.setVisible(false);
		attributionLabel.setVisible(false);
		setMaximumSize(emptySize);
	}
	
	/**
	 * does not work
	 */
	private void resizePanel() {		
		System.out.println("resizing table");
		if (scrollPane.isVisible()) {
			int rows = Math.min(sizeRows, resultModel.getRowCount());
			int rowHeight = resultTable.getRowHeight();
			int height = rows * rowHeight
					+ entryPanel.getHeight() + attributionLabel.getHeight();
			System.out.println("height=" + height);
			Dimension newSize = new Dimension(getWidth(), height);
			setPreferredSize(newSize);
			setMaximumSize(null);
		} else {
			setPreferredSize(emptySize);
			setMaximumSize(emptySize);
		}
		
		if (getParent() instanceof JSplitPane) {
			JSplitPane parent = (JSplitPane) getParent();
			parent.resetToPreferredSizes();
		}
		System.out.println("height now: " + getSize().height);
	}
	
	
	/**
	 * 
	 */
	private void clearEntryPanel() {
		tableOff();
		textField.setText("");
		resultModel.clear();
		if (result != null) {
			result.clear();
		}
		searchButton.setIcon(searchIcon);
		wptButton.setEnabled(false);
		rptButton.setEnabled(false);
		GpsMaster.active.removeMarker(pin);
		GpsMaster.active.repaintMap();
	}
	
	private void busyOn() {
		Cursor cursor = new Cursor(Cursor.WAIT_CURSOR);
		searchButton.setCursor(cursor);
		textField.setCursor(cursor);
		searchButton.setEnabled(false);
		isBusy = true;
	}
	
	private void busyOff() {
		Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		searchButton.setCursor(cursor);
		textField.setCursor(cursor);
		searchButton.setEnabled(true);
		isBusy = false;		
	}
}
