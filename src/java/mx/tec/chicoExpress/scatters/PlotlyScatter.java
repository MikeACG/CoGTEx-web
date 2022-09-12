/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tec.chicoExpress.scatters;

import java.util.List;

/**
 *
 * @author mike
 */
public class PlotlyScatter {
    
    Trace trace = new Trace();
    Layout layout = new Layout();
    
    public PlotlyScatter() {
        
    }
    
    public static String format(double v) {
        
        double roundFactor = 1000.0;
        double vRounded = Math.round(v * roundFactor) / roundFactor;
        return Double.toString(vRounded);
        
    }
    
    public class Trace {
        
        List<Double> x;
        List<Double> y;
        String[] text;
        
        String type = "scatter";
        String mode = "markers";
        
        public Trace() {
            
        }
        
        public void setX(List<Double> x) {
            
            this.x = x;
            
        }
        
        public void setY(List<Double> y) {
            
            this.y = y;
            
        }
        
        public void setText(List<Gene> genes) {
            
            int n = genes.size();
            this.text = new String[n];
            for (int i = 0; i < n; i++) {
                
                this.text[i] = genes.get(i).getMetadata("ensembl") + " " + genes.get(i).getMetadata("symbol") + "<br>";
                //this.text[i] += "x: " + format(this.x.get(i)) + " - y: " + format(this.y.get(i)) + "<br>";
                this.text[i] += "x estimate: " + genes.get(i).getRawSigned("x") + " - y estimate: " + genes.get(i).getRawSigned("y") + "<br>";
                this.text[i] += "x rank: " + genes.get(i).getRank("x") + " - y rank: " + genes.get(i).getRank("y");
            
            }
            
        }
        
    }
    
    public class Layout {
        
        //String title;
        LayoutAxis xaxis = new LayoutAxis();
        LayoutAxis yaxis = new LayoutAxis();
        
        public Layout() {
            
        }
        
        //public void setTitle(String title) {
            
        //   this.title = title;
            
        //}
        
        public class LayoutAxis {
            
            LayoutAxisTitle title = new LayoutAxisTitle();
            //int[] range;
            
            public LayoutAxis() {
                
            }
            
            public class LayoutAxisTitle {
    
                String text;

                public LayoutAxisTitle() {

                }
                
            }
            
            public void setTitle(String ensembl, String symbol, String format, 
                    String version, String database) {
                
                String title = ensembl + "-" + symbol + " " + format + "<br>"
                        + version + " " + database;
                this.title = new LayoutAxisTitle();
                this.title.text = title;
                
            }
            
            //public void setRange(int[] range) {
                
            //    this.range = range;
                
            //}
            
        }
        
    }
    
}
