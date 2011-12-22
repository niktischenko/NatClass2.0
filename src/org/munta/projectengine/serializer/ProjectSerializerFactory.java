package org.munta.projectengine.serializer;

import org.munta.projectengine.serializer.xml.XmlSerializer;

public class ProjectSerializerFactory {
    public static final int TYPE_XML = 0;
    public static final int TYPE_BINARY = 1;
    
    public static IProjectSerializer createSerializer(Class objectType) throws SerializerException {
        return createSerializer(objectType, TYPE_XML);
    }
    
    public static IProjectSerializer createSerializer(Class objectType, int type) throws SerializerException {
        switch (type) {
            case ProjectSerializerFactory.TYPE_BINARY:
                return new ObjectSerializer();
            case ProjectSerializerFactory.TYPE_XML:
                return new XmlSerializer(objectType);
            default:
                throw new SerializerException("Unknown serializer type");
        }
    }
}
