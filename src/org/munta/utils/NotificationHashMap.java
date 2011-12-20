package org.munta.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NotificationHashMap<K, V> extends HashMap<K, V> {
    private Set<MapChangedListener> listeners;
    
    public NotificationHashMap() {
        listeners = new HashSet<MapChangedListener>();
    }
    
    public NotificationHashMap(Map<K, V> map) {
        this();
        putAll(map);
    }
    
    private void fireElementAdded(Object key, Object value) {
        for(MapChangedListener listener : listeners) {
            listener.elementAdded(key, value);
        }
    }
    
    private void fireElementRemoved(Object e) {
        for(MapChangedListener listener : listeners) {
            listener.elementRemoved(e);
        }
    }

    @Override
    public void clear() {
        fireElementRemoved(null);
        super.clear();
    }

    @Override
    public V put(K k, V v) {
        fireElementAdded(k, v);
        return super.put(k, v);
    }

    @Override
    public V remove(Object o) {
        fireElementRemoved(o);
        return super.remove(o);
    }
    
    public void addMapChangedListener(MapChangedListener mcl) {
        listeners.add(mcl);
    }
    
    public void remoteMapChangedListener(MapChangedListener mcl) {
        listeners.remove(mcl);
    }
    
    public MapChangedListener[] getMapChangedListeners() {
        return listeners.toArray(new MapChangedListener[0]);
    }
}
