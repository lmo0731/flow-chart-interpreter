/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drawer;

import ui.BlockEditor;
import drawer.entity.block.EndBlock;
import drawer.entity.block.BeginBlock;
import config.Config;
import drawer.entity.*;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.ho.yaml.Yaml;

/**
 *
 * @author LMO
 */
public final class DrawPanel extends javax.swing.JPanel {

    /**
     * Creates new form DrawPanel
     */
    Arrow selectedBlock = null;
    Arrow selectedArrow = null;
    Arrow hoverBlock = null;
    Arrow hoverArrow = null;
    BeginBlock root;
    Stack<String> redo = new Stack<String>();
    Stack<String> undo = new Stack<String>();
    File file = null;
    Class newBlock = null;
    Block errorBlock = null;
    Block currentBlock = null;
    private static int PADDING = 50;

    public DrawPanel() {
        initComponents();
        root = new BeginBlock();
        root.setNextBlock(new EndBlock());
        this.setFocusTraversalKeysEnabled(true);
        draw();
    }

    public BeginBlock getRoot() {
        return root;
    }

    public File getFile() {
        return file;
    }

    public void undo() {
        if (!undo.empty()) {
            redo.push(Yaml.dump(root));
            root = (BeginBlock) Yaml.load(undo.pop());
            draw();
            draw();
        }
    }

    public void redo() {
        if (!redo.empty()) {
            undo.push(Yaml.dump(root));
            root = (BeginBlock) Yaml.load(redo.pop());
            draw();
            draw();
        }
    }

    public void backup() {
        undo.push(Yaml.dump(root));
        if (undo.size() > 500) {
            undo.removeElementAt(0);
        }
        redo.clear();
    }

