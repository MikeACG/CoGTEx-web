/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tec.chicoExpress.rankComp;

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
        
        public void setText(String[] ensembls, String[] symbols, List<Double> xvals,
                List<Double> yvals) {
            
            int n = this.x.size();
            this.text = new String[n];
            for (int i = 0; i < n; i++) {
                this.text[i] = ensembls[i] + " " + symbols[i] + "<br>";
                this.text[i] += "xval: " + xvals.get(i) + " - yval: " + yvals.get(i);
            }
            
        }
        
    }
    
    public class Layout {
        
        String title;
        LayoutAxis xaxis = new LayoutAxis();
        LayoutAxis yaxis = new LayoutAxis();
        
        public Layout() {
            
        }
        
        public void setTitle(String ensembl, String symbol, int ngenes) {
            
            this.title = ensembl + " " + symbol + " reverse rank comparison (" +
                    ngenes + " total genes)";
            
        }
        
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
            
            public void setTitle(String version, String db) {
                
                this.title = new LayoutAxisTitle();
                this.title.text = version + " " + db;
                
            }
            
            //public void setRange(int[] range) {
                
            //    this.range = range;
                
            //}
            
        }
        
    }
    
}
