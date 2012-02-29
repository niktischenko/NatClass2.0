package org.munta.gui;

import java.awt.Color;

public class ListItem {

    private Color color;
    private String value;
    private Boolean isBold;

    public ListItem(String s) {
        this(null, s, false);
    }
    
    public ListItem(Color c, String s) {
        this(c, s, false);
    }
    
    public ListItem(Color c, String s, Boolean isBold) {
        color = c;
        value = s;
        this.isBold = isBold;
    }

    public Color getColor() {
        return color;
    }

    public String getValue() {
        return value;
    }
    
    public Boolean getIsBold() {
        return isBold;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
