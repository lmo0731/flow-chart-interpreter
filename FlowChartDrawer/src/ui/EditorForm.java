/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EditorForm.java
 *
 * Created on May 15, 2012, 7:09:54 PM
 */
package ui;

import drawer.entity.block.SetBlock;
import drawer.entity.block.IfBlock;
import drawer.entity.block.LoopBlock;
import drawer.entity.block.ReadBlock;
import drawer.entity.block.PrintBlock;
import config.Config;
import drawer.DrawPanel;
import drawer.entity.*;
import interpreter.Interpreter;
import interpreter.exceptions.BuildException;
import interpreter.memory.Location;
import interpreter.memory.MemoryException;
import interpreter.memory.Pointer;
import interpreter.tools.Input;
import interpreter.tools.Output;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author munkhochir
 */
public class EditorForm extends javax.swing.JFrame implements ActionListener {

    Timer timer = new Timer(5, this);
    Class selected;
    Interpreter interpreter;
    FlowchartReader reader;
    Input input;
    Output output;
    Block currentBlock;
    Block errorBlock;
    DefaultListModel watchListModel;
    ArrayList<String> watchListVars = new ArrayList<String>();
    FileFilter fileFilter = new FlowchartFileFilter();
    FileFilter pictureFilter = new PngFileFilter();

    /**
     * Creates new form EditorForm
     */
    public EditorForm() {
        initComponents();
        nextButton.setEnabled(false);
        stopButton.setEnabled(false);
        reader = new FlowchartReader();
        input = new Input() {

            @Override
            public Object read() {
                Object ret = null;
                String in = JOptionPane.showInputDialog(null, "", "Оруул", JOptionPane.PLAIN_MESSAGE);
                try {
                    ret = Long.parseLong(in);
                } catch (NumberFormatException ex) {
                    try {
                        ret = Double.parseDouble(in);
                    } catch (NumberFormatException ex1) {
                        ret = in;
                    }
                }
                return ret;
            }
        };
        output = new Output() {

            String body = "";

            @Override
            public void write(String out) {
                out = out.replaceAll("[\\n]", "<br/>");
                body += out;
                console.setText("<html><head><style>body {font-family: Courier new;}</style></head><body>" + body + "</body></html>");
            }
        };
        watchListModel = new DefaultListModel();
        this.watchList.setModel(watchListModel);
        newFile();
    }

    void refresh() {
        Config.setZoomLevel(zoomSlider.getValue());
        for (int i = 0; i < drawTabbedPane.getTabCount(); i++) {
            ((DrawPanel) this.drawTabbedPane.getComponentAt(i)).draw();
            ((DrawPanel) this.drawTabbedPane.getComponentAt(i)).setNewBlock(selected);
        }
    }

