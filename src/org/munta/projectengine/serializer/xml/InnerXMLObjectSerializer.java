package org.munta.projectengine.serializer.xml;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.munta.projectengine.serializer.IMapper;
import org.munta.projectengine.serializer.SerializerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class InnerXMLObjectSerializer {

    private Document document;
    private IMapper mapper;
    private final String ENTRY_TAG_NAME = "entry";

    public InnerXMLObjectSerializer(IMapper mapper) {
        this.mapper = mapper;
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
                    Element ell = document.createElement(ENTRY_TAG_NAME);
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
                            Element ell = document.createElement(ENTRY_TAG_NAME);
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

    private Class getClassByTagName(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            return String.class;
        }
        Collection<Class> registeredClasses = mapper.getRegisteredClasses();
        for (Class clazz : registeredClasses) {
            XMLObject objectAnnotation = (XMLObject) clazz.getAnnotation(XMLObject.class);
            if (objectAnnotation.name().equals(node.getNodeName())) {
                return clazz;
            }
        }
        System.err.println("Unknown node: " + node.getNodeType() + "::" + node.getNodeName() + "::" + node.getNodeValue());
        return null;
    }

    public Object deserialize(Node node) throws SerializerException {
        try {
            Class clazz = getClassByTagName(node);
            if (clazz.equals(String.class)) {
                return deserializeBasic(node);
            }
            Object object = clazz.newInstance();
            Element element = (Element) node;

            XMLObject objectAnnotation = (XMLObject) clazz.getAnnotation(XMLObject.class);
            if (objectAnnotation.collection()) {
                NodeList children = element.getChildNodes();
                deserializeToCollection((Collection<Object>) object, children);
            }
            if (objectAnnotation.map()) {
                NodeList children = element.getChildNodes();
                String attributeName = objectAnnotation.mapKeyAttribute();
                deserializeToMap((Map<Object, Object>) object, children, attributeName);
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                XMLProperty propertyAnnotation = (XMLProperty) field.getAnnotation(XMLProperty.class);
                if (null == propertyAnnotation) { // not mapped to XML
                    continue;
                }
                if (propertyAnnotation.attribute()) { // mapped as attribute
                    field.set(object, element.getAttribute(propertyAnnotation.name()));
                    continue;
                }

                NodeList children = element.getElementsByTagName(propertyAnnotation.name()).item(0).getChildNodes(); // get enclosed children

                Object property = null;
                if (propertyAnnotation.collection()) {
                    property = propertyAnnotation.propertyClass().newInstance();
                    deserializeToCollection((Collection<Object>) property, children);
                } else if (propertyAnnotation.map()) {
                    property = propertyAnnotation.propertyClass().newInstance();
                    String attributeName = propertyAnnotation.mapKeyAttribute();
                    deserializeToMap((Map<Object, Object>) property, children, attributeName);
                } else {
                    for (int i = 0; i < children.getLength(); i++) {
                        Node item = children.item(i);
                        if (item.getNodeType() == Node.ELEMENT_NODE) {
                            property = deserialize(children.item(i));
                            break;
                        }
                    }
                }
                if (property == null) {
                    property = deserialize(children.item(0));
                }
                field.set(object, property);
            }
            return object;
        } catch (Exception ex) {
            Logger.getLogger(InnerXMLObjectSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new SerializerException(ex);
        }
    }

    private void deserializeToCollection(Collection<Object> collection, NodeList nodes) throws SerializerException {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node entry = nodes.item(i);
            if (entry.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Object entity = deserialize(entry);
            collection.add(entity);
        }
    }

    private void deserializeToMap(Map<Object, Object> map, NodeList nodes, String keyAttributeName) throws SerializerException {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node entry = nodes.item(i);
            if (entry.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Object entity = deserialize(entry);
            map.put(((Element) entry).getAttribute(keyAttributeName), entity);
        }
    }

    private Object deserializeBasic(Node node) {
        String text = node.getNodeValue();
        if (text.equals(Boolean.TRUE.toString()) || text.equals(Boolean.FALSE.toString())) {
            return Boolean.valueOf(text);
        }
        try {
            try {
                return Integer.valueOf(text);
            } catch (NumberFormatException e) {
                return Double.valueOf(text);
            }
        } catch (NumberFormatException e) {
            return text;
        }
    }
}
