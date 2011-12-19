package org.munta.projectengine;

final class ProjectObjectWithIdentifier {
    private Object projectObject;
    private String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object getProjectObject() {
        return projectObject;
    }

    public void setProjectObject(Object projectObject) {
        this.projectObject = projectObject;
    }
    
    public ProjectObjectWithIdentifier(Object projectObject, String identifier) {
        this.projectObject = projectObject;
        this.identifier = identifier;
    }
}
