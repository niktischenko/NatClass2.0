package org.munta.model;

import java.util.HashMap;

public class Entity {

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
