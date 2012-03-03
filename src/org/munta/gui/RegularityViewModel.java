package org.munta.gui;

import java.util.Map.Entry;
import org.munta.model.Attribute;
import org.munta.model.Entity;
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
    protected Boolean onFilter(Entry<String, Regularity> obj) {
        if(colorer.getMode() == AnalysisColorer.CLASS_ANALYSIS && colorer.isClassAnalysisReady()) {
            Entity idealClass = colorer.getIdealClass();
            Regularity r = obj.getValue();
            
            if(!idealClass.checkAttribute(r.getTarget())) {
                return false;
            }
            
            for(Attribute a : r.getConditions()) {
                if(!idealClass.checkAttribute(a)) {
                    return false;
                }
            }
            
            return true;
        } else {
            return super.onFilter(obj);
        }
    }

    @Override
    public Object getElementAt(int i) {
        return getModelObjectAt(i).getKey();
    }
}
