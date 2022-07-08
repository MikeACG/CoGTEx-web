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
public interface Datum {
    
    class StrDatum implements Datum {
        private String v;
        private String f;

        public StrDatum(String v, String f) {
            super();
            this.v = v;
            this.f = f;
        }
    }
    
    class NumDatum implements Datum {
        private double v;
        private String f;

        public NumDatum(double v, String f) {
            super();
            this.v = v;
            this.f = f;
        }
    }

}
