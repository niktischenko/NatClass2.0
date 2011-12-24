package org.munta.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class NotificationSet<E> extends TreeSet<E> {
    private Set<CollectionChangedListener> listeners;
    
    public NotificationSet() {
        listeners = new HashSet<CollectionChangedListener>();
    }
    
    public NotificationSet(Set<E> collection) {
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
    public boolean addAll(Collection<? extends E> clctn) {
        Boolean result = true;
        for(E e : clctn) {
            result &= this.add(e);
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
