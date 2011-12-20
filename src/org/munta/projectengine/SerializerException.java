package org.munta.projectengine;

public class SerializerException extends Exception {
    public SerializerException(Exception innerException) {
        super("Serialization exception", innerException);
    }
}
