package org.munta.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.munta.NatClassApp;
import org.munta.projectengine.ProjectManager;

public class MainFrame extends JFrame {

    private NatClassApp app;
    private EntityViewModel entityViewModel = null;
    private EntityDetailsViewModel entityDetailsViewModel = null;
    private AbstractCollectionViewModel regularityViewModel = null;
    private EntityAttributesViewModel regularityDetailsViewModel = null;
    
    private ActionListener exitActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            app.exitApplication();
        }
    };
    
    private void InitMenuBar() {
        JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
                JMenuItem newProjMenuItem = new JMenuItem("New Project");
                fileMenu.add(newProjMenuItem);
                JMenuItem openProjMenuItem = new JMenuItem("Open project...");
                fileMenu.add(openProjMenuItem);
                JMenuItem saveProjMenuItem = new JMenuItem("Save");
                fileMenu.add(saveProjMenuItem);
                JMenuItem saveAsProjMenuItem = new JMenuItem("Save As...");
                fileMenu.add(saveAsProjMenuItem);
                fileMenu.addSeparator();
                JMenuItem importMenuItem = new JMenuItem("Import...");
                fileMenu.add(importMenuItem);
                JMenuItem exportMenuItem = new JMenuItem("Export...");
                fileMenu.add(exportMenuItem);
                fileMenu.addSeparator();
                JMenuItem exitMenuItem = new JMenuItem("Exit");
                exitMenuItem.addActionListener(exitActionListener);
            fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    private void initPanels() {
        entityViewModel = new EntityViewModel(ProjectManager.getInstance().getCollectionOfEntities());
        entityDetailsViewModel = new EntityDetailsViewModel(entityViewModel);
        
        regularityViewModel = new RegularityViewModel(ProjectManager.getInstance().getCollectionOfRegularities());
        //regularityDetailsViewModel = new EntityAttributesViewModel(regularityViewModel);
        
        ProjectManager.getInstance().getCollectionOfEntities().addCollectionChangedListener(entityViewModel);
        
        JList entityList = new JList();
        entityList.setModel(entityViewModel);
        JList entityDetailsList = new JList();
        entityDetailsList.setModel(entityDetailsViewModel);
        entityList.addListSelectionListener(entityDetailsViewModel);
        
        JPanel entitiesPanel = new JPanel();
        entitiesPanel.setLayout(new GridLayout(1, 2));
        entitiesPanel.add(new JScrollPane(entityList));
        entitiesPanel.add(new JScrollPane(entityDetailsList));
        
        JList regularitiesList = new JList();
        regularitiesList.setModel(regularityViewModel);
        JList regularitiesDetailsList = new JList();
        
        JPanel regularitiesPanel = new JPanel();
        regularitiesPanel.setLayout(new GridLayout(1, 2));
        regularitiesPanel.add(new JScrollPane(regularitiesList));
        regularitiesPanel.add(new JScrollPane(regularitiesDetailsList));
        
        JPanel classesPanel = new JPanel();
        
        JSplitPane innerSplitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, regularitiesPanel, classesPanel);
        
        JSplitPane outerSplitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, entitiesPanel, innerSplitPane);
        
        add(outerSplitPane);
    }

    @Override
    public void dispose() {
        entityViewModel.dispose();
        ProjectManager.getInstance().getCollectionOfEntities().removeCollectionChangedListener(entityViewModel);
        super.dispose();
    }
    
    public MainFrame(NatClassApp app) throws HeadlessException {
        super("NatClass 2.0");
        
        this.app = app;
        InitMenuBar();
        initPanels();

        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
