package org.munta.gui;

import java.util.Map.Entry;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;

public class RegularityViewModel
        extends AbstractCollectionViewModel<Entry<String, Regularity>> {

    private AnalysisColorer colorer;
    
    public RegularityViewModel(AnalysisColorer colorer, RegularityCollection collection) {
        super(collection);
        this.colorer = colorer;
    }

    @Override
    public Object getElementAt(int i) {
        return getModelObjectAt(i).getKey();
    }
}
