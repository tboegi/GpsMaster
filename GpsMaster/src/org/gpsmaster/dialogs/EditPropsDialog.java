package org.gpsmaster.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.gpsmaster.Const;
import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.GPXObject;
import org.gpsmaster.gpxpanel.Route;
import org.gpsmaster.gpxpanel.Track;


/**
 * 
 * A dialog enabling the user to update GPX metadata.
 * 
 * @author Matt Hoover
 *
 */
@SuppressWarnings("serial")
public class EditPropsDialog extends JDialog {

    private GPXObject gpxObject;
    
    private String name = null;
    private String desc = null;
    private String gpxType = null;
    private Integer number = null;
    private String activity = "";
    
    private JTextField inputName;
    private JTextArea inputDesc;
    private JTextField inputGPXType;
    private JTextField inputNumber;
    private JTextField inputActivity;

    /**
     * Constructs the {@link EditPropsDialog}.
     * 
     * @param parentFrame     The parent container for the dialog.
     * @param label     The dialog's label.
     * @param gpxObject The GPX element being edited.
     */
    public EditPropsDialog(Frame frame, String title, GPXObject gpxObject) {
        super(frame, title, true);
        setForeground(Color.BLACK);
        getContentPane().setForeground(Color.BLACK);
        this.gpxObject = gpxObject;
                
        ImageIcon icon = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/menubar/edit-properties.png"));
        setIconImage(icon.getImage());
        
        // set bounds
        int width = 400;
        int x_offset = (frame.getWidth() - width) / 2;
        int height = 250;
        int y_offset = (frame.getHeight() - height) / 2;
        setBounds(frame.getX() + x_offset, frame.getY() + y_offset, width, height);
        setMinimumSize(new Dimension(400, 250));
        
        // button msgPanel
        JPanel btnPanel = new JPanel();
        final JButton btnOk = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");
        btnPanel.add(btnOk);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButton();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButton();
            }
        });
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        // input msgPanel
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(inputPanel);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {70, 300};
        gridBagLayout.rowHeights = new int[] {20, 90};
        inputPanel.setLayout(gridBagLayout);
        
        JLabel lblName = new JLabel("Name");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblName.insets = new Insets(1, 1, 1, 1);
        gbc_lblName.gridx = 0;
        gbc_lblName.gridy = 0;
        gbc_lblName.weightx = 0;
        gbc_lblName.weighty = 0;
        inputPanel.add(lblName, gbc_lblName);
        
        inputName = new JTextField();
        if (gpxObject.isGPXFile()) {
        inputName.setText(((GPXFile) gpxObject).getMetadata().getName());
        } else {
        	inputName.setText(gpxObject.getName());
        }
        inputName.setFont(new Font("Tahoma", Font.PLAIN, 11));
        inputName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnOk.doClick();
                    e.consume();
                }
            }
        });
        GridBagConstraints gbc_inputName = new GridBagConstraints();
        gbc_inputName.fill = GridBagConstraints.HORIZONTAL;
        gbc_inputName.insets = new Insets(1, 1, 1, 1);
        gbc_inputName.gridx = 1;
        gbc_inputName.gridy = 0;
        gbc_inputName.weightx = 1;
        gbc_inputName.weighty = 0;
        inputPanel.add(inputName, gbc_inputName);

        // Activity
        if (gpxObject.isGPXFile()) {

	        JLabel lblActivity = new JLabel("Activity");
	        GridBagConstraints gbc_lblActivity = new GridBagConstraints();
	        gbc_lblActivity.fill = GridBagConstraints.HORIZONTAL;
	        gbc_lblActivity.insets = new Insets(1, 1, 1, 1);
	        gbc_lblActivity.gridx = 0;
	        gbc_lblActivity.gridy = 1;
	        gbc_lblActivity.weightx = 0;
	        gbc_lblActivity.weighty = 1;
	        inputPanel.add(lblActivity, gbc_lblActivity);
	
	        GPXFile gpx = (GPXFile) gpxObject;
	        inputActivity = new JTextField();
	        if (gpx.getExtension().containsKey(Const.EXT_ACTIVITY)) {
	        	inputActivity.setText(gpx.getExtension().getSubValue(Const.EXT_ACTIVITY));
        	}
	        inputActivity.setFont(new Font("Tahoma", Font.PLAIN, 11));
	        inputActivity.addKeyListener(new KeyAdapter() {
	            @Override
	            public void keyPressed(KeyEvent e) {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                    btnOk.doClick();
	                    e.consume();
	                }
	            }
	        });
	        GridBagConstraints gbc_inputActivity = new GridBagConstraints();
	        gbc_inputActivity.fill = GridBagConstraints.HORIZONTAL;
	        gbc_inputActivity.insets = new Insets(1, 1, 1, 1);
	        gbc_inputActivity.gridx = 1;
	        gbc_inputActivity.gridy = 1;
	        gbc_inputActivity.weightx = 0;
	        gbc_inputActivity.weighty = 1;
	        inputPanel.add(inputActivity, gbc_inputActivity);
        }

        
        JLabel lblDesc = new JLabel("Description");
        GridBagConstraints gbc_lblDesc = new GridBagConstraints();
        gbc_lblDesc.fill = GridBagConstraints.BOTH;
        gbc_lblDesc.insets = new Insets(1, 1, 1, 1);
        gbc_lblDesc.gridx = 0;
        gbc_lblDesc.gridy = 2;
        gbc_lblDesc.weightx = 0;
        gbc_lblDesc.weighty = 1;
        inputPanel.add(lblDesc, gbc_lblDesc);
        
        inputDesc = new JTextArea();
        inputDesc.setText(gpxObject.getDesc());
        inputDesc.setBorder(null);
        inputDesc.setFont(new Font("Tahoma", Font.PLAIN, 11));
        inputDesc.setLineWrap(true);
        inputDesc.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    if (e.getModifiers() > 0) {
                        inputDesc.transferFocusBackward();
                    } else {
                        inputDesc.transferFocus();
                    }
                    e.consume();
                }
            }
        });
        final JScrollPane scrollPane = new JScrollPane(inputDesc);
        scrollPane.setBorder(new LineBorder(new Color(171, 173, 179)));
        GridBagConstraints gbc_inputDesc = new GridBagConstraints();
        gbc_inputDesc.fill = GridBagConstraints.BOTH;
        gbc_inputDesc.insets = new Insets(1, 1, 1, 1);
        gbc_inputDesc.gridx = 1;
        gbc_inputDesc.gridy = 2;
        gbc_inputDesc.weightx = 1;
        gbc_inputDesc.weighty = 1;
        inputPanel.add(scrollPane, gbc_inputDesc);
        inputDesc.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        scrollPane.getViewport().setViewPosition(new Point(0,0));
                    }
                });
            }
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                        scrollBar.setValue(scrollBar.getMaximum());
                        inputDesc.setCaretPosition(inputDesc.getText().length());
                    }
                });

            }
        });
        btnPanel.add(btnCancel);
        getContentPane().add(btnPanel);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollPane.getViewport().setViewPosition(new Point(0,0));
            }
        });
        
        if (gpxObject.isRoute() || gpxObject.isTrack()) {
            JLabel lblType = new JLabel("Type");
            GridBagConstraints gbc_lblType = new GridBagConstraints();
            gbc_lblType.fill = GridBagConstraints.HORIZONTAL;
            gbc_lblType.insets = new Insets(1, 1, 1, 1);
            gbc_lblType.gridx = 0;
            gbc_lblType.gridy = 3;
            gbc_lblType.weightx = 0;
            gbc_lblType.weighty = 0;
            inputPanel.add(lblType, gbc_lblType);
            
            inputGPXType = new JTextField();
            if (gpxObject.isRoute()) {
                inputGPXType.setText(((Route) gpxObject).getType());
            } else if (gpxObject.isTrack()) {
                inputGPXType.setText(((Track) gpxObject).getType());
            }
            inputGPXType.setFont(new Font("Tahoma", Font.PLAIN, 11));
            inputGPXType.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        btnOk.doClick();
                        e.consume();
                    }
                }
            });
            GridBagConstraints gbc_inputType = new GridBagConstraints();
            gbc_inputType.fill = GridBagConstraints.HORIZONTAL;
            gbc_inputType.insets = new Insets(1, 1, 1, 1);
            gbc_inputType.gridx = 1;
            gbc_inputType.gridy = 3;
            gbc_inputType.weightx = 1;
            gbc_inputType.weighty = 0;
            inputPanel.add(inputGPXType, gbc_inputType);
            
            JLabel lblNumber = new JLabel("Number");
            GridBagConstraints gbc_lblNumber = new GridBagConstraints();
            gbc_lblNumber.fill = GridBagConstraints.HORIZONTAL;
            gbc_lblNumber.insets = new Insets(1, 1, 1, 1);
            gbc_lblNumber.gridx = 0;
            gbc_lblNumber.gridy = 4;
            gbc_lblNumber.weightx = 0;
            gbc_lblNumber.weighty = 0;
            inputPanel.add(lblNumber, gbc_lblNumber);
            
            inputNumber = new JTextField();
            if (gpxObject.isRoute()) {
                inputNumber.setText(Integer.toString(((Route) gpxObject).getNumber()));
            } else if (gpxObject.isTrack()) {
                inputNumber.setText(Integer.toString(((Track) gpxObject).getNumber()));
            }
            if (inputNumber.getText().equals("0")) {
                inputNumber.setText("");
            }
            inputNumber.setFont(new Font("Tahoma", Font.PLAIN, 11));
            inputNumber.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        btnOk.doClick();
                        e.consume();
                    }
                }
            });
            GridBagConstraints gbc_inputNumber = new GridBagConstraints();
            gbc_inputNumber.fill = GridBagConstraints.HORIZONTAL;
            gbc_inputNumber.insets = new Insets(1, 1, 1, 1);
            gbc_inputNumber.gridx = 1;
            gbc_inputNumber.gridy = 4;
            gbc_inputNumber.weightx = 1;
            gbc_inputNumber.weighty = 0;
            inputPanel.add(inputNumber, gbc_inputNumber);
            inputNumber.addFocusListener(new FocusListener() {
                @Override
                public void focusLost(FocusEvent e) {
                    String unformattedStr = inputNumber.getText();
                    NumberFormat formatter = NumberFormat.getIntegerInstance();
                    Number formattedNumber = null;
                    try {
                        formattedNumber = formatter.parse(unformattedStr);
                    } catch (ParseException e1) {
                    }
                    if (formattedNumber != null && formattedNumber.intValue() > 0
                            && formattedNumber.intValue() < Integer.MAX_VALUE) {
                        String formattedStr = String.format("%d", formattedNumber);
                        inputNumber.setText(formattedStr);
                    } else {
                        inputNumber.setText("");
                    }
                }
                @Override
                public void focusGained(FocusEvent e) {
                }
            });
        }
    }

    public String getName() {
        return name;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public String getGPXType() {
        return gpxType;
    }
    
    public Integer getNumber() {
        return number;
    }
    
    public String getActivity() {
    	return activity;
    }

    /**
     * Do this if the user clicks ok.
     */
    private void okButton() {
        name = inputName.getText();
        desc = inputDesc.getText();
        if (gpxObject.isRoute() || gpxObject.isTrack()) {
            gpxType = (inputGPXType.getText().equals("")) ? null : inputGPXType.getText(); 
            number = (inputNumber.getText().equals("")) ? null : Integer.parseInt(inputNumber.getText());
        }
        if (gpxObject.isGPXFile()) {
        	activity = inputActivity.getText();
        }
        setVisible(false);
    }

    /**
     * Do this if the user clicks cancel.
     */
    private void cancelButton() {
        name = null;
        desc = null;
        gpxType = null;
        number = null;
        setVisible(false);
    }
 }  
