package org.munta.model;

import java.io.Serializable;

public class GlobalProperties implements Serializable {

    public int test = 1123;
    
    /// Block 1 start
    /// Block 1 end
    
    /// Block 2 start
    private double probabilityThreshold;
    public double getProbabilityThreshold() {
        return probabilityThreshold;
    }
    /// Block 2 end

    public GlobalProperties() {
        clear();
    }
    
    public void clear() {
        probabilityThreshold = 0.8;
    }
}
