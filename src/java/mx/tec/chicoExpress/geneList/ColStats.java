/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneList;

/**
 *
 * @author INTEL
 */
public class ColStats {
    
    private String min;
    private String max;
    
    public ColStats(String min, String max) {
        this.min = min;
        this.max = max;
    }
    
    public String getMin() {
        return min;
    }
    
    public String getMax() {
        return max;
    }
    
}
