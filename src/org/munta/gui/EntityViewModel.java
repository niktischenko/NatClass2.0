package org.munta.gui;

import java.awt.Color;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;
import org.munta.model.SerializableString;

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
            
            for(SerializableString ss : ideal.getChildEntities()) {
                if(ss.getName().equals(obj.getName())) {
                    return true;
                }
            }
            return false;
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
                return new ListItem(colorer.getPositiveColor(), getModelObjectAt(i).getName(), true);
            }
        } else if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS && colorer.isRegularityAnalysisReady()) {
            Regularity r = colorer.getRegularity();
            
            for(Attribute attr : e.getAttributes()) {
                if(attr.getName().equals(r.getTarget().getName()))
                {
                   if(attr.getValue().equals(r.getTarget().getValue())) {
                       return new ListItem(colorer.getPositiveColor(), e.getName(), true);
                   } else {
                       return new ListItem(colorer.getNegativeColor(), e.getName(), true);
                   }
                }
            }
        } else if(colorer.getMode() == AnalysisColorer.CLASS_ANALYSIS && colorer.isClassAnalysisReady()) {
            return new ListItem(colorer.getPositiveColor(), e.getName(), true);
        }
        
        return new ListItem(e.getName());
    }
}
