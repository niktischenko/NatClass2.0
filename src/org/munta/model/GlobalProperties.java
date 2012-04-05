package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.projectengine.serializer.xml.XmlProperty;

@XmlObject(name="Properties")
public class GlobalProperties implements Serializable {

    public int test = 1123;
    
    /// Block 1 start
    /// Block 1 end
    
    /// Block 2 start
    @XmlProperty(name="ProbabilityThreshold")
    private double probabilityThreshold;
    
    @XmlProperty(name="FisherThreshold")
    private double fisherThreshold;
    
    @XmlProperty(name="YuleThreshold")
    private double yuleThreshold;

    public double getYuleThreshold() {
        return yuleThreshold;
    }

    public void setYuleThreshold(double yuleThreshold) {
        this.yuleThreshold = yuleThreshold;
    }

    public double getFisherThreshold() {
        return fisherThreshold;
    }

    public void setFisherThreshold(double fisherThreshold) {
        this.fisherThreshold = fisherThreshold;
    }
    
    public double getProbabilityThreshold() {
        return probabilityThreshold;
    }
    
    @XmlProperty(name="MinLength")
    private int minLength;
    
    public double getMinLength() {
        return minLength;
    }
    
    @XmlProperty(name="UseIntermediateResults")
    private Boolean useIntermediateResults;
    
    public Boolean getUseIntermediateResults() {
        return useIntermediateResults;
    }
    /// Block 2 end

    public GlobalProperties() {
        clear();
    }
    
    public final void clear() {
        probabilityThreshold = 0.8;
        minLength = 2;
        fisherThreshold = 0.05;
        yuleThreshold = 0.9;
        useIntermediateResults = false;
    }
    
    public void set(GlobalProperties g) {
        probabilityThreshold = g.probabilityThreshold;
        minLength = g.minLength;
        useIntermediateResults = g.useIntermediateResults;
    }
}
