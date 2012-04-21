package org.munta.gui;

import java.awt.Color;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;

public class ClassesViewModel extends AbstractCollectionViewModel<Entity> {
    
    private AnalysisColorer colorer;
    
    public ClassesViewModel(AnalysisColorer colorer, EntityCollection collection) {
        super(collection);
        this.colorer = colorer;
    }

    @Override
    protected Boolean onFilter(Entity obj) {
        if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS && colorer.isRegularityAnalysisReady()) {
            Regularity r = colorer.getRegularity();
            
            for(Attribute attr : r.getConditions()) {
                if(!obj.getAttributes().contains(attr)) return false;
            }
            return true;
        }
        
        return true;
    }

    @Override
    public Object getElementAt(int i) {
        Entity e = getModelObjectAt(i);
        
        if(colorer.getMode() == AnalysisColorer.ENTITY_ANALYSIS && colorer.isClassAnalysisReady()) {
            if(e.equals(colorer.getIdealClass())) {
                return new ListItem(colorer.getTargetColor(), getModelObjectAt(i).getName(), true);
            }
        } else if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS && colorer.isRegularityAnalysisReady()) {
            Regularity r = colorer.getRegularity();
            
            for(Attribute attr : e.getAttributes()) {
                if(attr.getName().equals(r.getTarget().getName()))
                {
                   if(attr.getValue().equals(r.getTarget().getValue())) {
                       return new ListItem(colorer.getPositiveColor(), e.getName(), true);
                   }
                }
            }
            return new ListItem(colorer.getNegativeColor(), e.getName(), true);
        }
        
        return new ListItem(e.getName());
    }
}
