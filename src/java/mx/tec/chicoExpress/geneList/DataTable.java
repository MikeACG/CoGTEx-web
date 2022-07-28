/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneList;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class DataTable {
    
    private List<List<String>> data;
    private List<DTCol> columns;
    
    private Object[][] order;
    private Object[] lengthMenu;
    
    // private boolean scrollX = true; // this option is desirable, but it duplicates headers/footers making the per-column search boxes non-functional
    private String pagingType = "full_numbers";
    private boolean responsive = true;
    private String[] buttons = new String[] {"copy", "csv", "excel"};
    private String dom = "Blfrtip";
    private Language language = new Language("General Search:");
    private boolean processing = true;
    
    public DataTable(List<List<String>> data, List<DTCol> columns, 
            int orderColIdx) {
        this.data = data;
        this.columns = columns;
        this.order = new Object[][] {{orderColIdx, "desc"}};
        this.lengthMenu = makeLengthMenu(data.size());
    }
    
    public String[] getColumnTitles() {
        int n = columns.size();
        String[] out = new String[n];
        for (int i = 0; i < n; i++) out[i] = columns.get(i).getTitle();
        return out;
    }
    
    public String[] getDataColumn(int colIdx) {
        
        int n = data.size();
        String[] out = new String[n];
        
        for (int i = 0; i < n; i++) out[i] = data.get(i).get(colIdx);
        
        return out;
    }
    
    public boolean[] getColumnVisibilities() {
        int n = columns.size();
        boolean[] out = new boolean[n];
        for (int i = 0; i < n; i++) out[i] = columns.get(i).getVisible();
        return out;
    }
    
    public static Object[] makeLengthMenu(int dataLength) {
        
        List<Integer> lengths = new ArrayList<>();
        List<String> names = new ArrayList<>();
        
        if (dataLength >= 10) lengths.add(10);
        if (dataLength >= 20) lengths.add(20);
        if (dataLength >= 50) lengths.add(50);
        if (dataLength >= 100) lengths.add(100);
        if (dataLength >= 200) lengths.add(200);
        if (dataLength >= 500) lengths.add(500);
        if (dataLength >= 1000) lengths.add(1000);
        
        if (dataLength > lengths.get(lengths.size() - 1)) lengths.add(-1);
        int n = lengths.size();
        String name;
        for (int i = 0; i < n; i++) {
            name = String.valueOf(lengths.get(i));
            if (i == n - 1) {
                names.add("All (" + String.valueOf(dataLength) + ")");
            } else {
                names.add(name);
            }
        }
        
        return new Object[] {lengths, names};
    }
    
    public void intStrDivide2doubleStr(int after, double dividingDoub) {
        
        List<String> row;
        int tmp, i, j;
        
        for (i = 0; i < data.size(); i++) {
            row = new ArrayList<>();
            for (j = 0; j < data.get(0).size(); j++) {
                if (j > after) {
                    tmp = Integer.parseInt(data.get(i).get(j));
                    row.add(String.valueOf( ((double) tmp) / dividingDoub ));
                } else {
                    row.add(data.get(i).get(j));
                }
            }
            data.set(i, row);
        } 
        
    }
    
    public void addMaxDeltas(String [] targetLabs, double roundFactor) {
        
        String[] colTitles;
        List<Integer> targetIdxs;
        List<double[]> deltas;
        String[] maxDelta;
        String colPrefix;
        int insertIdx;
        
        for (String lab : targetLabs) {
            colTitles = getColumnTitles();
            targetIdxs = containsIdxs(lab, colTitles);
            deltas = combinationsDeltas(targetIdxs);
            maxDelta = doublePmax2Str(deltas, roundFactor);
            insertIdx = targetIdxs.get(targetIdxs.size() - 1) + 1; // after max index of target cols
            insertDataColumn(maxDelta, insertIdx);
            colPrefix = colTitles[targetIdxs.get(0)].split(" ")[0];
            columns.add(insertIdx, new DTCol(colPrefix + lab + "max &Delta;"));
        }
        
    }
    
    public static List<Integer> containsIdxs(String substr, String[] strArr) {
        
        int n = strArr.length;
        List<Integer> out = new ArrayList<>();
        
        for (int i = 0; i < n; i++) {
            if (strArr[i].contains(substr)) out.add(i);
        }
        
        return out;
    }
    
    public List<double[]> combinationsDeltas(List<Integer> idxs) {
        
        String[] icol, jcol;
        int n = idxs.size();
        int i, j;
        List<double[]> out = new ArrayList<>();
        
        for (i = 0; i < n - 1; i++) {
            icol = getDataColumn(idxs.get(i));
            for (j = i + 1; j < n; j++) {
                jcol = getDataColumn(idxs.get(j));
                out.add(delta(icol, jcol));
            }
        }
        
        return out;
    }
    
    public static double[] delta(String[] x, String[] y) {
        
        int n = x.length;
        double[] out = new double[n];
        double xd, yd, checkmin;
        
        for (int i = 0; i < n; i++){
            xd = Double.parseDouble(x[i]);
            yd = Double.parseDouble(y[i]);
            checkmin = Math.min(xd, yd); // if min is negative, delta needs to be padded
            out[i] = (checkmin >= 0) ? Math.abs(xd - yd) : 
                    Math.abs( (xd + Math.abs(checkmin)) - (yd + Math.abs(checkmin)) );
        } 
        
        return out;
    }
    
    public static String[] doublePmax2Str(List<double[]> list, double roundFactor) {
        
        int n = list.size();
        int m = list.get(0).length;
        double[] col;
        int j, i;
        String[] out = new String[m];
        
        for (j = 0; j < m; j++) {
            col = new double[n];
            for (i = 0; i < n; i++) col[i] = list.get(i)[j];
            out[j] = String.valueOf(format(max(col), roundFactor));
        }
        
        return out;
    }
    
    public static double max(double[] arr) {
        
        double currentMax = Double.NEGATIVE_INFINITY;
        
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] > currentMax) currentMax = arr[i];
        }
        
        return currentMax;
    }
    
    public void insertDataColumn(String[] col, int idx) {
        
        List<String> row;
        
        for (int i = 0; i < data.size(); i++) {
            row = data.get(i);
            row.add(idx, col[i]);
            data.set(i, row);
        }
        
    }
    
    public static double format(double v, double roundFactor) {
        return Math.round(v * roundFactor) / roundFactor;
    }
    
}
