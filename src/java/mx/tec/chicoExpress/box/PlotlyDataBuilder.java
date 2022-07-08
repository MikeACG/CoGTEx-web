/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.box;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author INTEL
 */
public class PlotlyDataBuilder {
    
    public static PlotlyTrace[] build(Gene gene) {
        
        List<String> uniqueColors = uniq(gene.getColors());
        List<String> uniqueDescriptions = uniq(gene.getDescriptions());
        List<String> uniqueGroups = uniq(gene.getGroups());
        HashMap<String,List<Double>> stratifiedExpr = stratifyExpression(gene, 
                uniqueGroups);
        List<String> uSizedDescriptions = addDescSampleSize(uniqueDescriptions, 
                uniqueGroups, stratifiedExpr); 
        int n = uniqueGroups.size();
        PlotlyTrace[] out = new PlotlyTrace[n];
        String group, color, description;
        
        for (int i = 0; i < n; i++) {
            group = uniqueGroups.get(i);
            color = uniqueColors.get(i);
            description = uSizedDescriptions.get(i);
            out[i] = new PlotlyTrace(description, stratifiedExpr.get(group), "box", 
                    group, color);
        }
        
        return out;
    }
    
    public static List<String> uniq(List<String> list) {
        
        Set<String> s = new HashSet<>();
        List<String> u = new ArrayList<>();
        String tmp;
        
        for (int i = 0; i < list.size(); i++) {
            tmp = list.get(i);
            if (!s.contains(tmp)) {
                s.add(tmp);
                u.add(tmp);
            }
        }
        
        return u;
    }
    
    public static HashMap<String,List<Double>> stratifyExpression(Gene gene, 
            List<String> uGroups) {
        
        List<Double> expression = gene.getExpression();
        List<String> groups = gene.getGroups();
        HashMap<String,List<Double>> out = new HashMap<>();
        int i;
        
        for (i = 0; i < uGroups.size(); i++) {
            out.put(uGroups.get(i), new ArrayList<>());
        }
        
        for (i = 0; i < groups.size(); i++) {
            out.get(groups.get(i)).add(expression.get(i));
        }
        
        return out;
    }
    
    public static String[] computeMedianColors(PlotlyTrace[] traces) {
        
        int n = traces.length;
        String[] out = new String[n];
        
        for (int i = 0; i < n; i++) {
            out[i] = contrastColor(traces[i].getFillcolor());
        }
        
        return out;
    }
    
    public static String contrastColor(String hexColor) {

        Color color = Color.decode(hexColor);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        // https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
        double luminance = 
                ((0.299 * red) + (0.587 * green) + (0.114 * blue)) / 255;
        int d = (luminance > 0.5) ? 0 : 255;

        return "rgb(" + d + "," + d + "," + d + ")";
    }
    
    public static List<String> addDescSampleSize(List<String> descs, 
            List<String> groups, HashMap<String,List<Double>> stratifiedExpr) {
        
        List<String> out = new ArrayList<>();
        String sampleSize;
        
        for (int i = 0; i < descs.size(); i++) {
            sampleSize = descs.get(i) + "<br>(n = " 
                    + stratifiedExpr.get(groups.get(i)).size() + ")";
            out.add(sampleSize);
        }
        
        return out;
    }
    
}
