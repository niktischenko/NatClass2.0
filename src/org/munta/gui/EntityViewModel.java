package org.munta.gui;

import java.awt.Color;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;

public class EntityViewModel
    extends AbstractCollectionViewModel<Entity> {

    private AnalysisColorer colorer;
    
    public EntityViewModel(AnalysisColorer colorer, EntityCollection collection) {
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
        } else if(colorer.getMode() == AnalysisColorer.CLASS_ANALYSIS && colorer.isClassAnalysisReady()) {
            Entity ideal = colorer.getIdealClass();
            
            for(Attribute attr : obj.getAttributes()) {
                if(!ideal.checkAttribute(attr)) {
                    return false;
                }
            }
            return true;
        }
        
        return true;
    }

    @Override
    public Object getElementAt(int i) {
        Entity e = getModelObjectAt(i);
        if(e == null)
            return null;
        
        if(colorer.getMode() == AnalysisColorer.ENTITY_ANALYSIS && colorer.isEntityAnalysisReady()) {
            if(e.equals(colorer.getEntity())) {
                return new ListItem(colorer.getTargetColor(), getModelObjectAt(i).getName(), true);
            }
            if(e.getAttributes().equals(colorer.getEntity().getAttributes())) {
                return new ListItem(colorer.getPositiveColor(), getModelObjectAt(i).getName());
            }
        } else if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS && colorer.isRegularityAnalysisReady()) {
            Regularity r = colorer.getRegularity();
            
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
        } else if(colorer.getMode() == AnalysisColorer.CLASS_ANALYSIS && colorer.isClassAnalysisReady()) {
            Entity ideal = colorer.getIdealClass();
            
            Boolean useColor = true; 
            for(Attribute attr : e.getAttributes()) {
                if(!e.checkAttribute(attr)) {
                    useColor = false;
                }
            }
            if(useColor) {
                return new ListItem(colorer.getPositiveColor(), e.getName());
            }
        }
        
        return new ListItem(e.getName());
    }
}
