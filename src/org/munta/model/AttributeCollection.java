package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.utils.NotificationSet;

@XmlObject(name="Attributes", collection=true)
public class AttributeCollection
        extends NotificationSet<Attribute>
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