    private void undo() {
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            d.undo();
        }
    }

    private void redo() {
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            d.redo();
        }
    }

    private void newFile() {
        DrawPanel d = new DrawPanel();
        this.drawTabbedPane.add("New", d);
        this.drawTabbedPane.setSelectedComponent(d);
        refresh();
    }

    private void closeFile() {
        if (drawTabbedPane.getTabCount() > 0) {
            this.drawTabbedPane.remove(drawTabbedPane.getSelectedIndex());
        }
        if (drawTabbedPane.getTabCount() == 0) {
            this.newFile();
        }
    }

    private void open() {
        boolean neednew = true;
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            if (d.getFile() != null) {
                neednew = false;
            }
        }
        this.fileOpenDialog.setCurrentDirectory(new File(Config.getFilePath()));
        if (this.fileOpenDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (neednew) {
                d = new DrawPanel();
            }
            try {
                d.open(this.fileOpenDialog.getSelectedFile());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EditorForm.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.drawTabbedPane.add(d.getFile().getName().substring(0, d.getFile().getName().lastIndexOf(".")), d);
            this.drawTabbedPane.setSelectedComponent(d);
        }
        refresh();
    }

    private void save(boolean as) {
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            if (d.getFile() == null || as) {
                this.fileSaveDialog.setCurrentDirectory(new File(Config.getFilePath()));
                if (this.fileSaveDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        d.save(this.fileSaveDialog.getSelectedFile());
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    return;
                }
            } else {
                try {
                    d.save(d.getFile());
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (d.getFile() != null) {
                this.drawTabbedPane.add(d.getFile().getName().substring(0, d.getFile().getName().lastIndexOf(".")), d);
                this.drawTabbedPane.setSelectedComponent(d);
            }
        }
    }

    private void export() {
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            this.exportDialog.setCurrentDirectory(new File(Config.getFilePath()));
            if (this.exportDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                d.export(this.exportDialog.getSelectedFile());
            }
        }
    }

    private void init() {
        if (errorBlock != null) {
            errorBlock.unerror();
        }
    }

    private void finish() {
        timer.stop();
        if (currentBlock != null) {
            currentBlock.popColor();
        }
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            d.draw();
        }
        nextButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    void declare() {
        final DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null && d.getFile() != null) {
            reader.setName(d.getFile().getName().substring(0, d.getFile().getName().lastIndexOf(".")));
            reader.setCode(d.getRoot().convert(reader.getDebug()));
            reader.setPath(d.getFile().getParent());
            interpreter = new Interpreter(reader, input, output);
        }
    }

    private void run(boolean debug) {
        timer.stop();
        save(false);
        nextButton.setEnabled(true);
        stopButton.setEnabled(true);
        reader.setDebug(debug);
        init();
        declare();
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            interpreter.interpret(d.getFile().getName().substring(0, d.getFile().getName().lastIndexOf('.')), new Vector<Object>(), true);
            timer.start();
        }
    }

    private void next() {
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            interpreter.next();
        }
    }

    private void stop() {
        nextButton.setEnabled(false);
        if (interpreter != null) {
            interpreter.stop();
        }
        finish();
    }

    private void addWatch(String s) {
        this.watchListVars.add(s);
    }

    private void refreshWatchList() {
        if (interpreter != null) {
            watchListModel.clear();
            for (int i = 0; i < this.watchListVars.size(); i++) {
                String value = "";
                try {
                    Object o = interpreter.getMemory().getData(new Location(new Pointer(0), this.watchListVars.get(i)));


                    if (Pointer.class.isInstance(o)) {
                        o = interpreter.getMemory().getArray((Pointer) o);
                    }
                    value = o.toString();


                } catch (MemoryException ex) {
                    Logger.getLogger(EditorForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                watchListModel.addElement(watchListVars.get(i) + " = " + value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DrawPanel d = (DrawPanel) this.drawTabbedPane.getSelectedComponent();
        if (d != null) {
            try {
                if (d.getFile().getName().equals(interpreter.getName() + "." + Config.getFileFormat())) {
                    Block c = d.getRoot().getBlockByLine(interpreter.getLine());

                    if (currentBlock != null) {
                        currentBlock.popColor();
                    }
                    currentBlock = c;
                    if (currentBlock != null) {
                        currentBlock.pushColor(Config.getBlockCurrentColor());
                    }
                }
                try {
                    this.refreshWatchList();
                } catch (Exception ex) {
                }
                try {
                    interpreter.checkError();
                } catch (BuildException ex) {
                    if (d.getFile().getName().equals(ex.getName() + "." + Config.getFileFormat())) {
                        errorBlock = d.getRoot().getBlockByLine(ex.getLine());
                        if (errorBlock != null) {
                            errorBlock.error();
                        }
                    }
                    throw ex;
                }
            } catch (Exception ex) {
                if (BuildException.class.isInstance(ex)) {
                    BuildException bx = (BuildException) ex;

                    output.write(
                            "<a style='color: red;'>" + String.format("Error on '%s': ", bx.getName()) + bx.getMessage() + "</a>\n");
                } else {
                    output.write("<a style='color: red;'>Error: " + ex.getMessage() + "</a>\n");
                }
                nextButton.setEnabled(false);
                finish();
                java.util.logging.Logger.getLogger(EditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            if (interpreter.isFinished()) {
                finish();
            }
            d.draw();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileSaveDialog = new javax.swing.JFileChooser();
        fileOpenDialog = new javax.swing.JFileChooser();
        toolboxButtonGroup = new javax.swing.ButtonGroup();
        exportDialog = new javax.swing.JFileChooser();
        toolBar = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        sepator1 = new javax.swing.JToolBar.Separator();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        sepator2 = new javax.swing.JToolBar.Separator();
        runButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        debugButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        sepator3 = new javax.swing.JToolBar.Separator();
        zoomOutLabel = new javax.swing.JLabel();
        zoomSlider = new javax.swing.JSlider();
        zoomInLabel = new javax.swing.JLabel();
        splitPane1 = new javax.swing.JSplitPane();
        splitPane2 = new javax.swing.JSplitPane();
        drawTabbedPane = new javax.swing.JTabbedPane();
        browser = new javax.swing.JTabbedPane();
        toolBoxPanel = new javax.swing.JPanel();
        ifBlockButton = new javax.swing.JToggleButton();
        setBlockButton = new javax.swing.JToggleButton();
        loopBlockButton = new javax.swing.JToggleButton();
        printBlockButton = new javax.swing.JToggleButton();
        readBlockButton = new javax.swing.JToggleButton();
        tabbedPane = new javax.swing.JTabbedPane();
        consoleScrollPane = new javax.swing.JScrollPane();
        console = new javax.swing.JEditorPane();
        watchListScroll = new javax.swing.JScrollPane();
        watchList = new javax.swing.JList();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        fileNewMenu = new javax.swing.JMenuItem();
        fileMenuSeperator1 = new javax.swing.JPopupMenu.Separator();
        fileOpenMenu = new javax.swing.JMenuItem();
        fileSaveMenu = new javax.swing.JMenuItem();
        fileSaveAsMenu = new javax.swing.JMenuItem();
        fileCloseMenu = new javax.swing.JMenuItem();
        fileExportMenu = new javax.swing.JMenuItem();
        fileMenuSeperator2 = new javax.swing.JPopupMenu.Separator();
        fileExitMenu = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editUndoMenu = new javax.swing.JMenuItem();
        editRedoMenu = new javax.swing.JMenuItem();
        runMenu = new javax.swing.JMenu();
        runRunMenu = new javax.swing.JMenuItem();
        debugMenu = new javax.swing.JMenu();
        debugDebugMenu = new javax.swing.JMenuItem();
        debugNextMenu = new javax.swing.JMenuItem();
        debugAddWatch = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        toolsOptionMenu = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();

        fileSaveDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fileSaveDialog.setFileFilter(fileFilter);

        fileOpenDialog.setFileFilter(fileFilter);

        exportDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        exportDialog.setFileFilter(pictureFilter);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Flow Chart Interpreter");

        toolBar.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/newFile.png"))); // NOI18N
        newButton.setToolTipText("New");
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        toolBar.add(newButton);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/openProject.png"))); // NOI18N
        openButton.setToolTipText("Open");
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        toolBar.add(openButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/save.png"))); // NOI18N
        saveButton.setToolTipText("Save");
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        toolBar.add(saveButton);
        toolBar.add(sepator1);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/undo.gif"))); // NOI18N
        undoButton.setToolTipText("Undo");
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });
        toolBar.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/redo.gif"))); // NOI18N
        redoButton.setToolTipText("Redo");
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });
        toolBar.add(redoButton);
        toolBar.add(sepator2);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/runProject.png"))); // NOI18N
        runButton.setToolTipText("Run");
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        toolBar.add(runButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/stop.png"))); // NOI18N
        stopButton.setToolTipText("Stop");
        stopButton.setEnabled(false);
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        toolBar.add(stopButton);

        debugButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buildProject.png"))); // NOI18N
        debugButton.setToolTipText("Debug");
        debugButton.setFocusable(false);
        debugButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        debugButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        debugButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugButtonActionPerformed(evt);
            }
        });
        toolBar.add(debugButton);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/next.png"))); // NOI18N
        nextButton.setToolTipText("Next");
        nextButton.setEnabled(false);
        nextButton.setFocusable(false);
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        toolBar.add(nextButton);
        toolBar.add(sepator3);

        zoomOutLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/zoomOut.png"))); // NOI18N
        toolBar.add(zoomOutLabel);

        zoomSlider.setMajorTickSpacing(100);
        zoomSlider.setMaximum(300);
        zoomSlider.setMinimum(50);
        zoomSlider.setMinorTickSpacing(25);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setValue(100);
        zoomSlider.setMaximumSize(new java.awt.Dimension(150, 23));
        zoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zoomSliderStateChanged(evt);
            }
        });
        zoomSlider.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                zoomSliderPropertyChange(evt);
            }
        });
        toolBar.add(zoomSlider);

        zoomInLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/zoomIn.png"))); // NOI18N
        toolBar.add(zoomInLabel);

        splitPane1.setBorder(null);
        splitPane1.setDividerLocation(400);
        splitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        splitPane2.setBorder(null);
        splitPane2.setDividerLocation(150);
        splitPane2.setRightComponent(drawTabbedPane);

        toolboxButtonGroup.add(ifBlockButton);
        ifBlockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/_blockif.png"))); // NOI18N
        ifBlockButton.setText("If");
        ifBlockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ifBlockButtonActionPerformed(evt);
            }
        });

        toolboxButtonGroup.add(setBlockButton);
        setBlockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/_blockset.png"))); // NOI18N
        setBlockButton.setText("Set");
        setBlockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setBlockButtonActionPerformed(evt);
            }
        });

        toolboxButtonGroup.add(loopBlockButton);
        loopBlockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/_blockloop.png"))); // NOI18N
        loopBlockButton.setText("Loop");
        loopBlockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopBlockButtonActionPerformed(evt);
            }
        });

        toolboxButtonGroup.add(printBlockButton);
        printBlockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/_blockprint.png"))); // NOI18N
        printBlockButton.setText("Print");
        printBlockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBlockButtonActionPerformed(evt);
            }
        });

        toolboxButtonGroup.add(readBlockButton);
        readBlockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/_blockread.png"))); // NOI18N
        readBlockButton.setText("Read");
        readBlockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readBlockButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout toolBoxPanelLayout = new javax.swing.GroupLayout(toolBoxPanel);
        toolBoxPanel.setLayout(toolBoxPanelLayout);
        toolBoxPanelLayout.setHorizontalGroup(
            toolBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(toolBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ifBlockButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(setBlockButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loopBlockButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(printBlockButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(readBlockButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        toolBoxPanelLayout.setVerticalGroup(
            toolBoxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ifBlockButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setBlockButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loopBlockButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(printBlockButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(readBlockButton)
                .addContainerGap(132, Short.MAX_VALUE))
        );

        browser.addTab("Toolbox", toolBoxPanel);

        splitPane2.setLeftComponent(browser);

        splitPane1.setLeftComponent(splitPane2);

        console.setContentType("text/html");
        console.setEditable(false);
        console.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        consoleScrollPane.setViewportView(console);

        tabbedPane.addTab("Output", consoleScrollPane);

        watchList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        watchListScroll.setViewportView(watchList);

        tabbedPane.addTab("Watch List", watchListScroll);

        splitPane1.setRightComponent(tabbedPane);

        fileMenu.setText("File");

        fileNewMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        fileNewMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/newFile.png"))); // NOI18N
        fileNewMenu.setText("New");
        fileNewMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNewMenuActionPerformed(evt);
            }
        });
        fileMenu.add(fileNewMenu);
        fileMenu.add(fileMenuSeperator1);

        fileOpenMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        fileOpenMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/openProject.png"))); // NOI18N
        fileOpenMenu.setText("Open");
        fileOpenMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileOpenMenuActionPerformed(evt);
            }
        });
        fileMenu.add(fileOpenMenu);

        fileSaveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        fileSaveMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/save.png"))); // NOI18N
        fileSaveMenu.setText("Save");
        fileSaveMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileSaveMenuActionPerformed(evt);
            }
        });
        fileMenu.add(fileSaveMenu);

        fileSaveAsMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        fileSaveAsMenu.setText("Save As");
        fileSaveAsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileSaveAsMenuActionPerformed(evt);
            }
        });
        fileMenu.add(fileSaveAsMenu);

        fileCloseMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        fileCloseMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/close.png"))); // NOI18N
        fileCloseMenu.setText("Close");
        fileCloseMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileCloseMenuActionPerformed(evt);
            }
        });
        fileMenu.add(fileCloseMenu);

        fileExportMenu.setText("Export");
        fileExportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileExportMenuActionPerformed(evt);
            }
        });
        fileMenu.add(fileExportMenu);
        fileMenu.add(fileMenuSeperator2);

        fileExitMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        fileExitMenu.setText("Exit");
        fileExitMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileExitMenuActionPerformed(evt);
            }
        });
        fileMenu.add(fileExitMenu);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");

        editUndoMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        editUndoMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/undo.gif"))); // NOI18N
        editUndoMenu.setText("Undo");
        editUndoMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editUndoMenuActionPerformed(evt);
            }
        });
        editMenu.add(editUndoMenu);

        editRedoMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        editRedoMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/redo.gif"))); // NOI18N
        editRedoMenu.setText("Redo");
        editRedoMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRedoMenuActionPerformed(evt);
            }
        });
        editMenu.add(editRedoMenu);

        menuBar.add(editMenu);

        runMenu.setText("Run");

        runRunMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        runRunMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/runProject.png"))); // NOI18N
        runRunMenu.setText("Run");
        runRunMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runRunMenuActionPerformed(evt);
            }
        });
        runMenu.add(runRunMenu);

        menuBar.add(runMenu);

        debugMenu.setText("Debug");

        debugDebugMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        debugDebugMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/buildProject.png"))); // NOI18N
        debugDebugMenu.setText("Debug");
        debugDebugMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugDebugMenuActionPerformed(evt);
            }
        });
        debugMenu.add(debugDebugMenu);

        debugNextMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        debugNextMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/next.png"))); // NOI18N
        debugNextMenu.setText("Next Stop");
        debugMenu.add(debugNextMenu);

        debugAddWatch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, java.awt.event.InputEvent.CTRL_MASK));
        debugAddWatch.setText("Add watch");
        debugAddWatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugAddWatchActionPerformed(evt);
            }
        });
        debugMenu.add(debugAddWatch);

        menuBar.add(debugMenu);

        toolsMenu.setText("Tools");

        toolsOptionMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/options.png"))); // NOI18N
        toolsOptionMenu.setText("Options");
        toolsMenu.add(toolsOptionMenu);

        menuBar.add(toolsMenu);

        helpMenu.setText("Help");
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
            .addComponent(splitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        newFile();
    }//GEN-LAST:event_newButtonActionPerformed

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        open();
    }//GEN-LAST:event_openButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        save(false);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
        undo();
    }//GEN-LAST:event_undoButtonActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
        redo();
    }//GEN-LAST:event_redoButtonActionPerformed

    private void zoomSliderPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_zoomSliderPropertyChange
        refresh();
    }//GEN-LAST:event_zoomSliderPropertyChange

    private void zoomSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zoomSliderStateChanged
        refresh();
    }//GEN-LAST:event_zoomSliderStateChanged

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        run(false);
    }//GEN-LAST:event_runButtonActionPerformed

    private void ifBlockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ifBlockButtonActionPerformed
        selected = IfBlock.class;
        refresh();
    }//GEN-LAST:event_ifBlockButtonActionPerformed
    private void setBlockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setBlockButtonActionPerformed
        selected = SetBlock.class;
        refresh();
    }//GEN-LAST:event_setBlockButtonActionPerformed
    private void loopBlockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopBlockButtonActionPerformed
        selected = LoopBlock.class;
        refresh();
    }//GEN-LAST:event_loopBlockButtonActionPerformed
    private void printBlockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBlockButtonActionPerformed
        selected = PrintBlock.class;
        refresh();
    }//GEN-LAST:event_printBlockButtonActionPerformed
    private void readBlockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readBlockButtonActionPerformed
        selected = ReadBlock.class;
        refresh();
    }//GEN-LAST:event_readBlockButtonActionPerformed
    private void fileNewMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNewMenuActionPerformed
        this.newFile();
    }//GEN-LAST:event_fileNewMenuActionPerformed

    private void fileOpenMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileOpenMenuActionPerformed
        this.open();
    }//GEN-LAST:event_fileOpenMenuActionPerformed

    private void fileCloseMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileCloseMenuActionPerformed
        this.closeFile();
    }//GEN-LAST:event_fileCloseMenuActionPerformed

    private void fileExitMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileExitMenuActionPerformed
        System.exit(0);
    }//GEN-LAST:event_fileExitMenuActionPerformed

    private void editUndoMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editUndoMenuActionPerformed
        this.undo();
    }//GEN-LAST:event_editUndoMenuActionPerformed

    private void editRedoMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRedoMenuActionPerformed
        this.redo();
    }//GEN-LAST:event_editRedoMenuActionPerformed

    private void runRunMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runRunMenuActionPerformed
        run(false);
    }//GEN-LAST:event_runRunMenuActionPerformed

    private void debugDebugMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugDebugMenuActionPerformed
        run(true);
    }//GEN-LAST:event_debugDebugMenuActionPerformed

    private void fileSaveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSaveMenuActionPerformed
        this.save(false);
    }//GEN-LAST:event_fileSaveMenuActionPerformed

    private void fileSaveAsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSaveAsMenuActionPerformed
        this.save(true);
    }//GEN-LAST:event_fileSaveAsMenuActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        next();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void debugButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugButtonActionPerformed
        run(true);
    }//GEN-LAST:event_debugButtonActionPerformed

