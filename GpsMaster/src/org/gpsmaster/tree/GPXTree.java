package org.gpsmaster.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXObject;


/**
 *
 * An extension of {@link JTree} customized for the display of GPX elements.
 *
 * @author Matt Hoover
 *
 */
@SuppressWarnings("serial")
public class GPXTree extends JTree {

    private GPXObject gpxObj;
    private JColorChooser colorChooser;
    private DefaultTreeModel treeModel;
    private JDialog dialog;

    /**
     * Default constructor.
     *
     * @param treeModel     The data model.
     */
    public GPXTree(final DefaultTreeModel treeModel) {
        super(treeModel);
        this.treeModel = treeModel;

        colorChooser = new JColorChooser();
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gpxObj.setColor(colorChooser.getColor());
                treeModel.nodeChanged((TreeNode) treeModel.getRoot());
            }
        };
        dialog = JColorChooser.createDialog(null, "Choose a Color", true, colorChooser, al, null);

        BufferedImage img = null;
        try {
            img = ImageIO.read(GpsMaster.class.getResourceAsStream("/org/gpsmaster/icons/color-palette.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setIconImage(img);
    }

    /**
     * Extends mouse interactivity with the tree.
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        int type = e.getID();
        if (type == MouseEvent.MOUSE_CLICKED || type == MouseEvent.MOUSE_RELEASED) {
            // do nothing
        } else if (type == MouseEvent.MOUSE_PRESSED) {
            int x = e.getX();
            int y = e.getY();
            int xOffset, yOffset;
            int row, rowTemp;

            row = this.getRowForLocation(x, y);
            if (row == -1) {
                super.processMouseEvent(e);
                return;
            }

            xOffset = x;
            rowTemp = row;
            while (rowTemp == row) {
                xOffset--;
                rowTemp = this.getRowForLocation(xOffset, y);
            }
            xOffset++;

            yOffset = y;
            rowTemp = row;
            while (rowTemp == row) {
                yOffset--;
                rowTemp = this.getRowForLocation(x, yOffset);
            }
            yOffset++;

            TreePath tp = this.getPathForLocation(x, y);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
            Object userObject = node.getUserObject();
            gpxObj = (GPXObject) userObject;

            x = x - xOffset;
            y = y - yOffset;
            if (x >= 0 && x <= 9) {
                if (y >= 4 && y <= 12) {
                    gpxObj.setVisible(!gpxObj.isVisible());
                    treeModel.nodeChanged(node);
                }
            } else if (x >= 13 && x <= 19) {
                if (y >= 4 && y <= 12) {
                    gpxObj.setWptsVisible(!gpxObj.isWptsVisible());
                    treeModel.nodeChanged(node);
                }
            } else if (x >= 23 && x <= 32) {
                if (y >= 4 && y <= 12) {
                    colorChooser.setColor(gpxObj.getColor());
                    dialog.setVisible(true);
                }
            } else if (x > 36) {
                this.clearSelection();
                super.processMouseEvent(e);
            }
        } else {
            super.processMouseEvent(e);
        }
    }
}
