package org.munta.gui;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import org.munta.utils.SpringUtilities;

public class SettingsDialog extends JDialog {
    
    private Object properiesObject;
    private Boolean isOK = false;
    
    public Boolean getDialogResult() {
        return isOK;
    }
    
    private AbstractAction okAction = new AbstractAction("OK") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            isOK = true;
            SettingsDialog.this.setVisible(false);
        }
    };
    
    private AbstractAction cancelAction = new AbstractAction("Cancel") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            SettingsDialog.this.setVisible(false);
        }
    };
    
    private JComponent createMemberEditor(final Method method) {
        JComponent component = null;
        final Class type = method.getReturnType();
        try {
            final Object o = method.invoke(properiesObject, (Object[]) null);
            String setMethodName = "set" + method.getName().substring(3);
            
            Method tmpSetMethod = null;
            try {
                tmpSetMethod = method.getDeclaringClass().getDeclaredMethod(setMethodName,
                        new Class[] {method.getReturnType()});
            } catch (NoSuchMethodException ex) { }
            final Method setMethod = tmpSetMethod;
        
            if(String.class.isAssignableFrom(type) ||
               double.class.isAssignableFrom(type) ||
               int.class.isAssignableFrom(type)) {
                component = new JTextField(10);
                final JTextField textBox = (JTextField) component;
                textBox.setText(o.toString());
                textBox.addFocusListener(new FocusAdapter() {

                    @Override
                    public void focusLost(FocusEvent fe) {
                        try {
                            String text = ((JTextField)fe.getSource()).getText();
                            if(String.class.isAssignableFrom(type)) {
                                setMethod.invoke(properiesObject, new Object[] {text});
                            } else if(double.class.isAssignableFrom(type)) {
                                setMethod.invoke(properiesObject, new Object[] {(double)Double.valueOf(text)});
                            } else if(int.class.isAssignableFrom(type)) {
                                setMethod.invoke(properiesObject, new Object[] {(int)Integer.valueOf(text)});
                            }
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(SettingsDialog.this, "Invalid input range");
                            try {
                                Object no = method.invoke(properiesObject, (Object[]) null);
                                textBox.setText(no.toString());
                            } catch (IllegalAccessException ex1) {
                                Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex1);
                            } catch (IllegalArgumentException ex1) {
                                Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex1);
                            } catch (InvocationTargetException ex1) {
                                Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        } catch (InvocationTargetException ex) {
                            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        super.focusLost(fe);
                    }
                    
                });
            } else if(type == boolean.class) {
                component = new JCheckBox();
                final JCheckBox cb = (JCheckBox) component;
                cb.setSelected((Boolean)o);
                cb.addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent ie) {
                        try {
                            setMethod.invoke(properiesObject, new Object[] {cb.isSelected()});
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex) {
                            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvocationTargetException ex) {
                            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
            
            if(component != null) {
                component.setEnabled(setMethod != null);
            } else {
                Logger.getLogger("No component");
            }
            
            
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return component;
    }
    
    private void init() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new SpringLayout());    
        
        int count = 0;
        Method[] methods = properiesObject.getClass().getDeclaredMethods();
        for(Method method : methods) {
            if(!method.getName().startsWith("get")) 
                continue;
            
            SettingsDialogItem item = method.getAnnotation(SettingsDialogItem.class);
            if(item != null) {
                JComponent editor = createMemberEditor(method);
                if(editor == null)
                    continue;
                
                JLabel l = new JLabel(item.displayName(), JLabel.TRAILING);
                add(l);
                    
                l.setLabelFor(editor);
                add(editor);
                
                count++;
            }
        }
        
        JButton button;
        button = new JButton(okAction);
        button.requestFocus();
        add(button);
        button = new JButton(cancelAction);
        add(button);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(contentPane,
                                        1 + count, 2,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 2);  //xPad, yPad
    }
    
    public SettingsDialog(Window parent, Object properiesObject) {
        super(parent, "Properties", ModalityType.APPLICATION_MODAL);
        
        this.properiesObject = properiesObject;
        
        init();
        
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }
}
