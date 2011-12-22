package org.munta.projectengine.serializer.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

public class XMLSerializer implements IProjectSerializer {

    private InnerXMLObjectSerializer serializer;
    private static IMapper mapper = new Mapper();

    public XMLSerializer() {
        serializer = new InnerXMLObjectSerializer(mapper);
    }

    @Override
    public void serializeProjectObject(Object o, OutputStream w) throws SerializerException {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            serializer.setDocument(doc);
            doc.appendChild(serializer.serialize(o));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Source source = new DOMSource(doc);
            Result output = new StreamResult(w);
            transformer.transform(source, output);
        } catch (TransformerException ex) {
            Logger.getLogger(XMLSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }

    @Override
    public Object deserializeProjectObject(InputStream r) throws SerializerException {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(r);
            return serializer.deserialize(doc.getDocumentElement());
        } catch (Exception ex) {
            Logger.getLogger(XMLSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }

    @Override
    public IMapper getMapper() {
        return mapper;
    }
}
