package org.munta.gui;

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

public class MainFrame extends JFrame {

    private NatClassApp app;
    
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
        JList entityList = new JList();
        entityList.setModel(new EntityViewModel());
        add(entityList);
    }
    
    public MainFrame(NatClassApp app) throws HeadlessException {
        this.app = app;
        InitMenuBar();
        initPanels();
        
        setSize(400, 400);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
