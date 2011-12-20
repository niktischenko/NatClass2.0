package org.munta.model;

import java.io.Serializable;
import java.util.HashSet;
import org.munta.projectengine.serializer.xml.XMLObject;

@XMLObject(name="Entities", collection=true)
public class EntityCollection
        extends HashSet<Entity>
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
