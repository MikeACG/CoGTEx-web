/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneList;

/**
 *
 * @author INTEL
 */
public class DTCol {
    
    private String title;
    private String className = "dt-nowrap dt-body-center";
    private String type = null;
    
    public DTCol(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
}
