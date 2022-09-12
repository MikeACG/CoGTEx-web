/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.coplots;

import java.util.List;

/**
 *
 * @author INTEL
 */
public class PearsonCorrelation {
    
    public static double main(Gene A, Gene B) {
        List<Double> x = A.getExpression();
        List<Double> y = B.getExpression();
        
        double xmean = mean(x);
        double ymean = mean(y);
        
        return productMoment(x, y, xmean, ymean);
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
}
