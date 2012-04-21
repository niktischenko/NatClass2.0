package org.munta.algorithm;

import java.util.HashMap;
import java.util.Map;
import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;

public class ClassTable {

    private Map<String, Map<String, ClassTableItem>> table;
    private Attribute attributeForMaxGamma;
    private AttributeCollection allAttributes;

    public ClassTable(ClassTable t) {
        table = new HashMap<String, Map<String, ClassTableItem>>();
        for (String name : t.table.keySet()) {
            if (!table.containsKey(name)) {
                table.put(name, new HashMap<String, ClassTableItem>());
            }
            for (String value : t.table.get(name).keySet()) {
                set(new Attribute(name, value), t.table.get(name).get(value));
            }
        }
        this.allAttributes = t.allAttributes;
    }

    public ClassTable(AttributeCollection attributes) {
        table = new HashMap<String, Map<String, ClassTableItem>>();
        for (Attribute attr : attributes) {
            if (!table.containsKey(attr.getName())) {
                table.put(attr.getName(), new HashMap<String, ClassTableItem>());
            }
            set(attr, new ClassTableItem());
        }
        allAttributes = attributes;
    }

    public ClassTableItem get(Attribute attr) {
        return table.get(attr.getName()).get(attr.getValue());
    }

    public final void set(Attribute attr, ClassTableItem item) {
        table.get(attr.getName()).put(attr.getValue(), item);
    }

    public double getMaxGamma(boolean on) {
        double gamma = ((table.values().iterator().next()).values().iterator().next()).getGamma();
        attributeForMaxGamma = new Attribute();
        for (String name : table.keySet()) {
            for (String value : table.get(name).keySet()) {
                ClassTableItem item = table.get(name).get(value);
                if (!item.isOn() && item.getGamma() >= gamma) {
                    gamma = item.getGamma();
                    attributeForMaxGamma.setName(name);
                    attributeForMaxGamma.setValue(value);
                }
            }
        }
        System.err.println("Gamma: " + gamma);
        return gamma;
    }

    public Attribute getAttributeForMaxGamma() {
        return attributeForMaxGamma;
    }

    public ClassTable generateForEntity(Entity e) {
        ClassTable t = new ClassTable(this);
        for (Attribute attr : e.getAttributes()) {
            t.get(attr).setOn(true);
        }
        return t;
    }

    public Entity generateClass() {
        Entity e = new Entity();
        for (String name : table.keySet()) {
            for (String value : table.get(name).keySet()) {
                if (table.get(name).get(value).isOn()) {
                    e.getAttributes().add(new Attribute(name, value));
                }
            }
        }
        return e;
    }

    public double calcGamma(RegularityCollection regularities) {
        double gamma = 0;
        for (Regularity r : regularities.values()) {
            if (checkAttributesCollection(r.getConditions())) {
                if (checkAttribute(r.getTarget())) {
                    gamma += Math.log(1 - r.getProbability());
                }
                if (checkAttributeNegative(r.getTarget()))  {
                    gamma -= Math.log(1 - r.getProbability());
                }
            }
        }
        System.err.println("Calculated gamma " + gamma);
        return gamma;
    }

    public AttributeCollection getAllAttributes() {
        return allAttributes;
    }

    private boolean checkAttributesCollection(AttributeCollection attributes) {
        for (Attribute attr : attributes) {
            if (!checkAttribute(attr)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAttribute(Attribute attr) {
        return get(attr).isOn();
    }

    private boolean checkAttributeNegative(Attribute attr) {
        Map<String, ClassTableItem> items = table.get(attr.getName());
        for (String value : items.keySet()) {
            if (!value.equals(attr.getValue()) && items.get(value).isOn()) {
                return true;
            }
        }
        return false;
    }
}
