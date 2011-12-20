package org.munta.gui;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.projectengine.ProjectManager;

public class EntityAttributesViewModel
        extends AbstractListModel
        implements ListSelectionListener {

    private EntityViewModel entityViewModel;
    
    private AttributeCollection attributes;
    private CollectionView<AttributeCollection> attributesView;
    
    public EntityAttributesViewModel(EntityViewModel entityViewModel) {
        this.entityViewModel = entityViewModel;
        attributes = new AttributeCollection();
        updateView(attributes);
    }
    
    private void updateView(AttributeCollection attributes) {
        this.attributesView = new CollectionView<AttributeCollection>(attributes);
    }
    
    @Override
    public int getSize() {
        return attributesView.size();
    }

    @Override
    public Object getElementAt(int i) {
        return attributesView.getAt(i);
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if(lse.getValueIsAdjusting())
            return;
        
        if(!JList.class.isAssignableFrom(lse.getSource().getClass())) {
            return;
        }
        JList jList = (JList)lse.getSource();
        int index = jList.getSelectedIndex();
        
        int oldSize = getSize();
        attributes = entityViewModel.getEntityAt(index).getAttributes();
        updateView(attributes);
        fireContentsChanged(this, 0, oldSize);
    }
}
