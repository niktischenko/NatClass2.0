package org.munta.algorithm;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

class FisherYuleAlgorithm {

    public static int RESULT_NOT_PASSED = 0;
    public static int RESULT_PASSED_AS_CONDITION = 1;
    private static double factorValues[];

    static {
        factorValues = new double[11];
        factorValues[0] = 0.;
        factorValues[1] = 0.;
        factorValues[2] = 0.692;
        factorValues[3] = 1.791;
        factorValues[4] = 3.177;
        factorValues[5] = 4.787;
        factorValues[6] = 6.579;
        factorValues[7] = 8.525;
        factorValues[8] = 10.604;
        factorValues[9] = 12.802;
        factorValues[10] = 15.104;
    }

    private static class FisherYuleResult {

        private double fisherValue;
        private double yuleValue;
        private int resultCode;
    }

    public static int checkFisherAndYuleCriteria(
            ProbabilityMatrix matrix,
            double fisherThreshold,
            double yuleThreshold) {
        try {
            FisherYuleResult result = checkFisherAndYuleAlgorithm(matrix, fisherThreshold, yuleThreshold);
//            System.err.println("Fisher value: "+result.fisherValue+"\nYule value: "+result.yuleValue);
            switch (result.resultCode) {
                case 1:
                case 2:
                    return RESULT_NOT_PASSED;
                case 3:
                    return RESULT_PASSED_AS_CONDITION;
                default:
//                    System.err.println("NO DATA");
                    return RESULT_NOT_PASSED;
            }

        } catch (MathException ex) {
            Logger.getLogger(FisherYuleAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return RESULT_NOT_PASSED;

    }

    private static FisherYuleResult checkFisherAndYuleAlgorithm(ProbabilityMatrix m, double fisherThreshold, double yuleThreshold) throws MathException {
        FisherYuleResult result = new FisherYuleResult();
        long n = 0, n1_ = 0, n_1 = 0, n11 = 0;
        long m00 = 0, m01 = 0, m10 = 0, m11 = 0;
        double constant = 0, variable = 0;
        double inGiper = 0, giper = 0;
        long i1 = 0, s1 = 0;
        double fisherValue = 0;

        n = m.get(0, 0) + m.get(1, 0) + m.get(0, 1) + m.get(1, 1);
        n1_ = m.get(0, 1) + m.get(0, 0);
        n_1 = m.get(1, 0) + m.get(0, 0);
        n11 = m.get(0, 0);

        if (n == 0) {
            result.resultCode = 0;
//            System.err.println("CASE 1");
            return result;
        }

        //даже если будет показана зависимость, т.е. отсутствие равенства
        //проверяем: может неравенство в другую сторогу (с)
        if ((n1_ * (((double) n_1) / ((double) n))) > n11) {
            result.resultCode = 1;
//            System.err.println("CASE 2");
            return result; // Negative correlation
        }

        constant = factor(n1_) + factor(n - n1_) + factor(n_1) + factor(n - n_1) - factor(n);
        variable = -factor(m.get(0, 0)) - factor(m.get(0, 1)) - factor(m.get(1, 0)) - factor(m.get(1, 1));

        inGiper = constant + variable;
        fisherValue = Math.exp(inGiper);
        m00 = m.get(0, 0);
        m01 = m.get(0, 1);
        m10 = m.get(1, 0);
        m11 = m.get(1, 1);

        if (n1_ < n_1) {
            s1 = n1_;
        } else {
            s1 = n_1;
        }
        i1 = 0;

        do {
            i1++;
            m00++;
            m11++;
            m01--;
            m10--;

            variable = -factor(m00) - factor(m01) - factor(m10) - factor(m11);
            inGiper = constant + variable;
            giper = Math.exp(inGiper);
            fisherValue += giper;
        } while ((i1 <= s1 - n11) && (giper != 0));

        if (fisherValue > 1.0) {
            fisherValue = 1.0;
        }

        result.fisherValue = fisherValue;

        if (fisherValue > fisherThreshold) {
            result.resultCode = 2;
//            System.err.println("CASE 3");
            return result; // Independent expertion
        }

        // fisher is OK
        // check Yule criterion
        double freq = 4;
        if (m.get(0, 0) > freq && m.get(0, 1) > freq && m.get(1, 0) > freq && m.get(1, 1) > freq) {
            m00 = m.get(0, 0);
            m01 = m.get(0, 1);
            m10 = m.get(1, 0);
            m11 = m.get(1, 1);

            double kritQ = 0., q_ = 0., lambda = 0.;
            NormalDistribution normal = new NormalDistributionImpl(0, 1);

            if (m01 == 0 || m10 == 0) {
                result.yuleValue = 1.;
                result.resultCode = 3;
//                System.err.println("CASE 4");
                return result;
            } else if (m11 * m00 == m01 * m10) {
                result.yuleValue = 0;
                result.resultCode = 2;
//                System.err.println("CASE 5");
                return result;
            } else if (m11 == 0 || m00 == 0) {
                result.yuleValue = -1;
                result.resultCode = 1;
//                System.err.println("CASE 6");
                return result;
            } else {
                q_ = ((double) (m00 * m11 - m01 * m10)) / ((double) (m00 * m11 + m01 * m10));
                lambda = normal.inverseCumulativeProbability(1 - 0.5 * fisherThreshold);
                kritQ = q_ - 0.5 * lambda * Math.abs(1 - q_ * q_) * Math.sqrt(1.0 / m00 + 1. / m01 + 1. / m10 + 1. / m11);
            }
            
            result.yuleValue = kritQ;
            if (kritQ < yuleThreshold) {
                result.resultCode = 2;
//                System.err.println("CASE 7");
                return result;
            }
        } else {
            result.yuleValue = 1;
        }
        result.resultCode = 3;
//        System.err.println("CASE 8");
        return result;
    }

    private static double factor(long var) {
        if (var <= 0) {
            return 0.0;
        }
        double ai;
        if (var < factorValues.length) {
            return factorValues[(int) var];
        }
        ai = (double) var;
        ai = ai * Math.log(ai) + 0.5 * (1.8374 + Math.log(ai)) - ai + 1. / (12. * ai) - 1.0 / (360. * ai * ai);
        return ai;
    }
}
