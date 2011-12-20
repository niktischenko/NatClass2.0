package org.munta.gui;

import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.projectengine.ProjectManager;

public class EntityViewModel implements ListModel {

    private EntityCollection entities = ProjectManager.getInstance().getCollectionOfEntities();
    private LinkedList<Entity> list = new LinkedList<Entity>();
    private Iterator<Entity> iterator = entities.iterator();
    
    private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();
    
    @Override
    public int getSize() {
        return entities.size();
    }

    
    @Override
    public Object getElementAt(int i) {
        while(i >= list.size()) {
            if(!iterator.hasNext()) throw new IndexOutOfBoundsException();
            list.add(iterator.next());
        }
        return list.get(i);
    }

    @Override
    public void addListDataListener(ListDataListener ll) {
        listeners.add(ll);
    }

    @Override
    public void removeListDataListener(ListDataListener ll) {
        listeners.remove(ll);
    }
    
}
