package org.munta.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
import org.munta.model.GlobalProperties;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;
import org.munta.projectengine.ProjectManager;

public class RegularityBuilder {

    private EntityCollection storedEntities;
    private AttributeCollection allAttributes;

    public RegularityBuilder() {
        storedEntities = new EntityCollection();
        allAttributes = new AttributeCollection();
    }

    private void prepareBuilder(EntityCollection entities) {
        if (storedEntities.equals(entities)) {
            return;
        }

        storedEntities.clear();
        storedEntities.addAll(entities);
        allAttributes.clear();
        for (Entity e : entities) {
            allAttributes.addAll(e.getAttributes());
        }
    }

    private synchronized void addRegularity(RegularityCollection regularities, Regularity regularity) {
        regularities.add(regularity);
    }

    private void fillRegularitiesImpl(Attribute target, Map<String, Attribute> set, RegularityCollection regularities) {
        for (Attribute attr : allAttributes) {
//            System.err.println(attr);

            if (target.getName().equals(attr.getName())) {
                continue;
            }

            if (set.containsKey(attr.getName())) {
                continue;
            }

            HashMap<String, Attribute> newSet = new HashMap<String, Attribute>(set);
            newSet.put(attr.getName(), attr);

            Regularity r = new Regularity();
            r.setTarget(target);
            r.getConditions().addAll(newSet.values());
            boolean regularityPassed = true;

            ProbabilityMatrix m = ProbabilityMatrix.build(r, storedEntities);
            GlobalProperties properties = ProjectManager.getInstance().getGlobalProperties();
            if (m.probability() < properties.getProbabilityThreshold()) {
                // failed for probability
                regularityPassed = false;
//                System.err.println("Regularity " + r.toString() + " does not meet probability threshold: " + m.probability() + " <> " + properties.getProbabilityThreshold());
            }

            if (m.get(0, 0) == 1) {
                regularityPassed = false;
            }

            if (regularityPassed) {
                int fisherYuleResult = FisherYuleAlgorithm.checkFisherAndYuleCriteria(m, properties.getFisherThreshold(), properties.getYuleThreshold());
                if (fisherYuleResult != FisherYuleAlgorithm.RESULT_PASSED_AS_CONDITION) {
                    // failed for fished
                    regularityPassed = false;
//                    System.err.println("Regularity " + r.toString() + " does not meet fisher thresold: " + properties.getFisherThreshold());
                }
            }

            if (!regularityPassed) {
                // regularity is not passed
                // reduce it and save if it was not saved on previous step
                if (!properties.getUseIntermediateResults()) {
                    r.getConditions().remove(attr);
                    if (r.getConditions().size() >= properties.getMinLength()) {
                        addRegularity(regularities, r);
                    }
                }
            } else {
                // regularity is ok
                if (properties.getUseIntermediateResults() && r.getConditions().size() >= properties.getMinLength()) {
                    // save intermediate regularity
                    addRegularity(regularities, r);
//                    System.err.println("Regularity " + r.toString() + " was saved as intermediate");
                }
                // continue generation
                fillRegularitiesImpl(target, newSet, regularities);
//                System.err.println("Continue generation for " + r.toString());
            }
        }
    }

    public void fillRegularities(EntityCollection entities, RegularityCollection regularities) {
        prepareBuilder(entities);

        regularities.clear();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        ArrayList<Callable<Object>> taskList = new ArrayList<Callable<Object>>();
        final RegularityCollection localRegularities = regularities;
        for (Attribute attr : allAttributes) {
//            fillRegularitiesImpl(attr, new HashMap<String, Attribute>(), localRegularities);
            final Attribute localAttr = attr;
            taskList.add(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    fillRegularitiesImpl(localAttr, new HashMap<String, Attribute>(), localRegularities);
                    return null;
                }
            });
        }
        try {
            threadPool.invokeAll(taskList);
        } catch (InterruptedException ex) {
            Logger.getLogger(RegularityBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
