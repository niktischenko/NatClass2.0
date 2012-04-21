package org.munta.importexport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.munta.model.EntityCollection;
import org.munta.model.GlobalProperties;
import org.munta.model.RegularityCollection;
import org.munta.projectengine.serializer.IProjectSerializer;
import org.munta.projectengine.serializer.ProjectSerializerFactory;
import org.munta.projectengine.serializer.SerializerException;

public final class Importer {
    
    private static Object handleXml(String filePath) throws IOException {
        
        Object x = null;
            
        IProjectSerializer serializer;
        FileInputStream fis = null;
     
        try {
            fis = new FileInputStream(filePath);
            serializer = ProjectSerializerFactory.createSerializer(EntityCollection.class);
            x = serializer.deserializeProjectObject(fis);
            if (x != null) {
                return x;
            }
        } catch (SerializerException ex) {
        } finally {
            fis.close();
        }

        try {
            fis = new FileInputStream(filePath);
            serializer = ProjectSerializerFactory.createSerializer(RegularityCollection.class);
            x = serializer.deserializeProjectObject(fis);
            if (x != null) {
                return x;
            }
        } catch (SerializerException ex) {
        } finally {
            fis.close();
        }

        try {
            fis = new FileInputStream(filePath);
            serializer = ProjectSerializerFactory.createSerializer(GlobalProperties.class);
            x = serializer.deserializeProjectObject(fis);
            if (x != null) {
                return x;
            }
        } catch (SerializerException ex) {
        } finally {
            fis.close();
        }
        
        return null;
    }
    
    public static Object importFromFile(String filePath) {
        
        //File f = new File(filePath);
        int dotPos = filePath.lastIndexOf(".");
        String extension = "";
        if(dotPos >= 0) {
            extension = filePath.substring(dotPos + 1);
        }
        
        try {
            if(extension.equals("txt")) {
                return null;
            } else if(extension.equals("xml")) {
                Object newObject = handleXml(filePath);
                if(newObject != null)
                    return newObject;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new IllegalArgumentException("Unsupported import file type: " + extension);
    }
}
