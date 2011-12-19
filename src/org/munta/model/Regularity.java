package org.munta.model;

public class Regularity {

    private AttributeCollection conditions;
    private AttributeCollection context;
    private Attribute target;

    public Regularity() {
        conditions = new AttributeCollection();
        context = new AttributeCollection();
        target = new Attribute();
    }

    public AttributeCollection getContext() {
        return context;
    }

    public AttributeCollection getContitions() {
        return conditions;
    }

    public Attribute getTarget() {
        return target;
    }

    public void setTarget(Attribute target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return String.format(
                "%s <- {%s} & {%s}",
                target.toString(),
                conditions.toString(),
                context.toString());
    }
}
