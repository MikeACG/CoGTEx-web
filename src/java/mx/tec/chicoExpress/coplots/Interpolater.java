/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.coplots;

/**
 *
 * @author INTEL
 */
public class Interpolater {
    
    public static double[] doubSeq(double start, double end, int len) {
        double step = (end - start) / (len - 1);
        double[] out = new double[len];
        
        out[0] = format(start, 1);
        for (int i = 1; i < len - 1; i++) {
            out[i] = format(start + (step * i), 0);
        }
        out[len - 1] = format(end, -1);
        
        return out;
    }
    
    public static String[] intSeqAsStr(int start, int len){
        String[] out = new String[len];
        int i = 0;
        
        while (i < len) {
            out[i++] = Integer.toString(start++);
        }
        
        return out;
    }
    
    public static double format(double v, int type) {
        double roundFactor = 100.0;
        double vRounded;
        
        if (type > 0) {
            vRounded = Math.ceil(v * roundFactor) / roundFactor;
        } else if (type < 0) {
            vRounded = Math.floor(v * roundFactor) / roundFactor;
        } else {
            vRounded = Math.round(v * roundFactor) / roundFactor;
        }
        
        return vRounded;
    }
            
}
