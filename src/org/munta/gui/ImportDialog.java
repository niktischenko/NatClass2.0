package org.munta.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class ImportDialog extends JDialog {
    
    public Boolean getDialogResult() {
        return true;
    }
    
    public ImportDialog(JFrame parent) {
        super(parent, "Import data", ModalityType.APPLICATION_MODAL);
        
        setLocationRelativeTo(parent);
    }
}
