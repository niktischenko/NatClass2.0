package org.munta.model;

import java.util.HashMap;

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
