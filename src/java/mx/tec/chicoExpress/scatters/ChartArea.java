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
public class ChartArea {
    private String width;
    private String height;
    private BackgroundColor backgroundColor = new BackgroundColor("black", 3);
    
    public ChartArea(String width, String height) {
        this.width = width;
        this.height = height;
    }
}
