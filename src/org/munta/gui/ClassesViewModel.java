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
        return true;
    }

    @Override
    public Object getElementAt(int i) {
        Entity e = getModelObjectAt(i);
        return new ListItem(e.getName());
    }
}
