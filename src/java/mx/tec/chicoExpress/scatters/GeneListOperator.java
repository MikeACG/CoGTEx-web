/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tec.chicoExpress.scatters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author mike
 */
public class GeneListOperator {
    
    public class GeneComparator implements Comparator<Gene> {
        
        private final String what;
        
        public GeneComparator(String what) {
            
            this.what = what;
            
        }
        
        @Override
        public int compare(Gene g1, Gene g2) {
            
            return Double.valueOf(g1.getRaw(this.what)).
                    compareTo(g2.getRaw(this.what));
            
        }
        
    }
    
    public static void rankAvgTie(List<Gene> genesSorted, String what) {
        
        int n = genesSorted.size();
        
        HashMap<Double,Double> map = new HashMap<>();
        List<Double> tiedRanks = new ArrayList<>();
        double r, next;
        for (int i = 0; i < n; i++) {
            
            r = i + 1; // rank for current value
            next = (i == n - 1) ? genesSorted.get(i).getRaw(what) + 1 
                    : genesSorted.get((int) r).getRaw(what); // look at dummy value if last index, otherwise look at next index
            if (doubleEq(genesSorted.get(i).getRaw(what), next)) { // tie with next value
                
               tiedRanks.add(r); // add current rank to tmp tied ranks
               
            } else { // not tied with next value
                
                if (!tiedRanks.isEmpty()) { // a tie streak ended
                    tiedRanks.add(r); // current index is still tied
                    r  = mean(tiedRanks); // use mean of tied ranks as the rank for tied value
                    tiedRanks.clear();
                }
                map.put(genesSorted.get(i).getRaw(what), r);   
                
            }
        }
        
        for (int i = 0; i < n; i++) {
            
            genesSorted.get(i).setRank(map.get(genesSorted.get(i).getRaw(what)), what);
        
        }
        
    }
    
    public static boolean doubleEq(double a, double b) {
        
        if (a == b) return true; // handle the +0.0 against -0.0 case
        return Double.compare(a, b) == 0;
        
    }
    
    public static double mean(List<Double> v) {
        int n = v.size();
        double sum = 0;
        
        for (int i = 0; i < n; i++) sum += v.get(i);
        
        return sum / (double) n ;
    }
    
    public static void head(List<Gene> genesSorted, int n) {
        
        int nremain = genesSorted.size();
        while (nremain > n) {
            
            genesSorted.remove(nremain - 1);
            nremain--;
            
        }
        
    }
    
    public static void logRank(List<Gene> genes, String what) {
        
        for (int i = 0; i < genes.size(); i++) {
            
            genes.get(i).setLogrank(Math.log10(genes.get(i).getRank(what)), what);
            
        }
        
    }
    
    public static List<Double> getEstimates(List<Gene> genes, String what) {
        
        List<Double> out = new ArrayList<>();
        for (int i = 0; i < genes.size(); i++) {
            
            out.add(genes.get(i).getRawSigned(what));
            
        }
        
        return out;
        
    }
    
    public static List<Double> getRanks(List<Gene> genes, String what) {
        
        List<Double> out = new ArrayList<>();
        for (int i = 0; i < genes.size(); i++) {
            
            out.add(genes.get(i).getRank(what));
            
        }
        
        return out;
        
    }
    
    public static List<Double> getLogranks(List<Gene> genes, String what) {
        
        List<Double> out = new ArrayList<>();
        for (int i = 0; i < genes.size(); i++) {
            
            out.add(genes.get(i).getLogrank(what));
            
        }
        
        return out;
        
    }
    
}
