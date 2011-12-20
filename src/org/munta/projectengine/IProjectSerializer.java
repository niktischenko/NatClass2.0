package org.munta.projectengine;

import org.munta.projectengine.SerializerException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IProjectSerializer {
    public void serializeProjectObject(Object o, OutputStream w) throws SerializerException;
    public Object deserializeProjectObject(InputStream r) throws SerializerException;
}
