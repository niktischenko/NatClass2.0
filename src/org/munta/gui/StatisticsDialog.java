package org.munta.gui;

import java.awt.Container;
import java.util.HashSet;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
    
    private String htmlize(String parameter, String value) {
        return "<html>" + parameter + ": <B>" + value + "</B></html>";
    }
    
    public void setObjectCount(Integer count) {
        objectsCount.setText(htmlize("Objects", count.toString()));
    }
    
    public void setAttributesCount(Integer count) {
        attributesCount.setText(htmlize("Attributes", count.toString()));
    }
    
    public void setMaxAttributesInObjectCount(Integer count) {
        maxAttributesInObjectCount.setText(htmlize("Max attributes", count.toString()));
    }
    
    public void setProbabilityThreshold(Double threshold) {
        probabilityThreshold.setText(htmlize("Probability", threshold.toString()));
    }
    
    public void setFisherThreshold(Double threshold) {
        fisherThreshold.setText(htmlize("Fisher", threshold.toString()));
    }
    
    public void setYuleThreshold(Double threshold) {
        yuleThreshold.setText(htmlize("Yule", threshold.toString()));
    }
    
    public void setUseIntermediateResults(Boolean use) {
        useIntermediateResults.setText(htmlize("Use intermediate results", use.toString()));
    }
    
    public void setRecursionDeep(Integer deep) {
        recursionDeep.setText(htmlize("Recursion deepness", deep.toString()));
    }
    
    public void setRegularitiesCount(Integer count) {
        regularitiesCount.setText(htmlize("Regularities", count.toString()));
    }
    
    public void setClassesCount(Integer count) {
        classesCount.setText(htmlize("Classes", count.toString()));
    }
    
    public StatisticsDialog(JFrame parent) {
        super(parent, "Statistics", ModalityType.APPLICATION_MODAL);
        
        initValues();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        addControls(panel);
        
        setContentPane(panel);
        
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
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
        pane.add(regularitiesCount);
        pane.add(classesCount);
        
        pane.add(new JLabel(" "));
        
        pane.add(attributesCount);
        pane.add(maxAttributesInObjectCount);
    
        pane.add(new JLabel(" "));
        
        pane.add(probabilityThreshold);
        pane.add(fisherThreshold);
        pane.add(yuleThreshold);
        pane.add(useIntermediateResults);
        pane.add(recursionDeep);
    }
}
