package org.munta.projectengine.serializer.xml;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.munta.projectengine.serializer.IMapper;

class Mapper implements IMapper {
    
    private Set<Class> registeredClasses;

    public Mapper() {
        registeredClasses = new HashSet<Class>();
    }

    @Override
    public void registerClass(Class clazz) {
        registeredClasses.add(clazz);
    }

    @Override
    public Collection<Class> getRegisteredClasses() {
        return registeredClasses;
    }
    
}
