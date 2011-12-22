package org.munta.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    private static Map<Attribute, Set<String>> cachePassed = new HashMap<Attribute, Set<String>>();
    private static Map<Attribute, Set<String>> cacheNotPassed = new HashMap<Attribute, Set<String>>();

    public static void resetCache() {
        cachePassed.clear();
        cacheNotPassed.clear();
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
        AttributeCollection attributes = regularity.getConditions();
        attributes.add(regularity.getTarget());
        int count = countAttributeCollectionPassed(attributes, entities).size();
        attributes.remove(regularity.getTarget());
        return count;
    }

    private static int countConditionOnly(Regularity regularity, EntityCollection entities) {
        Set<String> passedOnCondition = countAttributeCollectionPassed(regularity.getConditions(), entities);
        Set<String> notPassedOnTarget = countAttributeNotPassed(regularity.getTarget(), entities);
        return setsIntersection(passedOnCondition, notPassedOnTarget).size();
    }

    private static int countTargetOnly(Regularity regularity, EntityCollection entities) {
        Set<String> notPassedOnCondition = countAttributeCollectionNotPassed(regularity.getConditions(), entities);
        Set<String> passedOnTarget = countAttributePassed(regularity.getTarget(), entities);
        return setsIntersection(notPassedOnCondition, passedOnTarget).size();
    }

    private static int countNone(Regularity regularity, EntityCollection entities) {
        AttributeCollection attributes = regularity.getConditions();
        attributes.add(regularity.getTarget());
        int count = countAttributeCollectionNotPassed(attributes, entities).size();
        attributes.remove(regularity.getTarget());
        return count;
    }

    private static Set<String> countAttributeCollectionPassed(AttributeCollection attributes, EntityCollection entities) {
        Set<String> passedEntities = null;
        for (Attribute attribute : attributes) {
            Set<String> passed = countAttributePassed(attribute, entities);
            if (passedEntities == null) {
                passedEntities = passed;
                continue;
            }
            passedEntities = setsIntersection(passedEntities, passed);
        }
        return passedEntities;
    }

    private static Set<String> countAttributeCollectionNotPassed(AttributeCollection attributes, EntityCollection entities) {
        Set<String> notPassedEntities = null;
        for (Attribute attribute : attributes) {
            Set<String> notPassed = countAttributeNotPassed(attribute, entities);
            if (notPassedEntities == null) {
                notPassedEntities = notPassed;
                continue;
            }
            notPassedEntities = setsIntersection(notPassedEntities, notPassed);
        }
        return notPassedEntities;
    }

    private static Set<String> countAttributePassed(Attribute attribute, EntityCollection entities) {
        if (!cachePassed.containsKey(attribute)) {
            Set<String> passedEntities = countAttribute(attribute, entities, true);
            cachePassed.put(attribute, passedEntities);
        }
        return cachePassed.get(attribute);
    }

    private static Set<String> countAttributeNotPassed(Attribute attribute, EntityCollection entities) {
        if (!cacheNotPassed.containsKey(attribute)) {
            Set<String> notPassedEntities = countAttribute(attribute, entities, false);
            cacheNotPassed.put(attribute, notPassedEntities);
        }
        return cacheNotPassed.get(attribute);
    }

    private static Set<String> countAttribute(Attribute attribute, EntityCollection entities, boolean condition) {
        Set<String> passedOnCondition = new HashSet<String>();
        for (Entity entity : entities) {
            if (entity.checkAttribute(attribute) == condition) {
                passedOnCondition.add(entity.getName());
            }
        }
        return passedOnCondition;
    }

    private static Set<String> setsIntersection(Set<String> one, Set<String> another) {
        Set<String> result = new HashSet<String>();
        Set<String> min = one.size() > another.size() ? another : one;
        Set<String> max = min == one ? another : one;
        result.addAll(min);
        result.retainAll(max);
        return result;
    }
}
