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
public class Gene2Idx {
    
    public static int convert(String gene, String genesFile) {
        
        List<String> genes = SimpleFileReader.readSingleField(genesFile);
        
        return genes.indexOf(gene) + 1; // internal database indices are 1-based
        
    }
    
}
