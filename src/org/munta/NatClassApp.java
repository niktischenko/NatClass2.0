package org.munta;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.munta.gui.MainFrame;

public final class NatClassApp {
    
    private MainFrame frame = null;
    
    private NatClassApp() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        frame = new MainFrame(this);
    }
    
    public void exitApplication() {
        if(frame != null) {
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
            new NatClassApp().run(args);
        } catch (Exception ex) {
            Logger.getLogger(NatClassApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
