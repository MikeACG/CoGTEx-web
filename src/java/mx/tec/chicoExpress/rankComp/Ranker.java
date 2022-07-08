/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tec.chicoExpress.rankComp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author mike
 */
public class Ranker {
    
    public static List<Double> rankAvgTie(List<Double> vSorted) {
        
        int n = vSorted.size();
        
        HashMap<Double,Double> map = new HashMap<>();
        List<Double> tiedRanks = new ArrayList<>();
        double r, next;
        for (int i = 0; i < n; i++) {
            r = i + 1; // rank for current value
            next = (i == n - 1) ? vSorted.get(i) + 1 : vSorted.get((int) r); // look at dummy value if last index, otherwise look at next index
            if (doubleEq(vSorted.get(i), next)) { // tie with next value
               tiedRanks.add(r); // add current rank to tmp tied ranks
            } else { // not tied with next value
                if (!tiedRanks.isEmpty()) { // a tie streak ended
                    tiedRanks.add(r); // current index is still tied
                    r  = mean(tiedRanks); // use mean of tied ranks as the rank for tied value
                    tiedRanks.clear();
                }
                map.put(vSorted.get(i), r);   
            }
        }
        
        List<Double> out = new ArrayList<>();
        for (int i = 0; i < n; i++) out.add(map.get(vSorted.get(i)));
        
        return out;
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
    
}
