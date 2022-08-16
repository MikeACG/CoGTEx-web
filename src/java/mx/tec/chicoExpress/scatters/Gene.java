/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author INTEL
 */
public class Gene {
    
    private List<Double> expression;
    private List<String> samples;
    private List<String> groups;
    private List<String> colors;
    private List<String> cvars;
    private List<String> shapes;
    private List<Double> brks;
    
    public Gene(List<Double> expression, List<String> samples){
        super();
        this.expression = expression;
        this.samples = samples;
    }
    
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
    
    public void setColors(List<String> colors) {
        this.colors = colors;
    }
    
    public void setCvars(List<String> cvars) {
        this.cvars = cvars;
    }
    
    public void setShapes(List<String> shapes) {
        this.shapes = shapes;
    }
    
    public void setBrks(List<Double> brks) {
        this.brks = brks;
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
    
    public List<String> getSizedGroupLabs() {
        
        List<String> ugroups = uniq(groups);
        int n = ugroups.size();
        HashMap<String,Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) map.put(ugroups.get(i), i);
        
        int[] counts = new int[n];
        for (int i = 0; i < groups.size(); i++) counts[map.get(groups.get(i))]++;
        
        List<String> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            out.add(ugroups.get(i) + " (n = " + counts[i] + ")");
        }
        
        return out;
    } 
    
    public List<String> GCOAes() {
        
        List<String> aes = new ArrayList<>();
        
        int nsamples = samples.size();
        String color, shape;
        for (int i = 0; i < nsamples; i++) {
            color = colors.get(i);
            shape = shapes.get(i);
            aes.add("point {fill-opacity: 0; stroke-color: " + color + "; shape-type: " + shape + ";}");
        }
        
        return aes;
        
    }
    
    public List<String> GCOTooltips() {
        List<String> tooltips = new ArrayList<>();
        
        int nsamples = samples.size();
        String id, group, cvar;
        for (int i = 0; i < nsamples; i++) {
            id = samples.get(i);
            group = groups.get(i);
            cvar = cvars.get(i);
            tooltips.add(id + "\n" + group + " " + cvar);
        }
        
        return tooltips;
    }
    
    public double getMinExp() {
        return brks.get(2);
    }
    
    public double getMaxExp() {
        return brks.get(3);
    }
    
    public double getBrk(int i) {
        return brks.get(i);
    }

    public List<String> uniq(List<String> attr) {
        Set<String> s = new HashSet<>();
        List<String> u = new ArrayList<>();
        
        int n = attr.size();
        String tmp;
        for (int i = 0; i < n; i++) {
            tmp = attr.get(i);
            if (!s.contains(tmp)) {
                s.add(tmp);
                u.add(tmp);
            }
        }
        
        return u;
    }
}
