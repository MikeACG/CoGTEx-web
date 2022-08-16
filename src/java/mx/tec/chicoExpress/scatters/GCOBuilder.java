/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.scatters;

import mx.tec.chicoExpress.scatters.Datum.NumDatum;
import mx.tec.chicoExpress.scatters.Datum.StrDatum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

/**
 *
 * @author INTEL
 */
public class GCOBuilder {
    
    public static GCScatterObj build(Gene x, Gene y) {
        int nGroupedCols = 3; // each group (series) of samples has y-value, tooltip and an aesthetic column; modifying this requires modifying the program
        HashMap<String, Integer> uGroups = uniqGroups(y.getGroups());
        int ncols = (uGroups.size() * nGroupedCols) + 5; // number of columns in GCO: 1 for x-values, 4 for the lines denoting the breaks, and (1 for aesthetics, 1 tooltips and 1 y-values + 1 tooltip column per unique group of samples)
        List<Row> gcoRows = buildRows(x, y, uGroups, ncols, nGroupedCols);
        ColDef [] gcoColDefs = buildCols(uGroups, ncols);
        return new GCScatterObj(gcoColDefs, gcoRows);
    }
    
    public static List<Row> buildRows(Gene x, Gene y, HashMap<String, Integer> uGroups, int ncols, int nGroupedCols) {
        
        List<String> aes = y.GCOAes(); // aesthetics of points in GCO format
        List<String> tooltips = y.GCOTooltips(); // tooltips of points in GCO format
        List<String> ctooltips = addTooltipCoords(tooltips, x.getExpression(), y.getExpression()); // add point coordinates to tooltips
        
        int nsamples = x.getSamples().size();
        int i, j, k;
        Row currentRow;
        List<Row> gcoRows = new ArrayList<>();
        for (i = 0; i < nsamples; i++) {
            currentRow = new Row(new StrDatum(null, null), ncols);  // initialize columns of row to a default state
            currentRow.setDatum(new NumDatum(x.getExp(i), format(x.getExp(i))), 0); // first column is always expression value for gene x, sample i
            k = uGroups.get(y.getGroup(i)); // unique index of sample i's group
            j = (k * nGroupedCols) - (nGroupedCols - 1); // index where the relevant columns for sample i start (as given by the group of sample i) 
            currentRow.setDatum(new NumDatum(y.getExp(i), format(y.getExp(i))), j);  // expression value for gene y, sample i
            currentRow.setDatum(new StrDatum(ctooltips.get(i), null), j + 1); // tooltips for sample i
            currentRow.setDatum(new StrDatum(aes.get(i), null), j + 2); // aesthetics of sample i
            gcoRows.add(currentRow);
        }
        addBrkRows(gcoRows, x, y, ncols);
        
        return gcoRows;
    }
    
    public static ColDef [] buildCols(HashMap<String, Integer> uGroups, int ncols) {
        ColDef [] gcoColDefs = new ColDef[ncols];
        
        gcoColDefs[0] = new ColDef("x", "xValues", "number", "domain"); // x-value column
        String key;
        int n = uGroups.size();
        int j = 1;
        for (int k = 1; k <= n; k++) {
            key = getKeyByValue(uGroups, k); // use google guaiva bimap instead ?
            gcoColDefs[j++] = new ColDef("y_" + key, key, "number", "data");
            gcoColDefs[j++] = new ColDef("tt_" + key, "info_" + key, "string", "tooltip");
            gcoColDefs[j++] = new ColDef("aes_" + key, "aesthetics_" + key, "string", "style");
        }
        addBrkCols(gcoColDefs, ncols);
        
        return gcoColDefs;
    }
    
