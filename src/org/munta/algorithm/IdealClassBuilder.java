package org.munta.algorithm;

import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;

public class IdealClassBuilder {

    private AttributeCollection allAttributes;
    private int num = 1;

    synchronized void saveClass(Entity idealizedClass, EntityCollection classes) {
        idealizedClass.setName("C" + num++);
        for (Entity c : classes) {
            if (c.getAttributes().equals(idealizedClass.getAttributes())) {
                System.err.println("DUBL: "+idealizedClass);
                return; // dublicate
            }
        }
        System.err.println("Add: "+idealizedClass);
        classes.add(idealizedClass);
    }
    
    synchronized void minimizeClassesCollection(EntityCollection classes) {
        EntityCollection clone = new EntityCollection(classes);
        for (Entity c : clone) {
            for (Entity t : clone) {
                if (c.getAttributes().containsAll(t.getAttributes()) && !c.equals(t)) {
                    classes.remove(t);
                }
            }
        }
    }

    public void fillRegularitiesProbabilitiy(EntityCollection entities, RegularityCollection regularities) {
        for (Regularity r : regularities.values()) {
            ProbabilityMatrix m = ProbabilityMatrix.build(r, entities);
            r.setProbability(m.probability());
        }
    }

    public void buildClass(Entity startObject, EntityCollection entities, RegularityCollection regularities, EntityCollection classes) {
        allAttributes = new AttributeCollection();
        for (Entity e : entities) {
            allAttributes.addAll(e.getAttributes());
        }

        ClassTable table = new ClassTable(allAttributes);
        table = table.generateForEntity(startObject);
        double gamma = table.calcGamma(regularities, new Attribute("", ""), false);

        System.err.println("Starting gamma: " + gamma);
        Attribute maxAttr = null;
        do {
            int ok = 0;
            System.err.println("Before step: " + table.generateClass());
            double newGamma = stepAdd(table, regularities);
            if (newGamma > gamma) {
                System.err.println("ADD OK");
                ok++;
                maxAttr = table.getAttributeForMaxGamma();
                table.get(maxAttr).setOn(true);
                saveClass(table.generateClass(), classes);
                minimizeClassesCollection(classes);
                System.err.println("=== NEW: " + newGamma + " OLD: " + gamma + " for attr " + table.getAttributeForMaxGamma() + " ====");
                System.err.println(table.generateClass());
                gamma = newGamma;
            } else {
                System.err.println("ADD FAIL");
            }
            newGamma = stepDel(table, regularities);
            if (newGamma > gamma) {
                System.err.println("DEL OK");
                ok++;
                maxAttr = table.getAttributeForMaxGamma();
                table.get(maxAttr).setOn(false);
                saveClass(table.generateClass(), classes);
                minimizeClassesCollection(classes);
                System.err.println("=== NEW: " + newGamma + " OLD: " + gamma + " for attr " + table.getAttributeForMaxGamma() + " ====");
                System.err.println(table.generateClass());
                gamma = newGamma;
            } else {
                System.err.println("DEL FAIL");
            }
            if (ok == 0) {
                System.err.println("Broken:"+table.generateClass());
                break;
            }
            ok = 0;
        } while (true); // && (!newMaxAttr.equals(maxAttr)));
        saveClass(table.generateClass(), classes);
        System.err.println(table.generateClass());
    }

    private double stepAdd(ClassTable table, RegularityCollection regularities) {
        AttributeCollection attributes = table.getAllAttributes();
        for (Attribute attr : attributes) {
            if (!table.get(attr).isOn()) {
                table.get(attr).setOn(true);
                table.get(attr).setGamma(table.calcGamma(regularities, attr, true));
                table.get(attr).setOn(false);
            }
        }
        return table.getMaxGamma(false);
    }

    private double stepDel(ClassTable table, RegularityCollection regularities) {
        AttributeCollection attributes = table.getAllAttributes();
        for (Attribute attr : attributes) {
            if (table.get(attr).isOn()) {
                table.get(attr).setOn(false);
                table.get(attr).setGamma(table.calcGamma(regularities, attr, true));
                table.get(attr).setOn(true);
            }
        }
        return table.getMaxGamma(true);
    }
}
