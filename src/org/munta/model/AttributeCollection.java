/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.munta.model;

import java.util.HashSet;

/**
 *
 * @author pavel_pro
 */
public class AttributeCollection extends HashSet<Attribute> {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(Attribute attr : this) {
            sb.append(attr.toString());
        }
        sb.append("]");
        return sb.toString();
    }
    
}
