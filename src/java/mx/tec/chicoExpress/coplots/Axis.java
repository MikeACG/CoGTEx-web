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
public class Axis {
    
    private String viewWindowMode = "explicit";
    private ViewWindow viewWindow;
    private String baselineColor = "transparent";
    private String gridlineColor = "transparent";
    private double[] ticks;
    private String title;
    private TextStyle textStyle;
    private boolean slantedText;
    private double slantedTextAngle;
    
    public Axis(ViewWindow viewWindow, String[] ids, double[] ticks, double fontSize, boolean slantedText) {
        this.viewWindow = viewWindow;
        this.ticks = ticks;
        this.title = makeTitle(ids);
        this.textStyle = new TextStyle(fontSize);
        this.slantedText = slantedText;
        this.slantedTextAngle = slantedText ? 90 : 0; // currently this does not work for the vAxis, seems to be a google charts problem
    }
    
    public static String makeTitle(String[] ids) {
        
        String out = "";
        for (String id : ids) out += id + " ";
        
        return out;
    }
}
