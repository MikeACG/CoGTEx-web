/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneHome;

import java.util.List;

/**
 *
 * @author INTEL
 */
public class Headers2DTCols {
    
    public static DTCol[] convert(List<String> headers, String[] shown) {
        
        int n = headers.size();
        DTCol dtCol;
        DTCol[] out = new DTCol[n];
        String header;
        
        for (int i = 0; i < n; i++) {
            header = headers.get(i);
            dtCol = new DTCol(header);
            if (i == 0) {
                dtCol.setClassName("dt-nowrap dt-body-left");
                dtCol.setWidth("5%");
            }
            if (strArrContains(header, shown)) {
                dtCol.setVisible(true);
            }
            if (header.toLowerCase().contains("description")) {
                dtCol.setClassName("dt-body-center"); // remove no-wrap
            }
            out[i] = dtCol;
        }
        
        return out;
    }
    
    public static boolean strArrContains(String query, String[] arr) {
        
        for (String str : arr) {
            if (str.toLowerCase().contains(query.toLowerCase())) return true;
        } 
        
        return false;
    }
    
}
