package org.munta.projectengine.serializer;

import java.io.InputStream;
import java.io.OutputStream;

public interface IProjectSerializer {
    public void serializeProjectObject(Object o, OutputStream w) throws SerializerException;
    public Object deserializeProjectObject(InputStream r) throws SerializerException;
}
