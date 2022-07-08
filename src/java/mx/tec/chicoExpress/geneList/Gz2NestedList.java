/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author INTEL
 */
public class Gz2NestedList {
    
    /**
     * @param infile path to single-character separated gzip file
     * @param sep character that separates the fields in infile
     * @param nrows maximum number of rows to return
     * @param filterColumn field used to determine the top rows to return
     * @return a list where each element are the rows of the .gz file to read in the form of arrays of strings
     */
    
    
    public static List<List<String>> main(String infile, String sep, int nrows, 
            int filterColumn, List<List<String>> lines){
        
        List<Integer> filterList = new ArrayList<>(); // to store values that will be the guide for sorting the lines
        
        try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(infile));
                BufferedReader br = new BufferedReader(new InputStreamReader(gzip));) {
            String line;
            String[] fields;
            int i = 0;
            while ( (line = br.readLine()) != null ){ // read until end of file
                fields = line.split(sep);
                filterList.add(Integer.parseInt(fields[filterColumn]));
                lines.get(i).addAll(Arrays.asList(fields));
                i++;
                //System.out.println(Arrays.toString(fields));
            }
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
        if (nrows > 0) filterLines(lines, filterList, nrows); // -1 can be passed as short-hand for return all rows
        return lines;
    }
    
    public static void filterLines(List<List<String>> lines, List<Integer> filterList, int nrows) {
        
        int n = filterList.size();
        Pair[] pairs = new Pair[n];
        for (int i = 0; i < n; i++) { // create the index-value pairs
            pairs[i] = new Pair(i, Math.abs(filterList.get(i))); // abs handles pearson and spearman correlation
        }
        Arrays.sort(pairs); // sorts the values but keeps track of original index
        
        int nremove = n - nrows;
        List<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < nremove; i ++) { // get the nremove weakest values
            toRemove.add((int) pairs[i].getIndex());
        }
        Collections.sort(toRemove, Collections.reverseOrder()); // reverse so that the highest index of lines is removed at each iteration, making lower indices unchanged
        
        for (int i = 0; i < nremove; i++) {
            lines.remove((int) toRemove.get(i));
        }
    }
    
    public static class Pair implements Comparable<Pair> {
    
        public final int index;
        public final int value;

        public Pair(int index, int value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public int compareTo(Pair other) {
            return Integer.valueOf(this.value).compareTo(other.value);
        }

        public Object getIndex() { 
            return index; 
        }

        public Object getValue() { 
            return value; 
        }

        @Override
        public String toString() {
            return "Index: " + getIndex() + " Value: " + getValue();
        }

    }
}
