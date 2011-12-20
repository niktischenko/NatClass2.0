package org.munta.algorithm;

import java.util.HashMap;
import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;

public class RegularityBuilder {
    
    private EntityCollection storedEntities;
    private AttributeCollection allAttributes;
    
    public RegularityBuilder() {
        storedEntities = new EntityCollection();
        allAttributes = new AttributeCollection();
    }
    
    private void prepareBuilder(EntityCollection entities) {
        if(storedEntities.equals(entities)) {
            return;
        }
        
        storedEntities.clear();
        storedEntities.addAll(entities);
        allAttributes.clear();
        for(Entity e : entities) {
            allAttributes.addAll(e.getAttributes());
        }
    }
    
    private void fillRegularitiesImpl(Attribute target, HashMap<String, Attribute> set, RegularityCollection regularities) {
        for(Attribute attr : allAttributes) {
            
            if(target.getName().equals(attr.getName()))
                continue;
            
            if(!set.containsKey(attr.getName())) {
                set.put(attr.getName(), attr);
            }
            
            if(false /*Check criteria*/) {
                fillRegularitiesImpl(target, set, regularities);
            } else {
                Regularity r = new Regularity();
                r.setTarget(target);
                r.getConditions().addAll(set.values());
                regularities.add(r);
            }
        }
    }
    
    public void fillRegularities(EntityCollection entities, RegularityCollection regularities) {
        prepareBuilder(entities);
        
        for(Attribute attr : allAttributes) {
            fillRegularitiesImpl(attr, new HashMap<String, Attribute>(), regularities);
        }
    }
}
