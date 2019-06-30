package org.gpsmaster.online;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.gpsmaster.Const;
import org.gpsmaster.dialogs.BrowserLauncher;
import org.gpsmaster.dialogs.GenericDialog;
import org.gpsmaster.dialogs.GuiGridLayout;
import org.gpsmaster.gpsloader.GpxLoader;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.messagecenter.MessagePanel;


/**
 * Function to upload track information up to Gpsies.com
 * 
 * @author rfu
 * @author tim.prune
 * Code taken from GpsPrune
 * http://activityworkshop.net 
 * 
 */
public class UploadGpsies extends GenericDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1243245722161680913L;
	/** Edit box for user name */
	private JTextField usernameField = null;
	/** Edit box for password */
	private JPasswordField passwordField = null;
	/** Name of track */
	private JTextField nameField = null;
	/** Description */
	private JTextArea descField = null;
	/** Private checkbox */
	private JCheckBox privateCheckbox = null;
	/** Activity checkboxes */
	private JCheckBox[] activityCheckboxes = null;

	/** upload button */
	private JButton uploadButton = null;
	private JButton webButton = null; // to open web page after upload
		
 	private String pageUrl = null;
 	private String activity = "Biking"; // default activity
 	
	/** URL to post form to */
	private static final String GPSIES_URL = "http://www.gpsies.com/upload.do";
	private static final String GPSIES_DEVICE = "GpsMaster";
	/** Keys for describing activities */
	// TODO consolidate with GpsMaster Activities
	// TODO extend to current list of activities on website
	private static final String[] ACTIVITY_KEYS = {
		"trekking", "walking", "jogging", 
		"skating", "crossskating", 
		"handcycle", "biking", "racingbike", "mountainbiking", 
		"motorbiking", "cabriolet", "car", 
		"skiingNordic", "skiingAlpine", "snowshoe", "wintersports", 
		"riding", "canoeing", "sailing", "boating", "climbing", "flying", "train", 
		"sightseeing", "geocaching", "miscellaneous"		
	};

	private MessageCenter msg = null;
	private GPXFile gpx = null;

	private PipedInputStream iStream = null;
	private PipedOutputStream oStream = null;

	/**
	 * Constructor
	 * @param gpx
	 * @param parentFrame
	 * @param msg
	 */
	public UploadGpsies(GPXFile gpx, JFrame parentFrame, MessageCenter msg) {
		super(parentFrame, msg);		
		this.msg = msg;
		this.gpx = gpx;
		
		usernameField = new JTextField(20);
	}

	/**
	 * Create dialog components
	 * @return Panel containing all gui elements in dialog
	 */
	private Component makeDialogComponents()
	{
		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BorderLayout());

		JPanel gridPanel = new JPanel();
		GuiGridLayout grid = new GuiGridLayout(gridPanel);
		grid.add(new JLabel("Username"));
		grid.add(usernameField);
		grid.add(new JLabel("Password"));
		passwordField = new JPasswordField(20);
		grid.add(passwordField);
		// Track name and description
		grid.add(new JLabel("Name"));
		nameField = new JTextField(20);
		grid.add(nameField);
		grid.add(new JLabel("Description"));
		descField = new JTextArea(5, 20);
		descField.setLineWrap(true);
		descField.setWrapStyleWord(true);
		grid.add(new JScrollPane(descField));
		// Listener on all these text fields to enable/disable the ok button
		KeyAdapter keyListener = new KeyAdapter() {
			/** Key released */
			public void keyReleased(KeyEvent inE) {
				enableOK();
				if (inE.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
				}
			}
		};
		usernameField.addKeyListener(keyListener);
		passwordField.addKeyListener(keyListener);
		nameField.addKeyListener(keyListener);
		// Listen for tabs on description field, to change focus not enter tabs
		descField.addKeyListener(new KeyAdapter() {
			/** Key pressed */
			public void keyPressed(KeyEvent inE) {
				if (inE.getKeyCode() == KeyEvent.VK_TAB) {
					inE.consume();
					if (inE.isShiftDown()) {
						nameField.requestFocusInWindow();
					}
					else {
						privateCheckbox.requestFocusInWindow();
					}
				}
			}
		});
		// Listen for Ctrl-backspace on password field (delete contents)
		passwordField.addKeyListener(new KeyAdapter() {
			/** Key released */
			public void keyReleased(KeyEvent inE) {
				if (inE.isControlDown() && (inE.getKeyCode() == KeyEvent.VK_BACK_SPACE
					|| inE.getKeyCode() == KeyEvent.VK_DELETE)) {
					passwordField.setText("");
				}
			}
		});
		// Checkbox for private / public
		grid.add(new JLabel("Keep private"));
		privateCheckbox = new JCheckBox();
		privateCheckbox.setSelected(false);
		grid.add(privateCheckbox);

		// msgPanel for activity type checkboxes
		JPanel activityPanel = new JPanel();
		activityPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		ChangeListener checkListener = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				enableOK();
			}
		};
		// Why not a simple grid layout here?
		GuiGridLayout actGrid = new GuiGridLayout(activityPanel, new double[] {1.0, 1.0}, new boolean[] {false, false});
		final int numActivities = ACTIVITY_KEYS.length;
		activityCheckboxes = new JCheckBox[numActivities];
		for (int i=0; i<numActivities; i++)
		{
			activityCheckboxes[i] = new JCheckBox(ACTIVITY_KEYS[i]);
			if (ACTIVITY_KEYS[i].equalsIgnoreCase(activity)) {
				activityCheckboxes[i].setSelected(true);
			}
			// IMPORTANT: add ChangeListener AFTER setSelected(), otherwise
			// it will fire before uploadButton() is instantiated.
			activityCheckboxes[i].addChangeListener(checkListener);
			actGrid.add(activityCheckboxes[i]);
		}
		grid.add(new JLabel("Activities"));
		grid.add(activityPanel);
		JPanel midPanel = new JPanel();
		midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
		midPanel.add(gridPanel);
		dialogPanel.add(midPanel, BorderLayout.CENTER);

		// button msgPanel at bottom
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		webButton = new JButton("Show Webpage");
		webButton.setEnabled(false);
		webButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BrowserLauncher.launchBrowser(pageUrl);
			}
		});				
		buttonPanel.add(webButton);
		
		uploadButton = new JButton("Upload");
		uploadButton.setEnabled(false);
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startUpload();
			}
		});				
		buttonPanel.add(uploadButton);
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});		
		buttonPanel.add(closeButton);
		dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
		dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
		return dialogPanel;
	}


	/**
	 * Check the inputs and enable or disable the upload button
	 */
	private void enableOK()
	{
		// Check for lengths of input fields - only username, password and filename are required
		boolean ok = (usernameField.getText().length() > 0 && nameField.getText().length() > 0 && gpx != null);
		if (ok) {
			// also check password field
			char[] pass = passwordField.getPassword();
			ok = pass.length > 0;
			for (int i=0; i<pass.length; i++) {pass[i] = '0';} // recommended by javadoc
			if (ok) {
				ok = false;
				for (int i=0; i<activityCheckboxes.length; i++) {
					ok = ok || activityCheckboxes[i].isSelected();
				}
			}
		}
		uploadButton.setEnabled(ok);
	}


	/**
	 * Start the upload process 
	 * TODO message panels not showing. (require separate thread?)
	 */
	private void startUpload()
	{
		BufferedReader reader = null;
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		MessagePanel infoPanel = msg.infoOn("Uploading " + nameField.getText()+ " to Gpsies.com ...");
		try
		{			
			webButton.setEnabled(false);
			pageUrl = null;
			
			// prepare password hash
			String validationString = usernameField.getText() + "|" + DigestUtils.md5Hex(passwordField.getText());
    		String encryptedString = new String(Base64.encodeBase64(validationString.getBytes()));
    		
			// "fill out" form
			FormPoster poster = new FormPoster(new URL(GPSIES_URL));
			poster.setParameter("device", GPSIES_DEVICE);
			poster.setParameter("authenticateHash", encryptedString);
			boolean hasActivity = false;
			for (int i=0; i<ACTIVITY_KEYS.length; i++)
			{
				if (activityCheckboxes[i].isSelected()) {
					hasActivity = true;
					poster.setParameter("trackTypes", ACTIVITY_KEYS[i]);
				}
			}
			if (!hasActivity) {poster.setParameter("trackTypes", "walking");} // default if none given
			poster.setParameter("filename", nameField.getText());
			poster.setParameter("fileDescription", descField.getText());
			poster.setParameter("status", (privateCheckbox.isSelected()?"3":"1"));
			poster.setParameter("uploadButton", "speichern"); // required
						
			// Use Pipes to connect the GpxExporter's output with the FormPoster's input
			iStream = new PipedInputStream();
			oStream = new PipedOutputStream(iStream);

			new Thread(new Runnable() {
				public void run() {
					GpxLoader gpxWriter = new GpxLoader();
					gpxWriter.save(gpx, oStream);

					try {oStream.close();} catch (IOException e) {}					
				}
			}).start();

			poster.setParameter("formFile", nameField.getText(), iStream);

			BufferedInputStream answer = new BufferedInputStream(poster.post());
			int response = poster.getResponseCode();
			reader = new BufferedReader(new InputStreamReader(answer));
			String line = reader.readLine();
			
			// Try to extract gpsies page url from the returned message
			if (response == 200 && line.substring(0, 2).toUpperCase().equals("OK"))
			{
				final int bracketPos = line.indexOf('[');
				if (bracketPos > 0 && line.endsWith("]")) {
					pageUrl = line.substring(bracketPos + 1, line.length()-1);
				}
				msg.volatileInfo("Upload finished.");
			}
			// enable "Show Webpage" button on success
			if (pageUrl != null)
			{
				webButton.setEnabled(true);
				firePropertyChange("gpsiesUsername", null, usernameField.getText());
				if (gpx.getExtension().containsKey(Const.EXT_GPSIESURL)) {
					gpx.getExtension().remove(Const.EXT_GPSIESURL);
				}
				gpx.getExtension().add(Const.EXT_GPSIESURL, pageUrl);
			}
			else {
				msg.error("Upload failed: " + line);
			}
		}
		catch (MalformedURLException e) {}
		catch (IOException ioe) {
			msg.error("Upload failed", ioe);
		}
		finally {
			try {if (reader != null) reader.close();} catch (IOException e) {}
			msg.infoOff(infoPanel);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
		}
		// dispose();
	}
	
	public String getUsername() {
		return usernameField.getText();
	}

	public void setUsername(String username) {
		usernameField.setText(username);
	}

	@Override
	public void begin() {
		setLocationRelativeTo(parentFrame);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().add(makeDialogComponents());
		setIcon(Const.ICONPATH_DLBAR, "gpsies-up.png");
		pack();	
		setCenterLocation();
		setVisible(true);
		if (gpx.getName() != null) {
			nameField.setText(gpx.getName());
		}
		if (gpx.getDesc() != null) {
			descField.setText(gpx.getDesc());
		}		
		if (gpx.getExtension().containsKey(Const.EXT_ACTIVITY)) {
			activity = gpx.getExtension().getSubValue(Const.EXT_ACTIVITY);			
		}		
	}

	@Override
	public String getTitle() {
		return "Upload to www.gpsies.com";
	}

}
