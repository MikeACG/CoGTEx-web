/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.coplots;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author INTEL
 */
public class GCScatterObj {
    private ColDef [] cols;
    private List<Row> rows;
    
    public GCScatterObj(ColDef [] cols, List<Row> rows) {
        super();
        this.cols = cols;
        this.rows = rows;
    }
    
    // this can be more robust by iterating cols for the 'data' role of column
    public int getGroupsN() {
        int n = cols.length;
        n -= 1; // minus the domain value column
        n -= 4; // minus the series for the breaks (lines)
        n /= 3; // minus the tooltip and aesthetics column for each series
        return n;
    }
    
    // this is currently unused as plotting order does not depend on row order in google charts
    public void permuteRows(int rng) {
        Collections.shuffle(rows, new Random((long) rng));
    }
}
