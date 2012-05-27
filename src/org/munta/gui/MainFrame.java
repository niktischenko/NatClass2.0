package org.munta.gui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.munta.NatClassApp;
import org.munta.importexport.Exporter;
import org.munta.importexport.Importer;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.GlobalProperties;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;
import org.munta.projectengine.ProjectManager;

public class MainFrame extends JFrame {

    private static final String staticTitle = "NatClass 2.0";
    
    private NatClassApp app;
    //Colorer
    private AnalysisColorer colorer = new AnalysisColorer();
    //List models
    private EntityViewModel entityViewModel = null;
    private EntityDetailsViewModel entityDetailsViewModel = null;
    private RegularityViewModel regularityViewModel = null;
    private RegularityDetailsViewModel regularityDetailsViewModel = null;
    private ClassesViewModel classViewModel = null;
    private ClassesDetailsViewModel classDetailsViewModel = null;
    // Lists
    private JList entityList;
    private JList entityDetailsList;
    private JList regularityList;
    private JList regularityDetailsList;
    private JList classList;
    private JList classDetailsList;
    // FileDialog
    private FileDialog importFileDialog;
    // Other stuff
    private JStatusBar statusBar;
    // Actions
    private Action importAction = new AbstractAction("Import...") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            importFileDialog.setMode(FileDialog.LOAD);
            importFileDialog.setVisible(true);

            if (importFileDialog.getFile() == null || importFileDialog.getFile().isEmpty()) {
                return;
            }

