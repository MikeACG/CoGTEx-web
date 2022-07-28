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
public class ServedObj {
    
    private DataTable dataTable;
    private String dataTableHTML;
    private String selectorsHTML;
    
    public ServedObj(DataTable dataTable, String dataTableHTML, 
            String selectorsHTML) {
        this.dataTable = dataTable;
        this.dataTableHTML = dataTableHTML;
        this.selectorsHTML = selectorsHTML;
    }
    
}