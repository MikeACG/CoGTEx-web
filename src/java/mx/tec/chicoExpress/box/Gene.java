/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class Gene {
    
    private List<Double> expression;
    private List<String> samples;
    private List<String> groups;
    private List<String> colors;
    private List<String> descriptions;
    
    public Gene(List<Double> expression, List<String> samples) {
        this.expression = expression;
        this.samples = samples;
    }
    
    public void setGroups(HashMap<String,String> groupsMap) {
        this.groups = grabSamplesFromMap(groupsMap);
    }
    
    public void setColors(HashMap<String,String> colorsMap) {
        this.colors = grabSamplesFromMap(colorsMap);
    }
    
    public void setDescriptions(HashMap<String,String> descriptionsMap) {
        this.descriptions = grabSamplesFromMap(descriptionsMap);
    }
    
    public List<Double> getExpression() {
        return expression;
    }
    
    public double getExp(int i) {
        return expression.get(i);
    }
    
    public List<String> getSamples() {
        return samples;
    }
    
    public List<String> getColors() {
        return colors;
    }
    
    public List<String> getGroups() {
        return groups;
    }
    
    public String getGroup(int i) {
        return groups.get(i);
    }
    
    public List<String> getDescriptions() {
        return descriptions;
    }
    
    public List<String> grabSamplesFromMap(HashMap<String,String> map) {
        
        List<String> out = new ArrayList<>();
        
        for (int i = 0; i < samples.size(); i++) {
            out.add(map.get(samples.get(i)));
        }
        
        return out;
    }
    
}

