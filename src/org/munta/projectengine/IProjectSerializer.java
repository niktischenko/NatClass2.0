package org.munta.projectengine;

import java.io.InputStream;
import java.io.OutputStream;

interface IProjectSerializer {
    public void serializeProjectObject(Object o, OutputStream w) throws SerializerException;
    public Object deserializeProjectObject(InputStream r) throws SerializerException;
}
