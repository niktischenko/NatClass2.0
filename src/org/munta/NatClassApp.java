package org.munta;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import org.munta.gui.MainFrame;
import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
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
            AttributeCollection aa = new AttributeCollection();
            aa.add(new Attribute("in_collection1", "true"));
            aa.add(new Attribute("in_collection2", "true"));
            s.serializeProjectObject(aa, System.err);
            Entity e = new Entity("entity");
            e.getAttributes().add(new Attribute("entityAttribute", "entityValue"));
            Regularity r = new Regularity();
            r.setTarget(new Attribute("regularityTarget1", "value1"));
            r.getConditions().add(new Attribute("conditionAttr11", "value11"));
            r.getConditions().add(new Attribute("conditionAttr12", "value12"));
            r.getContext().add(new Attribute("contextAttr11", "value11"));
            r.getContext().add(new Attribute("contextAttr12", "value12"));
            RegularityCollection rr = new RegularityCollection();
            rr.add(r);
            r = new Regularity();
            r.setTarget(new Attribute("regularityTarget2", "value2"));
            r.getConditions().add(new Attribute("conditionAttr21", "value21"));
            r.getConditions().add(new Attribute("conditionAttr22", "value22"));
            r.getContext().add(new Attribute("contextAttr21", "value21"));
            r.getContext().add(new Attribute("contextAttr22", "value22"));
            rr.add(r);
            s.serializeProjectObject(rr, System.err);
            s.serializeProjectObject(r, System.err);
            s.serializeProjectObject(e, System.err);
            
            ProjectManager pm = ProjectManager.getInstance();
            pm.getCollectionOfEntities().add(e);
            pm.getCollectionOfIdealClasses().add(e);
            pm.getCollectionOfRegularities().add(r);
            pm.saveAsProject("/tmp/tmp/test.zip");
            
            new NatClassApp().run(args);
        } catch (Exception ex) {
            Logger.getLogger(NatClassApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
