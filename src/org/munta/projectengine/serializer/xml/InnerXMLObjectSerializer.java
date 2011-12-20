package org.munta.projectengine.serializer.xml;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.munta.projectengine.serializer.SerializerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class InnerXMLObjectSerializer {

    private Document document;

    public InnerXMLObjectSerializer() {
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Node serialize(Object o) throws SerializerException {
        Class clazz = o.getClass();
        if (clazz.equals(String.class) 
                || clazz.equals(Integer.class) 
                || clazz.equals(Double.class)
                || clazz.equals(Boolean.class)) {
            return document.createTextNode(o.toString());
        }

        XMLObject objectAnnotation = (XMLObject) clazz.getAnnotation(XMLObject.class);
        Element element = document.createElement(objectAnnotation.name());
        Field[] fields = clazz.getDeclaredFields();
        if (objectAnnotation.collection()) {
            Collection<Object> collection = (Collection<Object>) o;
            for (Object object : collection) {
                Node child = serialize(object);
                element.appendChild(child);
            }
        }
        if (objectAnnotation.map()) {
            AbstractMap<Object, Object> map = (AbstractMap<Object, Object>) o;
            for (Object key : map.keySet()) {
                Node child = serialize(map.get(key));
                if (child instanceof Element) {
                    ((Element) child).setAttribute(objectAnnotation.mapKeyAttribute(), key.toString());
                } else {
                    Element ell = document.createElement("entry");
                    ell.setAttribute(objectAnnotation.mapKeyAttribute(), key.toString());
                    ell.appendChild(child);
                    child = ell;
                }
                element.appendChild(child);
            }
        }
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                XMLProperty propertyAnnotation = (XMLProperty) field.getAnnotation(XMLProperty.class);
                if (null == propertyAnnotation) {
                    continue;
                }             
                if (propertyAnnotation.attribute()) {
                    element.setAttribute(propertyAnnotation.name(), field.get(o).toString());
                    continue;
                }
                if (propertyAnnotation.collection()) {
                    Collection<Object> collection = (Collection<Object>) field.get(o);
                    Element el = document.createElement(propertyAnnotation.name());
                    element.appendChild(el);

                    for (Object object : collection) {
                        Node child = serialize(object);
                        el.appendChild(child);
                    }
                    continue;
                }
                if (propertyAnnotation.map()) {
                    Element el = element;
                    AbstractMap<Object, Object> map = (AbstractMap<Object, Object>) o;
                    for (Object key : map.keySet()) {
                        Node child = serialize(map.get(key));
                        if (child instanceof Element) {
                            ((Element) child).setAttribute(propertyAnnotation.mapKeyAttribute(), key.toString());
                        } else {
                            Element ell = document.createElement("entry");
                            ell.setAttribute(propertyAnnotation.mapKeyAttribute(), key.toString());
                            ell.appendChild(child);
                            child = ell;
                        }
                        el.appendChild(child);
                    }
                }

                Element el = document.createElement(propertyAnnotation.name());
                el.appendChild(serialize(field.get(o)));
                element.appendChild(el);

            }
        } catch (Exception ex) {
            Logger.getLogger(InnerXMLObjectSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
        return element;
    }
}
