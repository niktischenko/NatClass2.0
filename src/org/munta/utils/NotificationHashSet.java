package org.munta.utils;

import java.util.HashSet;
import java.util.Set;

public class NotificationHashSet<E> extends HashSet<E> {
    private Set<CollectionChangedListener> listeners;
    
    public NotificationHashSet() {
        listeners = new HashSet<CollectionChangedListener>();
    }
    
    public NotificationHashSet(Set<E> collection) {
        this();
        addAll(collection);
    }
    
    private void fireElementAdded(Object e) {
        for(CollectionChangedListener listener : listeners) {
            listener.elementAdded(e);
        }
    }
    
    private void fireElementRemoved(Object e) {
        for(CollectionChangedListener listener : listeners) {
            listener.elementRemoved(e);
        }
    }

    @Override
    public boolean add(E e) {
        Boolean result = super.add(e);
        if(result) {
            fireElementAdded(e);
        }
        return result;
    }

    @Override
    public void clear() {
        fireElementRemoved(null);
        super.clear();
    }

    @Override
    public boolean remove(Object o) {
        Boolean result = super.remove(o);
        if(result) {
            fireElementRemoved(o);
        }
        return result;
    }
    
    public void addCollectionChangedListener(CollectionChangedListener ccl) {
        listeners.add(ccl);
    }
    
    public void removeCollectionChangedListener(CollectionChangedListener ccl) {
        listeners.remove(ccl);
    }
    
    public CollectionChangedListener[] getCollectionChangedListeners() {
        return listeners.toArray(new CollectionChangedListener[0]);
    }
}
