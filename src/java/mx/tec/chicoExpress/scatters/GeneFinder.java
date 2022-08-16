/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.util.List;

/**
 *
 * @author INTEL
 */
public class GeneFinder {
    
    public static int main(String gene, String ensemblsPath, String namesPath) {
        String cleanGene = clean(gene);
        String idType = typeId(cleanGene, ensemblsPath, namesPath);
        return id2idx(cleanGene, idType);
    }
    
    public static String clean(String gene) {
        // strip whitespace
        return gene.replaceAll("\\s+", "");
    }
    
    public static String typeId(String gene, String ensemblsPath, String namesPath) {
        String idType;
        
        if (gene.contains("ENSG")) {
            idType = ensemblsPath;
        } else if (gene.matches(".*[a-zA-Z].*")) {
            idType = namesPath;
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
            List<String> genes = SimpleFileReader.readSingleField(idType);
            idx = genes.indexOf(gene) + 1; // internal database indices are 1-based
        }

        return idx;
    }
    
}
