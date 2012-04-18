package org.munta.model;

import java.io.Serializable;
import org.munta.gui.SettingsDialogItem;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.projectengine.serializer.xml.XmlProperty;

@XmlObject(name = "Properties")
public final class GlobalProperties implements Serializable {

    /// Block 1 start
    /// Block 1 end
    /// Block 2 start
    @XmlProperty(name = "ProbabilityThreshold")
    private double probabilityThreshold;
    @XmlProperty(name = "FisherThreshold")
    private double fisherThreshold;
    @XmlProperty(name = "YuleThreshold")
    private double yuleThreshold;
    @XmlProperty(name = "UseIntermediateResults")
    private boolean useIntermediateResults;
    @XmlProperty(name = "RecursionDeep")
    private int recursionDeep;

    @SettingsDialogItem(displayName = "Yule Threshold")
    public double getYuleThreshold() {
        return yuleThreshold;
    }

    public void setYuleThreshold(double yuleThreshold) {
        this.yuleThreshold = yuleThreshold;
    }

    @SettingsDialogItem(displayName = "Fisher Threshold")
    public double getFisherThreshold() {
        return fisherThreshold;
    }

    public void setFisherThreshold(double fisherThreshold) {
        this.fisherThreshold = fisherThreshold;
    }

    @SettingsDialogItem(displayName = "Probability Threshold")
    public double getProbabilityThreshold() {
        return probabilityThreshold;
    }

    public void setProbabilityThreshold(double value) {
        probabilityThreshold = value;
    }
    @XmlProperty(name = "MinLength")
    private int minLength;

    @SettingsDialogItem(displayName = "Minimum chain length")
    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int value) {
        minLength = value;
    }

    @SettingsDialogItem(displayName = "Guaranteed recursion deep")
    public int getRecursionDeep() {
        return recursionDeep;
    }

    public void setRecursionDeep(int recursionDeep) {
        this.recursionDeep = recursionDeep;
    }

    @SettingsDialogItem(displayName = "Use intermediate chains")
    public boolean getUseIntermediateResults() {
        return useIntermediateResults;
    }

    public void setUseIntermediateResults(boolean value) {
        useIntermediateResults = value;
    }

    /// Block 2 end
    public GlobalProperties() {
        clear();
    }

    public void clear() {
        probabilityThreshold = 0.75;
        minLength = 2;
        fisherThreshold = 0.001;
        yuleThreshold = 0.9;
        useIntermediateResults = false;
        recursionDeep = 2;
    }

    public void set(GlobalProperties g) {
        probabilityThreshold = g.probabilityThreshold;
        minLength = g.minLength;
        fisherThreshold = g.fisherThreshold;
        yuleThreshold = g.yuleThreshold;
        useIntermediateResults = g.useIntermediateResults;
        recursionDeep = g.recursionDeep;
    }
}
