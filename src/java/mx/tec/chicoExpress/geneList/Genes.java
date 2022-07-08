/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneList;

import java.util.List;

/**
 *
 * @author INTEL
 */
public class Genes {
    
    List<String> genes;
    
    public Genes(List<String> genes) {
        this.genes = genes;
    }
    
    public int id2idx(String id) {
        return genes.indexOf(id);
    }
    
    public int[] calcCoidxs(int geneIdx) {
        
        int n = genes.size();
        int consecStart, i;
        int[] out = new int[n];

        // find position where consecutive pairs for geneIdx start
        consecStart = (geneIdx * n); // if all indices before geneIdx where paired with all genes
        for (i = 1; i <= geneIdx; i++) {
            consecStart -= i;
            out[i] = out[i - 1] + (n - i); // consecutive pairs start of i
        }

        // pinpoint index of genIdx as a pair of each of the left behind indices
        for (i = 0; i < geneIdx; i++) {
            out[i] += geneIdx - i - 1;
        }

        // fill the rest of the vector with the consecutive pairs indices, skip own pair
        for (i = geneIdx + 1; i < n; i++){
            out[i] = consecStart++;
        }

        return out;
    }
    
}
