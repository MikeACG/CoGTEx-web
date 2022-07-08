/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author INTEL
 */
public class SimpleFileReader {
    
    public static List<GeneInfo> readMultiFieldFilter2GeneInfo(String file, 
            String sep, int filteringColumnIdx, Set<String> toKeepOnFilter, 
            List<String> headers, String[] headers2keep) {     
        
        int n = headers2keep.length;
        int[] headers2keepIdxs = new int[n];
        int i;
        for (i = 0; i < n; i++) {
            headers2keepIdxs[i] = headers.indexOf(headers2keep[i]);
        }
        
        String line;
        String[] fields;
        String[] info;
        List<GeneInfo> infoTable = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                fields = line.split(sep);
                if (toKeepOnFilter.contains(fields[filteringColumnIdx])) {
                    info = new String[n];
                    for (i = 0; i < n; i++) {
                        info[i] = fields[headers2keepIdxs[i]];
                    }
                    infoTable.add(new GeneInfo(fields[filteringColumnIdx], info));
                }
            }
            //for (String[] strings : matrix) System.out.println(Arrays.toString(strings));
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
        
        return infoTable;
    }
    
    public static List<String> readSingleField(String file) {     
        List<String> out = new ArrayList<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                out.add(line);
            }
            //for (String[] strings : matrix) System.out.println(Arrays.toString(strings));
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
        
        return out;
    }
    
}
