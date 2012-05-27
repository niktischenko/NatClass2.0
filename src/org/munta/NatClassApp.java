package org.munta;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import org.munta.algorithm.CancelEvent;
import org.munta.algorithm.IdealClassBuilder;
import org.munta.algorithm.RegularityBuilder;
import org.munta.gui.MainFrame;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;
import org.munta.projectengine.ProjectManager;

public final class NatClassApp {

    private MainFrame frame = null;
    private Thread t;
    private Boolean tb = false;
    private static Boolean isMac = null;
    
    private FileDialog fileDialog;
    FilenameFilter ff = new FilenameFilter() {
        @Override
        public boolean accept(File file, String string) {
            return string.toLowerCase().endsWith(".ncp");
        }
    };

    public static Boolean isMac() {
        if (isMac == null) {
            String os = System.getProperty("os.name");
            if (os != null && os.toLowerCase().contains("mac")) {
                isMac = true;
            } else {
                isMac = false;
            }
        }
        return isMac;
    }

    private NatClassApp() throws Exception {
        if (isMac()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "NatClass 2.0");
        }
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        fileDialog = new FileDialog(frame);
        fileDialog.setDirectory(new java.io.File(".").getAbsolutePath());
        fileDialog.setFilenameFilter(ff);
        fileDialog.setModal(true);
        
        frame = new MainFrame(this);
    }

    public void newProject() {
        stopAlgoritms();
        frame.reset();
        ProjectManager.getInstance().newProject();
    }

    public String openProject() {
        
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null || fileDialog.getFile().isEmpty()) {
            return null;
        }
        
        String filePath = new File(fileDialog.getDirectory(), fileDialog.getFile()).getAbsolutePath();
            
        newProject();
        if(ProjectManager.getInstance().loadProject(filePath)) {
            return new File(filePath).getName();
        }
        
        return null;
    }

    public String saveAsProject() {
        fileDialog.setMode(FileDialog.SAVE);
        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null || fileDialog.getFile().isEmpty()) {
            return null;
        }
        
        String filePath = new File(fileDialog.getDirectory(), fileDialog.getFile()).getAbsolutePath();
        if(ProjectManager.getInstance().saveAsProject(filePath)) {
            return new File(filePath).getName();
        }
        
        return null;
    }

    public String saveProject() {
        if(!ProjectManager.getInstance().isOnFileSystem()) {
            return saveAsProject();
        } else {
            ProjectManager.getInstance().saveProject();
            return null;
        }
    }

    public void exitApplication() {

        stopAlgoritms();
        t.interrupt();

        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }

    public void buildRegularities() {
        new RegularityBuilder().fillRegularities(
                ProjectManager.getInstance().getCollectionOfEntities(),
                ProjectManager.getInstance().getCollectionOfRegularities());
    }

    public void buildIdealClasses() {
        IdealClassBuilder builder = new IdealClassBuilder();
        RegularityCollection regularities = ProjectManager.getInstance().getCollectionOfRegularities();
        EntityCollection entities = ProjectManager.getInstance().getCollectionOfEntities();
        builder.fillRegularitiesProbabilitiy(entities, regularities);
        ProjectManager.getInstance().getCollectionOfIdealClasses().clear();
        for (Entity e : entities) {
            if (CancelEvent.getInstance().getStopPendingReset()) {
                break;
            }
            builder.buildClass(e, entities, regularities, ProjectManager.getInstance().getCollectionOfIdealClasses());
        }
        CancelEvent.getInstance().setFlag();
    }
    
    public void stopAlgoritms() {
        CancelEvent.getInstance().setStopPending();
        CancelEvent.getInstance().waitFlag();
    }

    private void run(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                frame.setVisible(true);
            }
        });

        t = new Thread(new Runnable() {

            @Override
            public void run() {
                int c = 20000;
                while (c > 0) {
                    try {
                        Set<Entity> s = ProjectManager.getInstance().getCollectionOfEntities();
                        synchronized (s) {
                            do {
                                Thread.sleep(10);
                            } while (!tb);

                            int count;
                            int mode = (int) ((0.5 + Math.random() * 6) + 1);

                            if ((mode & 1) == 0 && (mode & 2) == 0 && (mode & 4) == 0) {
                                break;
                            }

                            if ((mode & 1) != 0) {
                                Entity e1 = new Entity("Object: " + (int) (Math.random() * 10000));
                                count = 1 + (int) (Math.random() * 10);
                                for (int i = 0; i < count;) {
                                    Attribute a = new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10));
                                    if (!e1.getAttributes().containsByName(a)) {
                                        e1.getAttributes().add(a);
                                        i++;
                                    }
                                }
                                ProjectManager.getInstance().getCollectionOfEntities().add(e1);
                            }

                            if ((mode & 2) != 0) {
                                Entity e2 = new Entity("Object: " + (int) (Math.random() * 10000));
                                count = 1 + (int) (Math.random() * 100);
                                for (int i = 0; i < count; i++) {
                                    Attribute a = new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10));
                                    if(!e2.getAttributes().contains(a)) {
                                        e2.getAttributes().add(a);
                                        i++;
                                    }
                                }
                                ProjectManager.getInstance().getCollectionOfIdealClasses().add(e2);
                            }

                            if ((mode & 4) != 0) {
                                Regularity r = new Regularity();
                                r.setTarget(new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10)));

                                count = 1 + (int) (Math.random() * 9);
                                for (int i = 0; i < count;) {
                                    Attribute attr = new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10));

                                    if (!r.getConditions().containsByName(attr) && !r.getTarget().getName().equals(attr.getName())) {
                                        r.getConditions().add(attr);
                                        i++;
                                    }
                                }

                                count = 1 + (int) (Math.random() * 10);
                                for (int i = 0; i < count; i++) {
                                    r.getContext().add(new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10)));
                                }
                                ProjectManager.getInstance().getCollectionOfRegularities().add(r);
                                c--;
                            }
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
        });
    }

    public void startStop() {
        if (!t.isAlive()) {
            t.start();
        }
        tb = !tb;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new NatClassApp().run(args);
        } catch (Exception ex) {
            Logger.getLogger(NatClassApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
