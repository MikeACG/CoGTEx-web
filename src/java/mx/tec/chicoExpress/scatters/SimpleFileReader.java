/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class SimpleFileReader {
    
    public static List<String []> readFirstAndIth(String file, String sep, int colIdx) {     
        List<String []> matrix = new ArrayList<>();
        String line;
        String[] fields;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                fields = line.split(sep);
                matrix.add(new String[] {fields[0], fields[colIdx]});
            }
            //for (String[] strings : matrix) System.out.println(Arrays.toString(strings));
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
        
        return matrix;
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
