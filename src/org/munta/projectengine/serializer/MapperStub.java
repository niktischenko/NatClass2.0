package org.munta.projectengine.serializer;

import java.util.Collection;
import java.util.HashSet;

public class MapperStub implements IMapper {

    @Override
    public void registerClass(Class clazz) {
    }

    @Override
    public Collection<Class> getRegisteredClasses() {
        return new HashSet<Class>();
    }
    
}
