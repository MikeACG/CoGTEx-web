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
public class ColDef {
    private String id;
    private String label;
    private String type;
    private String role;
    
    public ColDef(String id, String label, String type, String role) {
        super();
        this.id = id;
        this.label = label;
        this.type = type;
        this.role = role;
    }
}
