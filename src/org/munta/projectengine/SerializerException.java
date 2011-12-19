package org.munta.projectengine;

class SerializerException extends Exception {
    public SerializerException(Exception innerException) {
        super("Serialization exception", innerException);
    }
}
