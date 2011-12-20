package org.munta.algorithm;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.util.MathUtils;

class FisherYuleAlgorithm {
    public static int RESULT_NOT_PASSED = 0;
    public static int RESULT_PASSED_AS_CONDITION = 1;
    public static int RESULT_PASSED_AS_CONTEXT = 2;

    private static class FisherYuleResult {

        private double fisherValue;
        private double yuleValue;
        private int resultCode;
    }

    public static int checkFisherCriteria(ProbabilityMatrix matrix, double fisherThreshold) {
        try {
            FisherYuleResult result = checkFisherAndYuleAlgorithm(matrix, fisherThreshold);
            switch (result.resultCode) {
                case 2:
                    return RESULT_PASSED_AS_CONDITION;
                case 3:
                    return RESULT_PASSED_AS_CONTEXT;
                default:
                    return RESULT_NOT_PASSED;                    
            }
            
        } catch (MathException ex) {
            Logger.getLogger(FisherYuleAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return RESULT_NOT_PASSED;
    }

    private static FisherYuleResult checkFisherAndYuleAlgorithm(ProbabilityMatrix m, double fisherThreshold) throws MathException {
        FisherYuleResult result = new FisherYuleResult();
        int n = m.get(0, 0) + m.get(0, 1) + m.get(1, 0) + m.get(1, 1);
        int n1_ = m.get(0, 1) + m.get(0, 0);
        int n_1 = m.get(1, 0) + m.get(0, 0);
        int n11 = m.get(1, 1);
        
        if (n == 0) { // no data
            result.resultCode = 0;
            return result;
        }
        
        if (n1_ * ((double) n_1) / ((double) n) > n11) {
            result.resultCode = 1;
            return result; // negative correlation
        }
        
        double constant = MathUtils.factorial(n1_) 
                + MathUtils.factorial(n - n1_)
                + MathUtils.factorial(n_1)
                + MathUtils.factorial(n - n_1)
                - MathUtils.factorial(n);
        
        double variable = - MathUtils.factorial(m.get(0, 0))
                - MathUtils.factorial(m.get(0, 1))
                - MathUtils.factorial(m.get(1, 0))
                - MathUtils.factorial(m.get(1, 1));
        
        double lnHyper = constant + variable;
        double hyper = 0;
        double clyq1 = 0.0;
        clyq1 = Math.exp(lnHyper);
        
        int m00 = m.get(0, 0);
        int m01 = m.get(0, 1);
        int m10 = m.get(1, 0);
        int m11 = m.get(1, 1);
        
        int i1, s1;
        
        if (n1_ < n_1) {
            s1 = n1_;
        } else {
            s1 = n_1;
        }
        i1 = 0;
        
        do {
            i1++;
            m00++; m11++; m01--; m10--;
            variable = -MathUtils.factorial(m00)
                    - MathUtils.factorial(m01)
                    - MathUtils.factorial(m10)
                    - MathUtils.factorial(m11);
            
            lnHyper = constant + variable;
            hyper = Math.exp(lnHyper);
            clyq1 += hyper;
        } while ((i1 <= s1 - n11) && (hyper != 0));
        
        clyq1 = clyq1 > 1.0 ? 1.0 : clyq1;
        
        if (clyq1 > fisherThreshold) {
            // independent expertio
            // fisher check is positive
            result.resultCode = 2;
            return result;
        }
        
        // Yule criteria
        
        double freq = 4;
        double urQ = 0.0;
        
        if (m.get(0, 0) <= freq 
                || m.get(0, 1) <= freq 
                || m.get(1, 0) <= freq 
                || m.get(1, 1) <= freq) {
            
            result.yuleValue = 1.0;
            result.resultCode = 3; // Positive correlation
            return result;
        }
        
        m00 = m.get(0, 0);
        m01 = m.get(0, 1);
        m10 = m.get(1, 0);
        m11 = m.get(1, 1);
        
        double criteryQ = 0;
        double q = 0;
        double lambda = 0;
        
        NormalDistribution normal = new NormalDistributionImpl(0, 1);

        if (m01 == 0 || m10 == 0) {
            result.yuleValue = 1.;
            result.resultCode = 3;
            return result;
        } else if (m11 * m00 == m01 * m10) {
            result.yuleValue= .0;
            result.resultCode = 2;
            return result;
        } else if (m11 == 0 || m00 == 0) {
            result.yuleValue = -1.;
            result.resultCode = 1;
            return result;
        } else {
            q = ((double) (m00 * m11 - m01 * m10)) /
                    ((double)(m00 * m11 + m01 * m10));
            lambda = normal.inverseCumulativeProbability (1. - 0.5*fisherThreshold);
            criteryQ = q - 0.5 * lambda * Math.abs(1 - q * q) * 
                    Math.sqrt(1. / m00 + 1. / m01 + 1. / m10 + 1. / m11);
        }
        result.yuleValue = criteryQ;
        if (criteryQ < urQ) {
            result.resultCode = 2;
        } else {
            result.resultCode = 3;
        }
        
        return result;
    }
}
