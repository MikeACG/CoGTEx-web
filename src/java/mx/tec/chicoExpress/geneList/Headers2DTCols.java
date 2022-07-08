/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneList;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class Headers2DTCols {
    
    public static List<DTCol> convert(List<String> headers, int after) {
        
        int n = headers.size();
        DTCol dtCol;
        List<DTCol> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            dtCol = new DTCol(headers.get(i));
            if (i == 0) dtCol.setClassName("dt-nowrap dt-body-left");
            if (i > after) dtCol.setType("abs-num");
            out.add(dtCol);
        }
        
        return out;
    }
    
}
