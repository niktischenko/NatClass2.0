package org.munta.importexport;

import java.io.FileOutputStream;
import org.munta.projectengine.serializer.IProjectSerializer;
import org.munta.projectengine.serializer.ProjectSerializerFactory;
import org.munta.projectengine.serializer.SerializerException;

public final class Exporter {
    public static void exportData(Object data, String filePath) throws Exception {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            IProjectSerializer serializer = ProjectSerializerFactory.createSerializer(data.getClass());
            serializer.serializeProjectObject(data, fos);
        } catch (SerializerException ex) {
            throw new Exception("Unexpected error in export operation", ex);
        }
    }
}
