package org.munta.algorithm;

import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;
import org.munta.model.SerializableString;

public class IdealClassBuilder {

    private AttributeCollection allAttributes;

    synchronized void saveClass(Entity idealizedClass, EntityCollection classes, Entity entity) {
        int num = classes.size() + 1;
        idealizedClass.setName("C" + num);
        for (Entity c : classes) {
            if (c.getAttributes().equals(idealizedClass.getAttributes())) {
                c.getChildEntities().add(new SerializableString(entity.getName()));
                return; // dublicate
            }
        }
        idealizedClass.getChildEntities().add(new SerializableString(entity.getName()));
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
        System.err.println(allAttributes);
        ClassTable table = new ClassTable(allAttributes);
        table = table.generateForEntity(startObject);
        table.initializeTableGamma(regularities);
        table.calcGamma(regularities);
        double gamma = table.getMaxGamma(true);
        
        System.err.println("Starting gamma: "+gamma);
        Attribute maxAttr = null;
        do {
            int goodChecks = 0;
            double newGamma = stepAdd(table, regularities);
            if (newGamma > gamma) {
                goodChecks++;
                maxAttr = table.getAttributeForMaxGamma();
                table.get(maxAttr).setOn(true);
                gamma = newGamma;
                System.err.println("STEPADD OK");
            }
            newGamma = stepDel(table, regularities);
            if (newGamma > gamma) {
                goodChecks++;
                maxAttr = table.getAttributeForMaxGamma();
                table.get(maxAttr).setOn(false);
                gamma = newGamma;
                System.err.println("STEPDEL OK");
            }
            if (goodChecks == 0) {
                break;
            }
        } while (true);
        saveClass(table.generateClass(), classes, startObject);
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
