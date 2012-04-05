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
    private int taskCountDone;
    private int taskCount;
    private final Object lock = new Object();

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

    private void fillRegularitiesImpl(Attribute target, Map<String, Attribute> set, RegularityCollection regularities, double fisherRecord, double probabilutyRecord) throws Exception {
        if (CancelEvent.getInstance().getStopPendingReset()) {
            throw new Exception("Stop request");
        }
        for (Attribute attr : allAttributes) {

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
            }
//            if (m.probability() <= probabilutyRecord) {
//                regularityPassed = false;
//                System.err.println("Probability became worse: "+m.probability()+" was: "+probabilutyRecord);
//            }

            if (m.get(0, 0) == 1) {
                regularityPassed = false;
            }

            FisherYuleAlgorithm.FisherYuleResult result = null;

            if (regularityPassed) {
                result = FisherYuleAlgorithm.checkFisherAndYuleCriteria(m, properties.getFisherThreshold(), properties.getYuleThreshold());
//                System.err.println("Fisher result: " + result.passedResult);
                if (result.passedResult != FisherYuleAlgorithm.RESULT_PASSED_AS_CONDITION) {
                    // failed for fished
                    regularityPassed = false;
                }
//                if (result.fisherValue >= fisherRecord && fisherRecord != 0) {
//                    regularityPassed = false;
//                    System.err.println("New fisher value is worse :" + fisherRecord + " -> " + result.fisherValue);
//                }
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
                }
                // continue generation
                fillRegularitiesImpl(target, newSet, regularities, result.fisherValue, m.probability());
            }
        }
    }

    public void fillRegularities(EntityCollection entities, RegularityCollection regularities) {
        prepareBuilder(entities);

        regularities.clear();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        ArrayList<Callable<Object>> taskList = new ArrayList<Callable<Object>>();
        final RegularityCollection localRegularities = regularities;
        taskCount = allAttributes.size();
        for (Attribute a : allAttributes) {
            System.err.print(a.toString() + " ");
        }
        taskCountDone = 0;
        for (Attribute attr : allAttributes) {
            final Attribute localAttr = attr;
            taskList.add(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    try {
                        fillRegularitiesImpl(localAttr, new HashMap<String, Attribute>(), localRegularities, 0, 0);
                    } catch (Exception e) {
                    } finally {
                        synchronized (lock) {
                            taskCountDone++;
                            System.err.println("Task done " + taskCountDone + " from " + taskCount);
                            if (taskCount == taskCountDone) {
                                CancelEvent.getInstance().setFlag();
                            }
                        }
                    }
                    return null;
                }
            });
        }
        try {
            CancelEvent.getInstance().resetFlag();
            threadPool.invokeAll(taskList);
        } catch (InterruptedException ex) {
            Logger.getLogger(RegularityBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
