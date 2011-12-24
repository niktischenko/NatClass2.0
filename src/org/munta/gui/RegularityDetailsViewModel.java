package org.munta.gui;

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

    public RegularityDetailsViewModel(AnalysisColorer colorer, RegularityViewModel regularityViewModel) {
        this.regularityViewModel = regularityViewModel;
        this.colorer = colorer;
    }

    @Override
    public Object getElementAt(int i) {
        return getModelObjectAt(i);
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

        Entry<String, Regularity> e = regularityViewModel.getModelObjectAt(index);
        if (e != null) {
            Regularity r = e.getValue();

            this.elementAdded("Target attribute:");
            this.elementAdded(r.getTarget());
            this.elementAdded(" ");

            this.elementAdded("Conditions:");
            for (Object o : r.getConditions()) {
                this.elementAdded(o);
            }
            this.elementAdded(" ");

            this.elementAdded("Context:");
            for (Object o : r.getContext()) {
                this.elementAdded(o);
            }
        }
    }
}
