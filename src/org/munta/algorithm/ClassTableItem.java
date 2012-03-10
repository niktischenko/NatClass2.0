package org.munta.algorithm;

public class ClassTableItem {
    private boolean on;
    private double gamma;

    public ClassTableItem() {
        on = false;
        gamma = 0;
    }

    public ClassTableItem(boolean on, double gamma) {
        this.on = on;
        this.gamma = gamma;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
