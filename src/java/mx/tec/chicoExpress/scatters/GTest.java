/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.util.List;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * @author INTEL
 */
public class GTest {
    
    private Gene x;
    private Gene y;
    private int ncats = 3; // this GTest assumes 3 discrete categories per random variable
    private double eAdj = 0.5; // for calculation at the individual cell level, if expected value is lesser than this it is set to expecAdj
    private double[] gAdj = new double[] {4, 1}; // for calculation at the individual cell level, if G is greater than gAdj[0] and expected value less than gAdj[1], G is recalculated with expected value gAdj[1]
    private double gsum = 0;
    private int[][] obs = new int[ncats][ncats];
    private int[] rowsums = new int[ncats];
    private int[] colsums = new int[ncats];
    private double[][] expec = new double[ncats][ncats];
    private double[][] g = new double[ncats][ncats];
    private double[][] pvals = new double[ncats][ncats];
    
    public GTest(Gene x, Gene y) {
        
        this.x = x;
        this.y = y;
        
    }
    
    public void run() {
        // discreticize gene expression
        int[] xdsc = discretize(x);
        int[] ydsc = discretize(y);
        
        // make crosstable of observed samples
        crosstable(xdsc, ydsc);
        
        // compute test
        compute(xdsc.length);
    }
    
    public static int[] discretize(Gene gene) {
        
        List<Double> exp = gene.getExpression();
        int n = exp.size();
        double brk1 = gene.getBrk(0);
        double brk2 = gene.getBrk(1);
        
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            out[i] = (exp.get(i) < brk1) ? 0 : (exp.get(i) > brk2) ? 2 : 1;
        }
        
        return out;
    }
    
    public void crosstable(int[] x, int[] y) {
        
        for (int i = 0; i < x.length; i++) {
            obs[x[i]][y[i]]++;
            rowsums[x[i]]++;
            colsums[y[i]]++;
        }
        
    }
    
    public void compute(int m) {
        int i, j, o;
        double e, gtmp;
        
        for (i = 0; i < ncats; i++) {
            for (j = 0; j < ncats; j++) {
                o = obs[i][j];
                e = (double) (rowsums[i] * colsums[j]) / (double) m;
                if (e < eAdj) e = eAdj;
                gtmp = (o > 0) ? o * Math.log(o / e) : 0;
                if (gtmp > gAdj[0] && e < gAdj[1]) {
                    e = gAdj[1];
                    gtmp = o * Math.log(o / e);
                }
                expec[i][j] = e;
                g[i][j] = gtmp;
                pvals[i][j] = cellPostHoc(o, e);
                gsum += gtmp;
                
            }
        }
    }
    
    public static double cellPostHoc(int o, double e) {
        double zscore = ((double) o - e) / Math.sqrt(e);
        if (zscore > 0) zscore *= -1;
        NormalDistribution d = new NormalDistribution(); // mean 0 and standard deviation 1
        return d.cumulativeProbability(zscore);
    }
    
    public double getG() {
        return gsum * 2;
    }
    
    public int getNcats() {
        return ncats;
    }
    
    public int[][] getObs() {
        return obs;
    }
    
    public double[][] getExpec() {
        return expec;
    }
    
    public double[][] getGcells() {
        return g;
    }
    
    public double[][] getPvals() {
        return pvals;
    }
}
