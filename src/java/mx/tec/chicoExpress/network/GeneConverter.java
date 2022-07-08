/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.network;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class GeneConverter {
    
    public static List<String> ensembls2symbols(String[] ensemblsQuery, 
            List<String> ensemblsRef, List<String> symbolsRef) {
        
        List<String> out = new ArrayList<>();
        
        for (String ensembl : ensemblsQuery) {
            out.add(symbolsRef.get(ensemblsRef.indexOf(ensembl)));
        }
        
        return out;
    }
    
    public static List<String> symbols2ensembls(String[] genesQuery, 
            List<String> ensemblsRef, List<String> symbolsRef) {
        
        List<String> out = new ArrayList<>();
        
        int idx;
        for (String gene : genesQuery) {
            if (gene.contains("ENSG")) { // already ensembl
                out.add(gene);
            } else {
                idx = symbolsRef.indexOf(gene);
                if (idx >= 0) { // symbol found in database
                    out.add(ensemblsRef.get(idx));
                }
            }
        }
        
        return out;
        
    }
}
