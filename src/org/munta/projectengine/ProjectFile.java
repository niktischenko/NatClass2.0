package org.munta.projectengine;

import java.io.ByteArrayInputStream;
import org.munta.projectengine.serializer.SerializerException;
import org.munta.projectengine.serializer.ObjectSerializer;
import org.munta.projectengine.serializer.IProjectSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.munta.projectengine.serializer.ProjectSerializerFactory;

class ProjectFile {

    private final Charset utf8 = Charset.forName("UTF-8");
    private String filename;
    private Map<Class, IProjectSerializer> cachedSerializers;

    private IProjectSerializer getCachedSerializer(Class objectClass) throws SerializerException {
        IProjectSerializer serializer;
        if (cachedSerializers.containsKey(objectClass)) {
            serializer = cachedSerializers.get(objectClass);
        } else {
            serializer = ProjectSerializerFactory.createSerializer(objectClass);
            //serializer = new JABXSerializer(objectClass);
            //serializer = new XStreamSerializer(objectClass);
            cachedSerializers.put(objectClass, serializer);
        }
        return serializer;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ProjectFile() {
        filename = null;
        cachedSerializers = new HashMap<Class, IProjectSerializer>();
    }

    public Boolean isOnFileSystem() {
        return filename != null;
    }

    public Boolean putProjectObjects(Map<String, Object> map) throws IOException {
        if (!isOnFileSystem()) {
            return false;
        }

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filename));

        for (Map.Entry<String, Object> identifier : map.entrySet()) {

            Class objectClass = identifier.getValue().getClass();
            IProjectSerializer serializer;
            try {
                serializer = getCachedSerializer(objectClass);

                ZipEntry zipEntry = new ZipEntry(identifier.getKey());
                zipEntry.setExtra(objectClass.getName().getBytes(utf8));
                zos.putNextEntry(zipEntry);
                serializer.serializeProjectObject(identifier.getValue(), zos);
            } catch (SerializerException ex) {
                continue;
            }
        }

        zos.close();


        return true;
    }

    public Map<String, Object> getProjectObjects() throws IOException {
        if (!isOnFileSystem() || !(new File(filename)).canRead()) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        ZipInputStream zif = new ZipInputStream(new FileInputStream(filename));
        ZipEntry zipEntry;
        while ((zipEntry = zif.getNextEntry()) != null) {

            byte[] data = new byte[(int)zipEntry.getSize()];
            zif.read(data, 0, (int)zipEntry.getSize());
            
            Class objectClass = null;
            try {
                String className = new String(zipEntry.getExtra(), utf8);
                objectClass = Class.forName(className);
                IProjectSerializer serializer = getCachedSerializer(objectClass);
                
                Object o = serializer.deserializeProjectObject(
                        new ByteArrayInputStream(data));
                map.put(zipEntry.getName(), o);
            } catch (ClassNotFoundException ex) {
                continue;
            } catch (SerializerException ex) {
                continue;
            }
        }

        return null;
    }
}
