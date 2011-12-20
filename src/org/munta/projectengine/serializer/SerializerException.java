package org.munta.projectengine.serializer;

public class SerializerException extends Exception {
    public SerializerException(Exception innerException) {
        super("Serialization exception", innerException);
    }

    SerializerException(String message) {
        super(message);
    }
}
