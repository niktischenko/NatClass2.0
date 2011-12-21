package org.munta.gui;

import java.util.Map.Entry;
import org.munta.model.Regularity;
import org.munta.model.RegularityCollection;

public class RegularityViewModel
        extends AbstractCollectionViewModel<Entry<String, Regularity>> {

    public RegularityViewModel(RegularityCollection collection) {
        super(collection);
    }

    @Override
    public Object getElementAt(int i) {
        return getModelObjectAt(i).getKey();
    }
}
