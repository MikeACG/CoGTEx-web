/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.box;

/**
 *
 * @author INTEL
 */
public class ServedObj {
    
    private PlotlyTrace[] data;
    private PlotlyLayout layout;
    private String[] medianColors;
    
    public ServedObj(PlotlyTrace[] data, PlotlyLayout layout, 
            String[] medianColors) {
        
        this.data = data;
        this.layout = layout;
        this.medianColors = medianColors;
        
    }
    
}
