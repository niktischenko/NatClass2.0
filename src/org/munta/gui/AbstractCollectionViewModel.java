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
    //private Collection collection;

    private AbstractCollectionViewModel(boolean bl) {
        list = new ArrayList<E>();
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
        list.addAll(map.entrySet());
    }

    public void dispose() {
    }

    @Override
    public int getSize() {
        return list.size();
    }

    public E getModelObjectAt(int i) {
        E e = null;
        //synchronized (list) {
        e = (E) list.get(i);
        //}
        return e;
    }

    private void elementAddedUnsafe(Object o) {
        int size = list.size();
        list.add((E) o);
        fireIntervalAdded(this, size, size);
    }

    private void elementRemovedUnsafe(Object o) {
        int size = list.size();
        if (o == null) {
            list.clear();
        } else {
            list.remove((E) o);
        }
        fireIntervalRemoved(this, size, size);
    }

    @Override
    public void elementAdded(final Object o) {
        if (SwingUtilities.isEventDispatchThread()) {
            elementAddedUnsafe(o);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

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
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        elementRemovedUnsafe(o);
                    }
                });
            } catch (Exception ex) {
            }
        }
    }
}
