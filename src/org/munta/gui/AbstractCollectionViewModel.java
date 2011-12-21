package org.munta.gui;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import org.munta.utils.CollectionChangedListener;

public abstract class AbstractCollectionViewModel<E>
        extends AbstractListModel
        implements ListModel, CollectionChangedListener {

    private final Object _this = this;
    private final List<E> list;
    private Timer updateTimer;
    private TimerTask listUpdateTask;
    private int visible_size = 0;
    private int action = 0;
    private int i0 = -1;
    private int i1 = -1;

    private AbstractCollectionViewModel(boolean bl) {
        listUpdateTask = new TimerTask() {

            @Override
            public void run() {
                synchronized (list) {
                    if (i1 == -1) {
                        i1 = i0;
                    }
                    switch (action) {
                        case 1:
                            visible_size = list.size();
                            fireIntervalAdded(_this, i0, i1);
                            break;
                        case 2:
                            visible_size = list.size();
                            fireIntervalRemoved(_this, i0, i1);
                            break;
                        default:
                            return;
                    }
                    action = 0;
                    i0 = i1 = -1;
                }
            }
        };

        updateTimer = new Timer();
        list = new LinkedList<E>();
        visible_size = list.size();
    }

    public AbstractCollectionViewModel(Collection collection) {
        this(true);

        initList(collection);
        startTimer();
    }

    public AbstractCollectionViewModel(Map map) {
        this(true);

        initList(map);
        startTimer();
    }

    public AbstractCollectionViewModel() {
        this(true);

        startTimer();
    }
    
    protected final void startTimer() {
        updateTimer.schedule(listUpdateTask, 0, 1000);
    }
    
    protected final void runUpdate() {
        listUpdateTask.run();
    }

    private void initList(Collection collection) {
        list.addAll(collection);
        visible_size = list.size();
    }

    private void initList(Map map) {
        list.addAll(map.entrySet());
        visible_size = list.size();
    }

    public void dispose() {
        updateTimer.cancel();
    }

    @Override
    public int getSize() {
        return visible_size;
    }

    public E getModelObjectAt(int i) {
        E e = null;
        //synchronized (list) {
        e = (E) list.get(i);
        //}
        return e;
    }

    @Override
    public void elementAdded(Object o) {
        synchronized (list) {
            int size = list.size();
            list.add((E) o);

            if (i0 == -1) {
                i0 = size;
            } else {
                i1 = size;
            }

            action = 1;
        }
    }

    @Override
    public void elementRemoved(Object o) {
        synchronized (list) {
            int size = list.size();

            if (o == null) {
                list.clear();
                i0 = 0;
                i1 = size;
            } else {
                list.remove((E) o);
                if (i0 == -1) {
                    i0 = size;
                } else {
                    i1 = size;
                }
            }

            action = 2;
        }
    }
}
