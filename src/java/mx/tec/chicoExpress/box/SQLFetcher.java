/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.box;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class SQLFetcher {
    
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
