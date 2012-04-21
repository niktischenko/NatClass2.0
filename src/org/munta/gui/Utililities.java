package org.munta.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class Utililities {
    public static Icon getIconFromResource(String iconName) {
        String iconPath = String.format("images/%s.png", iconName);
        return new ImageIcon(MainFrame.class.getResource(iconPath));
    }
}
