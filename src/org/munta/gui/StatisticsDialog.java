package org.munta.gui;

import java.awt.Container;
import java.util.HashSet;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import org.munta.model.Attribute;
import org.munta.model.Entity;
import org.munta.projectengine.ProjectManager;

public class StatisticsDialog extends JDialog {
    
    private JLabel objectsCount = new JLabel();
    private JLabel attributesCount = new JLabel();
    private JLabel maxAttributesInObjectCount = new JLabel();
    
    private JLabel probabilityThreshold = new JLabel();
    private JLabel fisherThreshold = new JLabel();
    private JLabel yuleThreshold = new JLabel();
    private JLabel useIntermediateResults = new JLabel();
    private JLabel recursionDeep = new JLabel();
    
    private JLabel regularitiesCount = new JLabel();
    
    private JLabel classesCount = new JLabel();
    
    public void setObjectCount(int count) {
        objectsCount.setText("Objects: " + count);
    }
    
    public void setAttributesCount(int count) {
        attributesCount.setText("Attributes: " + count);
    }
    
    public void setMaxAttributesInObjectCount(int count) {
        maxAttributesInObjectCount.setText("Max attributes: " + count);
    }
    
    public void setProbabilityThreshold(double threshold) {
        probabilityThreshold.setText("Probability: " + threshold);
    }
    
    public void setFisherThreshold(double threshold) {
        fisherThreshold.setText("Fisher: " + threshold);
    }
    
    public void setYuleThreshold(double threshold) {
        yuleThreshold.setText("Yule: " + threshold);
    }
    
    public void setUseIntermediateResults(Boolean use) {
        useIntermediateResults.setText("Use intermediate results: " + use);
    }
    
    public void setRecursionDeep(int deep) {
        recursionDeep.setText("Recursion deepness: " + deep);
    }
    
    public void setRegularitiesCount(int count) {
        regularitiesCount.setText("Regularities: " + count);
    }
    
    public void setClassesCount(int count) {
        classesCount.setText("Classes: " + count);
    }
    
    public StatisticsDialog(JFrame parent) {
        super(parent, "Statistics", ModalityType.APPLICATION_MODAL);
        
        initValues();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        addControls(panel);
        
        setContentPane(panel);
        
        setLocationRelativeTo(parent);
        setResizable(false);
        pack();
    }
    
    private void initValues() {
        setObjectCount(ProjectManager.getInstance().getCollectionOfEntities().size());
        
        HashSet<String> attrNames = new HashSet<String>();
        int max = Integer.MIN_VALUE;
        
        for(Entity e : ProjectManager.getInstance().getCollectionOfEntities()) {
            for(Attribute a : e.getAttributes()) {
                attrNames.add(a.getName());
            }
            
            if(e.getAttributes().size() > max) {
                max = e.getAttributes().size();
            }
        }
        if(attrNames.isEmpty()) {
            max = 0;
        }
        
        setAttributesCount(attrNames.size());
        setMaxAttributesInObjectCount(max);
        
        setProbabilityThreshold(ProjectManager.getInstance().getGlobalProperties().getProbabilityThreshold());
        setFisherThreshold(ProjectManager.getInstance().getGlobalProperties().getFisherThreshold());
        setYuleThreshold(ProjectManager.getInstance().getGlobalProperties().getYuleThreshold());
        setUseIntermediateResults(ProjectManager.getInstance().getGlobalProperties().getUseIntermediateResults());
        setRecursionDeep(ProjectManager.getInstance().getGlobalProperties().getRecursionDeep());
        setRegularitiesCount(ProjectManager.getInstance().getCollectionOfRegularities().size());
        setClassesCount(ProjectManager.getInstance().getCollectionOfIdealClasses().size());
    }
    
    private void addControls(Container pane) {
        pane.add(objectsCount);
        pane.add(attributesCount);
        pane.add(maxAttributesInObjectCount);
    
        pane.add(new JSeparator());
        
        pane.add(probabilityThreshold);
        pane.add(fisherThreshold);
        pane.add(yuleThreshold);
        pane.add(useIntermediateResults);
        pane.add(recursionDeep);
        
        pane.add(new JSeparator());
        
        pane.add(regularitiesCount);
   
        pane.add(new JSeparator());
        
        pane.add(classesCount);
    }
}
