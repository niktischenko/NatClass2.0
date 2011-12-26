package org.munta.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.munta.model.Attribute;
import org.munta.model.AttributeCollection;
import org.munta.model.Entity;
import org.munta.model.EntityCollection;
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

    private void fillRegularitiesImpl(Attribute target, Map<String, Attribute> set, RegularityCollection regularities) {
        for (Attribute attr : allAttributes) {

            if (target.getName().equals(attr.getName())) {
                continue;
            }

            HashMap<String, Attribute> newSet;

            if (!set.containsKey(attr.getName())) {
                newSet = new HashMap<String, Attribute>(set);
                newSet.put(attr.getName(), attr);
            } else {
                continue;
            }

            Regularity r = new Regularity();
            r.setTarget(target);
            r.getConditions().addAll(newSet.values());

            ProbabilityMatrix matrix = ProbabilityMatrix.build(r, storedEntities);

            Boolean toAdd = true;
            if (matrix.probability() >= ProjectManager.getInstance().getGlobalProperties().getProbabilityThreshold()) {
                int fisherRes = FisherYuleAlgorithm.checkFisherCriteria(matrix, 0.005);
                if (fisherRes == FisherYuleAlgorithm.RESULT_PASSED_AS_CONDITION) {
                    toAdd = false;
                    fillRegularitiesImpl(target, newSet, regularities);
                }
            }

            if (toAdd) {
                if(newSet.size() >= ProjectManager.getInstance().getGlobalProperties().getMinLength())
                synchronized (regularities) {
                    regularities.add(r);
                }
            }
        }
    }

    public void fillRegularities(EntityCollection entities, RegularityCollection regularities) throws InterruptedException {
        prepareBuilder(entities);

        regularities.clear();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        ArrayList<Callable<Object>> taskList = new ArrayList<Callable<Object>>();
        final RegularityCollection localRegularities = regularities;
        for (Attribute attr : allAttributes) {
            final Attribute localAttr = attr;
            taskList.add(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    fillRegularitiesImpl(localAttr, new HashMap<String, Attribute>(), localRegularities);
                    return null;
                }
            });
        }
        threadPool.invokeAll(taskList);
    }
}
