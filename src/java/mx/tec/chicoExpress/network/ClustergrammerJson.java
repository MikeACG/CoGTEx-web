/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class ClustergrammerJson {
    
    double[][] mat;
    Node[] row_nodes;
    Node[] col_nodes;
    MatrixColors matrix_colors;
    
    public ClustergrammerJson(double[][] mat) {
        this.mat = mat;
    }
    
    public void makeRowNodes(HashMap<Integer, Cluster> clusters, 
            List<String> geneNames) {
        
        int nobs = mat.length;
        row_nodes = new Node[nobs];
        System.out.println(Arrays.toString(clusters.get(clusters.size() - 1).getMembers()));
        double[] sums = rowSums();
        double[] variances = rowVariances(sums);
        int[] sumRanks = rankNoTie(sums);
        int[] varRanks = rankNoTie(variances);
        for (int i = 0; i < nobs; i++) {
            row_nodes[i] = makeRowNode(i, geneNames, sumRanks, varRanks, 
                    clusters);
        }
        
    }
    
    public double[] rowSums() {
        
        int n = mat.length;
        double[] out = new double[n];
        
        for (int i = 0; i < n; i++) {
            for (double val : mat[i]) {
                out[i] += val;
            }
        }
        
        return out;
    }
    
    public double[] rowVariances(double[] sums) {
        
        int n = sums.length;
        double[] out = new double[n];
        int i, j;
        
        double[] means = new double[n];
        for (i = 0; i < n; i++) {
            means[i] = sums[i] / ( (double) n );
        }
        
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                out[i] += Math.pow(mat[i][j] - means[i], 2);
            }
            out[i] /= (double) n - 1;
        }
        
        return out;
    }
    
    public static int[] rankNoTie(double[] v) {
        
        double[] vSorted = v.clone();
        Arrays.sort(vSorted);
        int n = vSorted.length;
        
        HashMap<Double, Integer> map = new HashMap<>();
        int i, r;
        for (i = 0; i < n; i++) {
            r = i + 1; // rank for current value
            map.put(vSorted[i], r);   
        }
        
        int[] out = new int[n];
        for (i = 0; i < n; i++) out[i] = map.get(v[i]);
        
        return out;
    }
    
    public Node makeRowNode(int obsIdx, List<String> geneNames, int[] sumRanks, 
            int[] varRanks, HashMap<Integer, Cluster> clusters) {
        
        int shortenLen = 11;
        
        int nclusts = clusters.size();
        
        String name = geneNames.get(obsIdx);
        int ini = obsIdx;
        int clust = arrayIndexOf(obsIdx, clusters.get(nclusts - 1).getMembers());
        int rank = sumRanks[obsIdx];
        int rankvar = varRanks[obsIdx];
        List<Integer> clusterHistory = makeClusterHistory(obsIdx, clusters, 
                nclusts);
        List<Integer> groups = shortenClusterHistory(clusterHistory, shortenLen);
        
        return new Node(name, ini, clust, rank, rankvar, groups);
    }
    
    public int arrayIndexOf(int query, int[] array) {
        
        int indexOf = -1;
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] == query) {
                indexOf = i;
                break;
            }
        }
        
        return indexOf;
    }
    
    public List<Integer> makeClusterHistory(int obsIdx, 
            HashMap<Integer, Cluster> clusters, int n) {
        
        List<Integer> out = new ArrayList<>();
        out.add(obsIdx); // observation is in its own singleton cluster first
        
        Cluster currentCluster;
        int idxInCluster;
        int lastClusterIdx = obsIdx;
        for (int i = 0; i < n; i++) {
            currentCluster = clusters.get(i);
            if (currentCluster.getIteration() > 0) { // non-singleton cluster
                idxInCluster = arrayIndexOf(obsIdx, currentCluster.getMembers());
                if (idxInCluster >= 0) { // was incorporated into current cluster
                    out.add(currentCluster.getId());
                    lastClusterIdx = currentCluster.getId();
                } else { // stayed in last cluster
                    out.add(lastClusterIdx);
                }
            }
        }
        
        return out;
    }
    
    public static List<Integer> shortenClusterHistory(List<Integer> history, 
            int shortenLength) {
        
        int n = history.size();
        if (n <= shortenLength) return history;
        List<Integer> out = new ArrayList<>();
        
        int[] idxs = interpolateRange(0, n - 1, shortenLength);
        for (int i = 0; i < shortenLength; i++) {
            out.add(history.get(idxs[i]));
        }
        
        return out;
    }
    
    public static int[] interpolateRange(int x, int y, int n) {
        
        double[] out = new double[n];
        out[0] = (double) x;
        
        double step = ((double)(y - x)) / ((double) (n - 1));
        for (int i = 1; i < n; i++) {
            out[i] = out[i - 1] + step;
        }
        
        return roundArray(out);
    }
    
    public static int[] roundArray(double[] array) {
        
        int n = array.length;
        int[] out = new int[n];
        
        for (int i = 0; i < n; i++) {
            out[i] = (int) Math.round(array[i]);
        }
        
        return out;
    }
    
    public class Node {
        
        String name;
        int ini;
        int clust;
        int rank;
        int rankvar;
        List<Integer> group;
        
        public Node(String name, int ini, int clust, int rank, int rankvar, 
                List<Integer> group) {
            this.name = name;
            this.ini = ini;
            this.clust = clust;
            this.rank = rank;
            this.rankvar = rankvar;
            this.group = group;
        }
        
    }
    
    public void makeColNodes() {
        col_nodes = row_nodes;
    }
    
    public void makeMatrixColors() {
        matrix_colors = new MatrixColors("blue", "red");
    }
    
    public class MatrixColors {
        
        private String neg;
        private String pos;
        
        public MatrixColors(String neg, String pos) {
            this.neg = neg;
            this.pos = pos;
        }
    }
    
}
