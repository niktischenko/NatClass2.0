package org.munta.projectengine;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.munta.model.EntitiesCollection;
import org.munta.model.GlobalProperties;
import org.munta.model.RegularityCollection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public final class ProjectManager {

    public static final String FILENAME_ENTITIES = "entities.xml";
    public static final String FILENAME_REGULARITIES = "regularities.xml";
    public static final String FILENAME_CLASSES = "classes.xml";
    public static final String FILENAME_PROPERTIES = "properties.xml";
    
    private EntitiesCollection collectionOfEntities;
    private RegularityCollection collectionOfRegularities;
    private EntitiesCollection collectionOfIdealClasses;
    private GlobalProperties globalProperties;

    public EntitiesCollection getCollectionOfEntities() {
        return collectionOfEntities;
    }

    public EntitiesCollection getCollectionOfIdealClasses() {
        return collectionOfIdealClasses;
    }

    public RegularityCollection getCollectionOfRegularities() {
        return collectionOfRegularities;
    }

    public GlobalProperties getGlobalProperties() {
        return globalProperties;
    }
    
    private ProjectFile projectFile;
    private Boolean isDirty = false;

    public ProjectManager() {
        collectionOfEntities = new EntitiesCollection();
        collectionOfRegularities = new RegularityCollection();
        collectionOfIdealClasses = new EntitiesCollection();
        globalProperties = new GlobalProperties();

        newProject();
    }

    public Boolean isDirty() {
        return true;
    }

    public void newProject() {
        projectFile = new ProjectFile();

        collectionOfEntities.clear();
        collectionOfRegularities.clear();
        collectionOfIdealClasses.clear();
        globalProperties.clear();
        isDirty = true;
    }

    public Boolean saveProject() {
        if (projectFile.isOnFileSystem()) {
            try {
                projectFile.putProjectObjects(
                        new ProjectObjectWithIdentifier(collectionOfEntities, FILENAME_ENTITIES),
                        new ProjectObjectWithIdentifier(collectionOfRegularities, FILENAME_REGULARITIES),
                        new ProjectObjectWithIdentifier(collectionOfIdealClasses, FILENAME_CLASSES),
                        new ProjectObjectWithIdentifier(globalProperties, FILENAME_PROPERTIES));
                
                isDirty = true;
                return true;
            } catch (IOException ex) {
                Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public Boolean saveAsProject(String filename) {
        projectFile.setFilename(filename);
        return saveProject();
    }

    public Boolean loadProject(String filename) {
        if (projectFile.isOnFileSystem()) {
            try {
                collectionOfEntities = (EntitiesCollection) projectFile.getProjectObject(FILENAME_ENTITIES);
                collectionOfRegularities = (RegularityCollection) projectFile.getProjectObject(FILENAME_REGULARITIES);
                collectionOfIdealClasses = (EntitiesCollection) projectFile.getProjectObject(FILENAME_CLASSES);
                globalProperties = (GlobalProperties) projectFile.getProjectObject(FILENAME_PROPERTIES);
            } catch (IOException ex) {
                Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
            isDirty = true;
            return true;
        }
        return false;
    }

    public void importData(ProjectDataContants dataToImport) {
        throw new NotImplementedException();
    }

    public void exportData(ProjectDataContants dataToExport) {
        throw new NotImplementedException();
    }
}
