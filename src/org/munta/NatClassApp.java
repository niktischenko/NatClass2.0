package org.munta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

public final class NatClassApp {

    private MainFrame frame = null;

    private NatClassApp() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        frame = new MainFrame(this);
    }

    public void exitApplication() {
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }

    private void run(String[] args) {
        frame.setVisible(true);
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
