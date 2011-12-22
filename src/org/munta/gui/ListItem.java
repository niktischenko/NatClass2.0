package org.munta.gui;

import java.awt.Color;

public class ListItem {

    private Color color;
    private String value;

    public ListItem(String s) {
        this(null, s);
    }
    
    public ListItem(Color c, String s) {
        color = c;
        value = s;
    }

    public Color getColor() {
        return color;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
