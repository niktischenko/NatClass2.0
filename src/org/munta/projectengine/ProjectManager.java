package org.munta.projectengine;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.munta.model.EntityCollection;
import org.munta.model.GlobalProperties;
import org.munta.model.RegularityCollection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public final class ProjectManager {

    public static final String FILENAME_ENTITIES = "entities.xml";
    public static final String FILENAME_REGULARITIES = "regularities.xml";
    public static final String FILENAME_CLASSES = "classes.xml";
    public static final String FILENAME_PROPERTIES = "properties.xml";
    private EntityCollection collectionOfEntities;
    private RegularityCollection collectionOfRegularities;
    private EntityCollection collectionOfIdealClasses;
    private GlobalProperties globalProperties;

    public EntityCollection getCollectionOfEntities() {
        return collectionOfEntities;
    }

    public EntityCollection getCollectionOfIdealClasses() {
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
    private static ProjectManager projectManager = null;

    public static ProjectManager getInstance() {
        if (projectManager == null) {
            projectManager = new ProjectManager();
        }
        return projectManager;
    }

    private ProjectManager() {
        collectionOfEntities = new EntityCollection();
        collectionOfRegularities = new RegularityCollection();
        collectionOfIdealClasses = new EntityCollection();
        globalProperties = new GlobalProperties();

        newProject();
    }

    public Boolean isDirty() {
        return true;
    }

    public void newProject() {
        projectFile = new ProjectFile();

        getCollectionOfEntities().clear();
        getCollectionOfRegularities().clear();
        getCollectionOfIdealClasses().clear();
        globalProperties.clear();
        isDirty = true;
    }

    public Boolean saveProject() {
        if (!projectFile.isOnFileSystem()) {
            return false;
        }
        try {
            projectFile.putProjectObjects(
                    new ProjectObjectWithIdentifier(collectionOfEntities, FILENAME_ENTITIES),
                    new ProjectObjectWithIdentifier(collectionOfRegularities, FILENAME_REGULARITIES),
                    new ProjectObjectWithIdentifier(collectionOfIdealClasses, FILENAME_CLASSES),
                    new ProjectObjectWithIdentifier(globalProperties, FILENAME_PROPERTIES));

            isDirty = false;
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Boolean saveAsProject(String filename) {
        projectFile.setFilename(filename);
        return saveProject();
    }

    public Boolean loadProject(String filename) {
        projectFile.setFilename(filename);

        if (!projectFile.isOnFileSystem()) {
            return false;
        }
        try {

            Map<String, Object> map = projectFile.getProjectObjects();
            if(map == null)
                return false;

            newProject();

            EntityCollection entities = (EntityCollection) map.get(FILENAME_ENTITIES);
            if (entities != null) {
                collectionOfEntities.addAll(entities);
            }
            RegularityCollection regularities = (RegularityCollection) map.get(FILENAME_REGULARITIES);
            if (regularities != null) {
                collectionOfRegularities.putAll(regularities);
            }
            EntityCollection classes = (EntityCollection) map.get(FILENAME_CLASSES);
            if (classes != null) {
                collectionOfIdealClasses.addAll(classes);
            }
            GlobalProperties properties = (GlobalProperties) map.get(FILENAME_PROPERTIES);
            if (classes != null) {
                globalProperties = properties;
            }

        } catch (IOException ex) {
            Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        isDirty = false;
        return true;
    }

    public void importData(int dataToImport) {
        throw new NotImplementedException();
    }

    public void exportData(int dataToExport) {
        throw new NotImplementedException();
    }
}
