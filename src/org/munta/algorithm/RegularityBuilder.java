package org.munta.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private boolean checkRegularity(Regularity r, EntityCollection entities) {
        GlobalProperties properties = ProjectManager.getInstance().getGlobalProperties();
        ProbabilityMatrix m = ProbabilityMatrix.build(r, entities);
        FisherYuleAlgorithm.FisherYuleResult result = FisherYuleAlgorithm.checkFisherAndYuleCriteria(m, properties.getFisherThreshold(), properties.getYuleThreshold());
//        System.err.println("Probability: " + m.probability());
//        System.err.println("Fisher: " + result.fisherValue);
//        System.err.println("Yule: " + result.yuleValue);
//        System.err.println(r.toString());
//        System.err.println("----------");

        if (m.probability() <= properties.getProbabilityThreshold()) {
            return false;
        }
        if (result.passedResult != FisherYuleAlgorithm.RESULT_PASSED_AS_CONDITION) {
            return false;
        }
        return true;
    }

    private int[] nextCombination(int[] current, int n) {
        int[] nn = current.clone();
        int k = nn.length;
        for (int i = k - 1; i >= 0; --i) {
            if (nn[i] < n - k + i) {
                ++nn[i];
                for (int j = i + 1; j < k; ++j) {
                    nn[j] = nn[j - 1] + 1;
                }
                return nn;
            }
        }
        return null;
    }

    private List<int[]> cnk(int all, int count) {
        List<int[]> combinations = new ArrayList<int[]>();
        int[] start = new int[count];
        for (int i = 0; i < count; i++) {
            start[i] = i;
        }
        combinations.add(0, start);
        for (int i = 1; true; i++) {
            int[] nn = nextCombination(combinations.get(i - 1), all);
            if (nn == null) {
                break;
            }
            combinations.add(i, nn);
        }
        return combinations;
    }

    private AttributeCollection selectByMask(List<Attribute> source, int[] mask) {
        AttributeCollection a = new AttributeCollection();
        for (int i : mask) {
            a.add(source.get(i));
        }
        return a;
    }

    private void fillRegularitiesImpl(Attribute target, Map<String, Attribute> set, RegularityCollection regularities, double fisherRecord, double probabilutyRecord) throws Exception {
        if (CancelEvent.getInstance().getStopPendingReset()) {
            throw new Exception("Stop request");
        }
        GlobalProperties properties = ProjectManager.getInstance().getGlobalProperties();
        boolean continued = false;
        for (Attribute attr : allAttributes) {
//            System.err.println("==========START============\n");
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

            boolean good = true;

            if (!checkRegularity(r, storedEntities)) {
                good = false;
            }

            if (r.getConditions().size() > 1) {
                List<Attribute> orderedAttributes = new ArrayList<Attribute>(r.getConditions());
                int size = r.getConditions().size();
                for (int i = 1; i < size; i++) {
                    List<int[]> combinations = cnk(size, i);
                    for (int[] combination : combinations) {
                        AttributeCollection aa = selectByMask(orderedAttributes, combination);
                        EntityCollection ee = filterEntities(storedEntities, aa);
                        if (!checkRegularity(r, ee)) {
//                            System.err.println("Dropped by minimization");
                            good = false;
                        }
                    }
                }
            }
            if (good || r.getConditions().size() < properties.getRecursionDeep()) {
                if (good && properties.getUseIntermediateResults()) {
                    addRegularity(regularities, r);
                }
                continued = true;
                fillRegularitiesImpl(target, newSet, regularities, fisherRecord, probabilutyRecord);
            }
//            System.err.println("==========END==============\n");
        }
        if (set.size() < properties.getMinLength() - 1) {
            return;
        }
        if (!continued) {
            Regularity r = new Regularity();
            r.setTarget(target);
            r.getConditions().addAll(set.values());
            r.setTerminated(true);
            addRegularity(regularities, r);

        } else if (properties.getUseIntermediateResults()) {
            Regularity r = new Regularity();
            r.setTarget(target);
            r.getConditions().addAll(set.values());
            r.setTerminated(false);
            addRegularity(regularities, r);
        }
    }

    public EntityCollection filterEntities(EntityCollection entities, AttributeCollection filter) {
//        System.err.println("ALL: "+entities);
//        System.err.println("FILTER: "+filter);
        EntityCollection newCollection = new EntityCollection();
        for (Entity e : entities) {
            boolean flag = true;
            for (Attribute a : filter) {
                if (!e.checkAttribute(a)) {
                    flag = false;
                }
            }
            if (flag) {
                newCollection.add(e);
            }
        }
//        System.err.println("FILTERED: "+newCollection);
        return newCollection;
    }

    public void fillRegularities(EntityCollection entities, RegularityCollection regularities) {
        /*        Regularity r = new Regularity();
        r.setTarget(new Attribute("J", "1"));
        r.getConditions().add(new Attribute("L", "1"));
        r.getConditions().add(new Attribute("O", "0"));
        checkRegularity(r, entities);
        
        if (r.getConditions().size() > 1) {
        List<Attribute> orderedAttributes = new ArrayList<Attribute>(r.getConditions());
        int size = r.getConditions().size();
        for (int i = 1; i < size; i++) {
        int[][] combinations = cnk(size, i);
        for (int[] combination : combinations) {
        AttributeCollection aa = selectByMask(orderedAttributes, combination);
        EntityCollection ee = filterEntities(entities, aa);
        checkRegularity(r, ee);
        }
        }
        }
         */
        prepareBuilder(entities);

        regularities.clear();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        ArrayList<Callable<Object>> taskList = new ArrayList<Callable<Object>>();
        final RegularityCollection localRegularities = regularities;
        taskCount = allAttributes.size();
        taskCountDone = 0;

        for (Attribute attr : allAttributes) {
            final Attribute localAttr = attr;
            taskList.add(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    try {
                        fillRegularitiesImpl(localAttr, new HashMap<String, Attribute>(), localRegularities, 0, 0);
                    } catch (Exception e) {
                        Logger.getLogger(RegularityBuilder.class.getName()).log(Level.SEVERE, null, e);
                    } finally {
                        synchronized (lock) {
                            taskCountDone++;
//                            System.err.println("Task done " + taskCountDone + " from " + taskCount);
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