    public void save(File file) throws FileNotFoundException {
        if (!file.getName().endsWith("." + Config.getFileFormat())) {
            file = new File(file.getAbsolutePath() + "." + Config.getFileFormat());
            // throw new FileNotFoundException(String.format("%s (File must ends with %s)",
            // file.getAbsolutePath(), Config.getFileFormat()));
        }
        if (file.exists()) {
            if (!file.equals(this.file)) {
                if (JOptionPane.showConfirmDialog(this,
                        "File will be overwritten with " + file.getName(), "Overwrite file?",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }
        Yaml.dump(root, file);
        this.file = file;
    }

    public void open(File file) throws FileNotFoundException {
        try {
            root = (BeginBlock) Yaml.load(file);
        } catch (ClassCastException ex) {
            throw new FileNotFoundException(String.format("%s (Invalid File)", file.getAbsolutePath()));
        }
        this.file = file;
        draw();
    }

    public void draw() {
        int w = Math.max(0, root.getTotalLeftWidth() + root.getTotalRightWidth() + 2 * PADDING);
        int h = Math.max(0, root.getTotalHeight() + 2 * PADDING);
        BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        root.draw(g, root.getTotalLeftWidth() + PADDING, PADDING, root.getTotalHeight());
        drawField.setIcon(new ImageIcon(buffer));
    }

    public void export(File file) {
        int w = Math.max(0, root.getTotalLeftWidth() + root.getTotalRightWidth() + 2 * PADDING);
        int h = Math.max(0, root.getTotalHeight() + 2 * PADDING);
        BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        root.draw(g, root.getTotalLeftWidth() + PADDING, PADDING, root.getTotalHeight());
        try {
            ImageIO.write(buffer, "png", file);
        } catch (Exception e) {
        }
    }

    public void setNewBlock(Class newBlock) {
        this.newBlock = newBlock;
    }

    void refreshMouse(java.awt.event.MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        if (hoverBlock != null && hoverBlock.getNextBlock() != null) {
            hoverBlock.getNextBlock().unhover();
        }
        hoverBlock = root.getBlock(x, y);
        if (hoverBlock != null && hoverBlock.getNextBlock() != null) {
            hoverBlock.getNextBlock().hover();
        }
        hoverArrow = root.getArrow(x, y);
        if (hoverArrow != null) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        draw();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        breakpointButton = new javax.swing.JMenuItem();
        deleteButton = new javax.swing.JMenuItem();
        drawScroll = new javax.swing.JScrollPane();
        drawField = new javax.swing.JLabel();

        breakpointButton.setText("Breakpoint");
        breakpointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakpointButtonActionPerformed(evt);
            }
        });
        popupMenu.add(breakpointButton);

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        popupMenu.add(deleteButton);

        setBackground(new java.awt.Color(255, 255, 255));

        drawField.setBackground(new java.awt.Color(255, 255, 255));
        drawField.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        drawField.setDoubleBuffered(true);
        drawField.setFocusTraversalPolicyProvider(true);
        drawField.setOpaque(true);
        drawField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                drawFieldMouseReleased(evt);
            }
        });
        drawField.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                drawFieldMouseMoved(evt);
            }
        });
        drawScroll.setViewportView(drawField);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(drawScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(drawScroll, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE));
    }// </editor-fold>//GEN-END:initComponents

    private void drawFieldMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_drawFieldMouseReleased
        this.refreshMouse(evt);
        if (evt.isPopupTrigger() || evt.getButton() == MouseEvent.BUTTON2 || evt.getButton() == MouseEvent.BUTTON3) {
            if (hoverBlock != null) {
                this.popupMenu.show(this.drawField, evt.getX(), evt.getY());
            }
        } else if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 2 && hoverBlock != null && hoverBlock.getNextBlock() != null) {
                new BlockEditor(hoverBlock.getNextBlock()).setVisible(true);
                draw();
            }
            if (this.selectedBlock != null && this.selectedBlock.getNextBlock() != null) {
                this.selectedBlock.getNextBlock().unselect();
            }
            this.selectedBlock = hoverBlock;
            if (hoverBlock != null && hoverBlock.getNextBlock() != null) {
                this.selectedBlock.getNextBlock().select();
            }
        }
        if (hoverArrow != null && evt.getButton() == MouseEvent.BUTTON1 && newBlock != null) {
            backup();
            Block k = hoverArrow.getNextBlock();
            ArrowedBlock b = null;
            try {
                try {
                    b = (ArrowedBlock) newBlock.getConstructor(new Class[] {}).newInstance(new Object[] {});
                } catch (InstantiationException ex) {
                    Logger.getLogger(DrawPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(DrawPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(DrawPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(DrawPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                b.getNextArrow().setNextBlock(k);
                hoverArrow.setNextBlock(b);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(DrawPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(DrawPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        draw();
        this.refreshMouse(evt);
    }// GEN-LAST:event_drawFieldMouseReleased

    private void drawFieldMouseMoved(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_drawFieldMouseMoved
        this.refreshMouse(evt);
    }// GEN-LAST:event_drawFieldMouseMoved

    private void breakpointButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_breakpointButtonActionPerformed
        if (this.hoverBlock != null && this.hoverBlock.getNextBlock() != null) {
            this.hoverBlock.getNextBlock().setBreakpoint(!this.hoverBlock.getNextBlock().is_Breakpoint());
        }
        draw();
    }// GEN-LAST:event_breakpointButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_deleteButtonActionPerformed
        if (hoverBlock != null && hoverBlock.getNextBlock() != null) {
            if (ArrowedBlock.class.isInstance(hoverBlock.getNextBlock())) {
                ArrowedBlock b = (ArrowedBlock) hoverBlock.getNextBlock();
                hoverBlock.setNextBlock(b.getNextBlock());
            }
        }
        draw();
    }// GEN-LAST:event_deleteButtonActionPerformed
     // Variables declaration - do not modify//GEN-BEGIN:variables

    private javax.swing.JMenuItem breakpointButton;
    private javax.swing.JMenuItem deleteButton;
    private javax.swing.JLabel drawField;
    private javax.swing.JScrollPane drawScroll;
    private javax.swing.JPopupMenu popupMenu;
    // End of variables declaration//GEN-END:variables
}
