package mx.tec.chicoExpress.scatters;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class GzTableReader {
    
    public static List<Gene> readCol2genes(String infile, String sep, 
            double formatFactor, int colIdx){
        
        List<Gene> genes = new ArrayList<>();
        
        try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(infile));
                BufferedReader br = new BufferedReader(new InputStreamReader(gzip));) {
            Gene g;
            String line;
            String[] fields;
            int i = 0;
            while ( (line = br.readLine()) != null ){ // read until end of file
                fields = line.split(sep);
                g = new Gene();
                g.setX(Integer.parseInt(fields[colIdx]) / formatFactor);
                g.setIdx(i);
                i++;
                genes.add(g);
                //System.out.println(Arrays.toString(fields));
            }
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
        return genes;
    }
    
    public static void readCol2geneList(List<Gene> genes, String infile, 
            String sep, double formatFactor, int colIdx){
        
        try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(infile));
                BufferedReader br = new BufferedReader(new InputStreamReader(gzip));) {
            String line;
            String[] fields;
            int i = 0;
            while ( (line = br.readLine()) != null ){ // read until end of file
                fields = line.split(sep);
                genes.get(i).setY(Integer.parseInt(fields[colIdx]) / formatFactor);
                i++;
                //System.out.println(Arrays.toString(fields));
            }
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
    }
    
    public static List<Gene> readCols2genes(String infile, String sep, 
            double formatFactor, int xcol, int ycol){
        
        List<Gene> genes = new ArrayList<>();
        
        try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(infile));
                BufferedReader br = new BufferedReader(new InputStreamReader(gzip));) {
            Gene g;
            String line;
            String[] fields;
            int i = 0;
            while ( (line = br.readLine()) != null ){ // read until end of file
                fields = line.split(sep);
                g = new Gene();
                g.setX(Integer.parseInt(fields[xcol]) / formatFactor);
                g.setY(Integer.parseInt(fields[ycol]) / formatFactor);
                g.setIdx(i);
                i++;
                genes.add(g);
                //System.out.println(Arrays.toString(fields));
            }
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
        return genes;
    }
    
    public static List<Pair> readCol2pairs(String infile, String sep, int colIdx){
        
        List<Pair> col = new ArrayList<>(); // to store values that will be the guide for sorting the lines
        
        try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(infile));
                BufferedReader br = new BufferedReader(new InputStreamReader(gzip));) {
            String line;
            String[] fields;
            int i = 0;
            while ( (line = br.readLine()) != null ){ // read until end of file
                fields = line.split(sep);
                col.add(new Pair(i, Math.abs(Integer.parseInt(fields[colIdx]))));
                i++;
                //System.out.println(Arrays.toString(fields));
            }
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
        return col;
    }
    
    public static void filterPair(List<Pair> colSorted, List<Double> vals, List<Double> rank, int ntop) {
        
        Collections.reverse(colSorted);
        Collections.reverse(rank);
        Collections.reverse(vals);
        if (ntop < 1) return;
        int n = colSorted.size();
        
        for (int i = n - 1; i >= ntop; i--) {
            colSorted.remove(i);
            rank.remove(i);
            vals.remove(i);
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