private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
    stop();
}//GEN-LAST:event_stopButtonActionPerformed

private void debugAddWatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugAddWatchActionPerformed
    String s = (String) JOptionPane.showInputDialog("Enter variable name");
    addWatch(s);
}//GEN-LAST:event_debugAddWatchActionPerformed

    private void fileExportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileExportMenuActionPerformed
        this.export();
    }//GEN-LAST:event_fileExportMenuActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;










                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new EditorForm().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane browser;
    private javax.swing.JEditorPane console;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JMenuItem debugAddWatch;
    private javax.swing.JButton debugButton;
    private javax.swing.JMenuItem debugDebugMenu;
    private javax.swing.JMenu debugMenu;
    private javax.swing.JMenuItem debugNextMenu;
    private javax.swing.JTabbedPane drawTabbedPane;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editRedoMenu;
    private javax.swing.JMenuItem editUndoMenu;
    private javax.swing.JFileChooser exportDialog;
    private javax.swing.JMenuItem fileCloseMenu;
    private javax.swing.JMenuItem fileExitMenu;
    private javax.swing.JMenuItem fileExportMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPopupMenu.Separator fileMenuSeperator1;
    private javax.swing.JPopupMenu.Separator fileMenuSeperator2;
    private javax.swing.JMenuItem fileNewMenu;
    private javax.swing.JFileChooser fileOpenDialog;
    private javax.swing.JMenuItem fileOpenMenu;
    private javax.swing.JMenuItem fileSaveAsMenu;
    private javax.swing.JFileChooser fileSaveDialog;
    private javax.swing.JMenuItem fileSaveMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JToggleButton ifBlockButton;
    private javax.swing.JToggleButton loopBlockButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton newButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton openButton;
    private javax.swing.JToggleButton printBlockButton;
    private javax.swing.JToggleButton readBlockButton;
    private javax.swing.JButton redoButton;
    private javax.swing.JButton runButton;
    private javax.swing.JMenu runMenu;
    private javax.swing.JMenuItem runRunMenu;
    private javax.swing.JButton saveButton;
    private javax.swing.JToolBar.Separator sepator1;
    private javax.swing.JToolBar.Separator sepator2;
    private javax.swing.JToolBar.Separator sepator3;
    private javax.swing.JToggleButton setBlockButton;
    private javax.swing.JSplitPane splitPane1;
    private javax.swing.JSplitPane splitPane2;
    private javax.swing.JButton stopButton;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolBoxPanel;
    private javax.swing.ButtonGroup toolboxButtonGroup;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenuItem toolsOptionMenu;
    private javax.swing.JButton undoButton;
    private javax.swing.JList watchList;
    private javax.swing.JScrollPane watchListScroll;
    private javax.swing.JLabel zoomInLabel;
    private javax.swing.JLabel zoomOutLabel;
    private javax.swing.JSlider zoomSlider;
    // End of variables declaration//GEN-END:variables
}
