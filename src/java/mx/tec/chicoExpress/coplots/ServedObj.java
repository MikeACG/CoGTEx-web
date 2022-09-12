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
public class ServedObj {
    private GCScatterObj scatter;
    private GCoptions scatteropts;
    private CustomLegend legend;
    private String[] brksSeries;
    private ContingencyTable contingencyTable;
    
    public ServedObj(GCScatterObj scatter, GCoptions scatteropts, 
            CustomLegend legend, ContingencyTable contingencyTable, 
            String[] brksSeries) {
        this.scatter = scatter;
        this.scatteropts = scatteropts;
        this.legend = legend;
        this.brksSeries = brksSeries;
        this.contingencyTable = contingencyTable;
    }
}
