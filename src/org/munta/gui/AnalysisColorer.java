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
    private Object obj;

    public Color getHighlightedColor() {
        return new Color(0, 180, 0);
    }
    
    public Color getPositiveColor() {
        return new Color(0, 180, 0);
    }
    
    public Color getNegativeColor() {
        return new Color(180, 0, 0);
    }
    
    public Entity getEntity() {
        if(obj instanceof Entity)
            return (Entity)obj;
        return null;
    }

    public Regularity getRegularity() {
        if(obj instanceof Regularity)
            return (Regularity)obj;
        return null;
    }
    
    public int getMode() {
        return mode;
    }
    
    public void setOverviewMode() {
        mode = OVERVIEW;
    }
    
    public void setEntityAnalysisMode(Entity e) {
        mode = ENTITY_ANALYSIS;
        obj = e;
    }
    
    public void setRegularityAnalysisMode(Regularity r) {
        mode = REGULARITY_ANALYSIS;
        obj = r;
    }
    
    public void setClassAnalysisMode(Entity c) {
        mode = CLASS_ANALYSIS;
        obj = c;
    }
    
    public AnalysisColorer() {
        setOverviewMode();
    }
}
