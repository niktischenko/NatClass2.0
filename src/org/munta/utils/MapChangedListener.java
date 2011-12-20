package org.munta.utils;

public interface MapChangedListener {
    public void elementAdded(Object key, Object value);
    public void elementRemoved(Object o);
}
