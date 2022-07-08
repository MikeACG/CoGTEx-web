/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class DatabaseParser {
    
    public static String main(String db, int x, int y, String version) {
        
        String dbName;
        if (db.contains("min_")) { // need to make a query to identify what realization gives the minimum
            int pairIdx = getPairIdx(x, y, getNgenes(version));
            int origin = SQLFetcher.fetchOrigin(pairIdx, db.split("_")[1] + ".sqlite", version);
            dbName = origin + ".sqlite";
        } else if (db.equals("ref")) { // reference database is just a predetermined realization
            dbName = "16.sqlite";
        } else { // just add extension
            dbName = db + ".sqlite";
        }
        
        return dbName;
    }
    
    public static int getNgenes(String version) {
        
        List<String[]> lines = SimpleFileReader.read(
                "chicoExpress/" + version + "/aux-files/ensembls.txt", "\t");
        List<String> genes = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) genes.add(lines.get(i)[0]);
        
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
