package org.munta.gui;

import java.awt.Dimension;
import javax.swing.JLabel;

public final class JStatusBar extends JLabel {
    
    public JStatusBar() {
        setPreferredSize(new Dimension(100, 16));
        setReadyStatus();
    }
        
    public void setMessage(String message) {
        setText(" " + message);
    }
    
    public void setReadyStatus() {
        setMessage("Ready");
    }
}
