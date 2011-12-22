package org.munta;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.munta.gui.MainFrame;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.model.Regularity;
import org.munta.projectengine.ProjectManager;

public final class NatClassApp {

    private MainFrame frame = null;
    private Thread t;
    
    private static Boolean isMac = null;
    public static Boolean isMac() {
        if(isMac == null) {
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

        frame = new MainFrame(this);
    }

    public void newProject() {
        ProjectManager.getInstance().newProject();
    }
    
    public Boolean openProject(String filePath) {
        return ProjectManager.getInstance().loadProject(filePath);
    }

    public Boolean saveAsProject(String filePath) {
        return ProjectManager.getInstance().saveAsProject(filePath);
    }

    public Boolean saveProject() {
        return ProjectManager.getInstance().saveProject();
    }

    public void exitApplication() {

        t.interrupt();

        if (frame != null) {
            frame.dispose();
            frame = null;
        }
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
                            Thread.sleep(1);
                            int count;
                            int mode = (int) ((0.5 + Math.random() * 6) + 1);

                            if ((mode & 1) == 0 && (mode & 2) == 0 && (mode & 4) == 0) {
                                break;
                            }

                            if ((mode & 1) != 0) {
                                Entity e1 = new Entity("Object: " + (int) (Math.random() * 10));
                                count = 1 + (int) (Math.random() * 10);
                                for (int i = 0; i < count; i++) {
                                    e1.getAttributes().add(new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10)));
                                }
                                ProjectManager.getInstance().getCollectionOfIdealClasses().add(e1);
                            }

                            if ((mode & 2) != 0) {
                                Entity e2 = new Entity("Object: " + (int) (Math.random() * 10));
                                count = 1 + (int) (Math.random() * 10);
                                for (int i = 0; i < count; i++) {
                                    e2.getAttributes().add(new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10)));
                                }
                                ProjectManager.getInstance().getCollectionOfEntities().add(e2);
                            }

                            if ((mode & 4) != 0) {
                                Regularity r = new Regularity();
                                r.setTarget(new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10)));

                                count = 1 + (int) (Math.random() * 10);
                                for (int i = 0; i < count; i++) {
                                    r.getConditions().add(new Attribute("a" + (int) (Math.random() * 10), "" + (int) (Math.random() * 10)));
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

        t.start();
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
