package org.munta.projectengine.serializer;

import java.util.Collection;

public interface IMapper {
    public void registerClass(Class clazz);
    public Collection<Class> getRegisteredClasses();
}
