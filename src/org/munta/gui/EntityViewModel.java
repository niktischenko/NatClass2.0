package org.munta.gui;

import java.awt.Color;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;

public class EntityViewModel extends AbstractCollectionViewModel<Entity> {

    private AnalysisColorer colorer;
    
    public EntityViewModel(AnalysisColorer colorer, EntityCollection collection) {
        super(collection);
        this.colorer = colorer;
    }

    @Override
    public Object getElementAt(int i) {
        
        Entity e = getModelObjectAt(i);
        if(colorer.getMode() == AnalysisColorer.ENTITY_ANALYSIS) {
            if(e.getAttributes().equals(colorer.getEntity().getAttributes())) {
                return new ListItem(colorer.getHighlightedColor(), getModelObjectAt(i).getName());
            }
        } else if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS) {
            Regularity r = colorer.getRegularity();
            for(Attribute attr : r.getConditions()) {
                if(!e.getAttributes().contains(attr)) return null;
            }
            Color color = null;
            for(Attribute attr : e.getAttributes()) {
                if(attr.getName().equals(r.getTarget().getName()))
                {
                   if(attr.getValue().equals(r.getTarget().getValue())) {
                       return new ListItem(colorer.getPositiveColor(), e.getName());
                   } else {
                       return new ListItem(colorer.getNegativeColor(), e.getName());
                   }
                }
            }
        }
        
        return new ListItem(e.getName());
    }
}
