package org.munta.projectengine.serializer.xml;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value= RetentionPolicy.RUNTIME)
public @interface XMLObject {
    public String name();
    public boolean collection() default false;
    public boolean map() default false;
    public String mapKeyAttribute() default "name";
}
