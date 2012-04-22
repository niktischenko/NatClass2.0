package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.projectengine.serializer.xml.XmlProperty;

@XmlObject(name = "Entity")
public class Entity implements Serializable, Comparable<Entity> {

    @XmlProperty(name = "name", attribute = true)
    private String name;
    @XmlProperty(name = "Attributes", collection = true, propertyClass = AttributeCollection.class)
    private AttributeCollection attributes;

    public Entity() {
        this("");
    }

    public Entity(Entity entity) {
        this.name = entity.name;
        this.attributes = new AttributeCollection(entity.attributes);
    }

    public Entity(String name) {
        this.name = name;
        attributes = new AttributeCollection();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entity)) {
            return false;
        }

        Entity e = (Entity) o;

        return this.name.equals(e.name) && this.attributes.equals(e.attributes);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ attributes.hashCode();
    }

    public boolean checkAttribute(Attribute attribute) {
        for (Attribute attr : attributes) {
            if (attr.equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    public AttributeCollection getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributeCollection attributes) {
        this.attributes.clear();
        this.attributes.addAll(attributes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, attributes.toString());
    }

    @Override
    public int compareTo(Entity t) {
        return getName().compareTo(t.getName());
    }
}
