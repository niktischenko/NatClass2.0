package org.munta.gui;

import java.awt.Color;
import java.util.Map.Entry;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.munta.model.Regularity;

public class RegularityDetailsViewModel
        extends AbstractCollectionViewModel<Object>
        implements ListSelectionListener {

    private AnalysisColorer colorer;
    private RegularityViewModel regularityViewModel;
    private Regularity selectedRegularity = null;

    public RegularityDetailsViewModel(AnalysisColorer colorer, RegularityViewModel regularityViewModel) {
        this.regularityViewModel = regularityViewModel;
        this.colorer = colorer;
    }

    @Override
    public Object getElementAt(int i) {
        return getModelObjectAt(i);
    }
    
    private Object getColorTarget(Object o) {
        if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS) {
            return new ListItem(colorer.getTargetColor(), o.toString(), true);
        } else {
            return o;
        }
    }

    private Object getColorCondition(Object o) {
        if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS) {
            return new ListItem(colorer.getConditionColor(), o.toString(), true);
        } else {
            return o;
        }
    }

    private Object getColorContext(Object o) {
        if(colorer.getMode() == AnalysisColorer.REGULARITY_ANALYSIS) {
            return new ListItem(colorer.getContextColor(), o.toString(), true);
        } else {
            return o;
        }
    }
    
    private void updateSelectedRegularity(Regularity r) {
        this.elementRemoved(null);
        
        if(r == null)
            return;
        
        this.elementAdded(new ListItem(Color.BLUE, "Target attribute:", true));
        this.elementAdded(getColorTarget(r.getTarget()));
        this.elementAdded("===========");

        this.elementAdded(new ListItem(Color.BLUE, "Conditions:", true));
        for (Object o : r.getConditions()) {
            this.elementAdded(getColorCondition(o));
        }
        this.elementAdded("===========");

        this.elementAdded(new ListItem(Color.BLUE, "Context:", true));
        for (Object o : r.getContext()) {
            this.elementAdded(getColorContext(o));
        }
    }

    @Override
    public void redrawList() {
        super.redrawList();
        updateSelectedRegularity(selectedRegularity);
    }
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (lse.getValueIsAdjusting()) {
            return;
        }
        if (!JList.class.isAssignableFrom(lse.getSource().getClass())) {
            return;
        }
        
        selectedRegularity = null;
        
        JList jList = (JList) lse.getSource();
        int index = jList.getSelectedIndex();
        if (index == -1) {
            return;
        }

        Entry<String, Regularity> e = regularityViewModel.getModelObjectAt(index);
        if (e != null) {
            Regularity r = e.getValue();
            selectedRegularity = r;
            updateSelectedRegularity(r);
        }
    }
}
