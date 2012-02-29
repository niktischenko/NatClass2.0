package org.munta.gui;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.munta.model.Entity;
import org.munta.model.Attribute;

public class EntityDetailsViewModel
        extends AbstractCollectionViewModel<Attribute>
        implements ListSelectionListener {

    private AnalysisColorer colorer;
    private EntityViewModel entityViewModel;

    public EntityDetailsViewModel(AnalysisColorer colorer, EntityViewModel entityViewModel) {
        super();
        this.entityViewModel = entityViewModel;
        this.colorer = colorer;
    }

    @Override
    public Object getElementAt(int i) {

        if (colorer.getMode() == AnalysisColorer.ENTITY_ANALYSIS) {
            Attribute attr = getModelObjectAt(i);
            if(attr == null)
                return null;
            if (colorer.getEntity().getAttributes().contains(attr)) {
                return new ListItem(colorer.getPositiveColor(), getModelObjectAt(i).toString());
            }
        }

        return new ListItem(getModelObjectAt(i).toString());
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

        Entity e = entityViewModel.getModelObjectAt(index);
        if (e != null) {
            for (Object o : e.getAttributes()) {
                this.elementAdded(o);
            }
        }
    }
}
