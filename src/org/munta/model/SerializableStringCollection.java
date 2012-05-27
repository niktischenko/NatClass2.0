package org.munta.model;

import java.io.Serializable;
import java.util.HashSet;
import org.munta.projectengine.serializer.xml.XmlObject;


@XmlObject(name="SerializableStringCollection", collection=true)
public class SerializableStringCollection
        extends HashSet<SerializableString>
        implements Serializable {

    public SerializableStringCollection(SerializableStringCollection entities) {
        super(entities);
    }

    public SerializableStringCollection() {
        super();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (SerializableString string : this) {
            sb.append(string.toString()).append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
