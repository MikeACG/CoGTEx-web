/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.rankComp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class SimpleFileReader {
    
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
    
    public static List<List<String>> readMultiField(String file, String sep) {     
        List<List<String>> matrix = new ArrayList<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                matrix.add(new ArrayList<>(Arrays.asList(line.split(sep)))); // nested lists are mutable
            }
            //for (String[] strings : matrix) System.out.println(Arrays.toString(strings));
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
        
        return matrix;
    }
    
}
