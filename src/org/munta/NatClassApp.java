package org.munta;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.model.Regularity;
import org.munta.projectengine.JABXSerializer;
import org.munta.projectengine.ProjectManager;

public class NatClassApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final JFrame frame = new JFrame("Show Message Dialog");
        JButton button = new JButton("Click Me");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(frame, "The NatClass Next Generation 2.0 GT-Ex Turbo Limited Edition");
            }
        });
        frame.add(button);
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Entity e = new Entity("Object1");
        e.getAttributes().add(new Attribute("Color", "Green"));
        
        Regularity r = new Regularity();
        r.setTarget(new Attribute("Height", "10"));
        r.getConditions().add(new Attribute("Weight", "5"));
        r.getConditions().add(new Attribute("Weight2", "5"));
        r.getContext().add(new Attribute());
        
        System.out.println(e);
        System.out.println(r);
        
        ProjectManager manager = new ProjectManager();
        manager.getCollectionOfEntities().add(e);
        manager.getCollectionOfRegularities().put("Hello", r);
        manager.saveAsProject("/tmp/test5.zip");
        
        org.munta.projectengine.IProjectSerializer s;
        try {
            s = new JABXSerializer(manager.getCollectionOfEntities().getClass());
            java.io.FileOutputStream fos = new FileOutputStream("/tmp/fos.xml");
            s.serializeProjectObject(manager.getCollectionOfEntities(), fos);
        } catch (Exception ex) {
            Logger.getLogger(NatClassApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
