package org.gpsmaster.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gpsmaster.GpsMaster;

@SuppressWarnings("serial")
public class InfoDialog extends JDialog {

	public InfoDialog(Frame frame) {
        // set bounds

        int width = 400;
        int x_offset = (frame.getWidth() - width) / 2;
        int height = 300;
        int y_offset = (frame.getHeight() - height) / 2;
        setBounds(frame.getX() + x_offset, frame.getY() + y_offset, width, height);
        setPreferredSize(new Dimension(400, 250));
        setTitle("About");

        ImageIcon icon = new ImageIcon(GpsMaster.class.getResource("/org/gpsmaster/icons/about.png"));
        setIconImage(icon.getImage());

        // button panel
        JPanel btnPanel = new JPanel();
        final JButton btnOk = new JButton("OK");
        btnPanel.add(btnOk);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        getContentPane().setLayout(new BorderLayout());

        JLabel textArea = new JLabel();
        getContentPane().add(textArea,BorderLayout.CENTER);

        InputStream in = GpsMaster.class.getResourceAsStream("/org/gpsmaster/About.txt");
        StringBuffer sb = new StringBuffer();
        int chr;
        // Read until the end of the stream
        try {
			while ((chr = in.read()) != -1)
			    sb.append((char) chr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        textArea.setText(sb.toString());

        getContentPane().add(btnPanel, BorderLayout.SOUTH);
	}

}
