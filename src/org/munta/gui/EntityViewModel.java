package org.munta.gui;

import org.munta.model.Entity;
import org.munta.model.EntityCollection;

public class EntityViewModel extends AbstractCollectionViewModel<Entity> {

    public EntityViewModel(EntityCollection collection) {
        super(collection);
    }

    @Override
    public Object getElementAt(int i) {
        return getModelObjectAt(i);
    }
}
