package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.projectengine.serializer.xml.XmlProperty;

@XmlObject(name = "SerializableString")
public class SerializableString implements Serializable, Comparable<SerializableString> {
    @XmlProperty(name = "name", attribute = true) 
    private String name;

    public SerializableString() {
    }

    public SerializableString(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return name.equals(obj);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(SerializableString o) {
        return name.compareTo(o.name);
    }
}