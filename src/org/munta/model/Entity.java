package org.munta.model;

import java.io.Serializable;

public class Entity implements Serializable {
    
    private String name;
    private AttributeCollection attributes;
    
    public Entity() {
        this("");
    }
    
    public Entity(String name) {
        this.name = name;
        attributes = new AttributeCollection();
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
