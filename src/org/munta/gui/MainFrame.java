package org.munta.gui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import org.munta.NatClassApp;
import org.munta.model.Entity;
import org.munta.model.Regularity;
import org.munta.projectengine.ProjectManager;

public class MainFrame extends JFrame {

    private NatClassApp app;
    //List models
    private EntityViewModel entityViewModel = null;
    private EntityDetailsViewModel entityDetailsViewModel = null;
    private RegularityViewModel regularityViewModel = null;
    private RegularityDetailsViewModel regularityDetailsViewModel = null;
    private EntityViewModel classViewModel = null;
    private EntityDetailsViewModel classDetailsViewModel = null;
    private AnalysisColorer colorer;
    // Lists
    private JList entityList;
    private JList entityDetailsList;
    private JList regularityList;
    private JList regularityDetailsList;
    private JList classList;
    private JList classDetailsList;
    // FileDialog
    private FileDialog fileDialog;
    // Actions
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

            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setVisible(true);

            if (fileDialog.getFile() == null || fileDialog.getFile().isEmpty()) {
                return;
            }

            app.openProject(new File(fileDialog.getDirectory(), fileDialog.getFile()).getAbsolutePath());
        }
    };
    private Action saveAsProjectAction = new AbstractAction("Save As...") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //int rVal = fileChooser.showSaveDialog(MainFrame.this);
            //if (rVal == JFileChooser.APPROVE_OPTION) {
            //    app.saveAsProject(fileChooser.getSelectedFile().getAbsolutePath());
            //}
            fileDialog.setMode(FileDialog.SAVE);
            fileDialog.setVisible(true);

            if (fileDialog.getFile() == null || fileDialog.getFile().isEmpty()) {
                return;
            }

            app.saveAsProject(new File(fileDialog.getDirectory(), fileDialog.getFile()).getAbsolutePath());
        }
    };
    private Action setOverviewModelAction = new AbstractAction("Overview") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            colorer.setOverviewMode();
        }
    };
    private Action setEntityAnalysisModelAction = new AbstractAction("Object Analysis") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            
            int index = entityList.getSelectedIndex();
            if(index == -1)
                return;
            
            colorer.setEntityAnalysisMode((Entity)entityViewModel.getModelObjectAt(index));
        }
    };
    
    private Action setRegularityAnalysisModelAction = new AbstractAction("Regularity Analysis") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            
            int index = regularityList.getSelectedIndex();
            if(index == -1)
                return;
            
            colorer.setRegularityAnalysisMode(
                    ((Entry<String, Regularity>)regularityViewModel.getModelObjectAt(index)).getValue());
        }
    };

    private Icon getIconFromResource(String iconName) {
        String iconPath = String.format("images/%s.png", iconName);
        return new ImageIcon(MainFrame.class.getResource(iconPath));
    }

    private void initFileChoosers() {
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("NatClass 2.0 Project Files (*.ncp)", "ncp");
        FilenameFilter ff = new FilenameFilter() {

            @Override
            public boolean accept(File file, String string) {
                return string.toLowerCase().endsWith(".ncp");
            }
        };

        fileDialog = new FileDialog(this);
        fileDialog.setDirectory(new java.io.File(".").getAbsolutePath());
        fileDialog.setFilenameFilter(ff);
        fileDialog.setModal(true);
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
        JMenuItem importMenuItem = new JMenuItem("Import...");
        fileMenu.add(importMenuItem);
        JMenuItem exportMenuItem = new JMenuItem("Export...");
        fileMenu.add(exportMenuItem);

        if (!NatClassApp.isMac()) {
            fileMenu.addSeparator();
            JMenuItem exitMenuItem = new JMenuItem();
            exitMenuItem.setAction(exitAction);
            fileMenu.add(exitMenuItem);
        }
        menuBar.add(fileMenu);

        JMenu modeMenu = new JMenu("View");
        JMenuItem overviewMenuItem = new JRadioButtonMenuItem();
        overviewMenuItem.setAction(setOverviewModelAction);
        modeMenu.add(overviewMenuItem);
        JMenuItem a1MenuItem = new JRadioButtonMenuItem("Object analysis");
        a1MenuItem.setAction(setEntityAnalysisModelAction);
        modeMenu.add(a1MenuItem);
        JMenuItem a2MenuItem = new JRadioButtonMenuItem("Regularity analysis");
        a2MenuItem.setAction(setRegularityAnalysisModelAction);
        modeMenu.add(a2MenuItem);
        JMenuItem a3MenuItem = new JRadioButtonMenuItem("Analysys 3");
        modeMenu.add(a3MenuItem);
        menuBar.add(modeMenu);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(overviewMenuItem);
        modeGroup.add(a1MenuItem);
        modeGroup.add(a2MenuItem);
        modeGroup.add(a3MenuItem);

        setJMenuBar(menuBar);
    }

    private void initToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        JButton button;
        button = new JButton();
        button.setAction(newProjectAction);
        button.setIcon(getIconFromResource("new"));
        button.setText(null);
        toolBar.add(button);

        button = new JButton();
        button.setAction(openProjectAction);
        button.setIcon(getIconFromResource("open"));
        button.setText(null);
        toolBar.add(button);

        button = new JButton();
        button.setAction(newProjectAction);
        button.setIcon(getIconFromResource("save"));
        button.setText(null);
        toolBar.add(button);

        toolBar.addSeparator();
        button = new JButton();
        button.setAction(newProjectAction);
        button.setIcon(getIconFromResource("save"));
        button.setText(null);
        toolBar.add(button);

        add(toolBar, BorderLayout.PAGE_START);
    }

    private void initPanels() {
        colorer = new AnalysisColorer();
        entityViewModel = new EntityViewModel(colorer, ProjectManager.getInstance().getCollectionOfEntities());
        entityDetailsViewModel = new EntityDetailsViewModel(colorer, entityViewModel);
        ProjectManager.getInstance().getCollectionOfEntities().addCollectionChangedListener(entityViewModel);

        regularityViewModel = new RegularityViewModel(colorer, ProjectManager.getInstance().getCollectionOfRegularities());
        regularityDetailsViewModel = new RegularityDetailsViewModel(colorer, regularityViewModel);
        ProjectManager.getInstance().getCollectionOfRegularities().addCollectionChangedListener(regularityViewModel);

        classViewModel = new EntityViewModel(colorer, ProjectManager.getInstance().getCollectionOfIdealClasses());
        classDetailsViewModel = new EntityDetailsViewModel(colorer, classViewModel);
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
        super("NatClass 2.0");

        setLayout(new BorderLayout());

        this.app = app;
        initFileChoosers();
        initMenuBar();
        initToolBar();
        initPanels();

        pack();
        setSize(800, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}
