package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gpsmaster.Config;
import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.filehub.FileHub;
import org.gpsmaster.filehub.FileItem;

import eu.fuegenstein.messagecenter.MessageCenter;

/**
 * 
 * @author rfu
 *
 */
public class FileOpenDialog extends GenericDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3238498222001098590L;
	
	protected JPanel mainPanel = new JPanel();
	protected ItemTargetSelectionPanel targetPanel = null;
	protected JFileChooser fileChooser = null;
	protected FileHub fileHub = null;
	
	private Config config = null;
	
	/**
	 * Constructor
	 * @param parentFrame
	 * @param msg
	 * @param fileHub
	 */
	public FileOpenDialog(JFrame parentFrame, MessageCenter msg, FileHub fileHub) {
		super(parentFrame, msg);
		this.fileHub = fileHub;
		
		fileChooser = new JFileChooser();
		targetPanel = new ItemTargetSelectionPanel(fileHub.getItemTargets());
		
		setIcon(Const.ICONPATH_MENUBAR, "file-open.png");
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(fileChooser, BorderLayout.CENTER);
		// mainPanel.add(targetPanel, BorderLayout.SOUTH);
		
		getContentPane().add(mainPanel);	
		
		// handle clicks on JFileChooser buttons
		fileChooser.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
					loadSelected();
					setVisible(false);
					dispose();
				} else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
					setVisible(false);
					dispose();
				}				
			}
		});
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		int chooserWidth = (parentFrame.getWidth() * 8) / 10;
        int chooserHeight = (parentFrame.getHeight() * 8) / 10;
        chooserWidth = Math.min(864, chooserWidth);
        chooserHeight = Math.min(539, chooserHeight);
        setPreferredSize(new Dimension(chooserWidth, chooserHeight));

		pack();
		setCenterLocation();
		setVisible(true);		
	}

	/**
	 * 
	 * @return
	 */
	public JFileChooser getFileChooser() {
		return fileChooser;
	}
	
	/**
	 * 
	 * @param config
	 */
	public void setConfig(Config config) {
		this.config = config;
		if (config != null) {
			fileChooser.setCurrentDirectory(new File(config.getLastOpenDirectory()));
		}
	}
	
	@Override
	public void begin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle() {		
		return "Open Files";
	}

	/**
	 * 
	 */
	private void loadSelected() {
		
		config.setLastOpenDirectory(fileChooser.getCurrentDirectory().getPath());
		
        for (File file : fileChooser.getSelectedFiles()) {
        	fileHub.addItem(new FileItem(file));	        	
        }
        fileHub.run();
	}
}
