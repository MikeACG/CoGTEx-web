/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.network;

import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author INTEL
 */
public class HierarchicalClusterer {
    
    private double[][] data;
    private double[] distances;
    private HashMap<Integer, Cluster> clusters;
    
    public HierarchicalClusterer(double[][] data) {
        this.data = data;
    }
    
    public double[][] getData() {
        return data;
    }
    
    public HashMap<Integer, Cluster> getClusters() {
        return clusters;
    }
    
    public void calculateEuclideanDists() {
        
        int nobs = data.length;
        distances = new double[(nobs * (nobs - 1)) / 2];
        int d = 0;
        
        double[] x, y;
        int i, j, k;
        for (i = 0; i < nobs - 1; i++) {
            x = data[i];
            for (j = i + 1; j < nobs; j++) {
                y = data[j];
                for (k = 0; k < nobs; k++) {
                    distances[d] += Math.pow(Math.abs(x[k]) - Math.abs(y[k]), 2);
                }
                distances[d] = Math.sqrt(distances[d]);
                d++;
            }
        }
        
    }
    
    public void cluster() {
        
        int iteration = 1;
        int nobs = data.length;
        clusters = initialize(nobs);
        int nextId = nobs;
        HashMap<Integer, Cluster> currentClusters = initialize(nobs);
        int ncurrent = nobs;
        
        while(ncurrent > 1) {
            int[] toMerge = which2merge(currentClusters, distances, nobs);
            Cluster mergedCluster = mergeClusters(currentClusters, toMerge, 
                    iteration, nextId);
            updateCurrentClusters(currentClusters, toMerge, mergedCluster, 
                    nextId);
            clusters.put(nextId, mergedCluster);
            nextId++;
            ncurrent--;
            iteration++;
        }
        
    }
    
    public static HashMap<Integer,Cluster> initialize(int n) {
        
        HashMap<Integer,Cluster> out = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            out.put(i, new Cluster(new int[] {i}, i, 0));
        }
        
        return out;
    }
    
    public static int[] which2merge(HashMap<Integer, Cluster> clusters, double[] D, int n) {
        
        double min = Double.POSITIVE_INFINITY;
        double d;
        int[] toMerge = new int[] {0, 0};
        
        for (int i : clusters.keySet()) {
            for (int j : clusters.keySet()) {
                if (i < j) {
                    d = singleLinkageDistance(clusters.get(i), 
                            clusters.get(j), D, n);
                    if (d < min) {
                        min = d;
                        toMerge = new int[] {i, j};
                    }
                }
            }
        }
        
        return toMerge;
    }
    
    public static double singleLinkageDistance(Cluster A, Cluster B, double[] D, 
            int n) {
        
        double d;
        double min = Double.POSITIVE_INFINITY;
        
        for (int i : A.getMembers()) {
            for (int j : B.getMembers()) {
                d = D[getPairIdx(i, j, n)];
                if (d < min) min = d;
            }
        }
        
        return min;
    }
    
    public static int getPairIdx(int x, int y, int n) {
        
        int min, max, a, b, c;
        
        if (x < y) {
            min = x;
            max = y;
        } else {
            min = y;
            max = x;
        }
        
        a = n * min;
        b = (min * (min + 1)) / 2;
        c = max - min - 1;
        
        return a - b + c;
    }
    
    public static Cluster mergeClusters(HashMap<Integer,Cluster> clusters, 
            int[] toMerge, int iteration, int id) {
        
        int[] members1 = clusters.get(toMerge[0]).getMembers();
        int n1 = members1.length;
        int[] members2 = clusters.get(toMerge[1]).getMembers();
        int n2 = members2.length;
        int[] merged = new int[n1 + n2];
        int j = 0;
        
        for (int i = 0; i < n1; i++) {
            merged[j++] = members1[i];
        }

        for (int i = 0; i < n2; i++) {
            merged[j++] = members2[i];
        }
        
        return new Cluster(merged, id, iteration);
    }
    
    public static void updateCurrentClusters(HashMap<Integer,Cluster> clusters, 
            int[] toMerge, Cluster mergedCluster, int id) {
        
        clusters.remove(toMerge[0]);
        clusters.remove(toMerge[1]);
        
        clusters.put(id, mergedCluster);
        
    }
    
}
