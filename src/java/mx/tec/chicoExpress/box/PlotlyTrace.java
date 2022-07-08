/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.box;

import java.util.List;

/**
 *
 * @author INTEL
 */
public class PlotlyTrace {
    
    private String name;
    private List<Double> y;
    private String type;
    private String text;
    private TraceLine line;
    private String fillcolor;
    private TraceHoverlabel hoverlabel;
    
    private boolean boxpoints = false;
    
    public PlotlyTrace(String name, List<Double> y, String type, String text, 
            String fillColor) {
        
        this.name = name;
        this.y = y;
        this.type = type;
        this.text = text;
        this.line = new TraceLine();
        this.fillcolor = fillColor;
        this.hoverlabel = new TraceHoverlabel(fillcolor);
        
    }
    
    public String getFillcolor() {
        return fillcolor;
    }
    
    public String getText() {
        return text;
    }
    
    public class TraceLine {
    
        private String color = "black";
        private int width = 1;

        public TraceLine() {

        }
        
    }
    
    public class TraceHoverlabel {
        
        private String bgcolor;
        
        private int namelength = -1;
        
        public TraceHoverlabel(String color) {
            
            this.bgcolor = color;
            
        }
        
    }
    
}
