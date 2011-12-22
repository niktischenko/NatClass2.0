package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.utils.NotificationHashSet;


@XmlObject(name="Entities", collection=true)
public class EntityCollection
        extends NotificationHashSet<Entity>
        implements Serializable {

    public EntityCollection(EntityCollection entities) {
        super(entities);
    }

    public EntityCollection() {
        super();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Entity entity : this) {
            sb.append(entity.toString());
        }
        sb.append("]");
        return sb.toString();
    }
}
