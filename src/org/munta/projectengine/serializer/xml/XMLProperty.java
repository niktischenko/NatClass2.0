package org.munta.projectengine.serializer.xml;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value= RetentionPolicy.RUNTIME)
public @interface XMLProperty {
    String name();
    boolean attribute() default false;
    boolean collection() default false;
    boolean map() default false;
    String mapKeyAttribute() default "name";
    Class propertyClass() default Object.class;
}
