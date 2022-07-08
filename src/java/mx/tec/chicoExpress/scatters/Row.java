/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

/**
 *
 * @author INTEL
 */
public class Row {
    private Datum [] c;
    
    public Row(Datum d, int n){
        super();
        this.c = new Datum[n];
        for (int i = 0; i < n; i++) {
            c[i] = d;
        }
    }
    
    public void setDatum(Datum d, int i) {
        c[i] = d;
    }
}
