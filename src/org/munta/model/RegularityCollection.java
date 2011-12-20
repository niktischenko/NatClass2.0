package org.munta.model;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegularityCollection extends HashMap<String, Regularity> {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for(String regularityName : this.keySet()) {
            sb.append(this.get(regularityName).toString());
        }
        sb.append("\n]");
        return sb.toString();
    }
    
}
