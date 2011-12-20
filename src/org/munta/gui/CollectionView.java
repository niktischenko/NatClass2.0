package org.munta.gui;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class CollectionView<T extends Collection<?>> {

    private T collection;
    private Iterator<?> iterator;
    private List<Object> list;

    CollectionView(T collection) {
        this.collection = collection;
        list = new LinkedList<Object>();
        
        updateView();
    }

    public Object getAt(int index) {
        while (index >= list.size()) {
            if (!iterator.hasNext()) {
                return null;
            }
            list.add(iterator.next());
        }
        return list.get(index);
    }

    public Iterator<? extends Object> iterator() {
        return collection.iterator();
    }

    public int size() {
        return collection.size();
    }
    
    public void updateView() {
        iterator = collection.iterator();
        list.clear();
    }
}
