package org.munta.utils;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class NotificationMap<K, V> extends TreeMap<K, V> {
    
    private class EntryImpl<K, V> implements Entry<K, V> {

        private K key;
        private V value;
        
        public EntryImpl(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V v) {
            return this.value = v;
        }
    }
    
    private Set<CollectionChangedListener> listeners;
    
    public NotificationMap() {
        listeners = new HashSet<CollectionChangedListener>();
    }
    
    public NotificationMap(Map<K, V> map) {
        this();
        putAll(map);
    }
    
    private void fireElementAdded(Object key, Object value) {
        for(CollectionChangedListener listener : listeners) {
            listener.elementAdded(new EntryImpl<Object, Object>(key, value));
        }
    }
    
    private void fireElementRemoved(Object e) {
        for(CollectionChangedListener listener : listeners) {
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
        V vv = super.put(k, v);
        fireElementAdded(k, v);
        return vv;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(Entry<? extends K, ? extends V> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V remove(Object o) {
        fireElementRemoved(o);
        return super.remove(o);
    }
    
    public void addCollectionChangedListener(CollectionChangedListener mcl) {
        listeners.add(mcl);
    }
    
    public void removeCollectionChangedListener(CollectionChangedListener mcl) {
        listeners.remove(mcl);
    }
    
    public MapChangedListener[] getMapChangedListeners() {
        return listeners.toArray(new MapChangedListener[0]);
    }
}