            String filePath = new File(importFileDialog.getDirectory(), importFileDialog.getFile()).getAbsolutePath();
            try {
                Object newData = Importer.importFromFile(filePath);
                if (newData instanceof EntityCollection) {

                    Object[] options = {"As Objects", "As Ideal Classes", "Cancel"};
                    int n = JOptionPane.showOptionDialog(MainFrame.this,
                            "Would you like to import new data as Objects or Ideal Classes?",
                            "Import Entities or Classes?",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, //do not use a custom Icon
                            options, //the titles of buttons
                            options[2]);
                    if (n == 0) {
                        ProjectManager.getInstance().getCollectionOfEntities().clear();
                        ProjectManager.getInstance().getCollectionOfEntities().addAll((EntityCollection) newData);
                    } else if (n == 1) {
                        ProjectManager.getInstance().getCollectionOfIdealClasses().clear();
                        ProjectManager.getInstance().getCollectionOfIdealClasses().addAll((EntityCollection) newData);
                    } else {
                        return;
                    }
                }
                if (newData instanceof RegularityCollection) {
                    int n = JOptionPane.showConfirmDialog(MainFrame.this,
                            "Would you like import Regularities?",
                            "Import Regularities?",
                            JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.NO_OPTION) {
                        return;
                    }

                    ProjectManager.getInstance().getCollectionOfRegularities().clear();
                    ProjectManager.getInstance().getCollectionOfRegularities().putAll((RegularityCollection) newData);
                }
                if (newData instanceof GlobalProperties) {
                    int n = JOptionPane.showConfirmDialog(MainFrame.this,
                            "Would you like import properties?",
                            "Import properties?",
                            JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.NO_OPTION) {
                        return;
                    }

                    ProjectManager.getInstance().getGlobalProperties().set((GlobalProperties) newData);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage());
            }
        }
    };
    private Action exportAction = new AbstractAction("Export...") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            Object[] possibilities = {"Objects", "Regularities", "Ideal Classes", "Properties"};
            String s = (String)JOptionPane.showInputDialog(MainFrame.this,
                    "What do you want to export?", "Export...", JOptionPane.PLAIN_MESSAGE, null,
                    possibilities, "Objects");

            Object data = null;
            if(s.equals(possibilities[0])) {
                data = ProjectManager.getInstance().getCollectionOfEntities();
            } else if(s.equals(possibilities[1])) {
                data = ProjectManager.getInstance().getCollectionOfRegularities();
            } else if(s.equals(possibilities[2])) {
                data = ProjectManager.getInstance().getCollectionOfIdealClasses();
            } else if(s.equals(possibilities[3])) {
                data = ProjectManager.getInstance().getGlobalProperties();
            } else {
                return;
            }
            
            importFileDialog.setMode(FileDialog.SAVE);
            importFileDialog.setVisible(true);

            if (importFileDialog.getFile() == null || importFileDialog.getFile().isEmpty()) {
                return;
            }

            String filePath = new File(importFileDialog.getDirectory(), importFileDialog.getFile()).getAbsolutePath();
            
            if (new File(filePath).exists()) {
                int n = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Target file already exists. Would you like to overwrite it?",
                        "Overwrite target file?",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            
            try {
                Exporter.exportData(data, filePath);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage());
            }
        }
    };
    private Action exitAction = new AbstractAction("Exit") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            app.exitApplication();
        }
    };
    private Action newProjectAction = new AbstractAction("New Project") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            app.newProject();
        }
    };
    private Action openProjectAction = new AbstractAction("Open project...") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //int rVal = fileChooser.showOpenDialog(MainFrame.this);
            //if (rVal == JFileChooser.APPROVE_OPTION) {
            //    app.openProject(fileChooser.getSelectedFile().getAbsolutePath());
            //}

            String fileName = app.openProject();
            if(fileName != null) {
                setFilename(fileName);
            }
        }
    };
    private Action saveProjectAction = new AbstractAction("Save") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            String fileName = app.saveProject();
            if(fileName != null) {
                setFilename(fileName);
            }
        }
    };
    private Action saveAsProjectAction = new AbstractAction("Save As...") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //int rVal = fileChooser.showSaveDialog(MainFrame.this);
            //if (rVal == JFileChooser.APPROVE_OPTION) {
            //    app.saveAsProject(fileChooser.getSelectedFile().getAbsolutePath());
            //}
            
            setFilename(app.saveAsProject());
        }
    };
    private Action setOverviewModelAction = new AbstractAction("Overview") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            colorer.setOverviewMode();
            redrawLists();
        }
    };
    private Action setEntityAnalysisModelAction = new AbstractAction("Object Analysis") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            colorer.setEntityAnalysisMode();
            setEntityAnalysisModelSetEntityAction.actionPerformed(null);
            setEntityAnalysisModelSetClassAction.actionPerformed(null);
        }
    };
    private Action setEntityAnalysisModelSetEntityAction = new AbstractAction("Set master object") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //if(colorer.getMode() != AnalysisColorer.ENTITY_ANALYSIS)
            //    return;
            
            int index = entityList.getSelectedIndex();
            if(index != -1) {
                colorer.setEntity((Entity)entityViewModel.getModelObjectAt(index));
            }
            
            redrawLists();
        }
    };
    private Action setEntityAnalysisModelSetClassAction = new AbstractAction("Set master class") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //if(colorer.getMode() != AnalysisColorer.ENTITY_ANALYSIS)
            //    return;
            
            int index = classList.getSelectedIndex();
            if(index != -1) {
                colorer.setIdealClass((Entity)classViewModel.getModelObjectAt(index));
            }
            
            redrawLists();
        }
    };
    private Action setRegularityAnalysisModelAction = new AbstractAction("Regularity Analysis") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            colorer.setRegularityAnalysisMode();
            
            int index = regularityList.getSelectedIndex();
            if(index != -1) {
                colorer.setRegularity(((Entry<String, Regularity>)regularityViewModel.getModelObjectAt(index)).getValue());
            }
            
            redrawLists();
        }
    };
    
    private ListSelectionListener regularityAnalysisModelListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent lse) {
            
            if(lse.getValueIsAdjusting()) {
                return;
            }
            
            int index = regularityList.getSelectedIndex();
            if(index == -1) {
                return;
            }
            
            if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS) {
                // This is a workaround for the selection bug
                int entityIndex = entityList.getSelectedIndex();
                int classIndex = classList.getSelectedIndex();
                entityList.clearSelection();
                classList.clearSelection();
                
                colorer.setRegularity(((Entry<String, Regularity>)regularityViewModel.getModelObjectAt(index)).getValue());
                
                redrawLists();
                
                if(entityIndex >= 0) { 
                    entityList.setSelectedIndex(entityIndex);
                }
                if(classIndex >= 0) { 
                    classList.setSelectedIndex(classIndex);
                }
            }
        }
    };
    
    private Action setClassesAnalysisModelAction = new AbstractAction("Classes Analysis") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            colorer.setClassAnalysisMode();
            
            int index = classList.getSelectedIndex();
            if(index != -1) {
                colorer.setIdealClass((Entity)classViewModel.getModelObjectAt(index));
            }
            redrawLists();
        }
    };
    
    private ListSelectionListener classAnalysisModelListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent lse) {
            
            if(lse.getValueIsAdjusting()) {
                return;
            }
            
            int index = classList.getSelectedIndex();
            if(index == -1) {
                return;
            }
            
            if(colorer.getMode() == AnalysisColorer.CLASS_ANALYSIS) {
                // This is a workaround for the selection bug
                int entityIndex = entityList.getSelectedIndex();
                int regularityIndex = regularityList.getSelectedIndex();
                entityList.clearSelection();
                regularityList.clearSelection();
                
                colorer.setIdealClass((Entity)classViewModel.getModelObjectAt(index));
                redrawLists();
                
                if(entityIndex >= 0) { 
                    entityList.setSelectedIndex(entityIndex);
                }
                if(regularityIndex >= 0) { 
                    regularityList.setSelectedIndex(regularityIndex);
                }
            }
        }
    };
    
    private ListSelectionListener classAnalysisModelSetEntityListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent lse) {
            
            if(lse.getValueIsAdjusting()) {
                return;
            }
            
            int index = entityList.getSelectedIndex();
            if(index == -1) {
                colorer.setEntity(null);
                return;
            }
            
            if(colorer.getMode() == AnalysisColorer.CLASS_ANALYSIS) {
                // This is a workaround for the selection bug
                
                //int regularityIndex = regularityList.getSelectedIndex();
                //entityList.clearSelection();
                //regularityList.clearSelection();
                
                colorer.setEntity((Entity)entityViewModel.getModelObjectAt(index));
                redrawLists();
            }
        }
    };
    
    /*
    private Action startStopAction = new AbstractAction("StartStop") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            app.startStop();
        }
    };
     * */
    
    private Action buildReguilaritiesAction = new AbstractAction("Build Regularities") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            final ActionEvent fae = ae;
            
            GlobalProperties newProps = new GlobalProperties();
            newProps.set(ProjectManager.getInstance().getGlobalProperties());
            
            SettingsDialog sd = new SettingsDialog(MainFrame.this, newProps);
            sd.setVisible(true);
            
            if(sd.getDialogResult()) {
                ProjectManager.getInstance().getGlobalProperties().set(newProps);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (ProjectManager.getInstance().getCollectionOfEntities().isEmpty()) {
                            JOptionPane.showMessageDialog(MainFrame.this, "No entities");
                            return;
                        }
                        AbstractButton b = ((AbstractButton)fae.getSource());       
                        b.setAction(cancelProcessAction);
    //                    buildReguilaritiesAction.setEnabled(false);
                        buildIdealClassesAction.setEnabled(false);
                        app.buildRegularities();
                        buildReguilaritiesAction.setEnabled(true);
                        buildIdealClassesAction.setEnabled(true);
                        b.setAction(buildReguilaritiesAction);
                    }
                }).start();
            };
        }
    };
    
    private Action buildIdealClassesAction = new AbstractAction("Build classes") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            final ActionEvent fae = ae;
            
            new Thread(new Runnable() {

                @Override
                public void run() {
                    if (ProjectManager.getInstance().getCollectionOfRegularities().isEmpty()) {
                        JOptionPane.showMessageDialog(MainFrame.this, "No regularities");
                        return;
                    }
                    AbstractButton b = ((AbstractButton)fae.getSource());       
                    b.setAction(cancelProcessAction);
//                    buildIdealClassesAction.setEnabled(false);
                    buildReguilaritiesAction.setEnabled(false);
                    app.buildIdealClasses();
                    buildIdealClassesAction.setEnabled(true);
                    buildReguilaritiesAction.setEnabled(true);
                    b.setAction(buildIdealClassesAction);
                }
            }).start();
        }
        
    };
    
    private Action cancelProcessAction = new AbstractAction("Stop") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            final ActionEvent fae = ae;
            
            int m = JOptionPane.showConfirmDialog(MainFrame.this,
                    "Do you want to stop current calculation process?",
                    "Are you sure?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(m == JOptionPane.YES_OPTION) {
                app.stopAlgoritms();
            }
        }
    };
    
    private Action showStatisticsAction = new AbstractAction("Statistics...") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            final ActionEvent fae = ae;
            
            StatisticsDialog dialog = new StatisticsDialog(MainFrame.this);
            dialog.setVisible(true);
        }
    };
    
    private DefaultButtonModel overviewButtonModel = new AnalysisModeButtonModel(colorer, AnalysisColorer.OVERVIEW);
    private DefaultButtonModel entitiesButtonModel = new AnalysisModeButtonModel(colorer, AnalysisColorer.ENTITY_ANALYSIS);
    private DefaultButtonModel regularitiesButtonModel = new AnalysisModeButtonModel(colorer, AnalysisColorer.REGULARITY_ANALYSIS);
    private DefaultButtonModel classesButtonModel = new AnalysisModeButtonModel(colorer, AnalysisColorer.CLASS_ANALYSIS);

    private void initFileChoosers() {
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("NatClass 2.0 Project Files (*.ncp)", "ncp");
        
        importFileDialog = new FileDialog(this);
        importFileDialog.setDirectory(new java.io.File(".").getAbsolutePath());
        importFileDialog.setModal(true);
        //fileChooser = new JFileChooser();
        //fileChooser.setCurrentDirectory(new java.io.File("."));
        //fileChooser.setFileFilter(filter);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newProjMenuItem = new JMenuItem();
        newProjMenuItem.setAction(newProjectAction);
        fileMenu.add(newProjMenuItem);
        JMenuItem openProjMenuItem = new JMenuItem();
        openProjMenuItem.setAction(openProjectAction);
        fileMenu.add(openProjMenuItem);
        JMenuItem saveProjMenuItem = new JMenuItem("Save");
        fileMenu.add(saveProjMenuItem);
        JMenuItem saveAsProjMenuItem = new JMenuItem();
        saveAsProjMenuItem.setAction(saveAsProjectAction);
        fileMenu.add(saveAsProjMenuItem);
        fileMenu.addSeparator();
        JMenuItem importMenuItem = new JMenuItem();
        importMenuItem.setAction(importAction);
        fileMenu.add(importMenuItem);
        JMenuItem exportMenuItem = new JMenuItem();
        exportMenuItem.setAction(exportAction);
        fileMenu.add(exportMenuItem);

        fileMenu.addSeparator();
        JMenuItem statisticsMenuItem = new JMenuItem();
        statisticsMenuItem.setAction(showStatisticsAction);
        fileMenu.add(showStatisticsAction);
        
        if (!NatClassApp.isMac()) {
            fileMenu.addSeparator();
            JMenuItem exitMenuItem = new JMenuItem();
            exitMenuItem.setAction(exitAction);
            fileMenu.add(exitMenuItem);
        }
        
        menuBar.add(fileMenu);

        ButtonGroup modeGroup = new ButtonGroup();
        JMenu modeMenu = new JMenu("View");
        JMenuItem menuItem;
        
        menuItem = new JRadioButtonMenuItem();
        menuItem.setAction(setOverviewModelAction);
        menuItem.setModel(overviewButtonModel);
        modeMenu.add(menuItem);
        modeGroup.add(menuItem);
        
        menuItem = new JRadioButtonMenuItem();
        menuItem.setAction(setEntityAnalysisModelAction);
        menuItem.setModel(entitiesButtonModel);
        modeMenu.add(menuItem);
        modeGroup.add(menuItem);
        
        menuItem = new JRadioButtonMenuItem();
        menuItem.setAction(setRegularityAnalysisModelAction);
        menuItem.setModel(regularitiesButtonModel);
        modeMenu.add(menuItem);
        modeGroup.add(menuItem);
        
        menuItem = new JRadioButtonMenuItem();
        menuItem.setAction(setClassesAnalysisModelAction);
        menuItem.setModel(classesButtonModel);
        modeMenu.add(menuItem);
        modeGroup.add(menuItem);
        
        menuBar.add(modeMenu);
        setJMenuBar(menuBar);
    }

    private void initToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        JButton button;
        button = new JButton();
        button.setAction(newProjectAction);
        button.setIcon(Utililities.getIconFromResource("new"));
        button.setText(null);
        toolBar.add(button);

        button = new JButton();
        button.setAction(openProjectAction);
        button.setIcon(Utililities.getIconFromResource("open"));
        button.setText(null);
        toolBar.add(button);

        button = new JButton();
        button.setAction(saveProjectAction);
        button.setIcon(Utililities.getIconFromResource("save"));
        button.setText(null);
        toolBar.add(button);

        toolBar.addSeparator();
        button = new JButton();
        button.setAction(buildReguilaritiesAction);
        toolBar.add(button);
        
        button = new JButton();
        button.setAction(buildIdealClassesAction);
        toolBar.add(button);
        
        toolBar.addSeparator();
        
        ButtonGroup bg = new ButtonGroup();
        JButton radioButton;
        
        radioButton = new JButton();
        radioButton.setAction(setOverviewModelAction);
        radioButton.setModel(overviewButtonModel);
        bg.add(radioButton);
        toolBar.add(radioButton);
        
        radioButton = new JButton();
        radioButton.setAction(setEntityAnalysisModelAction);
        radioButton.setModel(entitiesButtonModel);
        bg.add(radioButton);
        toolBar.add(radioButton);
        
        radioButton = new JButton();
        radioButton.setAction(setRegularityAnalysisModelAction);
        radioButton.setModel(regularitiesButtonModel);
        bg.add(radioButton);
        toolBar.add(radioButton);
        
        radioButton = new JButton();
        radioButton.setAction(setClassesAnalysisModelAction);
        radioButton.setModel(classesButtonModel);
        bg.add(radioButton);
        toolBar.add(radioButton);
        
        //toolBar.addSeparator();
        //button = new JButton();
        //button.setAction(startStopAction);
        //toolBar.add(button);
        
        toolBar.addSeparator();
        button = new JButton();
        button.setAction(showStatisticsAction);
        toolBar.add(button);
        
        add(toolBar, BorderLayout.PAGE_START);
    }

    private void initPanels() {
        entityViewModel = new EntityViewModel(colorer, ProjectManager.getInstance().getCollectionOfEntities());
        entityDetailsViewModel = new EntityDetailsViewModel(colorer, entityViewModel);
        ProjectManager.getInstance().getCollectionOfEntities().addCollectionChangedListener(entityViewModel);

        regularityViewModel = new RegularityViewModel(colorer, ProjectManager.getInstance().getCollectionOfRegularities());
        regularityDetailsViewModel = new RegularityDetailsViewModel(colorer, regularityViewModel);
        ProjectManager.getInstance().getCollectionOfRegularities().addCollectionChangedListener(regularityViewModel);

        classViewModel = new ClassesViewModel(colorer, ProjectManager.getInstance().getCollectionOfIdealClasses());
        classDetailsViewModel = new ClassesDetailsViewModel(colorer, classViewModel);
        ProjectManager.getInstance().getCollectionOfIdealClasses().addCollectionChangedListener(classViewModel);

        CellRenderer cr = new CellRenderer();

        // Entity list and details
        entityList = new JList();
        entityList.setCellRenderer(cr);
        entityList.setModel(entityViewModel);
        entityDetailsList = new JList();
        entityDetailsList.setCellRenderer(cr);
        entityDetailsList.setModel(entityDetailsViewModel);
        entityList.addListSelectionListener(entityDetailsViewModel);
        entityList.addListSelectionListener(classAnalysisModelSetEntityListener);
        entityList.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(me.getClickCount() == 2 || !colorer.isEntityAnalysisReady()) {
                    setEntityAnalysisModelSetEntityAction.actionPerformed(null);
                }
                super.mouseClicked(me);
            }
            
        });
        entityList.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    setEntityAnalysisModelSetEntityAction.actionPerformed(null);
                }
                super.keyPressed(ke);
            }
            
        });

        JPanel entityPanel = new JPanel();
        entityPanel.setLayout(new GridLayout());
        entityPanel.add(new JScrollPane(entityList));
        entityPanel.add(new JScrollPane(entityDetailsList));

        // Regularity list and details
        regularityList = new JList();
        regularityList.setCellRenderer(cr);
        regularityList.setModel(regularityViewModel);
        regularityDetailsList = new JList();
        regularityDetailsList.setCellRenderer(cr);
        regularityDetailsList.setModel(regularityDetailsViewModel);
        regularityList.addListSelectionListener(regularityDetailsViewModel);
        regularityList.addListSelectionListener(regularityAnalysisModelListener);

        JPanel regularityPanel = new JPanel();
        regularityPanel.setLayout(new GridLayout());
        regularityPanel.add(new JScrollPane(regularityList));
        regularityPanel.add(new JScrollPane(regularityDetailsList));

        // Classes list and details
        classList = new JList();
        classList.setCellRenderer(cr);
        classList.setModel(classViewModel);
        classDetailsList = new JList();
        classDetailsList.setCellRenderer(cr);
        classDetailsList.setModel(classDetailsViewModel);
        classList.addListSelectionListener(classDetailsViewModel);
        classList.addListSelectionListener(classAnalysisModelListener);
        classList.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(me.getClickCount() == 2 || !colorer.isClassAnalysisReady()) {
                    setEntityAnalysisModelSetClassAction.actionPerformed(null);
                }
                super.mouseClicked(me);
            }
            
        });
        classList.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    setEntityAnalysisModelSetClassAction.actionPerformed(null);
                }
                super.keyPressed(ke);
            }
            
        });

        JPanel classPanel = new JPanel();
        classPanel.setLayout(new GridLayout());
        classPanel.add(new JScrollPane(classList));
        classPanel.add(new JScrollPane(classDetailsList));

        JSplitPane innerSplitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, regularityPanel, classPanel);
        innerSplitPane.setBorder(null);
        innerSplitPane.setDividerLocation(0.5);
        innerSplitPane.setResizeWeight(0.5);
        JSplitPane outerSplitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, entityPanel, innerSplitPane);
        outerSplitPane.setBorder(null);
        outerSplitPane.setDividerLocation(0.3);
        outerSplitPane.setResizeWeight(0.3);

        add(outerSplitPane, BorderLayout.CENTER);
    }
    
    private void redrawLists() {
        entityViewModel.redrawList();
        entityDetailsViewModel.redrawList();
        regularityViewModel.redrawList();
        regularityDetailsViewModel.redrawList();
        classViewModel.redrawList();
        classDetailsViewModel.redrawList();
    }
    
    private void initStatusBar() {
        statusBar = new JStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    @Override
    public void dispose() {
        entityViewModel.dispose();
        entityDetailsViewModel.dispose();

        regularityViewModel.dispose();
        regularityDetailsViewModel.dispose();

        classViewModel.dispose();
        classDetailsViewModel.dispose();

        ProjectManager.getInstance().getCollectionOfEntities().removeCollectionChangedListener(entityViewModel);
        ProjectManager.getInstance().getCollectionOfRegularities().removeCollectionChangedListener(regularityViewModel);
        ProjectManager.getInstance().getCollectionOfIdealClasses().removeCollectionChangedListener(classViewModel);
        super.dispose();
    }

    public MainFrame(NatClassApp app) throws HeadlessException {
        super();
        
        setLayout(new BorderLayout());

        this.app = app;
        initFileChoosers();
        initMenuBar();
        initToolBar();
        initPanels();
        initStatusBar();

        pack();
        setSize(800, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        
        reset();
    }
    
    public final void reset() {
        setFilename("Untitled");
        colorer.reset();
    }
    
    private void setFilename(String filename) {
        setTitle(MainFrame.staticTitle + " - " + filename);
    }
}
