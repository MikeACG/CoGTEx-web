/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.coplots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class SpearmanCorrelation {
    
    public static double main(Gene A, Gene B) {
        // get expression vectors
        List<Double> x = A.getExpression();
        List<Double> y = B.getExpression();
        
        // rank expresison values
        List<Double> xRanks = rankAvgTie(x);
        List<Double> yRanks = rankAvgTie(y);
        
        // rank expresison values
        // List<Double> xRanks = rankNoTie(x);
        // List<Double> yRanks = rankNoTie(y);

        // get means of ranked vectors
        double xmean = mean(xRanks);
        double ymean = mean(yRanks);
        
        return productMoment(xRanks, yRanks, xmean, ymean);
    }
    
    public static double mean(List<Double> v) {
        int n = v.size();
        double sum = 0;
        
        for (int i = 0; i < n; i++) sum += v.get(i);
        
        return sum / (double) n ;
    }
    
    public static double productMoment(List<Double> x, List<Double> y, double xmean, double ymean) {
        double cov = 0;
        double varx = 0;
        double vary = 0;
        
        for (int i = 0; i < x.size(); i++) {
            cov += (x.get(i) - xmean) * (y.get(i) - ymean);
            varx += Math.pow(x.get(i) - xmean, 2);
            vary += Math.pow(y.get(i) - ymean, 2);
        }
        
        return cov / Math.sqrt(varx * vary);
    }
    
    public static List<Double> rankNoTie(List<Double> v) {
        double[] vSorted = arrayList2array(v);
        Arrays.sort(vSorted);
        int n = vSorted.length;
        
        HashMap<Double, Integer> map = new HashMap<>();
        int i, r;
        for (i = 0; i < n; i++) {
            r = i + 1; // rank for current value
            map.put(vSorted[i], r);   
        }
        
        List<Double> out = new ArrayList<>();
        for (i = 0; i < n; i++) out.add((double) map.get(v.get(i)));
        
        return out;
    }
    
    public static List<Double> rankAvgTie(List<Double> v) {
        
        double[] vSorted = arrayList2array(v);
        Arrays.sort(vSorted);
        int n = vSorted.length;
        
        HashMap<Double,Double> map = new HashMap<>();
        List<Double> tiedRanks = new ArrayList<>();
        double r, next;
        for (int i = 0; i < n; i++) {
            r = i + 1; // rank for current value
            next = (i == n - 1) ? vSorted[i] + 1 : vSorted[(int) r]; // look at dummy value if last index, otherwise look at next index
            if (doubleEq(vSorted[i], next)) { // tie with next value
               tiedRanks.add(r); // add current rank to tmp tied ranks
            } else { // not tied with next value
                if (tiedRanks.size() > 0) { // a tie streak ended
                    tiedRanks.add(r); // current index is still tied
                    r  = mean(tiedRanks); // use mean of tied ranks as the rank for tied value
                    tiedRanks.clear();
                }
                map.put(vSorted[i], r);   
            }
        }
        
        List<Double> out = new ArrayList<>();
        for (int i = 0; i < n; i++) out.add(map.get(v.get(i)));
        
        return out;
    }
    
    public static double[] arrayList2array(List<Double> al) {
        int n = al.size();
        double[] out = new double[n];
        
        for (int i = 0; i < n; i++) out[i] = al.get(i);
        
        return out;
    }
    
    public static boolean doubleEq(double a, double b) {
        
        if (a == b) return true; // handle the +0.0 against -0.0 case
        return Double.compare(a, b) == 0;
        
    }
}
