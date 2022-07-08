/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import java.sql.*;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import java.io.File;

/**
 *
 * @author INTEL
 */
public class SQLFetcher {
    
    public static Gene main(int geneIdx, String dbFile, String version) {
        String expPath = "chicoExpress/" + version + "/source-data/sql/numExp/" + dbFile;
        String brksDir = "chicoExpress/" + version + "/source-data/sql/dscBins/";
        boolean check = new File(brksDir, dbFile).exists();
        String brksPath =  check ? brksDir + dbFile : brksDir + "mean.sqlite";
        
        Gene gene = fetchGene(geneIdx, expPath);
        List<Double> brks = fetchBreaks(gene, geneIdx, brksPath);
        gene.setBrks(brks);
        
        return gene;
    }
    
    public static Connection connect(String dbpath) {
        
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
    
    public static Gene fetchGene(int geneIdx, String dbpath) {
        List<Double> expression = new ArrayList<>();
        List<String> samples = new ArrayList<>(); 
        String preSql = "SELECT rowid,* FROM '";
        String posSql = "' WHERE rowid=" + geneIdx;
        
        ResultSet rs;
        String sql;
        ResultSetMetaData meta;
        int i, j, ncols;
        try (Connection conn = connect(dbpath)) {
            int nTables = countTables(conn);
            Statement stmt = conn.createStatement();
            for (i = 1; i <= nTables; i++) {
                sql = preSql + i + posSql;
                rs = stmt.executeQuery(sql);
                meta = rs.getMetaData();
                ncols = meta.getColumnCount();
                for (j = 2; j <= ncols; j++) {
                    expression.add(rs.getDouble(j));
                    samples.add(meta.getColumnName(j));
                }
            }
            conn.close();
            //System.out.println(Arrays.toString(expression.toArray()));
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return new Gene(expression, samples);
    }
    
    public static List<Double> fetchBreaks(Gene gene, int geneIdx, String dbpath) {
        List<Double> brks = new ArrayList<>();
        String preSql = "SELECT rowid,* FROM '";
        String posSql = "' WHERE rowid=" + geneIdx;
        
        ResultSet rs;
        String sql;
        ResultSetMetaData meta;
        int i, j, ncols;
        try (Connection conn = connect(dbpath)) {
            int nTables = countTables(conn);
            Statement stmt = conn.createStatement();
            for (i = 1; i <= nTables; i++) {
                sql = preSql + i + posSql;
                rs = stmt.executeQuery(sql);
                meta = rs.getMetaData();
                ncols = meta.getColumnCount();
                for (j = 2; j <= ncols; j++) brks.add(rs.getDouble(j));
            }
            conn.close();
            //System.out.println(Arrays.toString(brks.toArray()));
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return brks;
    }
    
    public static int fetchOrigin(int idx, String dbName, String version) {
        String dbpath = "chicoExpress/" + version + "/source-matrix/sql/minsOrigin/" + dbName;
        String sql = "SELECT rowid,* FROM '1' WHERE rowid=" + idx;
        
        ResultSet rs;
        int origin = -1;
        try (Connection conn = connect(dbpath)) {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            origin = rs.getInt(2);
            conn.close();
            //System.out.println(Arrays.toString(brks.toArray()));
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return origin;
    }
    
    public static int countTables(Connection conn) {
        int count = 0;
        try {
            ResultSet md = conn.getMetaData().getTables(null, null, null, null);
            while (md.next()) {
                count++;
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }
    
}
