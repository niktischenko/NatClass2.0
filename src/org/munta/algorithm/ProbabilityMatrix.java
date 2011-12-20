package org.munta.algorithm;

import java.util.HashMap;
import java.util.Map;
import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.Regularity;

public class ProbabilityMatrix {

    private int data[];

    public ProbabilityMatrix() {
        data = new int[4];
    }

    public int get(int i, int j) {
        assert 0 <= i && i <= 1;
        assert 0 <= j && j <= 1;
        return data[2 * i + j];
    }

    public void set(int i, int j, int value) {
        assert 0 <= i && i <= 1;
        assert 0 <= j && j <= 1;
        data[2 * i + j] = value;
    }
    
    public double probability() {
        double passed = get(0, 0);
        double all = get(0, 0) + get(1, 0);
        return passed / all;
    }
    
    private static Map<Attribute, Boolean> cache = new HashMap<Attribute, Boolean>();

    public static void resetCache() {
        cache.clear();
    }

    public static ProbabilityMatrix build(Regularity regularity, EntityCollection entities) {
        ProbabilityMatrix matrix = new ProbabilityMatrix();
        matrix.set(0, 0, countBoth(regularity, entities));
        matrix.set(1, 0, countConditionOnly(regularity, entities));
        matrix.set(0, 1, countTargetOnly(regularity, entities));
        matrix.set(1, 1, countNone(regularity, entities));
        return matrix;
    }

    private static int countBoth(Regularity regularity, EntityCollection entities) {
        int count = 0;
        for (Entity entity : entities) {
            if (entity.checkAttribute(regularity.getTarget()) && 
                    checkAttributeCollection(regularity.getConditions(), entities)) {
                count++;
            }
        }
        return count;
    }

    private static int countConditionOnly(Regularity regularity, EntityCollection entities) {
        int count = 0;
        for (Entity entity : entities) {
            if (!entity.checkAttribute(regularity.getTarget()) && 
                    checkAttributeCollection(regularity.getConditions(), entities)) {
                count++;
            }
        }
        return count;
    }

    private static int countTargetOnly(Regularity regularity, EntityCollection entities) {
        int count = 0;
        for (Entity entity : entities) {
            if (entity.checkAttribute(regularity.getTarget()) && 
                    !checkAttributeCollection(regularity.getConditions(), entities)) {
                count++;
            }
        }
        return count;
    }

    private static int countNone(Regularity regularity, EntityCollection entities) {
        int count = 0;
        for (Entity entity : entities) {
            if (!entity.checkAttribute(regularity.getTarget()) && 
                    !checkAttributeCollection(regularity.getConditions(), entities)) {
                count++;
            }
        }
        return count;
    }
    
    private static boolean checkAttributeCollection(AttributeCollection attributes, EntityCollection entities) {
        for (Attribute attribute : attributes) {
            if (!checkAttribute(attribute, entities)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkAttribute(Attribute attribute, EntityCollection entities) {
        if (!cache.containsKey(attribute)) {
            cache.put(attribute, Boolean.TRUE);
            for (Entity entity : entities) {
                if (!entity.checkAttribute(attribute)) {
                    cache.put(attribute, Boolean.FALSE);
                    break;
                }
            }
        }
        return cache.get(attribute);
    }
}
