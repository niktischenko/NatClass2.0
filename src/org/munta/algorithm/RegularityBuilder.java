package org.munta.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private long factorial(long n) {
        if (n <= 1) {
            return 1;
        }
        return n * (factorial(n - 1));
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
//        int size = (int)(factorial(all) / (factorial(all - count) * factorial(count)));
//        System.err.println("cnk: " + all + " " + count + " size: " + size);
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
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < count; j++) {
//                System.err.print(combinations[i][j] + " ");
//            }
//            System.err.println();
//        }
        return combinations;
    }

    private AttributeCollection selectByMask(List<Attribute> source, int[] mask) {
        AttributeCollection a = new AttributeCollection();
        for (int i : mask) {
            a.add(source.get(i));
        }
//        System.err.println("FILTERED: " + a);
        return a;
    }

    private void fillRegularitiesImpl(Attribute target, Map<String, Attribute> set, RegularityCollection regularities, double fisherRecord, double probabilutyRecord) throws Exception {
        if (CancelEvent.getInstance().getStopPendingReset()) {
            throw new Exception("Stop request");
        }
        GlobalProperties properties = ProjectManager.getInstance().getGlobalProperties();
        boolean continued = false;
        for (Attribute attr : allAttributes) {
            System.err.println("==========START============\n");
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
            if (good) {
                if (properties.getUseIntermediateResults()) {
                    addRegularity(regularities, r);
                }
                continued = true;
                fillRegularitiesImpl(target, newSet, regularities, fisherRecord, probabilutyRecord);
            }
            System.err.println("==========END==============\n");
        }
        if (!continued) {
            if (set.size() >= properties.getMinLength()) {
                Regularity r = new Regularity();
                r.setTarget(target);
                r.getConditions().addAll(set.values());
                addRegularity(regularities, r);
            }
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
//        for (Attribute a : allAttributes) {
//            System.err.print(a.toString() + " ");
//        }
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
