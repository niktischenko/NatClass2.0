package org.munta.model;

import java.io.Serializable;
import java.util.HashSet;

public class AttributeCollection
        extends HashSet<Attribute>
        implements Serializable {

    AttributeCollection(AttributeCollection attributes) {
        super(attributes);
    }

    public AttributeCollection() {
        super();
    }

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
