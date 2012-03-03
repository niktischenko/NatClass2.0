package org.munta.gui;

import java.awt.Dimension;
import javax.swing.JLabel;

public final class JStatusBar extends JLabel {
    
    public JStatusBar() {
        setPreferredSize(new Dimension(100, 16));
        setMessage("Ready");
    }
        
    public void setMessage(String message) {
        setText(" " + message);
    }
}
