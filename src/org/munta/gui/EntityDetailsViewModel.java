package org.munta.gui;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.munta.model.Entity;
import org.munta.model.Attribute;
import org.munta.model.Regularity;

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
        Attribute attr = getModelObjectAt(i);
        if(attr == null)
            return null;
        
        if (colorer.getMode() == AnalysisColorer.ENTITY_ANALYSIS) {
            if (colorer.getEntity().getAttributes().contains(attr)) {
                return new ListItem(colorer.getPositiveColor(), attr.toString());
            }
        } else if (colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS) {
            
            Regularity r = colorer.getRegularity();
            if(r.getConditions().contains(attr)) {
                return new ListItem(colorer.getConditionColor(), attr.toString(), true);
            }
            
            //if(r.getContext().contains(attr)) {
            //    return new ListItem(colorer.getContextColor(), attr.toString(), true);
            //}
            
            if(attr.getName().equals(r.getTarget().getName())) {
                if(attr.equals(r.getTarget())) {
                    return new ListItem(colorer.getTargetColor(), attr.toString(), true);
                } else {
                    return new ListItem(colorer.getNegativeColor(), attr.toString(), true);
                }
            }
        }

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

        Entity e = entityViewModel.getModelObjectAt(index);
        if (e != null) {
            for (Object o : e.getAttributes()) {
                this.elementAdded(o);
            }
        }
    }
}
