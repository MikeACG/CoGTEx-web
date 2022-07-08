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
public class GCoptions {
    private Axis hAxis;
    private Axis vAxis;
    private int pointSize;
    private ChartArea chartArea;
    private String legend = "none";
    private String seriesType = "scatter";
    private String title = "";
    
    public GCoptions(Axis hAxis, Axis vAxis, int pointSize, ChartArea chartArea) {
        this.hAxis = hAxis;
        this.vAxis = vAxis;
        this.pointSize = pointSize;
        this.chartArea = chartArea;
    }
    
    public void makeTitle(double[] stats, String[] labels, String db, int n, 
            String version) {
        
        int nstats = stats.length;
        String sep = " | ";
        
        for (int i = 0; i < nstats; i++) {
            if (i == nstats - 1) {
               title += labels[i] + " " + format(stats[i]);  
            } else {
               title += labels[i] + " " + format(stats[i]) + sep;
            }
        }
        
        title += "\ndb: " + db + sep + "n: " + n + sep + "version: " + version;
        
    }
    
    public static double format(double v) {
        double roundFactor = 100.0;
        
        double vRounded = Math.floor(v * roundFactor) / roundFactor;
        
        return vRounded;
    }
}
