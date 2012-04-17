package org.munta.algorithm;

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
        double passedAll = get(0, 0);
        double passedRest = get(1, 0) + get(0, 0);
        if (passedRest == 0) {
            return 0;
        }
//        System.err.println("probability: " + passedAll + " / " + passedRest + " total: " + (data[0] + data[1] + data[2] + data[3]));
        return (passedAll) / (passedRest);
    }
    
    public int total() {
        return data[0]+data[1]+data[2]+data[3];
    }
    
    public static ProbabilityMatrix build(Regularity regularity, EntityCollection entities) {
        ProbabilityMatrix matrix = new ProbabilityMatrix();
        AttributeCollection target = new AttributeCollection();
        target.add(regularity.getTarget());
        
        int m[][] = buildMatrix(target, regularity.getConditions(), entities);
        matrix.set(0, 0, m[0][0]);
        matrix.set(1, 0, m[1][0]);
        matrix.set(0, 1, m[0][1]);
        matrix.set(1, 1, m[1][1]);
        return matrix;
    }

    
    private static int[][] buildMatrix(AttributeCollection target, AttributeCollection condition, EntityCollection entities) {
        int[][] matrix = new int[2][];
        matrix[0] = new int[2];
        matrix[1] = new int[2];
        matrix[0][0] = 0;
        matrix[0][1] = 0;
        matrix[1][0] = 0;
        matrix[1][1] = 0;
        for (Entity entity : entities) {
            boolean bp0 = checkOnEntity(entity, target);
            boolean bp1 = checkOnEntity(entity, condition);
            if (bp0 && bp1) {
                matrix[0][0] += 1;
            }
            if (bp0 && !bp1) {
                matrix[0][1] += 1;
            }
            if (!bp0 && bp1) {
                matrix[1][0] += 1;
            }
            if (!bp0 && !bp1) {
                matrix[1][1] += 1;
            }
        }
        return matrix;
    }

    private static boolean checkOnEntity(Entity entity, AttributeCollection attributes) {
        for (Attribute attr: attributes) {
            if (!entity.checkAttribute(attr)) {
                return false;
            }
        }
        return true;
    }
}
