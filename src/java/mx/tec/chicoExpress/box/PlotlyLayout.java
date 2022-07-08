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
public class PlotlyLayout {
    
    private String title;
    private LayoutAxis xaxis;
    private LayoutAxis yaxis;
    
    private LayoutMargin margin = new LayoutMargin(80, 50, 20, 30);
    
    private boolean showlegend = false;
    
    public PlotlyLayout(String ensembl, String symbol, String db, String xTitle, 
            String yTitle, double xlabsAngle, double ylabsAngle, 
            double xlabsSize, double ylabsSize, PlotlyTrace[] traces) {
        
        this.title = makeTitle(ensembl, symbol, db);
        this.xaxis = new LayoutAxis(xTitle, xlabsAngle, xlabsSize, "array", 
                traces);
        this.yaxis = new LayoutAxis(yTitle, ylabsAngle, ylabsSize, "auto", 
                traces);
        
        
    }
    
    public class LayoutAxis {
    
        private LayoutAxisTitle title;
        private double tickangle;
        private LayoutAxisTickfont tickfont;
        private String tickmode;
        private String[] ticktext;
        private int[] tickvals;

        private String linecolor = "black";
        private boolean mirror = true;
        private boolean zeroline = false;
        private String hoverformat = ".3f";

        public LayoutAxis(String title, double tickangle, double size, 
                String tickmode, PlotlyTrace[] traces) {
            
            this.title = new LayoutAxisTitle(title);
            this.tickangle = tickangle;
            this.tickfont = new LayoutAxisTickfont(size);
            this.tickmode = tickmode;
            this.ticktext = extractTracesTexts(traces);
            this.tickvals = intSeq(0, traces.length);
            
        }
        
        public class LayoutAxisTitle {
    
            private String text;

            public LayoutAxisTitle(String text) {
                
                this.text = text;
                
            }
        }
        
        public class LayoutAxisTickfont {
            
            private double size;
            
            public LayoutAxisTickfont(double size) {
                
                this.size = size;
                
            }
            
        }

    }
    
    public class LayoutMargin {
        
        private int b;
        private int l;
        private int r;
        private int t;
        
        public LayoutMargin(int b, int l, int r, int t) {
            this.b = b;
            this.l = l;
            this.r = r;
            this.t = t;
        }
        
    }
    
    public static String makeTitle(String ensembl, String symbol, String db) {
        
        return ensembl + " " + symbol + " - db: " + db;
        
    }
    
    public static String[] extractTracesTexts(PlotlyTrace[] traces) {
        
        int n = traces.length;
        String[] out = new String[n];
        
        for (int i = 0; i < n; i++) {
            out[i] = traces[i].getText();
        }
        
        return out;
    }
    
    public static int[] intSeq(int start, int end) {
        
        int n = end - start;
        int[] out = new int[n];
        
        for (int i = start; i < end; i++) {
            out[i] = i;
        }
        
        return out;
    }
    
}
