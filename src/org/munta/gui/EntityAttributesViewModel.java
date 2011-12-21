package org.munta.gui;

import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.munta.model.Entity;

public class EntityAttributesViewModel
        extends AbstractListModel
        implements ListSelectionListener {

    private AbstractCollectionViewModel entityViewModel;
    private List<Object> list;
    
    public EntityAttributesViewModel(AbstractCollectionViewModel entityViewModel) {
        this.entityViewModel = entityViewModel;
        list = new LinkedList<Object>();
    }
    
    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Object getElementAt(int i) {
        return list.get(i);
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
        list.clear();
        list.addAll(((Entity)entityViewModel.getModelObjectAt(index)).getAttributes());
        fireContentsChanged(this, 0, oldSize);
    }
}
