package org.munta.gui;

import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;

public class AnalysisModeButtonModel extends DefaultButtonModel {
    
    private Boolean oldState = false;
    private AnalysisColorer colorer;
    private int mode;
    
    public AnalysisModeButtonModel(AnalysisColorer colorer, int mode) {
        this.colorer = colorer;
        this.mode = mode;
    }

    @Override
    public boolean isSelected() {
        Boolean newState = colorer.getMode() == mode;
        if(oldState != newState) {
            fireStateChanged();
            oldState = newState;
            
            ButtonGroup bg = getGroup();
            if(bg != null) {
                bg.setSelected(this, newState);
            }
        }
        return newState;
    }
}
