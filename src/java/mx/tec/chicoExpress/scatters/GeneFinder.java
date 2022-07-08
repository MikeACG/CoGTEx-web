/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class GeneFinder {
    
    public static int main(String gene) {
        String cleanGene = clean(gene);
        String idType = typeId(cleanGene);
        return id2idx(cleanGene, idType);
    }
    
    public static String clean(String gene) {
        // strip whitespace
        return gene.replaceAll("\\s+", "");
    }
    
    public static String typeId(String gene) {
        String idType;
        
        if (gene.contains("ENSG")) {
            idType = "chicoExpress/v0.2A/aux-files/ensembls";
        } else if (gene.matches(".*[a-zA-Z].*")) {
            idType = "chicoExpress/v0.2A/aux-files/symbols";
        } else {
            idType = "index";
        }
        
        return idType;
    }
    
    public static int id2idx(String gene, String idType) {
        int idx;
        
        if (idType.equals("index")) {
            idx = Integer.valueOf(gene);
        } else {
            List<String[]> lines = SimpleFileReader.read(idType + ".txt", "\t");
            List<String> genes = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) genes.add(lines.get(i)[0]);
            idx = genes.indexOf(gene) + 1; // internal database indices are 1-based
        }

        return idx;
    }
    
}
