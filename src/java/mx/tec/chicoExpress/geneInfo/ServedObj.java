/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneInfo;

import java.util.List;

/**
 *
 * @author INTEL
 */
public class ServedObj {
    
    private List<GeneInfo> geneInfoArray;
    private String[] headers;
    
    public ServedObj(List<GeneInfo> geneInfoArray, String[] headers) {
        
        this.geneInfoArray = geneInfoArray;
        this.headers = headers;
        
    }
    
}
