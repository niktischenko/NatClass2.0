package org.munta.gui;

import java.awt.Color;
import org.munta.model.Entity;
import org.munta.model.Regularity;

public final class AnalysisColorer {
    public static final int OVERVIEW = 1;
    public static final int ENTITY_ANALYSIS = 2;
    public static final int REGULARITY_ANALYSIS = 3;
    public static final int CLASS_ANALYSIS = 4;
    
    private int mode;
    private Entity entity = null;
    private Entity ideal = null;
    private Regularity regularity = null;

    public Color getTargetColor() {
        return Color.ORANGE;
    }
    
    public Color getConditionColor() {
        return new Color(0, 191, 255);
    }
    
    public Color getContextColor() {
        return Color.LIGHT_GRAY;
    }
    
    public Color getHighlightedColor() {
        return new Color(0, 0, 180);
    }
    
    public Color getPositiveColor() {
        return new Color(0, 150, 0);
    }
    
    public Color getNegativeColor() {
        return new Color(180, 0, 0);
    }
    
    private void resetSelected() {
        entity = null;
        ideal = null;
        regularity = null;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity value) {
        entity = value;
    }
    
    public Entity getIdealClass() {
        return ideal;
    }
    
    public void setIdealClass(Entity value) {
        ideal = value;
    }

    public Regularity getRegularity() {
        return regularity;
    }

    public void setRegularity(Regularity regularity) {
        this.regularity = regularity;
    }
    
    public int getMode() {
        return mode;
    }
    
    public void setOverviewMode() {
        mode = OVERVIEW;
    }
    
    public boolean isEntityAnalysisReady() {
        return entity != null;
    }
    
    public boolean isClassAnalysisReady() {
        return ideal != null;
    }
    
    public boolean isRegularityAnalysisReady() {
        return regularity != null;
    }
    
    public void setEntityAnalysisMode() {
        resetSelected();
        mode = ENTITY_ANALYSIS;
    }
    
    public void setRegularityAnalysisMode() {
        resetSelected();
        mode = REGULARITY_ANALYSIS;
    }
    
    public void setClassAnalysisMode() {
        resetSelected();
        mode = CLASS_ANALYSIS;
    }
    
    public AnalysisColorer() {
        setOverviewMode();
    }
    
    public void reset() {
        setOverviewMode();
        setEntity(null);
        setRegularity(null);
        setIdealClass(null);
    }
}
