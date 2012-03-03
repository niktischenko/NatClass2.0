package org.munta.gui;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.model.Regularity;

public class ClassesDetailsViewModel
        extends AbstractCollectionViewModel<Attribute>
        implements ListSelectionListener {
    
    private AnalysisColorer colorer;
    private ClassesViewModel classesViewModel;

    public ClassesDetailsViewModel(AnalysisColorer colorer, ClassesViewModel entityViewModel) {
        super();
        this.classesViewModel = entityViewModel;
        this.colorer = colorer;
    }

    @Override
    public Object getElementAt(int i) {
        Attribute attr = getModelObjectAt(i);
        return new ListItem(attr.toString());
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (lse.getValueIsAdjusting()) {
            return;
        }

        if (!JList.class.isAssignableFrom(lse.getSource().getClass())) {
            return;
        }
        JList jList = (JList) lse.getSource();
        int index = jList.getSelectedIndex();

        this.elementRemoved(null);

        if (index == -1) {
            return;
        }

        Entity e = classesViewModel.getModelObjectAt(index);
        if (e != null) {
            for (Object o : e.getAttributes()) {
                this.elementAdded(o);
            }
        }
    }   
}
