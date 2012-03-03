package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.projectengine.serializer.xml.XmlProperty;

@XmlObject(name = "Regularity")
public class Regularity implements Serializable {

    @XmlProperty(name = "Conditions", collection = true, propertyClass = AttributeCollection.class)
    private AttributeCollection conditions;
    @XmlProperty(name = "Context", collection = true, propertyClass = AttributeCollection.class)
    private AttributeCollection context;
    @XmlProperty(name = "Target")
    private Attribute target;

    public Regularity() {
        conditions = new AttributeCollection();
        context = new AttributeCollection();
        target = new Attribute();
    }

    public Regularity(Regularity regularity) {
        this.conditions = new AttributeCollection(regularity.conditions);
        this.context = new AttributeCollection(regularity.context);
        this.target = new Attribute(target);
    }

    public AttributeCollection getContext() {
        return context;
    }

    public AttributeCollection getConditions() {
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Regularity)) {
            return false;
        }

        Regularity r = (Regularity) obj;

        return this.target.equals(r.target) && this.conditions.equals(r.conditions) && this.context.equals(r.context);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.conditions != null ? this.conditions.hashCode() : 0);
        hash = 97 * hash + (this.context != null ? this.context.hashCode() : 0);
        hash = 97 * hash + (this.target != null ? this.target.hashCode() : 0);
        return hash;
    }
}
