package org.munta.projectengine.serializer;

import org.munta.projectengine.serializer.IProjectSerializer;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public final class JABXSerializer implements IProjectSerializer {
    
    private JAXBContext context;
    
    public JABXSerializer(Class objectType) throws SerializerException {
        try {
            context = JAXBContext.newInstance(objectType);
        } catch (JAXBException ex) {
            Logger.getLogger(JABXSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        } 
    }

    @Override
    public void serializeProjectObject(Object o, OutputStream w) throws SerializerException {
        try {
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(o, w);
        } catch (JAXBException ex) {
            Logger.getLogger(JABXSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }

    @Override
    public Object deserializeProjectObject(InputStream r) throws SerializerException {
        try {
            Unmarshaller um = context.createUnmarshaller();
            return um.unmarshal(r);
        } catch (JAXBException ex) {
            Logger.getLogger(JABXSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }
}
