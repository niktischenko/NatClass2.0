package org.munta.gui;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SpringLayout;
import org.munta.model.GlobalProperties;
import org.munta.utils.SpringUtilities;

public class SettingsDialog extends JDialog {
    
    private Boolean isOK = false;
    
    public Boolean getDialogResult() {
        return isOK;
    }
    
    private AbstractAction okAction = new AbstractAction("OK") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            isOK = true;
            SettingsDialog.this.setVisible(false);
        }
    };
    
    private AbstractAction cancelAction = new AbstractAction("Cancel") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            SettingsDialog.this.setVisible(false);
        }
    };
    
    private void init() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new SpringLayout());    
        
        JButton button;
        button = new JButton(okAction);
        button.requestFocus();
        add(button);
        button = new JButton(cancelAction);
        add(button);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(contentPane,
                                        1, 2,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 2);  //xPad, yPad
        
        setResizable(false);
        pack();
    }
    
    public SettingsDialog(Window parent, GlobalProperties gp) {
        super(parent, "Properties", ModalityType.APPLICATION_MODAL);
        init();
        setLocationRelativeTo(parent);
    }
}
