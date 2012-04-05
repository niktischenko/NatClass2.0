package org.munta.algorithm;

import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;

public class IdealClassBuilder {

    private AttributeCollection allAttributes;

    synchronized void saveClass(Entity idealizedClass, EntityCollection classes) {
        int num = classes.size() + 1;
        idealizedClass.setName("C" + num);
        for (Entity c : classes) {
            if (c.getAttributes().equals(idealizedClass.getAttributes())) {
                return; // dublicate
            }
        }
        classes.add(idealizedClass);
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
        table.calcGamma(regularities);

        double gamma = table.getMaxGamma(true);
        System.err.println("Starting gamma: " + gamma);
        Attribute maxAttr = null;
        Attribute newMaxAttr = null;
        do {
            int ok = 0;
            System.err.println("Before step: "+table.generateClass());
            double newGamma = stepAdd(table, regularities);
            if (newGamma > gamma) {
                ok++;
                newMaxAttr = table.getAttributeForMaxGamma();
                table.get(newMaxAttr).setOn(true);
                saveClass(table.generateClass(), classes);
                System.err.println("=== NEW: " + newGamma + " OLD: " + gamma + " for attr " + table.getAttributeForMaxGamma() + " ====");
                System.err.println(table.generateClass());
                gamma = newGamma;
            }
            newGamma = stepDel(table, regularities);
            if (newGamma > gamma) {
                ok++;
                newMaxAttr = table.getAttributeForMaxGamma();
                table.get(newMaxAttr).setOn(false);
                saveClass(table.generateClass(), classes);
                System.err.println("=== NEW: " + newGamma + " OLD: " + gamma + " for attr " + table.getAttributeForMaxGamma() + " ====");
                System.err.println(table.generateClass());
                gamma = newGamma;
            }
            if (ok == 0) {
                break;
            }
            ok = 0;
            maxAttr = newMaxAttr;
        } while (true); // && (!newMaxAttr.equals(maxAttr)));
        saveClass(table.generateClass(), classes);
        System.err.println(table.generateClass());
    }

    private double stepAdd(ClassTable table, RegularityCollection regularities) {
        AttributeCollection attributes = table.getAllAttributes();
        for (Attribute attr : attributes) {
            if (!table.get(attr).isOn()) {
                table.get(attr).setOn(true);
                table.get(attr).setGamma(table.calcGamma(regularities));
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
                table.get(attr).setGamma(table.calcGamma(regularities));
                table.get(attr).setOn(true);
            }
        }
        return table.getMaxGamma(true);
    }
}
