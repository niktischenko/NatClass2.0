package org.munta.projectengine;

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

class ProjectFile {

    private final Charset utf8 = Charset.forName("UTF-8");
    private String filename;
    private Map<Class, IProjectSerializer> cachedSerializers;

    private IProjectSerializer getCachedSerializer(Class objectClass) throws SerializerException {
        IProjectSerializer serializer;
        if (cachedSerializers.containsKey(objectClass)) {
            serializer = cachedSerializers.get(objectClass);
        } else {
            serializer = new JABXSerializer(objectClass);
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

    public Boolean putProjectObjects(ProjectObjectWithIdentifier... identifiers) throws IOException {
        if (!isOnFileSystem() || (new File(filename)).canWrite()) {
            return false;
        }

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filename));

        for (ProjectObjectWithIdentifier identifier : identifiers) {
            Class objectClass = identifier.getProjectObject().getClass();
            IProjectSerializer serializer;
            try {
                serializer = getCachedSerializer(objectClass);

                ZipEntry zipEntry = new ZipEntry(identifier.getIdentifier());
                zipEntry.setExtra(objectClass.toString().getBytes(utf8));
                zos.putNextEntry(zipEntry);
                serializer.serializeProjectObject(identifier.getProjectObject(), zos);
            } catch (SerializerException ex) {
                continue;
            }
        }

        zos.close();


        return true;
    }

    public Object getProjectObject(String identifier) throws IOException {
        if (!isOnFileSystem() || (new File(filename)).canRead()) {
            return null;
        }

        ZipInputStream zif = new ZipInputStream(new FileInputStream(filename));
        ZipEntry zipEntry;
        while ((zipEntry = zif.getNextEntry()) != null) {
            if(!zipEntry.getName().equals(identifier))
                continue;
            
            Class objectClass = null;
            try {
                objectClass = Class.forName(new String(zipEntry.getExtra(), utf8));
                IProjectSerializer serializer = getCachedSerializer(objectClass);
                
                return serializer.deserializeProjectObject(zif);
            } catch (ClassNotFoundException ex) {
                continue;
            } catch (SerializerException ex) {
                continue;
            }
        }

        return null;
    }
}
