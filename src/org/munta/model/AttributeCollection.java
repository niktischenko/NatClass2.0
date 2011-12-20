package org.munta.model;

import java.io.Serializable;
import java.util.HashSet;

public class AttributeCollection
        extends HashSet<Attribute>
        implements Serializable {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Attribute attr : this) {
            sb.append(attr.toString());
        }
        sb.append("]");
        return sb.toString();
    }
}
