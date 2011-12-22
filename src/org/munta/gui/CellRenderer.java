package org.munta.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class CellRenderer extends JLabel
        implements ListCellRenderer {

    private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    
    public CellRenderer() {
        // Don't paint behind the component
        setOpaque(true);
    }

    // Set the attributes of the
    //class and return a reference
    @Override
    public Component getListCellRendererComponent(JList list,
            Object value, // value to display
            int index, // cell index
            boolean iss, // is selected
            boolean chf) // cell has focus?
    {
        if (value instanceof ListItem) {
            // Set the text and
            //background color for rendering
            setText(((ListItem) value).getValue());

            // Set a border if the
            // list item is selected
            if (iss) {
                setForeground(list.getSelectionForeground());
                setBackground(list.getSelectionBackground());
            } else {
                Color c = ((ListItem) value).getColor();
                if(c == null) {
                    c = list.getForeground();
                }
                setForeground(c);
                setBackground(list.getBackground());
            }

            return this;
        } else {
            return defaultRenderer.getListCellRendererComponent(list, value, index, iss, chf);
        }
    }
}
