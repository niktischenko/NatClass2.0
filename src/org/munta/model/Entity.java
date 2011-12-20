package org.munta.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
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
    
    @XmlElement(name="Attribute")
    @XmlElementWrapper(name="Attributes")
    public AttributeCollection getAttributes() {
        return attributes;
    }
    
    public void setAttributes(AttributeCollection attributes) {
        this.attributes.clear();
        this.attributes.addAll(attributes);
    }

    @XmlAttribute
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
