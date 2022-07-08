/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneList;

/**
 *
 * @author INTEL
 */
public class DataTableHTML {
    
    public static String build(DataTable dataTable, int statAfter, 
            double dividingDoub) {
        
        String headHTML = tableHead(dataTable.getColumnTitles());
        String footHTML = tableFoot(dataTable, statAfter, dividingDoub);
        
        return headHTML + footHTML;
    }
    
    public static String tableHead(String[] ths) {
        
        String html = "<thead> <tr>";
        for (String th : ths) html += "<th>" + th + "</th>";
        html += "</tr> </thead>";
        
        return html;
    }
    
    public static String tableFoot(DataTable dataTable, int statAfter, 
            double dividingDoub) {
        
        String html = "<tfoot>";
        
        int inputBoxSize;
        String[] ths = dataTable.getColumnTitles();
        
        html += "<tr>";
        for (String th : ths) {
            inputBoxSize = 4;
            html += "<th> <input class='searchCol' type='text' size=" + inputBoxSize 
                    + " placeholder='?" + th + "' /> </th>";
        }
        html += "</tr>";
        
        int ncols = ths.length;
        ColStats[] colStats = new ColStats[ncols];
        
        for (int i = 0; i < ncols; i++) {
            if (i < statAfter) { // pad with missing values
                colStats[i] = new ColStats("", "");
            } else if (i == statAfter) { // pad with statistic name
                colStats[i] = new ColStats("Min:", "Max:");
            } else { // compute statistics
                colStats[i] = computeColStats(dataTable.getDataColumn(i), 
                    dividingDoub);
            }
            
        }
        html += addColStats(colStats);
        
        html += "</tfoot>";
        
        return html;
    }
    
    public static ColStats computeColStats(String[] col, double dividingDoub) {
        
        double currentMin = Double.parseDouble(col[0]);
        double currentMax = Double.parseDouble(col[0]);
        double tmp;
        
        for (int i = 1; i < col.length; i++) {
            tmp = Double.parseDouble(col[i]);
            if (Math.abs(tmp) < Math.abs(currentMin)) currentMin = tmp;
            if (Math.abs(tmp) > Math.abs(currentMax)) currentMax = tmp;
        }
        String min = String.valueOf(format(currentMin, dividingDoub));
        String max = String.valueOf(format(currentMax, dividingDoub));
        
        return new ColStats(min, max);
    }
    
    public static String addColStats(ColStats[] colStats) {
        
        String html = "";
        
        html += "<tr>";
        for (ColStats colStat : colStats) {
            html += "<th>" + colStat.getMin() + "</th>";
        }
        html += "</tr>";
        
        html += "<tr>";
        for (ColStats colStat : colStats) {
            html += "<th>" + colStat.getMax() + "</th>";
        }
        html += "</tr>";
        
        return html;
    }
    
    public static double format(double v, double roundFactor) {
        return Math.round(v * roundFactor) / roundFactor;
    }
}
