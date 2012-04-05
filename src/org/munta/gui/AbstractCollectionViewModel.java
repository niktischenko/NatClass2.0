package org.munta.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import org.munta.utils.CollectionChangedListener;

public abstract class AbstractCollectionViewModel<E>
        extends AbstractListModel
        implements ListModel, CollectionChangedListener {

    private final List<E> list;
    private final List<E> filteredList;
    //private Collection collection;

    private AbstractCollectionViewModel(boolean reserved) {
        list = new ArrayList<E>();
        filteredList = new ArrayList<E>();
    }

    public AbstractCollectionViewModel(Collection collection) {
        this(true);

        initList(collection);
    }

    public AbstractCollectionViewModel(Map map) {
        this(true);

        initList(map);
    }

    protected AbstractCollectionViewModel() {
        this(true);
    }

    private void initList(Collection collection) {
        //this.collection = collection;
        list.addAll(collection);
    }

    private void initList(Map map) {
        //this.collection = map.entrySet();
        list.addAll(map.entrySet());
    }
    
    private void updateFilteredList() {
        int firstSize = filteredList.size();
        
        filteredList.clear();
        
        for(E obj : list) {
            if(onFilter(obj))
                filteredList.add(obj);
        }
        
        int secondSize = filteredList.size();
        if(secondSize > firstSize) {
            fireIntervalAdded(this, firstSize, secondSize - 1);
        } else if(secondSize < firstSize) {
            fireIntervalRemoved(this, secondSize, firstSize - 1);
        } else if(secondSize > 0) {
            fireContentsChanged(this, 0, secondSize - 1);
        }
    }
    
    protected Boolean onFilter(E obj) {
        return true;
    }

    public void dispose() {
    }

    @Override
    public int getSize() {
        return filteredList.size();
    }

    public E getModelObjectAt(int i) {
        E e = null;
        if(i < 0 || i >= filteredList.size())
            return null;
        //synchronized (list) {
        e = (E) filteredList.get(i);
        //}
        return e;
    }

    private void elementAddedUnsafe(Object o) {
        int size = list.size();
        list.add((E) o);
        updateFilteredList();
        //fireIntervalAdded(this, size, size);
    }

    private void elementRemovedUnsafe(Object o) {
        int size = list.size();
        int size0;
        if (o == null) {
            list.clear();
            size0 = 0;
        } else {
            list.remove((E) o);
            size0 = size;
        }
        updateFilteredList();
        //fireIntervalRemoved(this, size0, size);
    }

    @Override
    public void elementAdded(final Object o) {
        if (SwingUtilities.isEventDispatchThread()) {
            elementAddedUnsafe(o);
        } else {
            try {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        elementAddedUnsafe(o);
                    }
                });
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void elementRemoved(final Object o) {
        if (SwingUtilities.isEventDispatchThread()) {
            elementRemovedUnsafe(o);
        } else {
            try {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        elementRemovedUnsafe(o);
                    }
                });
            } catch (Exception ex) {
            }
        }
    }
    
    public void redrawList() {
        updateFilteredList();
        //fireContentsChanged(this, 0, filteredList.size() - 1);
    }
}
