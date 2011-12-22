package org.munta.model;

import java.io.Serializable;
import org.munta.projectengine.serializer.xml.XmlObject;
import org.munta.utils.NotificationHashMap;

@XmlObject(name="Regularities", map=true)
public class RegularityCollection
        extends NotificationHashMap<String, Regularity>
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
        this.put(String.format("R%d", this.size() + 1), r);
    }
}
