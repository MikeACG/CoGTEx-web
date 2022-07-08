/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.network;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class AssociationsFetcher {
    
    public static double[][] fetchGeneset(String[] geneset, String dbpath, 
            List<String> ensemblsRef, int formatFactor, int diagval) {
        
        HashMap<String,Integer> ensemblsInDb = list2mapKeys(ensemblsRef);
        int[] genesetIdxs = mapIds2Idxs(geneset, ensemblsInDb);
        int[] idxs2fetch = combinationsPositions(genesetIdxs, 
                ensemblsInDb.size());
        int[] associations = sqlFetchIdxs(idxs2fetch, dbpath);
        double[][] associationsMatrix = expandFlatComatrix(associations, 
                geneset.length, formatFactor, diagval);
        
        return associationsMatrix;
    }
    
    public static HashMap<String,Integer> list2mapKeys(List<String> L) {
        
        HashMap<String,Integer> out = new HashMap<>();
        
        for (int i = 0; i < L.size(); i++) {
            out.put(L.get(i), i);
        }
        
        return out;
    }
    
    public static int[] mapIds2Idxs(String[] geneset, 
            HashMap<String,Integer> allgenes) {
        
        int n = geneset.length;
        int[] out = new int[n];
        
        for (int i = 0; i < n; i++) {
            out[i] = allgenes.get(geneset[i]);
        }
        
        return out;
        
    }
    
    public static int[] combinationsPositions(int[] idxs, int ngenes) {
        
        int k = 0;
        int n = idxs.length;
        int[] out = new int[ (n * (n - 1)) / 2 ];
        
        int i, j;
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                if (i < j) out[k++] = getPairIdx(idxs[i], idxs[j], ngenes);
            }
        }
        
        return out;
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
        
        return a - b + c + 1;
    }
    
    public static int[] sqlFetchIdxs(int[] idxs, String dbpath) {
        
        int n = idxs.length;
        int[] out = new int[n];
        String sql = "SELECT rowid,* FROM '1' WHERE rowid=";
        ResultSet rs;
        
        try (Connection conn = sqlConnect(dbpath)) {
            Statement stmt = conn.createStatement();
            for (int i = 0; i < n; i++) {
                rs = stmt.executeQuery(sql + idxs[i]);
                out[i] = rs.getInt(2);
            }
            conn.close();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return out;
    }
    
    public static Connection sqlConnect(String dbpath) {
        
        String url = "jdbc:sqlite:" + dbpath;
        Connection conn = null;
        
        try{
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite successful");
        }
        catch(ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
        }
        
        return conn;
    }
    
    
    public static double[][] expandFlatComatrix(int[] flatM, int nobs, 
            int formatFactor, int diagval) {
        
        double[][] out = new double[nobs][nobs];
        
        int[] indexPairs;
        int i, j;
        for (i = 0; i < nobs; i++) {
            indexPairs = getIndexPairs(i, flatM, nobs, diagval);
            for (j = 0; j < nobs; j++) {
                out[i][j] = ( (double) indexPairs[j]) / ( (double) formatFactor);
            }
        }
        
        return out;
    }
    
    public static int[] getIndexPairs(int idx, int[] data, int n, int ownval) {
        
        int consecStart, i, j;
        int[] out = new int[n];

        // find position where consecutive pairs for idx start
        consecStart = (idx * n); // if all indices had passed for all before idx
        for (i = 1; i <= idx; i++) {
            consecStart -= i;
            out[i] = out[i - 1] + (n - i);
        }

        // pinpoint index of idx as a pair of each of the left behind indices
        for (i = 0; i < idx; i++) {
            out[i] += idx - i - 1;
        }

        // fill the rest of the vector with the consecutive pairs indices, skip own pair
        for (j = idx + 1; j < n; j++){
            out[j] = consecStart++;
        }

        // get the pairs
        for (i = 0; i < n; i++) {
            if(i != idx) out[i] = data[out[i]];
        }

        // own pair is forced
        out[idx] = ownval;
        return out;
    }
}
