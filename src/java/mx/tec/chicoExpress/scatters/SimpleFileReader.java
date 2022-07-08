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
    
    public static List<String []> read(String file, String sep) {     
        List<String []> matrix = new ArrayList<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                matrix.add(line.split(sep));
            }
            //for (String[] strings : matrix) System.out.println(Arrays.toString(strings));
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
        
        return matrix;
    }
    
}