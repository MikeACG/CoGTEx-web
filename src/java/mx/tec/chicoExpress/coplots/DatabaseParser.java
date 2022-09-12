/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.coplots;

import java.util.List;

/**
 *
 * @author INTEL
 */
public class DatabaseParser {
    
    public static String main(String db, int x, int y, String version, String ensemblsPath) {
        
        String dbName;
        if (db.contains("min_")) { // need to make a query to identify what realization gives the minimum
            int pairIdx = getPairIdx(x, y, getNgenes(ensemblsPath));
            int origin = SQLFetcher.fetchOrigin(pairIdx, db.split("_")[1] + ".sqlite", version);
            dbName = origin + ".sqlite";
        } else if (db.equals("ref")) { // reference database is just a predetermined realization
            dbName = "16.sqlite";
        } else { // just add extension
            dbName = db + ".sqlite";
        }
        
        return dbName;
    }
    
    public static int getNgenes(String genesFilePath) {
        
        List<String> genes = SimpleFileReader.readSingleField(genesFilePath);
        
        return genes.size();
    }
    
    public static int getPairIdx(int x, int y, int n) {
        
        int min, max;
        if (x < y) {
            min = x;
            max = y;
        } else {
            min = y;
            max = x;
        }
        
        int havePassed = 0;
        for (int i = 1; i < min; i++) havePassed += n - i;
        
        return havePassed + (max - min);
        
    }
    
}
