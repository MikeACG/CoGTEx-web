/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneHome;

/**
 *
 * @author INTEL
 */
public class DTCol {
    
    private String title;
    private String className = "dt-nowrap dt-body-center";
    private boolean visible = false;
    private String width = null;
    
    public DTCol(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public boolean getVisible() {
        return visible;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void setWidth(String width) {
        this.width = width;
    }
}
