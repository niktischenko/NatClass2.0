package org.munta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import org.munta.gui.MainFrame;
import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.GlobalProperties;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;
import org.munta.projectengine.ProjectManager;
import org.munta.projectengine.serializer.IProjectSerializer;
import org.munta.projectengine.serializer.ProjectSerializerFactory;
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
            IProjectSerializer s = ProjectSerializerFactory.createSerializer(null, ProjectSerializerFactory.TYPE_XML);
            s.getMapper().registerClass(Attribute.class);
            s.getMapper().registerClass(AttributeCollection.class);
            s.getMapper().registerClass(Entity.class);
            s.getMapper().registerClass(EntityCollection.class);
            s.getMapper().registerClass(Regularity.class);
            s.getMapper().registerClass(RegularityCollection.class);
            s.getMapper().registerClass(GlobalProperties.class);

            AttributeCollection attributes = new AttributeCollection();
            EntityCollection entities = new EntityCollection();
            RegularityCollection regularities = new RegularityCollection();
            GlobalProperties properties = new GlobalProperties();

            attributes.add(new Attribute("attribute1", "1"));
            attributes.add(new Attribute("attribute2", "2"));

            Entity entity = new Entity("entity1");
            entity.getAttributes().addAll(attributes);
            entities.add(entity);
            entity = new Entity("entity2");
            entity.getAttributes().addAll(attributes);

            Regularity regularity = new Regularity();
            regularity.setTarget(attributes.iterator().next());
            regularity.getConditions().addAll(attributes);
            regularity.getContext().addAll(attributes);
            regularities.add(regularity);

            ByteArrayOutputStream out = null;
            ByteArrayInputStream in = null;
            Object object = null;

            System.err.println("re-serialized AttributesCollection");
            out = new ByteArrayOutputStream();
            s.serializeProjectObject(attributes, out);
            in = new ByteArrayInputStream(out.toByteArray());
            object = s.deserializeProjectObject(in);
            s.serializeProjectObject(object, System.err);
            System.err.println("-----------------\n");

            System.err.println("re-serialized EntityCollection");
            out = new ByteArrayOutputStream();
            s.serializeProjectObject(entities, out);
            in = new ByteArrayInputStream(out.toByteArray());
            object = s.deserializeProjectObject(in);
            s.serializeProjectObject(object, System.err);
            System.err.println("-----------------\n");

            System.err.println("re-serialized RegularityCollection");
            out = new ByteArrayOutputStream();
            s.serializeProjectObject(regularities, out);
            in = new ByteArrayInputStream(out.toByteArray());
            object = s.deserializeProjectObject(in);
            s.serializeProjectObject(object, System.err);
            System.err.println("-----------------\n");

            System.err.println("re-serialized GlobalProperties");
            out = new ByteArrayOutputStream();
            s.serializeProjectObject(properties, out);
            in = new ByteArrayInputStream(out.toByteArray());
            object = s.deserializeProjectObject(in);
            s.serializeProjectObject(object, System.err);
            System.err.println("-----------------\n");

            ProjectManager pm = ProjectManager.getInstance();
            pm.getCollectionOfEntities().addAll(entities);
            pm.getCollectionOfIdealClasses().addAll(entities);
            pm.getCollectionOfRegularities().putAll(regularities);
            pm.saveAsProject("/tmp/tmp/test.zip");

            new NatClassApp().run(args);
        } catch (Exception ex) {
            Logger.getLogger(NatClassApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
