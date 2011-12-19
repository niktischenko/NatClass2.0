package org.munta.model;

import java.util.HashSet;

public class EntitiesCollection extends HashSet<Entity> {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(Entity entity : this) {
            sb.append(entity.toString());
        }
        sb.append("]");
        return sb.toString();
    }
    
}
