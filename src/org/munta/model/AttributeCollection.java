package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XMLObject;
import org.munta.utils.NotificationHashSet;

@XMLObject(name="Attributes", collection=true)
public class AttributeCollection
        extends NotificationHashSet<Attribute>
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
