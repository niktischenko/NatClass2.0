/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.munta.model;

/**
 *
 * @author pavel_pro
 */
public class Attribute {
    private String name;
    private String value;

    public Attribute() {
        this("", "");
    }
    
    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Attribute))
            return false;
        
        Attribute attr = (Attribute)o;
        
        return this.name.equals(attr.name) && this.value.equals(attr.value);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ value.hashCode();
    }

    @Override
    public String toString() {
        return String.format("[%s:%s]", name, value);
    }
}
