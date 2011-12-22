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
