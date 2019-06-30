package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;

public class InfoDialog extends GenericDialog {
	
	JFrame parentFrame = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1796804637625027944L;
	String version = "";
	
	public InfoDialog(JFrame frame, String version) {
		super(frame, null);
		this.version = version;
		this.parentFrame= frame;  		
	}

	/**
	 * 
	 */
	public void begin() {
		
		Container contentPane = getContentPane();
		Dimension dimension = new Dimension(450, 500);
		setMinimumSize(dimension);
		setCenterLocation();
		contentPane.setLayout(new BorderLayout());
						
		setIcon(Const.ICONPATH_MENUBAR, "about.png");
		Font defaultFont = new JLabel().getFont();		
        Font titleFont = new Font(defaultFont.getFamily(), Font.BOLD, defaultFont.getSize() + 2);        
        JLabel title = new JLabel();
        title.setBackground(Color.WHITE);
        title.setFont(titleFont);
        title.setText(version);
        JPanel titlePanel = new JPanel();
        titlePanel.add(title, BorderLayout.CENTER);
        
        // Tabbed Pane
		JPanel aboutPanel = new JPanel();
		Component aboutText = makePanel("/org/gpsmaster/About.html");
		aboutText.setPreferredSize(dimension);
		aboutPanel.add(aboutText, BorderLayout.CENTER);
		
		JPanel creditPanel = new JPanel();
		Component creditText = makePanel("/org/gpsmaster/Credits.html");
		creditText.setPreferredSize(dimension);
		creditPanel.add(creditText, BorderLayout.CENTER);
		
		JPanel changelogPanel = new JPanel();
		Component changelogText = makePanel("/org/gpsmaster/Changelog.html");
		changelogText.setPreferredSize(dimension);
		changelogPanel.add(changelogText, BorderLayout.CENTER);

		JPanel licensePanel = new JPanel();
		Component licenseText = makePanel("/org/gpsmaster/license.txt");
		licenseText.setPreferredSize(dimension);
		licensePanel.add(licenseText, BorderLayout.CENTER);
		
		// JPanel sponsorPanel = new JPanel();
		// to come

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		tabbedPane.addTab("About", aboutPanel);
		tabbedPane.addTab("Credits", creditPanel);
		tabbedPane.addTab("Changelog", changelogPanel);
		tabbedPane.addTab("License", licensePanel);
		// tabbedPane.addTab("Sponsors", sponsorPanel);
		
        // button msgPanel
        JPanel buttonPanel = new JPanel();
        JButton btnOk = new JButton("OK");
        buttonPanel.add(btnOk);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        contentPane.add(titlePanel, BorderLayout.NORTH);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        pack();
        contentPane.setVisible(true);        
        		
	}
	
	private Component makePanel(String filename) {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);		
		java.net.URL helpURL = GpsMaster.class.getResource(filename);
		if (helpURL != null) {
			try {
			editorPane.setPage(helpURL);
			} catch (IOException e) {
			System.err.println("Attempted to read a bad URL: " + helpURL);
			}
		} 
		JScrollPane scrollPane = new JScrollPane(editorPane);
		return scrollPane;
	}

	@Override
	public String getTitle() {		
		return "About";
	}
}
