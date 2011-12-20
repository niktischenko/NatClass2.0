package org.munta.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.munta.NatClassApp;
import org.munta.projectengine.ProjectManager;

public class MainFrame extends JFrame {

    private NatClassApp app;
    private EntityViewModel entityViewModel = null;
    private EntityAttributesViewModel entityAttributeViewModel = null;
    
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
        entityAttributeViewModel = new EntityAttributesViewModel(entityViewModel);
        
        ProjectManager.getInstance().getCollectionOfEntities().addCollectionChangedListener(entityViewModel);
        
        JList entityList = new JList();
        entityList.setModel(entityViewModel);
        JList entityAttributeList = new JList();
        entityAttributeList.setModel(entityAttributeViewModel);
        
        entityList.addListSelectionListener(entityAttributeViewModel);
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        contentPane.add(entityList, BorderLayout.LINE_START);
        contentPane.add(entityAttributeList, BorderLayout.LINE_END);
    }

    @Override
    public void dispose() {
        entityViewModel.dispose();
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
