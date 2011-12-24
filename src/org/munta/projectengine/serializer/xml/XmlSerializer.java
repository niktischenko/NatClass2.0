package org.munta.projectengine.serializer.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.munta.projectengine.serializer.IMapper;
import org.munta.projectengine.serializer.IProjectSerializer;
import org.munta.projectengine.serializer.SerializerException;
import org.w3c.dom.Document;

public class XmlSerializer implements IProjectSerializer {

    private XmlObjectSerializerInternal serializer;
    private static IMapper mapper;

    public XmlSerializer(Class objectType) {
        mapper = new XmlDynMapperImpl(objectType);
        serializer = new XmlObjectSerializerInternal(mapper);
    }

    @Override
    public void serializeProjectObject(Object o, OutputStream w) throws SerializerException {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Source source = new DOMSource(serializer.serialize(o));
            Result output = new StreamResult(w);
            transformer.transform(source, output);

        } catch (TransformerException ex) {
            Logger.getLogger(XmlSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }

    @Override
    public Object deserializeProjectObject(InputStream r) throws SerializerException {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(r);
            return serializer.deserialize(doc);
        } catch (Exception ex) {
            Logger.getLogger(XmlSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }

    @Override
    public IMapper getMapper() {
        return mapper;
    }
}
