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

    private NatClassApp() throws Exception {

        Entity e = new Entity("Object1");
        e.getAttributes().add(new Attribute("Color", "Green"));
        e.getAttributes().add(new Attribute("Color", "Red"));
        e.getAttributes().add(new Attribute("Color", "Test"));
        e.getAttributes().add(new Attribute("Color", "Black"));

        Entity e2 = new Entity("Object2");
        e2.getAttributes().add(new Attribute("Weight", "15"));
        e2.getAttributes().add(new Attribute("Color", "Black"));

        Regularity r = new Regularity();
        r.setTarget(new Attribute("Height", "10"));
        r.getConditions().add(new Attribute("Weight", "5"));
        r.getConditions().add(new Attribute("Weight2", "5"));
        r.getContext().add(new Attribute());

        ProjectManager.getInstance().getCollectionOfEntities().add(e);
        ProjectManager.getInstance().getCollectionOfEntities().add(e2);
        ProjectManager.getInstance().getCollectionOfRegularities().add(r);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        frame = new MainFrame(this);
    }

    public void exitApplication() {

        t.interrupt();

        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }

    private void run(String[] args) {
        frame.setVisible(true);

        t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {

                        Set<Entity> s = ProjectManager.getInstance().getCollectionOfEntities();
                        synchronized (s) {
                            Thread.sleep(10);

                            Entity e2 = new Entity("Object: " + (int) (Math.random() * Integer.MAX_VALUE));
                            int count = (int) (Math.random() * 10);
                            for (int i = 0; i < count; i++) {
                                e2.getAttributes().add(new Attribute("Weight", "" + (int) (Math.random() * Integer.MAX_VALUE)));
                            }
                            ProjectManager.getInstance().getCollectionOfEntities().add(e2);
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
