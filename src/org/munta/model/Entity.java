package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XMLObject;
import org.munta.projectengine.serializer.xml.XMLProperty;

@XMLObject(name = "Entity")
public class Entity implements Serializable {

    @XMLProperty(name = "name", attribute = true)
    private String name;
    @XMLProperty(name = "Attributes", collection = true, propertyClass = AttributeCollection.class)
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
}
