/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.geneHome;

/**
 *
 * @author INTEL
 */
public class DataTableHTML {
    
    public static String buildSkeleton(DataTable dataTable) {
        
        String headHTML = tableHead(dataTable.getColumnTitles());
        String footHTML = tableFoot(dataTable);
        
        return headHTML + footHTML;
    }
    
    public static String tableHead(String[] ths) {
        
        String html = "<thead> <tr>";
        for (String th : ths) html += "<th>" + th + "</th>";
        html += "</tr> </thead> ";
        
        return html;
    }
    
    public static String tableFoot(DataTable dataTable) {
        
        String html = "<tfoot>";
        
        int inputBoxSize;
        String[] ths = dataTable.getColumnTitles();
        
        html += "<tr>";
        for (String th : ths) {
            inputBoxSize = 4;
            html += "<th> <input class='searchCol' type='text' size=" + inputBoxSize 
                    + " placeholder='?" + th + "' /> </th>";
        }
        html += "</tr> </tfoot>";
        
        return html;
    }
    
    public static String buildColSelectors(DataTable dataTable) {
        
        int maxWordLength = 6;
        int shortLength = 3;
        
        String[] headers = dataTable.getColumnTitles();
        boolean[] visibilities = dataTable.getColumnVisibilities();
        String label, h;
        String html = "Table fields: ";
        
        for (int i = 0; i < headers.length; i++) {
            h = headers[i];
            label = shortenColname(h, maxWordLength, shortLength);
            html += "<input type='checkbox' id='" + h + "'";
            if (visibilities[i]) {
                html += " checked";
            }
            html += ">";
            html += "<label for='" + h + "'>" + label + "</label>";
        }
        
        return html;
    }
    
    public static String shortenColname(String colname, int maxWordLength, int
            shortenLength) {
        
        String cleanName = colname.trim().replaceAll("\\s\\s+", " ");
        String[] words = cleanName.split(" ");
        int n = words.length;
        String[] out = new String[n];
        
        for (int i = 0; i < n; i++) {
            out[i] = (words[i].length() > maxWordLength) 
                    ? words[i].substring(0, shortenLength) : words[i];
        }
        
        return String.join(" ", out);
    }
    
}
