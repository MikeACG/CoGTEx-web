/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneHome;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author INTEL
 */
public class DataTable {
    
    private List<String[]> data;
    private DTCol[] columns;
    
    private Object[][] order;
    private Object[] lengthMenu;
    
    // private boolean scrollX = true; // this option is desirable, but it duplicates headers/footers making the per-column search boxes non-functional
    private String pagingType = "full_numbers";
    private boolean responsive = true;
    private String[] buttons = new String[] {"copy", "csv", "excel"};
    private String dom = "Blfrtip";
    private Language language = new Language("General Search:");
    private boolean processing = true;
    
    public DataTable(List<String[]> data, DTCol[] columns, String orderHeader) {
        this.data = data;
        this.columns = columns;
        this.order = findColIndex(orderHeader, columns);
        this.lengthMenu = makeLengthMenu(data.size());
    }
    
    public String[] getColumnTitles() {
        int n = columns.length;
        String[] out = new String[n];
        for (int i = 0; i < n; i++) out[i] = columns[i].getTitle();
        return out;
    }
    
    public boolean[] getColumnVisibilities() {
        int n = columns.length;
        boolean[] out = new boolean[n];
        for (int i = 0; i < n; i++) out[i] = columns[i].getVisible();
        return out;
    }
    
    public static Object[][] findColIndex(String col, DTCol[] cols) {
        
        DTCol dtcol;
        int idx = -1;
        
        for (int i = 0; i < cols.length; i++) {
            dtcol = cols[i];
            if(dtcol.getTitle().toLowerCase().contains(col.toLowerCase())) {
                idx = i;
            }
        }
        
        return new Object[][] {{idx, "desc"}};
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
    
}
