/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class FetchSampleMeta {
    
    public static HashMap<String, String> getSampleMap(String file, String sep, int colIdx) {
        
        HashMap<String, String> map = new HashMap<>();
        
        List<String[]> meta = SimpleFileReader.readFirstAndIth(file, sep, colIdx);
        int nrows = meta.size();
        String [] row;
        for (int i = 0; i < nrows; i++) {
            row = meta.get(i);
            if (row.length != 2){
                System.out.println(i);
                System.out.println(Arrays.toString(row));
            }
            map.put(row[0], row[1]);
        }
        return map;
    }
    
    public static List<String> mapSamples(List<String> samples, HashMap<String, String> map) {
        
        List<String> meta = new ArrayList<>();
        
        int nsamples = samples.size();
        for (int i = 0; i < nsamples; i++) {
            meta.add(map.get(samples.get(i)));
        }
        
        return meta;
    }
    
}
