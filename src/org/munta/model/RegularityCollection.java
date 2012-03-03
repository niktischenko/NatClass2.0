package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.utils.NotificationMap;

@XmlObject(name="Regularities", map=true)
public class RegularityCollection
        extends NotificationMap<String, Regularity>
        implements Serializable {

    public RegularityCollection() {
    }

    public RegularityCollection(RegularityCollection collection) {
        super(collection);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (String regularityName : this.keySet()) {
            sb.append(this.get(regularityName).toString());
        }
        sb.append("\n]");
        return sb.toString();
    }
    
    public void add(Regularity r) {
        if (values().contains(r)) {
            return;
        }
        this.put(String.format("R%d", this.size() + 1), r);
    }
}
