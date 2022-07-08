/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.box;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author INTEL
 */
public class DatabaseParser {
 
    public static String makeFile(String dbrequest, String dbspath) {
        
        String dbfile;
        
        if (dbrequest.equals("ref")) {
            dbfile = "16.sqlite";
        } else if (dbrequest.equals("all")) { // special case depending on the name of the database that has all samples
            dbfile = "E_sampleFilter_geneFilter_normalized_combat_cluster_tsne.sqlite";   
        } else {
            dbfile = dbrequest + ".sqlite";
        }
        
        return dbspath + dbfile;
    }
    
    public static String makeTitle(String dbRequest, String dbFile) {
        
        Path path = Paths.get(dbFile);
        String baseName = path.getFileName().toString();
        String dbName = baseName.replace(".sqlite", "");
        
        String title;
        if (dbRequest.equals(dbName)) {
            title = dbRequest;
        } else {
            title = dbRequest + "(" + dbName + ")";
        }
        if (title.contains("E_sampleFilter_geneFilter_normalized_combat_cluster_tsne")) {
            title = "All";
        } // special case depending on the name of the database that has all samples
        
        return title;
        
    }
    
}
