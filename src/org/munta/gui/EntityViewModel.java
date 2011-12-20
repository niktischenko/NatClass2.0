package org.munta.gui;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.utils.CollectionChangedListener;

public class EntityViewModel
        extends AbstractListModel
        implements ListModel, CollectionChangedListener {

    private EntityCollection entities;
    private CollectionView<EntityCollection> entitiesView;
    
    public EntityViewModel(EntityCollection entities) {
        this.entities = entities;
        entitiesView = new CollectionView<EntityCollection>(entities);
    }
    
    public void dispose() {
        entities.removeCollectionChangedListener(this);
    }
    
    @Override
    public int getSize() {
        return entitiesView.size();
    }

    @Override
    public Object getElementAt(int i) {
        return entitiesView.getAt(i);
    }
    
    public Entity getEntityAt(int i) {
        return (Entity)entitiesView.getAt(i);
    }

    @Override
    public void elementAdded(Object o) {
        int size = getSize();
        entitiesView.updateView();
        fireIntervalAdded(this, 1, 1);
    }

    @Override
    public void elementRemoved(Object o) {
        int size = getSize();
        entitiesView.updateView();
        fireIntervalAdded(this, size-1, size-1);
    }
}
