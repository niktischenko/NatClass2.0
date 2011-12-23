package org.munta.projectengine.serializer.xml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.munta.projectengine.serializer.IMapper;

final class XmlMapperImpl implements IMapper {

    private Map<String, Class> registeredClasses;

    public XmlMapperImpl(Class objectType) {
        registeredClasses = new HashMap<String, Class>();
        registerClass(objectType);
    }

    /**
     * Get the underlying class for a type, or null if the type is a variable type.
     * @param type the type
     * @return the underlying class
     */
    public static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get the actual type arguments a child class has used to extend a generic base class.
     *
     * @param baseClass the base class
     * @param childClass the child class
     * @return a list of the raw classes for the actual type arguments.
     */
    public static <T> List<Class<?>> getTypeArguments(
            Class<T> baseClass, Class<? extends T> childClass) {
        Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
        Type type = childClass;
        // start walking up the inheritance hierarchy until we hit baseClass
        while (type != null && !getClass(type).equals(baseClass)) {
            if (type instanceof Class) {
                // there is no useful information for us in raw types, so just keep going.
                type = ((Class) type).getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class) parameterizedType.getRawType();

                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }

                if (!rawType.equals(baseClass)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }

        List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
        // resolve types by chasing down type variables.
        for (Entry<Type, Type> typeDef : resolvedTypes.entrySet()) {
            Class c = getClass(typeDef.getValue());
            if(c != null)
                typeArgumentsAsClasses.add(c);
        }
        return typeArgumentsAsClasses;
    }

    @Override
    public void registerClass(Class clazz) {
        if (clazz == null) {
            return;
        }

        XmlObject objectAnnotation = (XmlObject) clazz.getAnnotation(XmlObject.class);
        if (objectAnnotation != null) {
            registeredClasses.put(objectAnnotation.name(), clazz);
        }

        List<Class> classes = getTypeArguments(Object.class, clazz);
        for(Class clazzz : classes) {
            registerClass(clazzz);
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            XmlProperty propertyAnnotation = (XmlProperty) field.getAnnotation(XmlProperty.class);
            if (null == propertyAnnotation) {
                continue;
            }
            registeredClasses.put(propertyAnnotation.name(), field.getType());
            registerClass(field.getType());
        }
    }

    @Override
    public Class getClassNameByTagName(String tag) {
        return registeredClasses.get(tag);
    }

    @Override
    public String getTagNameClassName(Class className) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Class> getRegisteredClasses() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}