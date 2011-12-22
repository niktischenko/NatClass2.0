package org.munta.projectengine.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectSerializer implements IProjectSerializer {

    @Override
    public void serializeProjectObject(Object o, OutputStream w) throws SerializerException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(w);
            oos.writeObject(o);
        } catch (IOException ex) {
            Logger.getLogger(ObjectSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }

    @Override
    public Object deserializeProjectObject(InputStream r) throws SerializerException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(r);
            Object o = ois.readObject();
            return o;
        } catch (Exception ex) {
            Logger.getLogger(ObjectSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }

    @Override
    public IMapper getMapper() {
        throw new UnsupportedOperationException("Method is not supported on this serializer.");
    }
}