    public static void addBrkRows(List<Row> rows, Gene x, Gene y, int ncols) {
        int[] brksIdxs = {ncols - 4, ncols - 2}; // where the columns for the series of first and second breaks start (each line is a series, there are 4 lines)
        double xmin = x.getMinExp();
        double ymin = y.getMinExp();
        double xmax = x.getMaxExp();
        double ymax = y.getMaxExp();
        
        double brk;
        String fbrk;
        Row row;
        
        for (int i = 0; i < brksIdxs.length; i++){ // for each break
            brk = x.getBrk(i);
            fbrk = format(brk);
            // assemble the point which says where the line starts for this break of gene x
            row = new Row(new StrDatum(null, null), ncols);
            row.setDatum(new NumDatum(brk, fbrk), 0); // set domain values column (always first) to the breaks of x gene 
            row.setDatum(new NumDatum(ymin, fbrk), brksIdxs[i]); // value for the column giving this break's series is the min of the y gene's expression
            rows.add(row);
            
            // assemble the point which says where the line ends for this break of gene x
            row = new Row(new StrDatum(null, null), ncols);
            row.setDatum(new NumDatum(brk, fbrk), 0); // set domain values column (always first) to the breaks of x gene 
            row.setDatum(new NumDatum(ymax, fbrk), brksIdxs[i]); // value for the column giving this break's series is the max of the y gene's expression
            rows.add(row);
            
            brk = y.getBrk(i);
            fbrk = format(brk);
            // assemble the point which says where the line starts for this break of gene y
            row = new Row(new StrDatum(null, null), ncols); 
            row.setDatum(new NumDatum(xmin, fbrk), 0); // set domain values column (always first) to the min of the x gene's expression
            row.setDatum(new NumDatum(brk, fbrk), brksIdxs[i] + 1); // value for the column giving this break's series is the break itself of the y gene's expression
            rows.add(row);
            
            // assemble the point which says where the line ends for this break of gene y
            row = new Row(new StrDatum(null, null), ncols); 
            row.setDatum(new NumDatum(xmax, fbrk), 0); // set domain values column (always first) to the max of the x gene's expression
            row.setDatum(new NumDatum(brk, fbrk), brksIdxs[i] + 1); // value for the column giving this break's series is the break itself of the y gene's expression
            rows.add(row);
        }
        
    }
    
    public static void addBrkCols(ColDef[] coldefs, int ncols) {
        int[] brksIdxs = {ncols - 4, ncols - 2}; // where the columns for the series of first and second breaks start (each line is a series, there are 4 lines)
        
        for (int i = 0; i < brksIdxs.length; i++){ // for each break
            coldefs[brksIdxs[i]] = new ColDef("x_brk" + i, "x_brk" + i, "number", "data");
            coldefs[brksIdxs[i] + 1] = new ColDef("y_brk" + i, "y_brk" + i, "number", "data");
        }
    }
    
    public static HashMap<String, Integer> uniqGroups(List<String> groups) {
        HashMap<String,Integer> ugroups = new HashMap<>();
        
        int n = groups.size();
        String g;
        int j = 1; // 0 is x-value, start from enumerating the groups from 1
        for (int i = 0; i < n; i++) {
            g = groups.get(i);
            if (!ugroups.containsKey(g)) ugroups.put(g, j++);
        }
      
        return ugroups;
    }
    
    public static String format(double v) {
        double roundFactor = 1000.0;
        double vRounded = Math.round(v * roundFactor) / roundFactor;
        return Double.toString(vRounded);
    }
    
    public static List<String> addTooltipCoords(List<String> tooltips, List<Double> x, List<Double> y) {
        List<String> ctooltips = new ArrayList<>();
        
        int nsamples = tooltips.size();
        String tooltip, xCoord, yCoord;
        for (int i = 0; i < nsamples; i++) {
            tooltip = tooltips.get(i);
            xCoord = format(x.get(i));
            yCoord = format(y.get(i));
            ctooltips.add(tooltip + " (" + xCoord + ", " + yCoord + ")");
        }
        
        return ctooltips;
    }
    
    public static String getKeyByValue(HashMap<String, Integer> map, int value) {
        for (Entry<String, Integer> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    
}
